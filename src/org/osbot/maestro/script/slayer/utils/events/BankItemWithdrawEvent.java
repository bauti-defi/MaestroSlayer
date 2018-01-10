package org.osbot.maestro.script.slayer.utils.events;

import org.osbot.rs07.event.Event;

public class BankItemWithdrawEvent extends Event {

    private final String name;
    private final int amount;
    private boolean needExactAmount;
    private final boolean stackable;

    public BankItemWithdrawEvent(String name, int amount, boolean stackable) {
        this.name = name;
        this.amount = amount;
        this.stackable = stackable;
        setBlocking();
    }

    public void setNeedExactAmount(boolean needExactAmount) {
        this.needExactAmount = needExactAmount;
    }

    @Override
    public int execute() throws InterruptedException {
        if (!getBank().isOpen()) {
            log("Failed to withdraw: bank closed");
            setFailed();
            return -1;
        }
        if (needExactAmount) {
            if (!stackable) {
                if (getInventory().getEmptySlotCount() < amount) {
                    log("Not enought inventory space for: " + name);
                    setFailed();
                    return -1;
                }
            } else if (getInventory().getEmptySlotCount() == 0) {
                log("Not enought inventory space for: " + name);
                setFailed();
                return -1;
            }
        }
        if (getBank().withdraw(name, amount >= 28 ? random(28, 99) : amount)) {
            setFinished();
        } else {
            log("Failed to withdraw: " + name);
            setFailed();
        }
        return random(250, 500);
    }
}