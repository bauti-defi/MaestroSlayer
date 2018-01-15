package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.banking.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerInventoryItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.utility.ConditionalSleep;


public class TaskValidator extends NodeTask implements BroadcastReceiver {

    private SlayerInventoryItem ENCHANTED_GEM = new SlayerInventoryItem("Enchanted gem", 1, false, true);
    private boolean forceCheck;

    public TaskValidator() {
        super(Priority.URGENT);
        registerBroadcastReceiver(this);
    }

    @Override
    public Response runnable() {
        if (RuntimeVariables.currentTask == null || forceCheck) {
            if (provider.getInventory().contains("Enchanted gem")) {
                return Response.EXECUTE;
            } else {
                sendBroadcast(new Broadcast("bank-request", new WithdrawRequest(ENCHANTED_GEM, true)));
            }
        }
        return Response.CONTINUE;
    }

    @Override
    public void execute() {
        if (provider.getTabs().open(Tab.INVENTORY)) {
            if (provider.getInventory().getSelectedItemName() == null) {
                Item gem = provider.getInventory().getItem("Enchanted Gem");
                if (gem != null) {
                    provider.log("Checking task");
                    gem.interact("Check");
                    new ConditionalSleep(3000, 1000) {

                        @Override
                        public boolean condition() throws InterruptedException {
                            return RuntimeVariables.currentTask != null;
                        }
                    }.sleep();
                }
                forceCheck = false;
            } else {
                provider.getInventory().deselectItem();
            }
        }
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "check-kills-antiban":
                forceCheck = true;
                break;
        }
    }
}
