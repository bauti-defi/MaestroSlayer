package org.osbot.maestro.script.slayer.utils;

import org.osbot.rs07.script.MethodProvider;

public enum CombatStyle {

    ACCURATE("Accurate", 43, 0, 3), AGGRESSIVE("Aggresive", 43, 1, 7), CONTROLLED("Controlled", 43, 2, 11), BLOCK("Block", 43, 3, 15),
    DEFENSIVE_SPELL("Defensive spell", 439, 256, 20), SPELL("Spell", 439, 256, 24);


    public static final int MAGIC_COMBAT_STYLE_CONFIG_ID = 108;
    public static final int MELEE_COMBAT_STYLE_CONFIG_ID = 43;
    public static final int ROOT_ID = 593;
    private final int configId, childId, configParentId;
    private final String name;

    CombatStyle(String name, int configParentId, int configId, int childId) {
        this.name = name;
        this.configId = configId;
        this.childId = childId;
        this.configParentId = configParentId;
    }

    public int getConfigParentId() {
        return configParentId;
    }

    public String getName() {
        return name;
    }

    public int getChildId() {
        return childId;
    }

    public int getConfigId() {
        return configId;
    }

    public static CombatStyle getCurrentCombatStyle(MethodProvider provider) {
        switch (provider.getConfigs().get(MELEE_COMBAT_STYLE_CONFIG_ID)) {
            case 0:
                return CombatStyle.ACCURATE;
            case 1:
                return CombatStyle.AGGRESSIVE;
            case 2:
                return CombatStyle.CONTROLLED;
            case 3:
                return CombatStyle.BLOCK;
            case 4:
                provider.log("Magic is not yet supported");
                switch (provider.getConfigs().get(MAGIC_COMBAT_STYLE_CONFIG_ID)) {
                    case 20:
                        return CombatStyle.DEFENSIVE_SPELL;
                    case 24:
                        return CombatStyle.SPELL;
                }
                break;
        }
        return null;
    }
}
