package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.Combat;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.maestro.script.slayer.utils.banking.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.events.EntityInteractionEvent;
import org.osbot.maestro.script.slayer.utils.events.OnFinishAction;
import org.osbot.maestro.script.slayer.utils.slayeritem.taskitem.InventoryTaskItem;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public class CombatHandler extends NodeTask implements BroadcastReceiver {

    private NPC monster;

    public CombatHandler() {
        super(Priority.VERY_LOW);
    }

    @Override
    public Response runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null) {
            if (!RuntimeVariables.currentTask.haveAllRequiredItems(provider)) {
                for (InventoryTaskItem inventoryItem : RuntimeVariables.currentTask.getAllInventoryItems()) {
                    if (!inventoryItem.hasInInventory(provider)) {
                        sendBroadcast(new Broadcast("bank-request", new WithdrawRequest(inventoryItem, true)));
                        continue;
                    }
                }
                return Response.RESTART_CYCLE;
            } else if (RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition())) {
                if (monster != null && monster.exists() && !Combat.inCombat(provider.myPlayer()) && !Combat.inCombat(monster) && !Combat
                        .isTryingToAttack(provider.myPlayer(), monster)) {
                    return Response.EXECUTE;
                }
            }
        }
        return Response.CONTINUE;
    }

    @Override
    public void execute() throws InterruptedException {
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
        } else if (monster == null || !monster.exists()) {
            return;
        } else if (!provider.getCombat().isAutoRetaliateOn()) {
            provider.log("Turning auto retaliate on...");
            provider.getCombat().toggleAutoRetaliate(true);
            new ConditionalSleep(2500, 500) {

                @Override
                public boolean condition() throws InterruptedException {
                    return provider.getCombat().isAutoRetaliateOn();
                }
            }.sleep();
        }
        provider.log("Attacking: " + monster.getName());
        EntityInteractionEvent attackMonster = new EntityInteractionEvent(monster, "Attack");
        attackMonster.setWalkTo(true);
        attackMonster.setMinDistanceThreshold(5);
        attackMonster.setEnergyThreshold(10, 30);
        attackMonster.setBreakCondition(new ConditionalSleep(5000, 1000) {
            @Override
            public boolean condition() throws InterruptedException {
                return Combat.inCombat(monster) || Combat.inCombat(provider.myPlayer());
            }
        });
        attackMonster.setOnFinishAction(new OnFinishAction() {
            @Override
            public void execute(MethodProvider provider) {
                provider.log("Moving mouse off screen");
                provider.getMouse().moveOutsideScreen();
            }
        });
        provider.execute(attackMonster);
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        if (broadcast.getKey().equalsIgnoreCase("new-target")) {
            monster = (NPC) broadcast.getMessage();
        }
    }
}
