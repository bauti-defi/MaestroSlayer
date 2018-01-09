package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.maestro.script.slayer.utils.Condition;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.script.MethodProvider;

import java.util.List;

public class SlayerInventoryItem extends SlayerItem {

    private final boolean stackable;

    public SlayerInventoryItem(String name, int amount, boolean stackable, Condition condition) {
        super(name, amount, condition);
        this.stackable = stackable;
    }

    public SlayerInventoryItem(String name, int amount, boolean stackable) {
        super(name, amount);
        this.stackable = stackable;
    }

    public boolean isStackable() {
        return stackable;
    }


    @Override
    public boolean haveItem(MethodProvider provider) {
        return getItem(provider) != null;
    }

    @Override
    public int getCount(MethodProvider provider) {
        if (!stackable) {
            List<Item> items = provider.getInventory().filter(new Filter<Item>() {
                @Override
                public boolean match(Item item) {
                    return item.getName().contains(name) && !item.getName().endsWith("(0)");
                }
            });
            if (items != null) {
                return items.size();
            }
        } else {
            Item item = getItem(provider);
            if (item != null) {
                return item.getAmount();
            }
        }
        return 0;
    }

    @Override
    public boolean withdrawFromBank(MethodProvider provider) {
        return provider.getBank().withdraw(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(name) && !item.getName().endsWith("(0)");
            }
        }, getAmount());
    }

    @Override
    protected Item getItem(MethodProvider provider) {
        return provider.getInventory().getItem(new Filter<Item>() {
            @Override
            public boolean match(Item item) {
                return item.getName().contains(name) && !item.getName().endsWith("(0)");
            }
        });
    }

}
