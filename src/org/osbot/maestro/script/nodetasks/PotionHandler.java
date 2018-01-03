package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.utils.consumable.Potion;
import org.osbot.rs07.api.ui.Tab;

import java.util.HashMap;
import java.util.Map;

public class PotionHandler extends NodeTask {

    private final HashMap<Potion, Integer> potions;
    private Map.Entry<Potion, Integer> entry;

    private PotionHandler(Builder builder) {
        super(Priority.HIGH);
        this.potions = builder.potions;
    }

    @Override
    public boolean runnable() {
        for (Map.Entry<Potion, Integer> entry : potions.entrySet()) {
            if (entry.getKey().hasConsumable(provider) && entry.getKey().needConsume(provider, entry.getValue())) {
                this.entry = entry;
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute() throws InterruptedException {
        if (provider.getTabs().open(Tab.INVENTORY)) {
            if (provider.getInventory().getSelectedItemName() == null) {
                entry.getKey().consume(provider, entry.getValue());
            } else {
                provider.getInventory().deselectItem();
            }
        }
    }

    public static class Builder {

        private final HashMap<Potion, Integer> potions;

        public Builder() {
            this.potions = new HashMap<>();
            potions.put(new Potion("Antipoison"), null);
            potions.put(new Potion("Antidote"), null);
        }

        public Builder addPotion(Potion potion, int requiredBuff) {
            potions.put(potion, requiredBuff);
            return this;
        }

        public PotionHandler build() {
            return new PotionHandler(this);
        }
    }

}
