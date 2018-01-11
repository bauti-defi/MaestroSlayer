package org.osbot.maestro.script.slayer;

import org.osbot.maestro.script.slayer.utils.position.SlayerArea;
import org.osbot.maestro.script.slayer.utils.templates.SlayerMasterTemplate;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

public class SlayerMaster {

    private final String name;
    private final SlayerArea area;
    private final int requiredCombat, requiredSlayer;


    private SlayerMaster(String name, SlayerArea area, int requiredCombat, int requiredSlayer) {
        this.area = area;
        this.name = name;
        this.requiredCombat = requiredCombat;
        this.requiredSlayer = requiredSlayer;
    }

    public int getRequiredCombat() {
        return requiredCombat;
    }

    public int getRequiredSlayerLevel() {
        return requiredSlayer;
    }

    public String getName() {
        return name;
    }

    public SlayerArea getArea() {
        return area;
    }


    public boolean hasRequirements(MethodProvider provider) {
        return provider.myPlayer().getCombatLevel() >= requiredCombat && provider.getSkills().getStatic(Skill.SLAYER) >= requiredSlayer;
    }

    public static SlayerMaster wrap(SlayerMasterTemplate template) {
        return new SlayerMaster(template.getName(), template.getArea(), template.getCombatLevel(), template.getSlayerLevel());
    }

    @Override
    public String toString() {
        return name;
    }
}
