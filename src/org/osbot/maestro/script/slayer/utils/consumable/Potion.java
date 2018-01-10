package org.osbot.maestro.script.slayer.utils.consumable;

import org.osbot.maestro.script.slayer.utils.events.BankItemWithdrawEvent;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public class Potion extends Consumable {

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
    public boolean withdrawFromBank(MethodProvider provider) {
        provider.log("Withdrawing " + getAmount() + " " + getName());
        BankItemWithdrawEvent withdrawEvent = new BankItemWithdrawEvent(getName(), new Filter<Item>() {

            @Override
            public boolean match(Item item) {
                return item.getName().contains(getName()) && (item.getName().contains("(3)") || item.getName().contains("(4)"));
            }
        }, getAmount(), false);
        withdrawEvent.setNeedExactAmount(true);
        return provider.execute(withdrawEvent).hasFinished();
    }

    public boolean needConsume(MethodProvider provider) {
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
