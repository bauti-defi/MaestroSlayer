package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.SlayerMaster;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.task.monster.Monster;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.maestro.script.slayer.utils.SlayerContainer;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.util.directory.Directory;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.util.ExperienceTracker;

public class RuntimeVariables {

    public static SlayerTask currentTask;
    public static SlayerMaster currentMaster;
    public static Monster currentMonster;
    public static boolean drinkPotions = true;
    public static int maxHpPercentToEat = 50;
    public static int minHpPercentToEat = 30;
    public static SlayerInventoryItem antipoisonChoice;
    public static CombatStyle combatStyle;
    public static boolean cannon = false;
    public static boolean safespot = false;
    public static Position cannonPosition = new Position(2528, 3371, 0);
    public static Position safespotPosition;
    public static ExperienceTracker experienceTracker;
    public static int tasksFinished = 0;
    public static Directory saveDirectory;
    public static SlayerContainer slayerContainer;
    public static String antipoisonType = "Antidote";

}
