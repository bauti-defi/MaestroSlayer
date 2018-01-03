package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

public class RequiredInventoryItem extends RequiredItem {


    public static final RequiredInventoryItem BAG_OF_SALT = new RequiredInventoryItem("Bag of salt", 250, true);
    public static final RequiredInventoryItem ANTIDOTE = new RequiredInventoryItem("Antidote", 1, true);
    public static final RequiredInventoryItem ANTIPOISON = new RequiredInventoryItem("Antipoison", 1, true);
    private final int amount;
    private final boolean stackable;

    public RequiredInventoryItem(String name, int amount, boolean stackable) {
        super(name);
        this.amount = amount;
        this.stackable = stackable;
    }

    public boolean isStackable() {
        return stackable;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean hasItem(MethodProvider provider) {
        return provider.getInventory().contains(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(name);
            }
        });
    }

    @Override
    public int getCount(MethodProvider provider) {
        return provider.getInventory().getItem(name).getAmount();
    }
}
