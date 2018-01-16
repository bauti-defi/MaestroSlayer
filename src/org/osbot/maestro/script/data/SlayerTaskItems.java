package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.utils.slayeritem.InventoryTaskItem;
import org.osbot.maestro.script.slayer.utils.slayeritem.WornTaskItem;
import org.osbot.rs07.api.ui.EquipmentSlot;

public class SlayerTaskItems {

    public static final WornTaskItem DESERT_BOOTS = new WornTaskItem("Desert boots", EquipmentSlot.FEET, 1, false);
    public static final WornTaskItem DESERT_SHIRT = new WornTaskItem("Desert shirt", EquipmentSlot.CHEST, 1, false);
    public static final WornTaskItem DESERT_ROBE = new WornTaskItem("Desert robe", EquipmentSlot.LEGS, 1, false);
    public static final WornTaskItem EARMUFFS = new WornTaskItem("Earmuffs", EquipmentSlot.HAT, 1, false);
    public static final WornTaskItem BUG_LANTERN = new WornTaskItem("Lit bug lantern", EquipmentSlot.SHIELD, 1, false);


    public static final InventoryTaskItem BAG_OF_SALT = new InventoryTaskItem("Bag of salt", 250, true);
    public static final InventoryTaskItem WATERSKIN = new InventoryTaskItem("Waterskin", 3, false);
    public static final InventoryTaskItem ICE_COOLER = new InventoryTaskItem("Ice cooler", 250, true);
    public static final InventoryTaskItem LANTERN = new InventoryTaskItem("Lantern", 1, false);

}
