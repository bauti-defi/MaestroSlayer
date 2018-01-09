package org.osbot.maestro.script.slayer.utils.antiban;

import java.util.Random;

public class AntibanCharacteristic {

    private static AntibanCharacteristic instance;
    private final int cameraMoveCount;
    private final int cameraMoveCountDeviation;
    private final Random randomGenerator;

    private AntibanCharacteristic() {
        this.randomGenerator = new Random();
        this.cameraMoveCountDeviation = randomGenerator.nextInt(4);
        this.cameraMoveCount = cameraMoveCountDeviation - randomGenerator.nextInt(3);
    }

    public static AntibanCharacteristic getInstance() {
        return instance == null ? instance = new AntibanCharacteristic() : instance;
    }

    public int getCameraMoveCount() {
        return cameraMoveCount;
    }
}
