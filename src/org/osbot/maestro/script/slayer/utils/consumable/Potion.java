package org.osbot.maestro.script.slayer.utils.consumable;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public class Potion extends Consumable {

    private Skill skill;

    public Potion(String name, Skill skill) {
        super(name);
        this.skill = skill;
    }

    public Potion(String name) {
        super(name);
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

    @Override
    public boolean needConsume(MethodProvider provider, int requiredBuff) {
        if (skill != null) {
            if (requiredBuff == 0) {
                return provider.getSkills().getDynamic(skill) == provider.getSkills().getStatic(skill);
            }
            return provider.getSkills().getDynamic(skill) < (provider.getSkills().getStatic(skill) + requiredBuff);
        }
        return (getName().contains("poison") || getName().contains("Antidote")) && provider
                .getCombat().isPoisoned();
    }

    @Override
    public void consume(MethodProvider provider, int requiredBuff) {
        Item potion = provider.getInventory().getItem(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(getName());
            }
        });
        if (potion != null) {
            provider.log("Drinking: " + getName());
            potion.interact("Drink");
            new ConditionalSleep(2800, 700) {

                @Override
                public boolean condition() throws InterruptedException {
                    if (skill != null) {
                        return (provider.getSkills().getStatic(skill) + requiredBuff) < provider.getSkills().getDynamic
                                (skill);
                    }
                    return !provider.getCombat().isPoisoned();
                }
            }.sleep();
            provider.getMouse().moveOutsideScreen();
        }
    }
}
