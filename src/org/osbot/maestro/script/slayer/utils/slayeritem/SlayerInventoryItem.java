package org.osbot.maestro.script.slayer.utils.slayeritem;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

public class SlayerInventoryItem extends SlayerItem {


    public SlayerInventoryItem(String name, int amount, boolean stackable, boolean required) {
        super(name, amount, stackable, required);
    }

    public boolean interact(MethodProvider provider, String... actions) {
        return getInventoryInstance(provider).interact(actions);
    }

    public void hoverOverInInventory(MethodProvider provider) {
        Item consumable = getInventoryInstance(provider);
        if (consumable != null) {
            provider.log("Hovering over: " + getName());
            consumable.hover();
        }
    }

}
