package org.osbot.maestro.script.slayer.utils.templates;


import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;

public enum MonsterMechanicTemplate {


    SALT_MONSTER, ICE_MONSTER;

    private static MonsterMechanicTemplate getMechanic(String name) {
        for (MonsterMechanicTemplate item : values()) {
            if (item.name().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    public static MonsterMechanicTemplate wrap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return getMechanic((String) jsonObject.get(Config.MONSTER_MECHANIC));
    }


}
