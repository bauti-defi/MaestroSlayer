package org.osbot.maestro.script.slayer.utils.requireditem;

import org.osbot.maestro.script.slayer.utils.Condition;
import org.osbot.maestro.script.slayer.utils.events.BankItemWithdrawEvent;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.ui.EquipmentSlot;
import org.osbot.rs07.script.MethodProvider;

public class SlayerWornItem extends SlayerItem {

    private final EquipmentSlot slot;

    public SlayerWornItem(String name, int amount, EquipmentSlot slot) {
        super(name, amount);
        this.slot = slot;
    }

    public SlayerWornItem(String name, int amount, EquipmentSlot slot, Condition condition) {
        super(name, amount, condition);
        this.slot = slot;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public void equip(MethodProvider provider) {
        Item item = getItem(provider);
        if (item != null) {
            item.interact(slot == EquipmentSlot.WEAPON ? "Wield" : "Wear");
        }
    }

    public boolean withdrawFromBank(MethodProvider provider) {
        if (slot == EquipmentSlot.ARROWS) {
            provider.log("Withdrawing " + getAmount() + " " + getName());
            BankItemWithdrawEvent withdrawEvent = new BankItemWithdrawEvent(getName(), getAmount(), true);
            return provider.execute(withdrawEvent).hasFinished();
        }
        return super.withdrawFromBank(provider);
    }

    @Override
    protected Item getItem(MethodProvider provider) {
        if (provider.getEquipment().isWearingItem(slot, getName())) {
            return provider.getEquipment().getItemInSlot(slot.slot);
        }
        return provider.getInventory().getItem(getName());
    }

    @Override
    public boolean haveItem(MethodProvider provider) {
        return getItem(provider) != null;
    }

    @Override
    public int getCount(MethodProvider provider) {
        Item item = getItem(provider);
        if (item != null) {
            return item.getAmount();
        }
        return 0;
    }

    public boolean isWearing(MethodProvider provider) {
        return provider.getEquipment().isWearingItem(slot, getName());
    }
}
