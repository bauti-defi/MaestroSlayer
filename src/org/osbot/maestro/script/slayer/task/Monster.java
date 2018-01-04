package org.osbot.maestro.script.slayer.task;

import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItem;

public enum Monster {

    //TODO: ALL RELEKA & LUMBRIDGE SWAMP DUNG TASKS NEED ANTI UNTIL 46 CB

    ROCKSLUGS("Rockslug", false, MonsterMechanic.ROCK_SLUG_MECHANIC, SlayerInventoryItem.BAG_OF_SALT, SlayerVariables.antipoisonChoice),
    CAVE_CRAWLERS("Cave crawler", true, null, SlayerVariables.antipoisonChoice),
    OGRES("Ogre", false, null),
    KALPHITES("Kalphite", true, null, SlayerVariables.antipoisonChoice),
    HILL_GIANTS("Hill Giant", false, null),
    MOSS_GIANTS("Moss giant", false, null),
    CAVE_SLIMES("Cave slime", false, null, SlayerVariables.antipoisonChoice),
    EARTH_WARRIORS("Earth warrior", false, null),
    HOBGOBLINS("Hobgoblin", false, null),
    OTHERWORDLY_BEINGS("Otherworldy being", false, null),
    PYREFIENDS("Pyrefiend", false, null, SlayerVariables.antipoisonChoice);

    private final String name;
    private final boolean poisonous;
    private final MonsterMechanic monsterMechanic;
    private final SlayerItem[] slayerItems;

    Monster(String name, boolean poisonous, MonsterMechanic monsterMechanic, SlayerItem... slayerItems) {
        this.name = name;
        this.monsterMechanic = monsterMechanic;
        this.slayerItems = slayerItems;
        this.poisonous = poisonous;
    }

    public boolean isPoisonous() {
        return poisonous;
    }

    public boolean hasMechanic() {
        return monsterMechanic != null;
    }

    public MonsterMechanic getMonsterMechanic() {
        return monsterMechanic;
    }

    public String getName() {
        return name;
    }

    public SlayerItem[] getSlayerItems() {
        return slayerItems;
    }
}
