package org.osbot.maestro.script.slayer.task;

import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerWornItem;
import org.osbot.rs07.script.MethodProvider;

public class SlayerTask {

    private final Monster monster;
    private final int assignedAmount;
    private final int amountLeft;
    private boolean finished;

    public SlayerTask(Monster monster, int assignedAmount) {
        this.monster = monster;
        this.assignedAmount = assignedAmount;
        this.amountLeft = assignedAmount;
    }

    public Monster getMonster() {
        return monster;
    }

    public int getAmountLeft() {
        return amountLeft;
    }

    public int getAssignedAmount() {
        return assignedAmount;
    }

    public boolean isFinished() {
        return getAmountLeft() <= 0;
    }

    public boolean hasRequiredItems(MethodProvider provider) {
        return hasRequiredInventoryItems(provider) && hasRequiredWornItems(provider);
    }

    public boolean hasRequiredWornItems(MethodProvider provider) {
        for (SlayerWornItem wornItem : monster.getSlayerWornItems()) {
            if (!wornItem.hasItem(provider)) {
                provider.log("Missing item: " + wornItem.getName());
                return false;
            }
        }
        return true;
    }

    public boolean hasRequiredInventoryItems(MethodProvider provider) {
        for (SlayerInventoryItem item : monster.getSlayerInventoryItems()) {
            if (!item.hasItem(provider)) {
                provider.log("Missing item: " + item.getName());
                return false;
            }
        }
        return true;
    }

}
