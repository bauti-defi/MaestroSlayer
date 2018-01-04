package org.osbot.maestro.script.slayer.data;

import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.rs07.api.map.Position;

public class SlayerVariables {

    public static SlayerTask currentTask;
    public static boolean antidote = true;
    public static boolean eating = true;
    public static CombatStyle combatStyle;
    public static boolean cannon = false;
    public static boolean safespot = false;
    public static Position cannonPosition = new Position(2528, 3371, 0);
    public static Position safespotPosition;


}
