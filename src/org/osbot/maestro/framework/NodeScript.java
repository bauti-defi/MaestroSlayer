package org.osbot.maestro.framework;

import org.osbot.rs07.script.Script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public abstract class NodeScript extends Script {

    private final List<NodeTask> tasks;
    private final List<BroadcastReceiver> receivers;

    public NodeScript() {
        this.tasks = new ArrayList<>();
        this.receivers = new ArrayList<>();
    }

    @Override
    public void onStart() throws InterruptedException {
        sortTasks();
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
        for (NodeTask task : tasks) {
            if (task.runnable()) {
                log("Executing: " + task.getClass().getName());
                task.execute();
            }
        }
        return random(400, 750);
    }

}
