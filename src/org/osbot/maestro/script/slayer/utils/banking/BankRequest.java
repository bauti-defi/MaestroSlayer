package org.osbot.maestro.script.slayer.utils.banking;

import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerItem;

public abstract class BankRequest {

    private final SlayerItem item;
    private int amount;
    private final boolean exactAmount;

    public BankRequest(SlayerItem item, boolean exactAmount) {
        this.item = item;
        this.amount = item.getAmount();
        this.exactAmount = exactAmount;
    }

    public boolean isRequired() {
        return item.isRequired();
    }

    public SlayerItem getItem() {
        return item;
    }

    public int getInventorySpaceRequired() {
        if (item.isStackable()) {
            return 1;
        }
        return item.getAmount();
    }

    public int getAmount() {
        return amount;
    }

    public void overrideAmount(int amount) {
        this.amount = amount;
    }

    public boolean isExactAmount() {
        return exactAmount;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BankRequest) {
            BankRequest bankRequest = (BankRequest) obj;
            return bankRequest.getItem().equals(item);
        }
        return false;
    }
}
