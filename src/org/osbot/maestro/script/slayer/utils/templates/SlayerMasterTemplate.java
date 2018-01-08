package org.osbot.maestro.script.slayer.utils.templates;

import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.slayer.utils.position.SlayerArea;

public class SlayerMasterTemplate {

    private String name;
    private int slayerLevel, combatLevel;
    private SlayerArea area;

    public static SlayerMasterTemplate wrap(JSONObject jsonObject) {
        SlayerMasterTemplate master = new SlayerMasterTemplate();

        master.setName((String) jsonObject.get(Config.NAME));
        master.setCombatLevel(((Long) jsonObject.get(Config.COMBAT_LEVEL)).intValue());
        master.setSlayerLevel(((Long) jsonObject.get(Config.SLAYER_LEVEL)).intValue());
        master.setArea(SlayerArea.wrap((JSONObject) jsonObject.get(Config.AREA)));

        return master;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getSlayerLevel() {
        return slayerLevel;
    }

    public void setSlayerLevel(final int slayerLevel) {
        this.slayerLevel = slayerLevel;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public void setCombatLevel(final int combatLevel) {
        this.combatLevel = combatLevel;
    }

    public SlayerArea getArea() {
        return area;
    }

    public void setArea(final SlayerArea area) {
        this.area = area;
    }

    @Override
    public String toString() {
        return name;
    }
}
