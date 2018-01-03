package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.rs07.script.MethodProvider;

public abstract class RequiredItem {

    protected final String name;

    public RequiredItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public abstract boolean hasItem(MethodProvider provider);

    public abstract int getCount(MethodProvider provider);
}
