package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.utils.MechanicMapper;
import org.osbot.maestro.script.slayer.utils.SlayerItemMapper;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.constants.Banks;

public class Config {


    public static final int CANNON_ID = 6;
    public static final int BROKEN_CANNON = 14916;
    public static final String POSITION = "A";
    public static final String AREA = "B";
    public static final String NAME = "C";
    public static final String COMBAT_LEVEL = "D";
    public static final String SLAYER_LEVEL = "E";
    public static final String TASKS = "F";
    public static final String SW_POSITION = "G";
    public static final String NE_POSITION = "H";
    public static final String POSITION_X = "I";
    public static final String POSITION_Y = "J";
    public static final String POSITION_Z = "K";
    public static final String MONSTERS = "L";
    public static final String MONSTER_MECHANIC = "M";
    public static final String REQUIRED_INVENTORY_ITEMS = "N";
    public static final String REQUIRED_WORN_ITEMS = "O";
    public static final String REQUIRED_INVENTORY_ITEM = "P";
    public static final String REQUIRED_WORN_ITEM = "Q";
    public static final String WILDERNESS_LEVEL = "R";
    public static final String NPC_COUNT = "S";
    public static final String ID = "R";
    public static final String POISONOUS = "T";
    public static final String CANT_MELEE = "U";
    public static final String MULTICOMBAT = "V";
    public static final String SAFESPOT = "W";
    public static final String CANNONSPOT = "X";
    public static final String MASTERS = "Y";
    public static final String SLAYER_DATA_FILE_NAME = "slayer.txt";
    public static final MechanicMapper mechanicMapper;
    public static final SlayerItemMapper itemMapper;
    public static final Area[] GAME_BANKS = new Area[]{
            Banks.AL_KHARID, Banks.ARCEUUS_HOUSE, Banks.ARDOUGNE_NORTH, Banks.ARDOUGNE_SOUTH, Banks.CAMELOT,
            Banks.CASTLE_WARS, Banks.CANIFIS, Banks.CATHERBY, Banks.DRAYNOR, Banks.DUEL_ARENA, Banks.EDGEVILLE, Banks.FALADOR_EAST, Banks
            .FALADOR_WEST, Banks.HOSIDIUS_HOUSE, Banks.LOVAKENGJ_HOUSE, Banks.LOVAKITE_MINE, Banks.GNOME_STRONGHOLD, Banks
            .GNOME_STRONGHOLD, Banks.LUMBRIDGE_LOWER, Banks.LUMBRIDGE_UPPER, Banks.VARROCK_EAST, Banks.VARROCK_WEST, Banks
            .YANILLE, Banks.TZHAAR};


    static {
        mechanicMapper = new MechanicMapper();
        mechanicMapper.map(MonsterMechanics.SALT_MONSTER);

        itemMapper = new SlayerItemMapper();
        itemMapper.map(SlayerItems.BAG_OF_SALT);
        itemMapper.map(SlayerItems.DESERT_BOOTS);
        itemMapper.map(SlayerItems.DESERT_ROBE);
        itemMapper.map(SlayerItems.DESERT_SHIRT);
        itemMapper.map(SlayerItems.EARMUFFS);
        itemMapper.map(SlayerItems.ICE_COOLER);
        itemMapper.map(SlayerItems.WATERSKIN);
        itemMapper.map(SlayerItems.LANTERN);
    }
}
