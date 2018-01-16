package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.utils.banking.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerInventoryItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.listener.MessageListener;
import org.osbot.rs07.utility.ConditionalSleep;


public class TaskValidator extends NodeTask implements BroadcastReceiver, MessageListener {

    private SlayerInventoryItem ENCHANTED_GEM = new SlayerInventoryItem("Enchanted gem", 1, false, true);
    private boolean forceCheck, needTaskFromMaster;

    public TaskValidator() {
        super(Priority.URGENT);
    }

    @Override
    public Response runnable() {
        if (!needTaskFromMaster && (RuntimeVariables.currentTask == null || forceCheck)) {
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
            case "need-slayer-task":
                needTaskFromMaster = (boolean) broadcast.getMessage();
                break;
        }
    }

    @Override
    public void onMessage(Message message) throws InterruptedException {
        switch (message.getType()) {
            case GAME:
                if (message.getMessage().toLowerCase().contains("assigned to kill")) {
                    if (RuntimeVariables.currentTask == null || !RuntimeVariables.currentTask.isFinished()) {
                        SlayerTask.setCurrentTask(message.getMessage());
                        if (RuntimeVariables.currentTask != null) {
                            sendBroadcast(new Broadcast("need-slayer-task", false));
                            sendBroadcast(new Broadcast("request-equipment-update"));
                            sendBroadcast(new Broadcast("requires-anti", RuntimeVariables.currentTask.getCurrentMonster().isPoisonous()));
                            provider.log("Current task: " + RuntimeVariables.currentTask.getName());
                            break;
                        }
                        provider.log("Task not supported.");
                        stopScript(true);
                    }
                } else if (message.getMessage().toLowerCase().contains("you've completed")) {
                    provider.log("Task complete.");
                    RuntimeVariables.tasksFinished++;
                    RuntimeVariables.currentTask.forceFinish();
                } else if (message.getMessage().toLowerCase().contains("you need something new to hunt.")) {
                    if (RuntimeVariables.currentTask != null) {
                        RuntimeVariables.currentTask.forceFinish();
                        break;
                    }
                    sendBroadcast(new Broadcast("need-slayer-task", true));
                    provider.log("Need new task from " + RuntimeVariables.currentMaster.getName());
                }
                break;
        }
    }
}
