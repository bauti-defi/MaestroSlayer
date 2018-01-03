package org.osbot.maestro.script.slayer.utils.consumable;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public class Food extends Consumable {

    public Food(String name) {
        super(name);
    }

    @Override
    public boolean needConsume(MethodProvider provider, int percentToConsumeAt) {
        return provider.myPlayer().getHealthPercent() <= percentToConsumeAt;
    }

    @Override
    public void consume(MethodProvider provider, int percentToConsumeAt) {
        Item food = provider.getInventory().getItem(getName());
        if (food != null) {
            provider.log("Eating: " + getName());
            food.interact("Eat");
            new ConditionalSleep(2800, 700) {

                @Override
                public boolean condition() throws InterruptedException {
                    return percentToConsumeAt < provider.myPlayer().getHealthPercent();
                }
            }.sleep();
            provider.getMouse().moveOutsideScreen();
        }
    }


}
