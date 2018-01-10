package org.osbot.maestro.script.slayer.utils.events;

import org.osbot.rs07.api.model.Entity;
import org.osbot.rs07.event.Event;

public class CameraMovementEvent extends Event {

    private final Entity entity;

    public CameraMovementEvent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public int execute() throws InterruptedException {
        if (entity == null) {
            log("Entity null");
            setFailed();
            return -1;
        } else if (!entity.isVisible()) {
            getCamera().toEntity(entity);
            setFinished();
        }
        return random(250, 500);
    }

}
