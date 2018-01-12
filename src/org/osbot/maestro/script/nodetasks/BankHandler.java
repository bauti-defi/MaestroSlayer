package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.events.BankItemWithdrawEvent;
import org.osbot.maestro.script.slayer.utils.events.EntityInteractionEvent;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BankHandler extends NodeTask implements BroadcastReceiver {

    private final List<WithdrawRequest> bankRequests;
    private boolean emergencyBank;

    public BankHandler() {
        super(Priority.URGENT);
        this.bankRequests = new ArrayList<>();
        registerBroadcastReceiver(this::receivedBroadcast);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null && (!RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition())
                || RuntimeVariables.currentTask.isFinished())) {
            return !bankRequests.isEmpty();
        }
        return provider.getBank().isOpen() || emergencyBank;
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
        } else if (!bankRequests.isEmpty()) {
            for (WithdrawRequest request : bankRequests) {
                BankItemWithdrawEvent withdrawEvent = new BankItemWithdrawEvent(request);
                if (provider.execute(withdrawEvent).hasFinished()) {
                    continue;
                }
            }
            bankRequests.clear();
            emergencyBank = false;
            sendBroadcast(new Broadcast("resupplied", true));
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
            case "bank-withdraw-request":
                WithdrawRequest request = (WithdrawRequest) broadcast.getMessage();
                if (!bankRequests.contains(request)) {
                    provider.log("Request registered.");
                    emergencyBank = request.isEmergency();
                    bankRequests.add(request);
                    organize(bankRequests);
                }
                sendBroadcast(new Broadcast("resupplied", false));
                break;
        }
    }

    private void organize(List<WithdrawRequest> bankRequests) {
        bankRequests.sort(new Comparator<WithdrawRequest>() {
            @Override
            public int compare(WithdrawRequest o1, WithdrawRequest o2) {
                return o2.getAmount() - o1.getAmount();
            }
        });
    }

}
