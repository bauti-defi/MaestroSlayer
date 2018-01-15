package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.banking.BankRequest;
import org.osbot.maestro.script.slayer.utils.banking.BankRequestManager;
import org.osbot.maestro.script.slayer.utils.banking.DepositRequest;
import org.osbot.maestro.script.slayer.utils.banking.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.events.BankItemDepositEvent;
import org.osbot.maestro.script.slayer.utils.events.BankItemWithdrawEvent;
import org.osbot.maestro.script.slayer.utils.events.EntityInteractionEvent;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

public class BankHandler extends NodeTask implements BroadcastReceiver {

    private final BankRequestManager manager;


    public BankHandler() {
        super(Priority.URGENT);
        this.manager = new BankRequestManager();
        registerBroadcastReceiver(this::receivedBroadcast);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null && (!RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition())
                || RuntimeVariables.currentTask.isFinished())) {
            return manager.requestPending();
        }
        return provider.getBank().isOpen() || manager.requiredRequestPending();
    }

    @Override
    protected void execute() throws InterruptedException {
        if (!provider.getBank().isOpen()) {
            RS2Object bank = getClosestBank();
            if (bank == null) {
                walkToBank();
            } else {
                openBank(bank);
            }
        } else if (manager.requestPending()) {
            for (BankRequest request : manager.getOptimizedList()) {
                if (request instanceof WithdrawRequest) {
                    WithdrawRequest withdrawRequest = (WithdrawRequest) request;
                    BankItemWithdrawEvent withdrawEvent = new BankItemWithdrawEvent(withdrawRequest);
                    provider.execute(withdrawEvent);
                } else {
                    DepositRequest depositRequest = (DepositRequest) request;
                    BankItemDepositEvent depositEvent = new BankItemDepositEvent(depositRequest);
                    provider.execute(depositEvent);
                }
            }
        } else {
            provider.log("Closing bank...");
            provider.getBank().close();
        }
    }

    private void walkToBank() {
        provider.log("Walking to nearest bank...");
        WebWalkEvent walkToBank = new WebWalkEvent(Config.GAME_BANKS);
        walkToBank.setPathPreferenceProfile(getToBankPathPreference());
        walkToBank.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                RS2Object bank = getClosestBank();
                return bank != null && provider.getMap().canReach(bank);
            }
        });
        if (walkToBank.prefetchRequirements(provider)) {
            provider.execute(walkToBank);
        } else {
            provider.log("Can't find path to bank.");
            stopScript(true);
        }
    }

    private PathPreferenceProfile getToBankPathPreference() {
        PathPreferenceProfile profile = new PathPreferenceProfile();
        profile.setAllowGliders(true);
        profile.setAllowCharters(true);
        profile.setAllowObstacles(true);
        profile.setAllowTeleports(true);
        profile.checkEquipmentForItems(true);
        profile.checkInventoryForItems(true);
        return profile;
    }

    private RS2Object getClosestBank() {
        return provider.getObjects().closest(new Filter<RS2Object>() {
            @Override
            public boolean match(RS2Object rs2Object) {
                return (rs2Object.getName().equalsIgnoreCase("Bank booth") || rs2Object.getName().equalsIgnoreCase("Bank chest")) && provider
                        .getMap().canReach(rs2Object) && provider.getMap().isWithinRange
                        (rs2Object, 10);
            }
        });
    }

    private void openBank(RS2Object bank) {
        if (bank != null) {
            provider.log("Opening bank...");
            EntityInteractionEvent bankInteraction = new EntityInteractionEvent(bank, bank.getName().contains("chest") ? "Use" : "Bank");
            bankInteraction.setWalkTo(true);
            bankInteraction.setEnergyThreshold(10, 30);
            bankInteraction.setBreakCondition(new ConditionalSleep(4000, 1000) {
                @Override
                public boolean condition() throws InterruptedException {
                    return provider.getBank().isOpen();
                }
            });
            provider.execute(bankInteraction);
        }
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "bank-request":
                BankRequest request = (BankRequest) broadcast.getMessage();
                manager.addRequest(request);
                break;
        }
    }

}
