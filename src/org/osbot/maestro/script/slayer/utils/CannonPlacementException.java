package org.osbot.maestro.script.slayer.utils;


import org.osbot.rs07.api.map.Position;

public class CannonPlacementException extends Exception {

    public CannonPlacementException(Position position) {
        super(position.toString() + " is not a valid cannon position");
    }
}
