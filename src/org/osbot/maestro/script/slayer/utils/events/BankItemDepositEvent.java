package org.osbot.maestro.script.slayer.utils.events;

import org.osbot.maestro.script.slayer.utils.banking.DepositRequest;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.utility.ConditionalSleep;

public class BankItemDepositEvent extends Event {

    private final DepositRequest request;

    public BankItemDepositEvent(DepositRequest request) {
        this.request = request;
    }

    @Override
    public int execute() throws InterruptedException {
        if (!getBank().isOpen()) {
            log("Failed to withdraw: bank closed");
            setFailed();
            return -1;
        }
        log("Depositing " + request.getAmount() + " " + request.getItem().getName());
        long invyCount = getItemCount();
        getBank().deposit(request.getItem().getName(), request.getAmount());
        new ConditionalSleep(2800, 700) {

            @Override
            public boolean condition() throws InterruptedException {
                return getItemCount() != invyCount;
            }
        }.sleep();
        setFinished();
        return random(250, 350);
    }

    private long getItemCount() {
        return request.getItem().getInventoryCount(this);
    }
}
