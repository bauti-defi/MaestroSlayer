package org.osbot.maestro.script.slayer.utils.slayeritem.consumables;

import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.slayeritem.Mutadable;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

import java.io.Serializable;

public class Potion extends Consumable implements Mutadable, Serializable {

    private final Skill skill;
    private final int requiredBuff;

    public Potion(String name, Skill skill, int requiredBuff, int amount, boolean required) {
        super(name, amount, false, required);
        this.skill = skill;
        this.requiredBuff = requiredBuff;
    }

    public Potion(String name, int amount, boolean required) {
        super(name, amount, false, required);
        this.skill = null;
        this.requiredBuff = 0;
    }

    public boolean isBuff() {
        return skill != null || requiredBuff > 0;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getRequiredBuff() {
        return requiredBuff;
    }

    @Override
    public Item getBankInstance(MethodProvider provider) {
        return provider.getBank().getItem(getMutationFilter());
    }

    @Override
    public boolean needConsume(MethodProvider provider) {
        if (RuntimeVariables.currentTask != null && RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider.myPosition()) && isBuff()) {
            return provider.getSkills().getDynamic(skill) <= (provider.getSkills().getStatic(skill) + requiredBuff);
        }
        return (getName().contains("poison") || getName().contains("Antidote")) && provider.getCombat().isPoisoned();
    }

    @Override
    public void consume(MethodProvider provider) {
        provider.log("Drinking: " + getName());
        interact(provider, "Drink");
        provider.getMouse().moveOutsideScreen();
        new ConditionalSleep(2800, 700) {

            @Override
            public boolean condition() throws InterruptedException {
                return !needConsume(provider);
            }
        }.sleep();
    }

    @Override
    public Filter<Item> getMutationFilter() {
        return new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.nameContains(getName()) && (item.nameContains("4") || item.nameContains("3"));
            }
        };
    }
}
