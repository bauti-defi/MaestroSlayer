package org.osbot.maestro.script.slayer.utils;

import org.osbot.rs07.api.ui.EquipmentSlot;

import java.util.HashMap;

public class EquipmentPreset {

    private final HashMap<EquipmentSlot, String> preset;

    private EquipmentPreset(Builder builder) {
        preset = builder.preset;
    }

    public HashMap<EquipmentSlot, String> getStartPreset() {
        return preset;
    }

    public String getItem(EquipmentSlot slot) {
        return preset.get(slot);
    }

    public static class Builder {

        private final HashMap<EquipmentSlot, String> preset;

        public Builder() {
            preset = new HashMap<>();
        }

        public Builder addItem(EquipmentSlot slot, String name) {
            this.preset.put(slot, name);
            return this;
        }

        public EquipmentPreset build() {
            return new EquipmentPreset(this);
        }
    }

}
