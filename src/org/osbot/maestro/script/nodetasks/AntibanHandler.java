package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;

public class AntibanHandler extends NodeTask {

    protected AntibanHandler() {
        super(Priority.VERY_LOW);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {
        //move mouse off screen
        //hover food
        //hover potion

        //Do through broadcasts? send request to hover after next pot
    }
}
