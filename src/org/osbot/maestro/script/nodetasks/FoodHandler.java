package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.utils.consumable.Food;
import org.osbot.rs07.api.ui.Tab;

public class FoodHandler extends NodeTask implements BroadcastReceiver {

    private final Food food;
    private boolean hoverAntiban;

    public FoodHandler(Food food) {
        super(Priority.CRITICAL);
        this.food = food;
        registerBroadcastReceiver(this);
    }


    @Override
    public boolean runnable() {
        if (!food.hasConsumable(provider)) {
            provider.log("Out of " + food.getName() + " banking...");
            sendBroadcast(new Broadcast("bank-for-food", food));
            return false;
        }
        return food.needConsume(provider);
    }

    @Override
    public void execute() {
        if (provider.getTabs().open(Tab.INVENTORY)) {
            if (provider.getInventory().getSelectedItemName() == null) {
                food.consume(provider);
                provider.log("Next eat at: " + food.getNextEatAt() + "% HP");
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
