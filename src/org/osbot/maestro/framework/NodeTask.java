package org.osbot.maestro.framework;

import org.osbot.rs07.script.MethodProvider;

public abstract class NodeTask extends Node {

    private NodeScript script;
    protected MethodProvider provider;

    public NodeTask(Priority priority) {
        super(priority);
    }

    protected void inject(NodeScript script) {
        this.script = script;
        this.provider = script;
    }

    public abstract Response runnable() throws InterruptedException;

    protected abstract void execute() throws InterruptedException;

    protected void stopScript(boolean logout) {
        script.forceStopScript(logout);
    }

    protected final void sendBroadcast(Broadcast broadcast) {
        script.sendBroadcast(broadcast);
    }

}
