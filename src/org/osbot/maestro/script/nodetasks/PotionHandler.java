package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.consumable.Potion;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Tab;

import java.util.List;

public class PotionHandler extends NodeTask implements BroadcastReceiver {

    private final List<Potion> potions;
    private Potion potion;
    private final Potion ANTIDOTE = new Potion("Antidote", 1, true);
    private final Potion ANTIPOISON = new Potion("Antipoison", 1, true);

    public PotionHandler(List<Potion> potions) {
        super(Priority.URGENT);
        this.potions = potions;
        registerBroadcastReceiver(this::receivedBroadcast);
    }

    @Override
    public boolean runnable() {
        for (Potion potion : potions) {
            if (!potion.hasConsumable(provider)) {
                provider.log("Out of " + potion.getName() + " banking...");
                sendBroadcast(new Broadcast("bank-withdraw-request", new WithdrawRequest(potion.getName(), new Filter<Item>() {
                    //(item.getName().contains("(3)") || item.getName().contains("(4)"))
                    @Override
                    public boolean match(Item item) {
                        return item.getName().contains(potion.getName()) || (item.getName().contains(potion.getName()) && item.getName()
                                .matches("[3-4]"));
                    }
                }, potion.getAmount(), false, true, potion.isRequired())));
                return false;
            } else if (RuntimeVariables.currentTask != null && RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider
                    .myPosition())) {
                if (potion.hasConsumable(provider) && potion.needConsume(provider)) {
                    this.potion = potion;
                    return true;
                }
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
                        potions.add(RuntimeVariables.settings.isUseAntidote() ? ANTIDOTE : ANTIPOISON);
                    }
                    break;
                }
                potions.remove(RuntimeVariables.settings.isUseAntidote() ? ANTIDOTE : ANTIPOISON);
                break;
        }
    }

}
