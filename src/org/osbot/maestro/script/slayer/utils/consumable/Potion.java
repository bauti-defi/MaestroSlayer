package org.osbot.maestro.script.slayer.utils.consumable;

import org.osbot.maestro.script.data.Potions;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

import java.io.Serializable;

public class Potion extends Consumable implements Serializable {

    private Skill skill;
    private int requiredBuff;

    public Potion(String name, int amount, Skill skill, int requiredBuff, boolean required) {
        super(name, amount, required);
        this.skill = skill;
        this.requiredBuff = requiredBuff;
    }

    public Potion(String name, int amount, boolean required) {
        super(name, amount, required);
    }

    public Potion(Potions potion, int amount, int requiredBuff) {
        this(potion.getName(), amount, potion.getSkill(), requiredBuff, potion.isRequired());
    }

    @Override
    public boolean hasConsumable(MethodProvider provider) {
        return provider.getInventory().contains(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(getName());
            }
        });
    }

    public boolean needConsume(MethodProvider provider) {
        if (RuntimeVariables.currentTask != null && RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider
                .myPosition()) && skill != null) {
            return provider.getSkills().getDynamic(skill) <= (provider.getSkills().getStatic(skill) + requiredBuff);
        }
        return (getName().contains("poison") || getName().contains("Antidote")) && provider
                .getCombat().isPoisoned();
    }

    @Override
    public void consume(MethodProvider provider) {
        Item potion = provider.getInventory().getItem(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(getName());
            }
        });
        if (potion != null) {
            provider.log("Drinking: " + getName());
            potion.interact("Drink");
            provider.getMouse().moveOutsideScreen();
            new ConditionalSleep(2800, 700) {

                @Override
                public boolean condition() throws InterruptedException {
                    return !needConsume(provider);
                }
            }.sleep();
        }
    }

}
