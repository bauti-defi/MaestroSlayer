package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.data.Constants;
import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.event.InteractionEvent;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.utility.ConditionalSleep;

public class CombatHandler extends NodeTask implements BroadcastReceiver {

    private NPC monster;

    public CombatHandler() {
        super(Priority.VERY_LOW);
        registerBroadcastReceiver(this);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (!SlayerVariables.currentTask.hasRequiredItems(provider)) {
            provider.log("Lacking required items. Stopping...");
            stopScript(true);
            return false;
        }
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
        if (provider.getConfigs().get(Constants.COMBAT_STYLE_ID) != SlayerVariables.combatStyle.getConfigId()) {
            provider.log("Switching back to original combat style.");
            if (provider.getTabs().open(Tab.ATTACK)) {
                RS2Widget combatStyleWidget = provider.getWidgets().get(CombatStyle.ROOT_ID, SlayerVariables.combatStyle.getChildId());
                if (combatStyleWidget != null && combatStyleWidget.isVisible()) {
                    combatStyleWidget.interact(combatStyleWidget.getInteractActions()[0]);
                    new ConditionalSleep(2500, 500) {

                        @Override
                        public boolean condition() throws InterruptedException {
                            return provider.getConfigs().get(Constants.COMBAT_STYLE_ID) == SlayerVariables.combatStyle.getConfigId();
                        }
                    }.sleep();
                }
            }
        }
        return monster != null && monster.exists() && !playerInCombat();
    }

    @Override
    public void execute() throws InterruptedException {
        if (monster == null) {
            provider.log("Requesting target");
            sendBroadcast(new Broadcast("request-target"));
            return;
        }
        if (monster.isOnScreen() && monster.isVisible()) {
            provider.log("Attacking " + SlayerVariables.currentTask.getMonster().getName());
            attackMonster(monster);
            new ConditionalSleep(4500, 500) {

                @Override
                public boolean condition() throws InterruptedException {
                    return provider.getCombat().isFighting() || provider.myPlayer().isUnderAttack();
                }
            }.sleep();
            provider.log("Moving mouse off screen.");
            provider.getMouse().moveOutsideScreen();
        } else {
            if (!provider.getMap().isWithinRange(monster, 7)) {
                walkToMonster(monster);
            }
        }
    }


    private void walkToMonster(NPC monster) {
        WalkingEvent walkingEvent = new WalkingEvent(monster);
        walkingEvent.setOperateCamera(true);
        walkingEvent.setMinDistanceThreshold(5);
        walkingEvent.setEnergyThreshold(20);
        walkingEvent.setMiniMapDistanceThreshold(9);
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
            return !character.isAttackable() || character.isUnderAttack() || character.isHitBarVisible();
        }
        return false;
    }

    private boolean playerInCombat() {
        return inCombat(provider.myPlayer()) || monster != null && provider.myPlayer().isInteracting(monster);
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        if (broadcast.getKey().equalsIgnoreCase("new-target")) {
            monster = (NPC) broadcast.getMessage();
        }
    }
}
