package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.utils.EquipmentPreset;
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
        } else if (SlayerVariables.currentTask != null) {
            if (!SlayerVariables.currentTask.hasRequiredWornItems(provider)) {
                provider.log("Need bank, missing item...");
                sendBroadcast(new Broadcast("bank-for-gear"));
                return false;
            }
            for (SlayerWornItem wornItem : SlayerVariables.currentTask.getMonster().getSlayerWornItems()) {
                if (!wornItem.isWearing(provider)) {
                    toWear = wornItem;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (toWear != null) {
            if (toWear.hasItem(provider) && !toWear.isWearing(provider)) {
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
