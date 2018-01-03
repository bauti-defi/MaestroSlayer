package org.osbot.maestro.script.slayer.task;

import org.osbot.maestro.script.slayer.utils.requireditem.RequiredItem;
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
        for (RequiredItem item : monster.getRequiredItems()) {
            if (!item.hasItem(provider)) {
                return false;
            }
        }
        return true;
    }
}
