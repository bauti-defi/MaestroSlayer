package org.osbot.maestro.script.slayer.utils.events;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.event.Event;

public class BankItemWithdrawEvent extends Event {

    private final String name;
    private final int amount;
    private boolean needExactAmount;
    private final boolean stackable;
    private Filter<Item> filter;

    public BankItemWithdrawEvent(String name, int amount, boolean stackable) {
        this.name = name;
        this.amount = amount;
        this.stackable = stackable;
        setBlocking();
    }

    public BankItemWithdrawEvent(String name, Filter<Item> filter, int amount, boolean stackable) {
        this(name, amount, stackable);
        this.filter = filter;
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
        if (withdraw()) {
            setFinished();
        } else {
            log("Failed to withdraw: " + name);
            setFailed();
        }
        return random(250, 500);
    }

    private boolean withdraw() {
        if (amount >= 28) {
            if (needExactAmount && stackable) {
                if (filter != null) {
                    return getBank().withdraw(filter, amount);
                }
                return getBank().withdraw(name, amount);
            }
            if (filter != null) {
                return getBank().withdrawAll(filter);
            }
            return getBank().withdrawAll(name);
        } else if (filter != null) {
            return getBank().withdraw(filter, amount);
        }
        return getBank().withdraw(name, amount);
    }
}