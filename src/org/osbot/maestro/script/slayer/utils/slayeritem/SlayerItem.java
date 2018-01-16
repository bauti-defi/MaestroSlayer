package org.osbot.maestro.script.slayer.utils.slayeritem;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

public abstract class SlayerItem {

    private final String name;
    private final int amount;
    private final boolean stackable, required;

    public SlayerItem(String name, int amount, boolean stackable, boolean required) {
        this.name = name;
        this.amount = amount;
        this.stackable = stackable;
        this.required = required;
    }

    public boolean isRequired() {
        return required;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public boolean isStackable() {
        return stackable;
    }

    public Item getInventoryInstance(MethodProvider provider) {
        return provider.getInventory().getItem(name);
    }

    public Item getBankInstance(MethodProvider provider) {
        return provider.getBank().getItem(name);
    }

    public boolean hasInInventory(MethodProvider provider) {
        return getInventoryInstance(provider) != null;
    }

    public boolean hasInBank(MethodProvider provider) {
        return getBankInstance(provider) != null;
    }

    public long getInventoryCount(MethodProvider provider) {
        return provider.getInventory().getAmount(name);
    }

    public long getBankCount(MethodProvider provider) {
        return provider.getBank().getAmount(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SlayerItem) {
            SlayerItem item = (SlayerItem) obj;
            return item.getName().equalsIgnoreCase(item.getName()) && item.isRequired() == isRequired() && amount == item.getAmount() &&
                    stackable == item.isStackable();
        }
        return false;
    }
}
