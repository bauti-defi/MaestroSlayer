package org.osbot.maestro.script.slayer.utils.antiban;

import java.util.concurrent.TimeUnit;

public enum AntibanFrequency {

    MIN(2, TimeUnit.MINUTES, 30, TimeUnit.SECONDS),
    VERY_LOW(90, TimeUnit.SECONDS, 30, TimeUnit.MINUTES),
    LOW(60, TimeUnit.SECONDS, 15, TimeUnit.SECONDS),
    MEDIUM(45, TimeUnit.SECONDS, 10, TimeUnit.SECONDS),
    HIGH(35, TimeUnit.SECONDS, 10, TimeUnit.SECONDS),
    VERY_HIGH(25, TimeUnit.SECONDS, 10, TimeUnit.SECONDS),
    MAX(15, TimeUnit.SECONDS, 5, TimeUnit.SECONDS);


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
