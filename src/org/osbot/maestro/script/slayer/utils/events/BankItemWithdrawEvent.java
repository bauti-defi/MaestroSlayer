package org.osbot.maestro.script.slayer.utils.events;

import org.osbot.maestro.script.slayer.utils.WithdrawRequest;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.utility.ConditionalSleep;

public class BankItemWithdrawEvent extends Event {

    private final String name;
    private final int amount;
    private final boolean stackable;
    private boolean needExactAmount;
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

    public BankItemWithdrawEvent(WithdrawRequest request) {
        this(request.getName(), request.getFilter(), request.getAmount(), request.isStackable());
        setNeedExactAmount(request.isNeedExactAmount());
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
        long currentCount = getItemCount();
        if (withdraw()) {
            log("Withdrawing " + amount + " " + name);
            new ConditionalSleep(3000, 500) {

                @Override
                public boolean condition() throws InterruptedException {
                    return currentCount != getItemCount();
                }
            }.sleep();
            setFinished();
        } else {
            log("Failed to withdraw: " + name);
            setFailed();
        }
        return random(250, 500);
    }

    private long getItemCount() {
        if (filter != null) {
            return getInventory().getAmount(filter);
        }
        return getInventory().getAmount(name);
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