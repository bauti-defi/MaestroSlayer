package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.consumable.Consumable;
import org.osbot.maestro.script.slayer.utils.events.EntityInteractionEvent;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItem;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BankHandler extends NodeTask implements BroadcastReceiver {

    private final List<Consumable> consumablesToRestock;
    private final List<Consumable> consumablesToRestockNow;

    public BankHandler() {
        super(Priority.URGENT);
        this.consumablesToRestockNow = new ArrayList<>();
        this.consumablesToRestock = new ArrayList<>();
        registerBroadcastReceiver(this::receivedBroadcast);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null) {
            return !RuntimeVariables.currentTask.haveAllRequiredItems(provider) || RuntimeVariables.currentTask.isFinished() ||
                    !consumablesToRestockNow.isEmpty();
        }
        return provider.getBank().isOpen() || !provider.getInventory().contains("Enchanted gem") || !provider.getInventory().contains(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains("Teleport");
            }
        });
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
        } else if (!provider.getInventory().contains("Enchanted gemn")) {
            provider.log("Withdrawing enchanted gem...");
            if (provider.getBank().withdraw("Enchanted gem", 1)) {
                new ConditionalSleep(3000, 500) {

                    @Override
                    public boolean condition() throws InterruptedException {
                        return provider.getInventory().contains("Enchanted gem");
                    }
                }.sleep();
            } else {
                outOfItem("Enchanted gem");
            }
        } else if (!RuntimeVariables.currentTask.haveAllRequiredItems(provider)) {
            for (SlayerItem item : RuntimeVariables.currentTask.getAllSlayerItems()) {
                if (!item.haveItem(provider)) {
                    provider.log("Withdrawing " + item.getAmount() + " " + item.getName());
                    if (!item.withdrawFromBank(provider)) {
                        outOfItem(item.getName());
                        return;
                    }
                }
            }
        } else if (!consumablesToRestockNow.isEmpty()) {
            for (Consumable consumable : consumablesToRestockNow) {
                if (!consumable.withdrawFromBank(provider)) {
                    outOfItem(consumable.getName());
                    return;
                }
            }
            consumablesToRestockNow.clear();
        } else if (!consumablesToRestock.isEmpty()) {
            for (Consumable consumable : consumablesToRestock) {
                if (!consumable.withdrawFromBank(provider)) {
                    outOfItem(consumable.getName());
                    return;
                }
            }
            consumablesToRestock.clear();
        } else {
            provider.log("Closing bank...");
            provider.getBank().close();
        }
    }

    private void outOfItem(String name) {
        provider.log(name + " not found in bank, stopping...");
        provider.getBank().close();
        stopScript(true);
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
                return rs2Object.getName().contains("Bank") && provider.getMap().canReach(rs2Object) && provider.getMap().isWithinRange
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
            case "bank-for-potions":
            case "bank-for-food":
                Consumable consumable = (Consumable) broadcast.getMessage();
                if (consumable.isRequired()) {
                    if (!consumablesToRestockNow.contains(consumable)) {
                        consumablesToRestockNow.add(consumable);
                        organize(consumablesToRestockNow);
                    }
                    break;
                } else if (!consumablesToRestock.contains(consumable)) {
                    consumablesToRestock.add(consumable);
                    organize(consumablesToRestock);
                }
                break;
        }
    }

    private void organize(List<Consumable> consumables) {
        consumables.sort(new Comparator<Consumable>() {
            @Override
            public int compare(Consumable o1, Consumable o2) {
                return o2.getAmount() - o1.getAmount();
            }
        });
    }

}
