package org.osbot.maestro.script.slayer.utils;

import java.util.concurrent.TimeUnit;

public enum AntibanFrequency {

    VERY_LOW(6, TimeUnit.MINUTES, 3, TimeUnit.MINUTES), LOW(270, TimeUnit.SECONDS, 2, TimeUnit.MINUTES),
    MEDIUM(3, TimeUnit.MINUTES, 90, TimeUnit.SECONDS), HIGH(150, TimeUnit.SECONDS, 30, TimeUnit.SECONDS), VERY_HIGH(2,
            TimeUnit.MINUTES, 45, TimeUnit.SECONDS), MAX(1, TimeUnit.MINUTES, 15, TimeUnit.SECONDS);


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
