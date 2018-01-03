package org.osbot.maestro.script.slayer.utils;

import java.util.concurrent.TimeUnit;

public enum AntibanFrequency {

    LOW(3, TimeUnit.MINUTES, 90, TimeUnit.SECONDS);


    private final int rate;
    private final TimeUnit rateUnit;
    private final int deviation;
    private final TimeUnit deviationUnit;

    AntibanFrequency(int rate, TimeUnit rateUnit, int deviation, TimeUnit deviationUnit) {
        this.rate = rate;
        this.rateUnit = rateUnit;
        this.deviation = deviation;
        this.deviationUnit = deviationUnit;
    }


    public int getDeviation() {
        return deviation;
    }

    public int getRate() {
        return rate;
    }

    public TimeUnit getDeviationUnit() {
        return deviationUnit;
    }

    public TimeUnit getRateUnit() {
        return rateUnit;
    }
}
