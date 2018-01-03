package org.osbot.maestro.script.slayer.utils;


import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;

import java.util.concurrent.TimeUnit;

public class SkillTimer {


    private final Skill skill;
    private final int startExp;
    private int startLevel;
    private final MethodProvider provider;
    private final long startTime;


    /**
     * Constructs new script timer.
     *
     * @param skill to track
     * @param skill
     */
    public SkillTimer(MethodProvider provider, Skill skill) {
        this.startTime = System.currentTimeMillis();
        this.provider = provider;
        this.startLevel = provider.getSkills().getStatic(skill);
        this.skill = skill;
        this.startExp = provider.getSkills().getExperience(skill);
    }


    /**
     * Gets the exp gained over time.
     *
     * @return the exp gained.
     */
    public int getXpGained() {
        return provider.getSkills().getExperience(skill) - startExp;
    }

    /**
     * @return levels gained
     */
    public int levelsGained() {
        return getCurrentRealLevel() - startLevel;
    }

    /**
     * @return current level not affected by boosts.
     */
    public int getCurrentRealLevel() {
        return provider.getSkills().getStatic(skill);
    }

    /**
     * @return the skill
     */
    public Skill getSkill() {
        return skill;
    }

    /**
     * @return the startExp
     */
    public double getStartExp() {
        return startExp;
    }

    public int getExpPerHour() {
        return (int) (getXpGained() / TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - startTime));
    }

}
