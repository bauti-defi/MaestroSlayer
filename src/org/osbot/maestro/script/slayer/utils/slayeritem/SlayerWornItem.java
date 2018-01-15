package org.osbot.maestro.script.slayer.utils.slayeritem;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

public class SlayerWornItem extends SlayerInventoryItem {

    private final EquipmentSlot slot;

    public SlayerWornItem(String name, EquipmentSlot slot, int amount, boolean stackable, boolean required) {
        super(name, amount, stackable, required);
        this.slot = slot;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public boolean equip(MethodProvider provider) {
        return interact(provider, slot == EquipmentSlot.WEAPON ? "Wield" : "Wear");
    }

    public Item getWornItemInstance(MethodProvider provider) {
        return provider.getInventory().getItem(getName());
    }

    public boolean unequip(MethodProvider provider) {
        return provider.getEquipment().unequip(slot);
    }

    public boolean isWearing(MethodProvider provider) {
        return provider.getEquipment().isWearingItem(slot, getName());
    }

    public boolean hasItem(MethodProvider provider) {
        return isWearing(provider) || hasInInventory(provider);
    }

}
