package org.osbot.maestro.script.slayer.utils;

import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerWornItem;
import org.osbot.maestro.script.slayer.utils.slayeritem.WornTaskItem;
import org.osbot.rs07.api.ui.EquipmentSlot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EquipmentPreset {

    private final HashMap<EquipmentSlot, SlayerWornItem> preset;

    private EquipmentPreset(Builder builder) {
        preset = builder.preset;
    }

    public List<SlayerWornItem> getItems() {
        return new ArrayList<SlayerWornItem>(preset.values());
    }

    public SlayerWornItem getItem(EquipmentSlot slot) {
        return preset.get(slot);
    }

    public void overrideItem(WornTaskItem item) {
        preset.put(item.getSlot(), item);
    }

    public static class Builder {

        private final HashMap<EquipmentSlot, SlayerWornItem> preset;

        public Builder() {
            preset = new HashMap<>();
        }

        public Builder addItem(SlayerWornItem item) {
            this.preset.put(item.getSlot(), item);
            return this;
        }

        public EquipmentPreset build() {
            return new EquipmentPreset(this);
        }
    }

}
