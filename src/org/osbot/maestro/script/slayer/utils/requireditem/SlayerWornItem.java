package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

public class SlayerWornItem extends SlayerItem {

    public static final SlayerWornItem DESERT_BOOTS = new SlayerWornItem("Desert boots", EquipmentSlot.FEET);
    public static final SlayerWornItem DESERT_SHIRT = new SlayerWornItem("Desert shirt", EquipmentSlot.CHEST);
    public static final SlayerWornItem DESERT_ROBE = new SlayerWornItem("Desert robe", EquipmentSlot.LEGS);
    public static final SlayerWornItem EARMUFFS = new SlayerWornItem("Earmuffs", EquipmentSlot.HAT);

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
    public boolean hasItem(MethodProvider provider) {
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
