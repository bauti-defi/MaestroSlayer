package org.osbot.maestro.script.slayer.utils.consumable;

import org.osbot.maestro.script.slayer.utils.events.BankItemWithdrawEvent;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

public abstract class Consumable {

    private final String name;
    private final boolean required;
    private final int amount;

    public Consumable(String name, int amount, boolean required) {
        this.name = name;
        this.amount = amount;
        this.required = required;
    }

    public int getAmount() {
        return amount;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean withdrawFromBank(MethodProvider provider) {
        provider.log("Withdrawing " + amount + " " + getName());
        BankItemWithdrawEvent withdrawEvent = new BankItemWithdrawEvent(name, amount, false);
        return provider.execute(withdrawEvent).hasFinished();
    }

    public boolean hasConsumable(MethodProvider provider) {
        return provider.getInventory().contains(name);
    }

    public abstract boolean needConsume(MethodProvider provider);

    public abstract void consume(MethodProvider provider);

    public void hoverOver(MethodProvider provider) {
        Item consumable = provider.getInventory().getItem(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(getName());
            }
        });
        if (consumable != null) {
            provider.log("Hovering over: " + getName());
            consumable.hover();
        }
    }

}
