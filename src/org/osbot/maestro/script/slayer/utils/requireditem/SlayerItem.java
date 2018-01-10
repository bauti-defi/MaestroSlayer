package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.maestro.script.slayer.utils.Condition;
import org.osbot.maestro.script.slayer.utils.events.BankItemWithdrawEvent;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

public abstract class SlayerItem {

    private final String name;
    private final int amount;
    private final Condition condition;

    public SlayerItem(String name, int amount, Condition condition) {
        this.name = name;
        this.amount = amount;
        this.condition = condition;
    }

    public SlayerItem(String name, int amount) {
        this(name, amount, null);
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public boolean withdrawFromBank(MethodProvider provider) {
        provider.log("Withdrawing " + getAmount() + " " + getName());
        BankItemWithdrawEvent withdrawEvent = new BankItemWithdrawEvent(getName(), new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().equalsIgnoreCase(getName()) || (!item.getName().contains("(0)") && item.getName().contains(getName()));
            }
        }, getAmount(), false);
        withdrawEvent.setNeedExactAmount(true);
        return provider.execute(withdrawEvent).hasFinished();
    }

    public abstract boolean haveItem(MethodProvider provider);

    public abstract int getCount(MethodProvider provider);

    protected abstract Item getItem(MethodProvider provider);

    public boolean required(MethodProvider provider) {
        if (condition == null) {
            return true;
        }
        return condition.run(provider);
    }

}
