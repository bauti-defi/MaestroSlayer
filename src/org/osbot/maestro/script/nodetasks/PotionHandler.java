package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.banking.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.slayeritem.Potion;
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
    }

    @Override
    public Response runnable() {
        for (Potion potion : potions) {
            if (!potion.hasInInventory(provider)) {
                sendBroadcast(new Broadcast("bank-request", new WithdrawRequest(potion, true)));
            } else if (RuntimeVariables.currentTask != null && RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider
                    .myPosition())) {
                if (potion.hasInInventory(provider) && potion.needConsume(provider)) {
                    this.potion = potion;
                    return Response.EXECUTE;
                }
            }
        }
        return Response.CONTINUE;
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
