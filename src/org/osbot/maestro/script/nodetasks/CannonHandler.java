package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.data.SlayerVariables;

public class CannonHandler extends NodeTask {

    public CannonHandler() {
        super(Priority.MEDIUM);
    }

    @Override
    public boolean runnable() {
        if (SlayerVariables.cannon) {
            if (SlayerVariables.cannonPosition == null) {
                provider.log("Cannon tile not set. Stopping...");
                stopScript(true);
            }
            return false;
        }
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {

    }

}
