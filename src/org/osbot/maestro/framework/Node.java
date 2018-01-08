package org.osbot.maestro.framework;

public abstract class Node implements Comparable<Node> {

    private final Priority priority;

    public Node(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }


    @Override
    public int compareTo(Node o) {
        if (priority.priority() > o.getPriority().priority()) {
            return 1;
        } else if (priority.priority() < o.getPriority().priority()) {
            return -1;
        }
        return 0;
    }
}
