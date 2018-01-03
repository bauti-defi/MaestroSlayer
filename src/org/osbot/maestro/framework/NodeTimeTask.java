package org.osbot.maestro.framework;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public abstract class NodeTimeTask extends NodeTask {

    private final long refreshRate;
    private long startTime;
    private long deviation;
    private long currentDeviation;
    private final Random randomGenerator;

    public NodeTimeTask(long refreshRate, long deviation, TimeUnit unit) {
        super(Priority.VERY_LOW);
        this.refreshRate = unit.toMillis(refreshRate);
        this.deviation = deviation;
        this.randomGenerator = new Random();
        resetTimer();
    }

    private void resetTimer() {
        this.startTime = System.currentTimeMillis();
        boolean positve = randomGenerator.nextBoolean();
        this.currentDeviation = randomGenerator.nextInt((int) deviation) * (positve ? 1 : -1);
    }

    public NodeTimeTask(long refreshRate, TimeUnit unit) {
        this(refreshRate, 0, unit);
    }

    public NodeTimeTask(long refreshRate, TimeUnit refreshUnit, long deviation, TimeUnit deviationUnit) {
        this(refreshUnit.toMillis(refreshRate), deviationUnit.toMillis(deviation), TimeUnit.MILLISECONDS);
    }

    protected final long getNextRefresh() {
        return (refreshRate + currentDeviation);

    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (currentDeviation != 0) {
            return (refreshRate + startTime + currentDeviation) - System.currentTimeMillis() >= 0 ? false : true;
        }
        return (refreshRate + startTime) - System.currentTimeMillis() >= 0 ? false : true;
    }

    @Override
    protected void execute() throws InterruptedException {
        resetTimer();
    }

}
