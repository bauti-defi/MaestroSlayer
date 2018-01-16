package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.slayer.utils.banking.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.slayeritem.Food;
import org.osbot.rs07.api.ui.Tab;

public class FoodHandler extends NodeTask implements BroadcastReceiver {

    private final Food food;
    private boolean hoverAntiban;

    public FoodHandler(Food food) {
        super(Priority.CRITICAL);
        this.food = food;
    }


    @Override
    public Response runnable() {
        if (!food.hasInInventory(provider)) {
            provider.log("Out of " + food.getName() + " banking...");
            sendBroadcast(new Broadcast("bank-request", new WithdrawRequest(food, false)));
        } else if (food.needConsume(provider)) {
            return Response.EXECUTE;
        }
        return Response.CONTINUE;
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
            food.hoverOverInInventory(provider);
        }
    }
}
