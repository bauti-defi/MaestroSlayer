package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.utils.consumable.Potion;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;

import java.util.ArrayList;
import java.util.List;

public class PotionHandler extends NodeTask {

    private final List<Potion> potions;
    private Potion potion;

    private PotionHandler(Builder builder) {
        super(Priority.URGENT);
        this.potions = builder.potions;
    }

    @Override
    public boolean runnable() {
        for (Potion potion : potions) {
            if (!potion.hasConsumable(provider) && potion.isRequired()) {
                sendBroadcast(new Broadcast("bank-for-potions", potion));
                return false;
            } else if (potion.hasConsumable(provider) && potion.needConsume(provider)) {
                this.potion = potion;
                return true;
            }
        }
        return false;
    }

    @Override
    public void execute() throws InterruptedException {
        if (provider.getTabs().open(Tab.INVENTORY)) {
            if (provider.getInventory().getSelectedItemName() == null) {
                if (potion != null) {
                    potion.consume(provider);
                }
            } else {
                provider.getInventory().deselectItem();
            }
        }
    }

    public static class Builder {

        private final List<Potion> potions;

        public Builder() {
            this.potions = new ArrayList<>();
        }

        public Builder addPotion(String name, int amount, Skill skill, int requiredBuff, boolean required) {
            potions.add(new Potion(name, amount, skill, requiredBuff, required));
            return this;
        }

        public Builder addPotion(String name, int amount, boolean required) {
            potions.add(new Potion(name, amount, required));
            return this;
        }

        public PotionHandler build() {
            return new PotionHandler(this);
        }
    }

}
