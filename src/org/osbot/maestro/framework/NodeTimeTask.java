package org.osbot.maestro.framework;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class NodeTimeTask extends NodeTask {

    private final long refreshRate;
    private long startTime;
    private long deviation;
    private long currentDeviation;
    private final Random randomGenerator;

    public NodeTimeTask(long refreshRate, long deviation, TimeUnit unit, Priority priority) {
        super(priority);
        this.refreshRate = unit.toMillis(refreshRate);
        this.deviation = deviation;
        this.randomGenerator = new Random();
        resetTimer();
    }

    public NodeTimeTask(long refreshRate, TimeUnit unit, Priority priority) {
        this(refreshRate, 0, unit, priority);
    }

    public NodeTimeTask(long refreshRate, TimeUnit refreshUnit, long deviation, TimeUnit deviationUnit, Priority priority) {
        this(refreshUnit.toMillis(refreshRate), deviationUnit.toMillis(deviation), TimeUnit.MILLISECONDS, priority);
    }

    protected void resetTimer() {
        this.startTime = System.currentTimeMillis();
        boolean positve = randomGenerator.nextBoolean();
        this.currentDeviation = randomGenerator.nextInt((int) deviation) * (positve ? 1 : -1);
    }

    protected final long getNextRefresh() {
        return (refreshRate + currentDeviation);

    }

    @Override
    public Response runnable() throws InterruptedException {
        if (currentDeviation != 0) {
            return (refreshRate + startTime + currentDeviation) - System.currentTimeMillis() >= 0 ? Response.CONTINUE : Response.EXECUTE;
        }
        return (refreshRate + startTime) - System.currentTimeMillis() >= 0 ? Response.CONTINUE : Response.EXECUTE;
    }

    @Override
    protected void execute() throws InterruptedException {
        resetTimer();
    }

}
