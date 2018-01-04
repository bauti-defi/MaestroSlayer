package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTimeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.data.Constants;
import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.utils.CannonPlacementException;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CannonHandler extends NodeTimeTask implements BroadcastReceiver {

    private boolean needRepair, cannonSet, needLoad, needPickUp;

    public CannonHandler() {
        super(15, 3, TimeUnit.SECONDS, Priority.MEDIUM);
        registerBroadcastReceiver(this);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (SlayerVariables.cannonPosition == null) {
            provider.log("Cannon tile not set. Stopping...");
            stopScript(true);
        }
        if (isCannonSet()) {
            return super.runnable() || needLoad || needRepair || needPickUp;
        }
        return !isCannonSet();
    }

    @Override
    protected void execute() throws InterruptedException {
        if (!isCannonSet()) {
            if (provider.myPlayer().getPosition().distance(SlayerVariables.cannonPosition) > 0) {
                provider.log("Walking to cannon position");
                provider.walking.walk(SlayerVariables.cannonPosition);
                new ConditionalSleep(5000, 500) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return SlayerVariables.cannonPosition.equals(provider.myPosition());
                    }
                }.sleep();
            }
            Item cannonBase = provider.getInventory().getItem("Cannon base");
            if (cannonBase != null) {
                provider.log("Setting up cannon");
                cannonBase.interact("Set-up");
                new ConditionalSleep(10000, 1000) {

                    @Override
                    public boolean condition() throws InterruptedException {
                        return isCannonSet();
                    }
                }.sleep();
            }
            if (getCannon() != null && !holdingCannon()) {
                needLoad = true;
                cannonSet = true;
            }
        }
        if (isCannonSet()) {
            if (needRepair) {
                repairCannon();
            } else if (needLoad) {
                reloadCannon();
                super.execute();
            } else if (needPickUp) {
                pickUpCannon();
            }
        }
    }

    private boolean interactWithCannon(String action, boolean condition) {
        RS2Object cannon = getCannon();
        if (cannon != null && cannon.exists()) {
            if (cannon.hasAction(action)) {
                cannon.interact(action);
                new ConditionalSleep(5000, 500) {

                    @Override
                    public boolean condition() throws InterruptedException {
                        return condition;
                    }
                }.sleep();
                return true;
            }
        }
        return false;
    }

    private void repairCannon() {
        provider.log("Repairing cannon");
        if (interactWithCannon("Repair", !needRepair)) {
            provider.log("Cannon repaired");
            needRepair = false;
            return;
        }
        needRepair = true;
    }

    private void reloadCannon() {
        provider.log("Reloading cannon");
        if (provider.getInventory().contains("Cannonball")) {
            if (interactWithCannon("Fire", !needLoad)) {
                provider.log("Cannon reloaded");
            }
        } else {
            provider.log("Out of cannonballs");
        }
    }

    private void pickUpCannon() {
        provider.log("Picking up cannon");
        if (interactWithCannon("Pick-Up", !isCannonSet())) {
            provider.log("Cannon picked up");
        }
    }

    private void walkToCannonPosition() {
        WalkingEvent walkingEvent = new WalkingEvent(SlayerVariables.cannonPosition);
        walkingEvent.setMiniMapDistanceThreshold(4);
        walkingEvent.setEnergyThreshold(20);
        walkingEvent.setOperateCamera(true);
        walkingEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return provider.myPosition().distance(SlayerVariables.cannonPosition) == 0;
            }
        });
        provider.execute(walkingEvent);
    }

    private RS2Object getCannon() {
        return provider.getObjects().closest(new Filter<RS2Object>() {
            @Override
            public boolean match(RS2Object rs2Object) {
                return rs2Object != null && rs2Object.exists() && rs2Object.getId() == (needRepair ? Constants.BROKEN_CANNON : Constants.CANNON_ID) &&
                        rs2Object.getPosition().equals(provider.myPosition());
            }
        });
    }

    private boolean holdingCannon() {
        List<Item> cannonParts = provider.getInventory().filter(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains("Cannon") && !item.getName().equalsIgnoreCase("Cannonball");
            }
        });
        return cannonParts.size() == 4;
    }

    private boolean isCannonSet() {
        if (getCannon() != null) {
            provider.log("here");
        }
        return getCannon() != null && !holdingCannon() && !cannonSet;
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "cannon-loaded":
                needLoad = (boolean) broadcast.getMessage();
                if (!needLoad) {
                    resetTimer();
                }
                break;
            case "cannon-broken":
                needRepair = (boolean) broadcast.getMessage();
                break;
            case "cannon-error":
                try {
                    throw new CannonPlacementException(SlayerVariables.cannonPosition);
                } catch (CannonPlacementException e) {
                    e.printStackTrace();
                    stopScript(true);
                }
                break;
            case "cannon-set":
                cannonSet = (boolean) broadcast.getMessage();
                break;
            case "cannon-pick-up":
                needPickUp = (boolean) broadcast.getMessage();
                break;

        }
    }
}
