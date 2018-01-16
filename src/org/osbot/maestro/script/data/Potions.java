package org.osbot.maestro.script.data;

import org.osbot.rs07.api.ui.Skill;

import java.io.Serializable;

public enum Potions implements Serializable {

    SUPER_ATTACK("Super attack", Skill.ATTACK, false), SUPER_STRENGTH("Super strength", Skill.STRENGTH, false), COMBAT_POTION("Combat " +
            "potion", Skill.ATTACK, false),
    PRAYER_POTION("Prayer potion", Skill.PRAYER, true);

    private final String name;
    private final boolean required;
    private final Skill skill;

    Potions(String name, Skill skill, boolean required) {
        this.name = name;
        this.skill = skill;
        this.required = required;
    }

    public Skill getSkill() {
        return skill;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }
}
