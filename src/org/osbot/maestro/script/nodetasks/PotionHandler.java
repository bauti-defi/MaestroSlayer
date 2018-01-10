package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.consumable.Potion;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.ui.Tab;

import java.util.ArrayList;
import java.util.List;

public class PotionHandler extends NodeTask implements BroadcastReceiver {

    private final List<Potion> potions;
    private Potion potion;
    private final Potion ANTIDOTE = new Potion("Antidote", 1, true);

    private PotionHandler(Builder builder) {
        super(Priority.URGENT);
        this.potions = builder.potions;
        registerBroadcastReceiver(this::receivedBroadcast);
    }

    @Override
    public boolean runnable() {
        for (Potion potion : potions) {
            if (!potion.hasConsumable(provider) && potion.isRequired()) {
                provider.log("Out of " + potion.getName() + " banking...");
                sendBroadcast(new Broadcast("bank-for-potions", potion));
                return false;
            } else if (potion.hasConsumable(provider) && potion.needConsume(provider) && RuntimeVariables.currentTask.getCurrentMonster().getArea()
                    .contains(provider.myPosition())) {
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

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "requires-anti":
                boolean poisonous = (boolean) broadcast.getMessage();
                if (poisonous) {
                    if (!potions.contains(ANTIDOTE)) {
                        potions.add(ANTIDOTE);
                    }
                    break;
                }
                potions.remove(ANTIDOTE);
                break;
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
