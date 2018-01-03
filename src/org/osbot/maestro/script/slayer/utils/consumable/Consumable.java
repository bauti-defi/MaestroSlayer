package org.osbot.maestro.script.slayer.utils.consumable;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

public abstract class Consumable {

    private final String name;

    public Consumable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean hasConsumable(MethodProvider provider) {
        return provider.getInventory().contains(name);
    }

    public abstract boolean needConsume(MethodProvider provider, int consumeAt);

    public abstract void consume(MethodProvider provider, int consumeAt);

    public void hoverOver(MethodProvider provider) {
        Item consumable = provider.getInventory().getItem(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(getName());
            }
        });
        if (consumable != null) {
            provider.log("Hovering over: " + getName());
            consumable.hover();
        }
    }

}
