package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;

public class TaskGetter extends NodeTask {

    public TaskGetter(Priority priority) {
        super(priority);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {

    }
}
