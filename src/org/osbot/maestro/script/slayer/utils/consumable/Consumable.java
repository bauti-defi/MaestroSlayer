package org.osbot.maestro.script.slayer.utils.consumable;

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

}
