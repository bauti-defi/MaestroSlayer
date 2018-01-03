package org.osbot.maestro.script.slayer.utils;

public enum CombatStyle {

    ACCURATE("Accurate", 0, 3), AGGRESSIVE("Aggresive", 1, 7), CONTROLLED("Controlled", 2, 11), BLOCK("Block", 3, 15);

    public static final int ROOT_ID = 593;
    private final int configId, childId;
    private final String name;

    CombatStyle(String name, int configId, int childId) {
        this.name = name;
        this.configId = configId;
        this.childId = childId;
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
}
