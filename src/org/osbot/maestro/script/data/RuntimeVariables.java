package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.SlayerMaster;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.maestro.script.slayer.utils.SlayerContainer;
import org.osbot.rs07.api.map.Position;
import org.osbot.rs07.api.util.ExperienceTracker;

public class RuntimeVariables {

    public static SlayerTask currentTask;
    public static SlayerMaster currentMaster;
    public static CombatStyle combatStyle;
    public static Position safespotPosition;
    public static ExperienceTracker experienceTracker;
    public static int tasksFinished = 0;
    public static Cache cache;
    public static SlayerContainer slayerContainer;
    public static SlayerSettings settings;

}
