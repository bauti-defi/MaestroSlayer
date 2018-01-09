package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerWornItem;
import org.osbot.rs07.api.ui.EquipmentSlot;

public class SlayerItems {

    public static final SlayerWornItem DESERT_BOOTS = new SlayerWornItem("Desert boots", 1, EquipmentSlot.FEET);
    public static final SlayerWornItem DESERT_SHIRT = new SlayerWornItem("Desert shirt", 1, EquipmentSlot.CHEST);
    public static final SlayerWornItem DESERT_ROBE = new SlayerWornItem("Desert robe", 1, EquipmentSlot.LEGS);
    public static final SlayerWornItem EARMUFFS = new SlayerWornItem("Earmuffs", 1, EquipmentSlot.HAT);


    public static final SlayerInventoryItem BAG_OF_SALT = new SlayerInventoryItem("Bag of salt", 250, true);
    public static final SlayerInventoryItem WATERSKIN = new SlayerInventoryItem("Waterskin", 4, false);
    public static final SlayerInventoryItem ICE_COOLER = new SlayerInventoryItem("Ice cooler", 250, true);
    public static final SlayerInventoryItem LANTERN = new SlayerInventoryItem("Lantern", 1, false);

}
