package org.osbot.maestro.script.slayer.utils.templates;


import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;

public enum WornItemTemplate {

    EARMUFFS("Earmuffs"), DESERT_BOOTS("Desert boots"), DESERT_SHIRT("Desert shirt"), DESERT_ROBES("Desert robes"), MIRROR_SHIELD("Mirror shield");

    private final String name;

    WornItemTemplate(String name) {
        this.name = name;
    }

    private static WornItemTemplate getItem(String name) {
        for (WornItemTemplate item : values()) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public static WornItemTemplate wrap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return getItem((String) jsonObject.get(Config.REQUIRED_WORN_ITEM));
    }

    public String getName() {
        return name;
    }

}
