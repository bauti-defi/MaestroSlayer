package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.utility.Condition;

public class TargetFinder extends NodeTask implements BroadcastReceiver {

    private NPC target;

    public TargetFinder() {
        super(Priority.LOW);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null) {
            if (!RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition())) {
                walkToTask();
                return false;
            } else if (target == null || !target.exists()) {
                return true;
            }
            return inCombat(target) && !inCombat(provider.myPlayer()) || target.getHealthPercent() == 0 ||
                    inCombat(provider.myPlayer()) && (!inCombat(target) || !target.isInteracting(provider.myPlayer()));
        }
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (target != null && target.exists()) {
            if (inCombat(provider.myPlayer()) && (!target.isInteracting(provider.myPlayer()) || !inCombat(target))) {
                provider.log("Updating target");
                target = (NPC) provider.myPlayer().getInteracting();
            }
        } else {
            target = provider.getNpcs().closest(new Filter<NPC>() {
                @Override
                public boolean match(NPC npc) {
                    if (npc.isInteracting(provider.myPlayer())) {
                        return true;
                    }
                    return !inCombat(npc) && npc.hasAction("Attack") && npc.getName().contains(RuntimeVariables.currentTask.getCurrentMonster().getName())
                            && provider.getMap().canReach(npc) && RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(npc.getPosition());
                }
            });
            provider.log("Target found");
        }
        sendBroadcast(new Broadcast("new-target", target));
    }

    private boolean inCombat(Character character) {
        if (character != null && character.exists()) {
            return !character.isAttackable() || character.isUnderAttack() || character.getInteracting() != null;
        }
        return false;
    }

    private void walkToTask() {
        if (provider.getMap().isWithinRange(RuntimeVariables.currentTask.getCurrentMonster().getArea().unwrap().getRandomPosition(), provider
                .myPlayer(), 10) && provider.getMap().canReach(RuntimeVariables.currentTask.getCurrentMonster().getArea().unwrap().getRandomPosition())) {
            provider.log("Task in normal walking range");
            WalkingEvent walkingEvent = new WalkingEvent(RuntimeVariables.currentTask.getCurrentMonster().getArea().unwrap());
            walkingEvent.setOperateCamera(true);
            walkingEvent.setBreakCondition(new Condition() {
                @Override
                public boolean evaluate() {
                    return RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition());
                }
            });
            provider.log("Walking to task...");
            provider.execute(walkingEvent);
        } else {
            provider.log("Task in web walking range");
            WebWalkEvent webWalkEvent = new WebWalkEvent(RuntimeVariables.currentTask.getCurrentMonster().getArea().unwrap());
            webWalkEvent.useSimplePath();
            webWalkEvent.setPathPreferenceProfile(getToTaskPathPreference());
            webWalkEvent.setBreakCondition(new Condition() {
                @Override
                public boolean evaluate() {
                    return provider.getBank().isOpen() || RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition());
                }
            });
            provider.log("Walking to task...");
            provider.execute(webWalkEvent);
        }
    }

    private PathPreferenceProfile getToTaskPathPreference() {
        PathPreferenceProfile profile = new PathPreferenceProfile();
        profile.setAllowTeleports(true);
        profile.setAllowObstacles(true);
        profile.setAllowCharters(true);
        profile.setAllowGliders(true);
        profile.checkInventoryForItems(true);
        profile.checkEquipmentForItems(true);
        profile.checkBankForItems(true);
        return profile;
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "hover-target-antiban":
                if (target != null) {
                    target.hover();
                }
                break;
        }
    }
}
