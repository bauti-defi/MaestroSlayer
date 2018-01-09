package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTimeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
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

    private static final int CANNON_ID = 6;
    private static final int BROKEN_CANNON_ID = 14916;
    private static final int CANNON_PARENT_CONFIG_ID = 1;
    private static final int FIRING_CANNON_CONFIG_CHILD = 1048576;
    private boolean needRepair, cannonSet, needLoad, needPickUp;

    public CannonHandler() {
        super(15, 3, TimeUnit.SECONDS, Priority.MEDIUM);
        registerBroadcastReceiver(this);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (RuntimeVariables.currentMonster.canCannon()) {
            if (isCannonSet()) {
                return super.runnable() || needReload() || needRepair || needPickUp;
            }
            return !isCannonSet();
        }
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (!isCannonSet()) {
            if (!RuntimeVariables.currentMonster.getCannonPosition().equals(provider.myPosition())) {
                provider.log("Walking to cannon position");
                walkToCannonPosition();
            }
            Item cannonBase = provider.getInventory().getItem("Cannon base");
            if (cannonBase != null) {
                provider.log("Setting up cannon");
                cannonBase.interact("Set-up");
                new ConditionalSleep(10000, 600) {

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
                repairCannon();
            } else if (needReload()) {
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

    private boolean needReload() {
        return needLoad || !isCannonFiring();
    }

    private boolean isCannonFiring() {
        return provider.getConfigs().get(CANNON_PARENT_CONFIG_ID) == FIRING_CANNON_CONFIG_CHILD;
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
            if (interactWithCannon("Fire", !needReload())) {
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
        WalkingEvent walkingEvent = new WalkingEvent(RuntimeVariables.currentMonster.getCannonPosition().unwrap());
        walkingEvent.setMiniMapDistanceThreshold(4);
        walkingEvent.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return RuntimeVariables.currentMonster.getCannonPosition().equals(provider.myPosition());
            }
        });
        provider.execute(walkingEvent);
    }

    private RS2Object getCannon() {
        return provider.getObjects().closest(new Filter<RS2Object>() {
            @Override
            public boolean match(RS2Object rs2Object) {
                return rs2Object != null && rs2Object.exists() && rs2Object.getId() == (needRepair ? BROKEN_CANNON_ID : CANNON_ID) &&
                        RuntimeVariables.currentMonster.getCannonPosition().equals(rs2Object.getPosition());
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
        return getCannon() != null && !holdingCannon() && cannonSet;
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "cannon-loaded":
                needLoad = (boolean) broadcast.getMessage();
                if (!needReload()) {
                    resetTimer();
                }
                break;
            case "cannon-broken":
                needRepair = (boolean) broadcast.getMessage();
                break;
            case "cannon-error":
                try {
                    throw new CannonPlacementException(RuntimeVariables.currentMonster.getCannonPosition().unwrap());
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
