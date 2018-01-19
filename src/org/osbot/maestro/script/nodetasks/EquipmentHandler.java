package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.EquipmentPreset;
import org.osbot.maestro.script.slayer.utils.banking.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerWornItem;
import org.osbot.maestro.script.slayer.utils.slayeritem.taskitem.WornTaskItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.utility.ConditionalSleep;

public class EquipmentHandler extends NodeTask implements BroadcastReceiver {

    private EquipmentPreset cachedPreset;
    private EquipmentPreset currentPreset;
    private SlayerWornItem toWear;

    public EquipmentHandler() {
        super(Priority.URGENT);
    }

    @Override
    public Response runnable() throws InterruptedException {
        if (cachedPreset == null) {
            this.cachedPreset = getEquipmentAsPreset();
            this.currentPreset = cachedPreset;
            provider.log("Current equipment preset saved");
        } else if (RuntimeVariables.currentTask != null) {
            for (SlayerWornItem slayerWornItem : currentPreset.getItems()) {
                if (!slayerWornItem.hasItem(provider)) {
                    sendBroadcast(new Broadcast("bank-request", new WithdrawRequest(slayerWornItem, true)));
                    continue;
                } else if (!slayerWornItem.isWearing(provider)) {
                    toWear = slayerWornItem;
                    return Response.EXECUTE;
                }
            }
        }
        return Response.CONTINUE;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (toWear != null) {
            if (toWear.hasInInventory(provider) && !toWear.isWearing(provider)) {
                provider.log("Equiping item: " + toWear.getName());
                toWear.equip(provider);
                new ConditionalSleep(2000, 500) {

                    @Override
                    public boolean condition() throws InterruptedException {
                        return toWear.isWearing(provider);
                    }
                }.sleep();
            }
        }
    }

    private EquipmentPreset getEquipmentAsPreset() {
        EquipmentPreset.Builder builder = new EquipmentPreset.Builder();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            Item item = provider.getEquipment().getItemInSlot(slot.slot);
            if (item != null) {
                builder.addItem(new SlayerWornItem(item.getName(), slot, item.getAmount(), item.getAmount() > 1, true));
            }
        }
        return builder.build();
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "request-equipment-update":
                currentPreset = cachedPreset;
                for (WornTaskItem item : RuntimeVariables.currentTask.getAllWornItems()) {
                    currentPreset.overrideItem(item);
                }
                break;
        }
    }
}
