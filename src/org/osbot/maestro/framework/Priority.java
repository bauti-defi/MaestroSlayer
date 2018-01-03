package org.osbot.maestro.framework;

public enum Priority {

    VERY_LOW(5), LOW(4), MEDIUM(3), HIGH(2), URGENT(1), CRITICAL(0);

    private final int priority;

    Priority(int priority) {
        this.priority = priority;
    }

    public int priority() {
        return priority;
    }
}
