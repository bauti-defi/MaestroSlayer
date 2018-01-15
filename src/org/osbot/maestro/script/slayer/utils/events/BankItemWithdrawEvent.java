package org.osbot.maestro.script.slayer.utils.events;

import org.osbot.maestro.script.slayer.utils.banking.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.slayeritem.Mutadable;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.utility.ConditionalSleep;

public class BankItemWithdrawEvent extends Event {

    private final WithdrawRequest request;

    public BankItemWithdrawEvent(WithdrawRequest request) {
        this.request = request;
    }

    @Override
    public int execute() throws InterruptedException {
        if (!getBank().isOpen()) {
            log("Failed to withdraw: bank closed");
            setFailed();
            return -1;
        } else if (getInventory().getEmptySlotCount() < request.getInventorySpaceRequired()) {
            log("Not enought inventory space for: " + request.getItem().getName());
            setFailed();
            return -1;
        } else if (!request.getItem().hasInBank(this)) {
            log("Out of: " + request.getItem().getName());
            setFailed();
            return -1;
        }
        long currentCount = getItemCount();
        if (withdraw()) {
            log("Withdrawing " + request.getAmount() + " " + request.getItem().getName());
            new ConditionalSleep(3000, 500) {

                @Override
                public boolean condition() throws InterruptedException {
                    return currentCount != getItemCount();
                }
            }.sleep();
            setFinished();
        } else {
            log("Failed to withdraw: " + request.getItem().getName());
            setFailed();
        }
        return random(250, 500);
    }

    private long getItemCount() {
        return request.getItem().getInventoryCount(this);
    }

    private boolean withdraw() {
        if (!request.isExactAmount()) {
            if (request.getAmount() >= 28) {
                if (request.getItem() instanceof Mutadable) {
                    return getBank().withdrawAll(((Mutadable) request.getItem()).getMutationFilter());
                }
                return getBank().withdrawAll(request.getItem().getName());
            }
        }
        if (request.getItem() instanceof Mutadable) {
            return getBank().withdraw(((Mutadable) request.getItem()).getMutationFilter(), request.getAmount());
        }
        return getBank().withdraw(request.getItem().getName(), request.getAmount());
    }


}