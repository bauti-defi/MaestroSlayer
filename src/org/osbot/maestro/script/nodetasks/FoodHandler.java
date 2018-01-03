package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.utils.consumable.Food;
import org.osbot.rs07.api.ui.Tab;

public class FoodHandler extends NodeTask {

    private final Food food;
    private final int percentToEatAt;

    public FoodHandler(Food food, int percentToEatAt) {
        super(Priority.CRITICAL);
        this.food = food;
        this.percentToEatAt = percentToEatAt;
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
            } else {
                provider.getInventory().deselectItem();
            }
        }
    }

}
