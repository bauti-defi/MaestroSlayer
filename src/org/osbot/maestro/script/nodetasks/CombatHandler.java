package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.InteractionEvent;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

public class CombatHandler extends NodeTask implements BroadcastReceiver {

    private NPC monster;

    public CombatHandler() {
        super(Priority.VERY_LOW);
        registerBroadcastReceiver(this);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (!provider.getCombat().isAutoRetaliateOn()) {
            provider.log("Turning auto retaliate on...");
            provider.getCombat().toggleAutoRetaliate(true);
            new ConditionalSleep(2500, 500) {

                @Override
                public boolean condition() throws InterruptedException {
                    return provider.getCombat().isAutoRetaliateOn();
                }
            }.sleep();
        }
        if (provider.getConfigs().get(RuntimeVariables.combatStyle.getConfigParentId()) != RuntimeVariables.combatStyle.getConfigId()) {
            provider.log("Switching back to original combat style.");
            if (provider.getTabs().open(Tab.ATTACK)) {
                RS2Widget combatStyleWidget = provider.getWidgets().get(CombatStyle.ROOT_ID, RuntimeVariables.combatStyle.getChildId());
                if (combatStyleWidget != null && combatStyleWidget.isVisible()) {
                    combatStyleWidget.interact(combatStyleWidget.getInteractActions()[0]);
                    new ConditionalSleep(2500, 500) {

                        @Override
                        public boolean condition() throws InterruptedException {
                            return provider.getConfigs().get(RuntimeVariables.combatStyle.getConfigParentId()) == RuntimeVariables.combatStyle.getConfigId();
                        }
                    }.sleep();
                }
            }
        }
        if (RuntimeVariables.currentTask != null) {
            if (!RuntimeVariables.currentTask.haveRequiredInventoryItems(provider)) {
                provider.log("Need bank, missing inventory item...");
                //TODO:request bank
                return false;
            } else if (RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition())) {
                return monster != null && monster.exists() && !inCombat(provider.myPlayer());
            }
        }
        return false;
    }

    @Override
    public void execute() throws InterruptedException {
        if (monster == null) {
            provider.log("Requesting target");
            sendBroadcast(new Broadcast("request-target"));
            return;
        } else if (monster.isOnScreen() && monster.isVisible()) {
            provider.log("Attacking " + RuntimeVariables.currentMonster.getName());
            attackMonster(monster);
            provider.log("Moving mouse off screen.");
            provider.getMouse().moveOutsideScreen();
            new ConditionalSleep(4500, 500) {

                @Override
                public boolean condition() throws InterruptedException {
                    return provider.getCombat().isFighting() || provider.myPlayer().isUnderAttack();
                }
            }.sleep();
        } else {
            if (!provider.getMap().isWithinRange(monster, 4)) {
                walkToMonster(monster);
            } else {
                provider.getCamera().toEntity(monster);
            }
        }
    }


    private void walkToMonster(NPC monster) {
        WalkingEvent walkingEvent = new WalkingEvent(monster);
        walkingEvent.setOperateCamera(true);
        walkingEvent.setMinDistanceThreshold(3);
        walkingEvent.setEnergyThreshold(20);
        walkingEvent.setMiniMapDistanceThreshold(5);
        walkingEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return monster.isOnScreen() && monster.isVisible() || inCombat(monster);
            }
        });
        provider.execute(walkingEvent);
    }

    private void attackMonster(NPC monster) {
        InteractionEvent attackMonster = new InteractionEvent(monster, "Attack");
        attackMonster.setMaximumAttempts(5);
        attackMonster.setWalkTo(false);
        attackMonster.setOperateCamera(true);
        provider.execute(attackMonster);
    }

    private boolean inCombat(Character character) {
        if (character != null && character.exists()) {
            return !character.isAttackable() || character.isUnderAttack() || character
                    .getInteracting() != null;
        }
        return false;
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        if (broadcast.getKey().equalsIgnoreCase("new-target")) {
            monster = (NPC) broadcast.getMessage();
        }
    }
}
