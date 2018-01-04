package org.osbot.maestro.script.slayer.task;

import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItem;

public enum Monster {

    //TODO: ALL RELEKA TASKS NEED ANTI UNTIL 46 CB

    ROCKSLUGS("Rockslug", MonsterMechanic.ROCK_SLUG_MECHANIC, SlayerInventoryItem.BAG_OF_SALT, SlayerVariables.antipoisonChoice),
    CAVE_CRAWLERS("Cave crawler", null, SlayerVariables.antipoisonChoice),
    OGRES("Ogre", null),
    KALPHITES("Kalphite", null),
    HILL_GIANTS("Hill Giant", null),
    MOSS_GIANTS("Moss giant", null),
    CAVE_SLIMES("Cave slime", null),
    EARTH_WARRIORS("Earth warrior", null),
    HOBGOBLINS("Hobgoblin", null),
    OTHERWORDLY_BEINGS("Otherworldy being", null),
    PYREFIENDS("Pyrefiend", null);

    private final String name;
    private final MonsterMechanic monsterMechanic;
    private final SlayerItem[] slayerItems;

    Monster(String name, MonsterMechanic monsterMechanic, SlayerItem... slayerItems) {
        this.name = name;
        this.monsterMechanic = monsterMechanic;
        this.slayerItems = slayerItems;
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
