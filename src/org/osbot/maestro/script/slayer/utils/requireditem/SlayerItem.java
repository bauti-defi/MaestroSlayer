package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

public abstract class SlayerItem {

    protected final String name;
    private final ItemRequired condition;

    public SlayerItem(String name, ItemRequired condition) {
        this.name = name;
        this.condition = condition;
    }

    public SlayerItem(String name) {
        this(name, null);
    }

    public String getName() {
        return name;
    }

    public abstract boolean hasItem(MethodProvider provider);

    public abstract int getCount(MethodProvider provider);

    protected abstract Item getItem(MethodProvider provider);

    public boolean required(MethodProvider provider) {
        if (condition == null) {
            return true;
        }
        return condition.required(provider);
    }

}
