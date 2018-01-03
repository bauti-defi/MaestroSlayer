package org.osbot.maestro.framework;

public class Broadcast {

    private final String key;
    private Object message;

    public Broadcast(String key, Object message) {
        this.key = key;
        this.message = message;
    }

    public Broadcast(String key) {
        this.key = key;
    }

    public Object getMessage() {
        return message;
    }

    public String getKey() {
        return key;
    }
}
