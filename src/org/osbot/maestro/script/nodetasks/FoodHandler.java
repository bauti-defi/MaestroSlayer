package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.utils.consumable.Food;
import org.osbot.rs07.api.ui.Tab;

import java.util.Random;

public class FoodHandler extends NodeTask implements BroadcastReceiver {

    private final Food food;
    private final int percentToEatAtMax, getPercentToEatAtMin;
    private int percentToEatAt;
    private boolean hoverAntiban;

    public FoodHandler(Food food, int percentToEatAtMax, int percentToEatAtMin) {
        super(Priority.CRITICAL);
        this.food = food;
        this.percentToEatAtMax = percentToEatAtMax;
        this.getPercentToEatAtMin = percentToEatAtMin;
        generateRandomPercentToEatAt();
        registerBroadcastReceiver(this);
    }

    private void generateRandomPercentToEatAt() {
        percentToEatAt = new Random().nextInt(percentToEatAtMax - getPercentToEatAtMin + 1) + getPercentToEatAtMin;
    }

    @Override
    public boolean runnable() {
        if (!food.hasConsumable(provider)) {
            provider.warn("Out of food. Stopping...");
            stopScript(true);
        }
        return food.needConsume(provider, percentToEatAt);
    }

    @Override
    public void execute() {
        if (provider.getTabs().open(Tab.INVENTORY)) {
            if (provider.getInventory().getSelectedItemName() == null) {
                food.consume(provider, percentToEatAt);
                generateRandomPercentToEatAt();
                provider.log("Next eat at: " + percentToEatAt + "HP");
            } else {
                provider.getInventory().deselectItem();
            }
        }
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        if (broadcast.getKey().equalsIgnoreCase("hover-food-antiban")) {
            food.hoverOver(provider);
        }
    }
}
