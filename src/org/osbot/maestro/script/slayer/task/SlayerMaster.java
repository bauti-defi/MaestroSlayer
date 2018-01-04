package org.osbot.maestro.script.slayer.task;

import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public enum SlayerMaster {

    TURAEL("Turael", null, 3, 1), VANNAKA("Vannaka", null, 40, 1), CHAELDAR("Chaeldar", null, 70, 1), NIEVE("Nieve", null, 85, 1), DURADEL("Duradel", null, 100, 50);

    private final String name;
    private final Area area;
    private final int requiredCombat, requiredSlayer;


    SlayerMaster(String name, Area area, int requiredCombat, int requiredSlayer) {
        this.area = area;
        this.name = name;
        this.requiredCombat = requiredCombat;
        this.requiredSlayer = requiredSlayer;
    }

    public int getRequiredCombat() {
        return requiredCombat;
    }

    public int getRequiredSlayer() {
        return requiredSlayer;
    }

    public String getName() {
        return name;
    }

    public Area getArea() {
        return area;
    }


    public boolean hasRequirements(MethodProvider provider) {
        return provider.myPlayer().getCombatLevel() >= requiredCombat && provider.getSkills().getStatic(Skill.SLAYER) >= requiredSlayer;
    }
}
