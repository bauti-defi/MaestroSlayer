package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

public class SlayerWornItem extends SlayerItem {

    private final EquipmentSlot slot;

    public SlayerWornItem(String name, EquipmentSlot slot) {
        super(name);
        this.slot = slot;
    }

    public SlayerWornItem(String name, EquipmentSlot slot, ItemRequired condition) {
        super(name, condition);
        this.slot = slot;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public void equip(MethodProvider provider) {
        Item item = getItem(provider);
        if (item != null) {
            item.interact(slot == EquipmentSlot.WEAPON ? "Wield" : "Wear");
        }
    }

    @Override
    protected Item getItem(MethodProvider provider) {
        if (provider.getEquipment().isWearingItem(slot, name)) {
            return provider.getEquipment().getItemInSlot(slot.slot);
        }
        return provider.getInventory().getItem(name);
    }

    @Override
    public boolean haveItem(MethodProvider provider) {
        return getItem(provider) != null;
    }

    @Override
    public int getCount(MethodProvider provider) {
        Item item = getItem(provider);
        if (item != null) {
            return item.getAmount();
        }
        return 0;
    }

    public boolean isWearing(MethodProvider provider) {
        return provider.getEquipment().isWearingItem(slot, name);
    }
}
