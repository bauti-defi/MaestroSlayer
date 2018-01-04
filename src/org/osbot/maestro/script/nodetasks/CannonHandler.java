package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTimeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.data.Constants;
import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.RS2Object;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CannonHandler extends NodeTimeTask implements BroadcastReceiver {

    private volatile boolean needLoad;
    private boolean needRepair;

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
            return super.runnable() || needLoad || needRepair;
        }
        return !isCannonSet();
    }

    @Override
    protected void execute() throws InterruptedException {
        if (!isCannonSet()) {
            if (provider.myPlayer().getPosition().distance(SlayerVariables.cannonPosition) > 0) {
                provider.log("Walking to cannon position");
                provider.getWalking().walk(SlayerVariables.cannonPosition);
                new ConditionalSleep(5000, 500) {
                    @Override
                    public boolean condition() throws InterruptedException {
                        return provider.myPlayer().getPosition().distance(SlayerVariables.cannonPosition) == 0;
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
            needLoad = true;
        }
        if (isCannonSet()) {
            if (needRepair) {
                provider.log("Repairing cannon");
                repairCannon();
            }
            provider.log("Reloading cannon");
            reloadCannon();
            super.execute();
        }
    }

    private void repairCannon() {
        RS2Object cannon = getCannon();
        if (cannon != null) {
            if (cannon.hasAction("Repair")) {
                cannon.interact("Repair");
                new ConditionalSleep(5000, 1000) {

                    @Override
                    public boolean condition() throws InterruptedException {
                        return !needRepair;
                    }
                }.sleep();
            } else {
                needRepair = false;
            }
        }
    }

    private void reloadCannon() {
        RS2Object cannon = getCannon();
        if (cannon != null) {
            if (provider.getInventory().contains("Cannonball")) {
                cannon.interact("Fire");
                new ConditionalSleep(5000, 1000) {

                    @Override
                    public boolean condition() throws InterruptedException {
                        return !needLoad;
                    }
                }.sleep();
            } else {
                provider.log("Out of cannonballs");
            }
        }
    }

    private void pickUpCannon() {
        RS2Object cannon = getCannon();
        if (cannon != null) {
            cannon.interact("Pick-up");
            new ConditionalSleep(5000, 1000) {

                @Override
                public boolean condition() throws InterruptedException {
                    return !isCannonSet();
                }
            }.sleep();
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
                return provider.myPlayer().getPosition().distance(SlayerVariables.cannonPosition) == 0;
            }
        });
        provider.execute(walkingEvent);
    }

    private RS2Object getCannon() {
        return provider.getObjects().closest(new Filter<RS2Object>() {
            @Override
            public boolean match(RS2Object rs2Object) {
                return rs2Object != null && rs2Object.exists() && rs2Object.getId() == Constants.CANNON_ID &&
                        rs2Object.getPosition().distance(SlayerVariables.cannonPosition) == 0;
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
        if (getCannon() == null) {
            provider.log("here");
        }
        if (getCannon() != null && !holdingCannon()) {
            provider.log("set");
            return true;
        }
        return false;
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "cannon-loaded":
                needLoad = false;
                break;
            case "cannon-broken":
                needRepair = true;
                break;
            case "cannon-reload":
                needLoad = true;
                break;
        }
    }
}
