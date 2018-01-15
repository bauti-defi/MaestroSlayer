package org.osbot.maestro.script.slayer.utils.slayeritem;

import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

import java.io.Serializable;
import java.util.Random;

public class Food extends Consumable implements Serializable {

    private final int minPercentToEatAt, maxPercentToEatAt;
    private int percentToEatAt;

    public Food(String name, int amount, int minPercentToEatAt, int maxPercentToEatAt) {
        super(name, amount, false, true);
        this.maxPercentToEatAt = maxPercentToEatAt;
        this.minPercentToEatAt = minPercentToEatAt;
    }

    public int getMinPercentToEatAt() {
        return minPercentToEatAt;
    }

    public int getMaxPercentToEatAt() {
        return maxPercentToEatAt;
    }

    private void generateRandomPercentToEatAt() {
        percentToEatAt = new Random().nextInt(maxPercentToEatAt - minPercentToEatAt + 1) + minPercentToEatAt;
    }

    public int getNextEatAt() {
        return percentToEatAt;
    }

    @Override
    public boolean needConsume(MethodProvider provider) {
        return provider.myPlayer().getHealthPercent() <= percentToEatAt;
    }

    public void consume(MethodProvider provider) {
        provider.log("Eating: " + getName());
        interact(provider, "Eat");
        provider.getMouse().moveOutsideScreen();
        new ConditionalSleep(2800, 700) {

            @Override
            public boolean condition() throws InterruptedException {
                if (!needConsume(provider)) {
                    generateRandomPercentToEatAt();
                    return true;
                }
                return false;
            }
        }.sleep();
    }

}
