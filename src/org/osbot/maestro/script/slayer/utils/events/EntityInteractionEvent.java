package org.osbot.maestro.script.slayer.utils.events;

import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.utility.ConditionalSleep;

public class EntityInteractionEvent extends Event {

    private final String action;
    private final Entity entity;
    private ConditionalSleep condition;
    private CameraMovementEvent cameraMovementEvent;
    private WalkingEvent walkingEvent;
    private boolean walkTo;
    private int minDistanceThreshold = 3;
    private int miniMapDistanceThreshold = 8;
    private int energyThreshold = 15;


    public EntityInteractionEvent(Entity entity, String action) {
        this.action = action;
        this.entity = entity;
        setBlocking();
    }

    public void setWalkTo(boolean walkTo) {
        this.walkTo = walkTo;
    }

    public void setMinDistanceThreshold(int minDistanceThreshold) {
        this.minDistanceThreshold = minDistanceThreshold;
    }

    public void setEnergyThreshold(int energyThreshold) {
        this.energyThreshold = energyThreshold;
    }

    public void setEnergyThreshold(int min, int max) {
        this.energyThreshold = random(min, max);
    }

    public void setMiniMapDistanceThreshold(int miniMapDistanceThreshold) {
        this.miniMapDistanceThreshold = miniMapDistanceThreshold;
    }

    public void setBreakCondition(ConditionalSleep condition) {
        this.condition = condition;
    }

    public boolean hasBreakCondition() {
        return condition != null;
    }

    @Override
    public int execute() throws InterruptedException {
        if (entity == null) {
            log("EntityInteractionEvent: Entity null");
            setFailed();
            return -1;
        } else if (!entity.exists() || !getMap().canReach(entity)) {
            log("EntityInteractionEvent: Invalid Entity");
            setFailed();
            return -1;
        } else if (!isRunning()) {

        } else if (!entity.isVisible()) {
            if (cameraMovementEvent == null || cameraMovementEvent.hasFinished() || cameraMovementEvent.hasFailed()) {
                cameraMovementEvent = new CameraMovementEvent(entity);
                cameraMovementEvent.setAsync();
            }
            if (!cameraMovementEvent.isWorking() && !cameraMovementEvent.isQueued()) {
                execute(cameraMovementEvent);
            }
        }
        if (walkTo && !getMap().isWithinRange(entity, minDistanceThreshold)) {
            if (walkingEvent == null || walkingEvent.hasFinished() || walkingEvent.hasFailed()) {
                walkingEvent = new WalkingEvent(entity);
                walkingEvent.setOperateCamera(false);
                walkingEvent.setEnergyThreshold(energyThreshold);
                walkingEvent.setMiniMapDistanceThreshold(miniMapDistanceThreshold);
                walkingEvent.setMinDistanceThreshold(minDistanceThreshold);
                walkingEvent.setAsync();
            }
            if (!walkingEvent.isQueued() && !walkingEvent.isWorking()) {
                execute(walkingEvent);
            }
        }
        if (entity.interact(action)) {
            if (hasBreakCondition()) {
                condition.sleep();
                setFinished();
                return -1;
            }
            setFinished();
        }
        return random(400, 650);
    }

    private boolean isRunning() {
        return getConfigs().get(173) == 1;
    }

}
