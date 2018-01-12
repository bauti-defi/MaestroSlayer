package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.utils.EquipmentPreset;
import org.osbot.maestro.script.slayer.utils.WithdrawRequest;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerWornItem;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.utility.ConditionalSleep;

public class EquipmentHandler extends NodeTask {

    private EquipmentPreset startingPreset;
    private EquipmentPreset currentPreset;
    private SlayerWornItem toWear;

    public EquipmentHandler() {
        super(Priority.HIGH);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (startingPreset == null) {
            this.startingPreset = getEquipmentAsPreset();
            this.currentPreset = startingPreset;
            provider.log("Current equipment preset saved");
        } else if (RuntimeVariables.currentTask != null) {
            if (!RuntimeVariables.currentTask.getAllSlayerItems().isEmpty()) {
                for (SlayerWornItem wornItem : RuntimeVariables.currentTask.getAllSlayerWornItems()) {
                    if (!wornItem.haveItem(provider)) {
                        sendBroadcast(new Broadcast("bank-withdraw-request", new WithdrawRequest(wornItem.getName(), wornItem.getAmount()
                                , wornItem.getSlot() == EquipmentSlot.ARROWS ? true : false, true, true)));
                        continue;
                    } else if (!wornItem.isWearing(provider)) {
                        toWear = wornItem;
                        return true;
                    }
                }
                currentPreset = getEquipmentAsPreset();
            } else {
                currentPreset = startingPreset;
            }
        }
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (toWear != null) {
            if (toWear.haveItem(provider) && !toWear.isWearing(provider)) {
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
                builder.addItem(slot, item.getName());
            }
        }
        return builder.build();
    }

}
