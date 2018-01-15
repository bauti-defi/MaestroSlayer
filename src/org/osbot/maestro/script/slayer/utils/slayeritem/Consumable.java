package org.osbot.maestro.script.slayer.utils.slayeritem;

import org.osbot.rs07.script.MethodProvider;

public abstract class Consumable extends SlayerInventoryItem {

    public Consumable(String name, int amount, boolean stackable, boolean required) {
        super(name, amount, stackable, required);
    }

    public abstract boolean needConsume(MethodProvider provider);

    public abstract void consume(MethodProvider provider);
}
