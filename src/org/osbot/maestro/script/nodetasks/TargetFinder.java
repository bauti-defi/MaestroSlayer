package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.map.Position;
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
                Position monsterAreaPosition = RuntimeVariables.currentTask.getCurrentMonster().getArea().unwrap().getRandomPosition();
                if (provider.getMap().isWithinRange(monsterAreaPosition, provider.myPlayer(), 25) && provider.getMap().canReach(monsterAreaPosition)) {
                    normalWalkToTask(monsterAreaPosition);
                } else {
                    webWalkToTask();
                }
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
        target = provider.getNpcs().closest(new Filter<NPC>() {
            @Override
            public boolean match(NPC npc) {
                if (!inCombat(provider.myPlayer())) {
                    return !inCombat(npc) && npc.hasAction("Attack") && npc.getId() == RuntimeVariables.currentTask.getCurrentMonster()
                            .getId() && npc.getName().contains(RuntimeVariables.currentTask.getCurrentMonster().getName())
                            && provider.getMap().canReach(npc) && RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(npc.getPosition());
                }
                return npc.isInteracting(provider.myPlayer());
            }
        });
        if (target != null && target.exists()) {
            provider.log("Target found");
            sendBroadcast(new Broadcast("new-target", target));
        } else {
            provider.log("No targets found");
        }
    }

    private boolean inCombat(Character character) {
        if (character != null && character.exists()) {
            return !character.isAttackable() || character.isUnderAttack() || character.getInteracting() != null;
        }
        return false;
    }

    private void webWalkToTask() {
        provider.log("Task in web walking range");
        WebWalkEvent webWalkEvent = new WebWalkEvent(RuntimeVariables.currentTask.getCurrentMonster().getArea().unwrap());
        webWalkEvent.setPathPreferenceProfile(getToTaskPathPreference());
        webWalkEvent.setEnergyThreshold(10);
        webWalkEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return provider.getBank().isOpen() || RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition());
            }
        });
        if (webWalkEvent.prefetchRequirements(provider)) {
            provider.log("Walking to task...");
            provider.execute(webWalkEvent);
        } else {
            provider.log("Couldn't find path to " + RuntimeVariables.currentTask.getCurrentMonster().getName());
        }
    }

    private void normalWalkToTask(Position position) {
        provider.log("Walking back into monster area");
        WalkingEvent walkingEvent = new WalkingEvent(position);
        walkingEvent.setMiniMapDistanceThreshold(3);
        walkingEvent.setEnergyThreshold(10);
        walkingEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition());
            }
        });
        provider.execute(walkingEvent);
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
