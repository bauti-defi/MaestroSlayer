package org.osbot.maestro.script.slayer.utils;

import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.Event;
import org.osbot.rs07.event.WalkingEvent;
import org.osbot.rs07.utility.ConditionalSleep;

public class NPCInteractionEvent extends Event {

    private final String action;
    private final NPC npc;
    private ConditionalSleep condition;
    private CameraMovementEvent cameraMovementEvent;
    private WalkingEvent walkingEvent;
    private boolean walkTo;
    private int minDistanceThreshold = 3;
    private int miniMapDistanceThreshold = 8;
    private int energyThreshold = 15;


    public NPCInteractionEvent(NPC npc, String action) {
        this.action = action;
        this.npc = npc;
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
        if (npc == null) {
            log("NPC null");
            setFailed();
            return -1;
        } else if (!npc.hasAction(action) || !npc.exists() || !getMap().canReach(npc)) {
            log("Invalid NPC");
            setFailed();
            return -1;
        } else if (walkTo && !getMap().isWithinRange(npc, minDistanceThreshold)) {
            if (walkingEvent == null) {
                walkingEvent = new WalkingEvent(npc);
                walkingEvent.setOperateCamera(false);
                walkingEvent.setEnergyThreshold(energyThreshold);
                walkingEvent.setMiniMapDistanceThreshold(miniMapDistanceThreshold);
                walkingEvent.setMinDistanceThreshold(minDistanceThreshold);
                walkingEvent.setAsync();
            } else if (!walkingEvent.isQueued() || !walkingEvent.isWorking()) {
                execute(walkingEvent);
            }
        }
        if (!npc.isOnScreen()) {
            if (cameraMovementEvent == null || cameraMovementEvent.hasFinished()) {
                cameraMovementEvent = new CameraMovementEvent(npc);
                cameraMovementEvent.setAsync();
            }
            if (!cameraMovementEvent.isWorking() && !cameraMovementEvent.isQueued()) {
                execute(cameraMovementEvent);
            }
        }
        if (npc.interact(action)) {
            if (hasBreakCondition()) {
                condition.sleep();
                setFinished();
                return -1;
            }
            setFinished();
        }
        return random(400, 650);
    }

}
