package org.osbot.maestro.script.slayer.task;

import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.utils.requireditem.RequiredInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.RequiredItem;

public enum Monster {


    ROCKSLUGS("Rockslug", MonsterMechanic.ROCK_SLUG_MECHANIC, RequiredInventoryItem.BAG_OF_SALT),
    CAVE_CRAWLERS("Cave crawler", null, SlayerVariables.antidote ? RequiredInventoryItem.ANTIDOTE : RequiredInventoryItem.ANTIPOISON),
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
    private final RequiredItem[] requiredItems;

    Monster(String name, MonsterMechanic monsterMechanic, RequiredItem... requiredItems) {
        this.name = name;
        this.monsterMechanic = monsterMechanic;
        this.requiredItems = requiredItems;
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

    public RequiredItem[] getRequiredItems() {
        return requiredItems;
    }
}
