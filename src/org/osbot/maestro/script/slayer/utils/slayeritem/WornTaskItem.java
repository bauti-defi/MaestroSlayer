package org.osbot.maestro.script.slayer.utils.slayeritem;

import org.osbot.rs07.api.ui.EquipmentSlot;

public class WornTaskItem extends SlayerWornItem {


    public WornTaskItem(String name, EquipmentSlot slot, int amount, boolean stackable) {
        super(name, slot, amount, stackable, true);
    }
}
