package org.osbot.maestro.framework;

import org.osbot.rs07.script.Script;
import org.osbot.rs07.utility.ConditionalSleep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public abstract class NodeScript extends Script implements BroadcastReceiver {

    private final List<NodeTask> tasks;
    private final List<BroadcastReceiver> receivers;
    private volatile boolean run;

    public NodeScript() {
        this.tasks = new ArrayList<>();
        this.receivers = new ArrayList<>();
        registerBroadcastReceiver(this);
    }

    @Override
    public void onStart() throws InterruptedException {
        sortTasks();
        run = true;
    }

    protected void addTask(NodeTask task) {
        task.inject(this);
        tasks.add(task);
    }

    protected void registerBroadcastReceiver(BroadcastReceiver receiver) {
        receivers.add(receiver);
    }

    protected void sendBroadcast(Broadcast broadcast) {
        for (BroadcastReceiver receiver : receivers) {
            receiver.receivedBroadcast(broadcast);
        }
    }

    protected final void forceStopScript(boolean logout) {
        if (run) {
            run = false;
        }
        if (logout && getClient().isLoggedIn()) {
            getLogoutTab().logOut();
            new ConditionalSleep(5000, 1000) {

                @Override
                public boolean condition() throws InterruptedException {
                    return !getClient().isLoggedIn();
                }
            }.sleep();
            forceStopScript(true);
        } else {
            stop();
        }
    }

    private void sortTasks() {
        Collections.sort(this.tasks, new Comparator<NodeTask>() {
            @Override
            public int compare(final NodeTask o1, final NodeTask o2) {
                if (o1.getPriority().priority() > o2.getPriority().priority()) {
                    return 1;
                } else if (o1.getPriority().priority() < o2.getPriority().priority()) {
                    return -1;
                }
                return 0;
            }
        });
    }


    @Override
    public int onLoop() throws InterruptedException {
        if (run) {
            for (NodeTask task : tasks) {
                if (task.runnable()) {
                    log("Executing: " + task.getClass().getName());
                    task.execute();
                }
            }
        }
        return random(400, 750);
    }

}
