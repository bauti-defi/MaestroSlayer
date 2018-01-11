package org.osbot.maestro.script.slayer.utils.consumable;

import org.osbot.maestro.script.data.Foods;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

import java.io.Serializable;
import java.util.Random;

public class Food extends Consumable implements Serializable {

    private final int maxPercentToEatAt, minPercentToEatAt;
    private int percentToEatAt;

    public Food(String name, int amount, int minPercentToEatAt, int maxPercentToEatAt) {
        super(name, amount, true);
        this.maxPercentToEatAt = maxPercentToEatAt;
        this.minPercentToEatAt = minPercentToEatAt;
        generateRandomPercentToEatAt();
    }

    public Food(Foods food, int amount, int minPercentToEatAt, int maxPercentToEatAt) {
        this(food.getName(), amount, minPercentToEatAt, maxPercentToEatAt);
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
        Item food = provider.getInventory().getItem(getName());
        int startingHpPercent = provider.myPlayer().getHealthPercent();
        if (food != null) {
            provider.log("Eating: " + getName());
            food.interact("Eat");
            provider.getMouse().moveOutsideScreen();
            new ConditionalSleep(2800, 700) {

                @Override
                public boolean condition() throws InterruptedException {
                    if (startingHpPercent < provider.myPlayer().getHealthPercent()) {
                        generateRandomPercentToEatAt();
                        return true;
                    }
                    return false;
                }
            }.sleep();
        }
    }

}
