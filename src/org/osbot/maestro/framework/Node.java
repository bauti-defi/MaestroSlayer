package org.osbot.maestro.framework;

public abstract class Node {

    private final Priority priority;

    public Node(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }
}
