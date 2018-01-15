package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.Combat;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.utility.Condition;

import java.util.Comparator;
import java.util.function.Predicate;

public class TargetFinder extends NodeTask implements BroadcastReceiver {

    private NPC target;
    private int currentExp = 0;

    public TargetFinder() {
        super(Priority.LOW);
    }

    @Override
    public Response runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null) {
            if (RuntimeVariables.experienceTracker.getGainedXP(Skill.SLAYER) != currentExp) {
                RuntimeVariables.currentTask.registerKill();
                currentExp = RuntimeVariables.experienceTracker.getGainedXP(Skill.SLAYER);
            }
            if (RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition())) {
                if (target == null || !target.exists()) {
                    return Response.EXECUTE;
                } else if (Combat.inCombat(target) && !Combat.inCombat(provider.myPlayer()) && !Combat.isAttacking(provider.myPlayer(),
                        target)) {
                    return Response.EXECUTE;
                } else if (Combat.inCombat(provider.myPlayer()) && !Combat.isAttacking(target, provider.myPlayer())) {
                    return Response.EXECUTE;
                }

            } else if (!RuntimeVariables.currentTask.isFinished() && !RuntimeVariables.currentTask.getCurrentMonster().getArea().contains
                    (provider.myPosition())) {
                return Response.EXECUTE;
            }
        }
        return Response.CONTINUE;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (!RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition())) {
            Position monsterAreaPosition = RuntimeVariables.currentTask.getCurrentMonster().getArea().unwrap().getRandomPosition();
            if (provider.getMap().isWithinRange(monsterAreaPosition, provider.myPlayer(), 25) && provider.getMap().canReach(monsterAreaPosition)) {
                normalWalkToTask(monsterAreaPosition);
            } else {
                webWalkToTask();
            }
            return;
        }
        if (!Combat.inCombat(provider.myPlayer())) {
            provider.log("Finding next target.");
            target = provider.getNpcs().getAll().stream().filter(new Predicate<NPC>() {
                @Override
                public boolean test(NPC npc) {
                    if (!Combat.isPartOfTask(npc)) {
                        return false;
                    }
                    return !Combat.inCombat(npc) && npc.hasAction("Attack") && npc.getHealthPercent() > 0 && provider.getMap().canReach(npc) &&
                            RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(npc.getPosition());
                }
            }).sorted(new Comparator<NPC>() {
                @Override
                public int compare(NPC o1, NPC o2) {
                    return provider.getMap().distance(o1) - provider.getMap().distance(o2);
                }
            }).findFirst().orElse(null);
        } else {
            provider.log("We were attacked, updating target to aggressor.");
            target = provider.getNpcs().getAll().stream().filter(new Predicate<NPC>() {
                @Override
                public boolean test(NPC npc) {
                    if (!Combat.isPartOfTask(npc)) {
                        return false;
                    }
                    return Combat.isAttacking(npc, provider.myPlayer());
                }
            }).findFirst().orElse(null);
        }
        if (target != null && target.exists()) {
            provider.log(target.getName() + " found.");
            sendBroadcast(new Broadcast("new-target", target));
        } else {
            provider.log("No targets found.");
        }
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
