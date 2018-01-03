package org.osbot.maestro.framework;

import org.osbot.rs07.script.MethodProvider;

public abstract class NodeTask extends Node {

    private NodeScript script;
    protected MethodProvider provider;
    private BroadcastReceiver receiver;

    protected NodeTask(Priority priority) {
        super(priority);
    }

    protected void inject(NodeScript script) {
        this.script = script;
        this.provider = script;
        if (receiver != null) {
            script.registerBroadcastReceiver(receiver);
        }
    }

    public abstract boolean runnable() throws InterruptedException;

    protected abstract void execute() throws InterruptedException;

    protected void stopScript(boolean logout) {
        script.forceStopScript(logout);
    }

    protected final void registerBroadcastReceiver(BroadcastReceiver receiver) {
        this.receiver = receiver;
    }

    protected final void sendBroadcast(Broadcast broadcast) {
        script.sendBroadcast(broadcast);
    }

}
