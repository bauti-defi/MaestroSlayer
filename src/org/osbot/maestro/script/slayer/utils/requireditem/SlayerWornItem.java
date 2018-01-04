package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.rs07.api.filter.Filter;
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

    @Override
    public boolean hasItem(MethodProvider provider) {
        return provider.getEquipment().isWearingItem(slot, name) || provider.getInventory().contains(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(name);
            }
        });
    }

    @Override
    public int getCount(MethodProvider provider) {
        if (provider.getEquipment().isWearingItem(slot, name)) {
            return provider.getEquipment().getItemInSlot(slot.slot).getAmount();
        }
        return 0;
    }
}
