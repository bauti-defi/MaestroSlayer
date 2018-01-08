package org.osbot.maestro.script.slayer.utils.templates;

import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;

public enum InventoryItemTemplate {


    ICE_COOLERS("Ice cooler"), LANTERN("Lantern"), WATERSKINS("Waterskin");

    private final String name;

    InventoryItemTemplate(String name) {
        this.name = name;
    }

    private static InventoryItemTemplate getItem(String name) {
        for (InventoryItemTemplate item : values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public static InventoryItemTemplate wrap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return getItem((String) jsonObject.get(Config.REQUIRED_INVENTORY_ITEM));
    }

    public String getName() {
        return name;
    }

}
