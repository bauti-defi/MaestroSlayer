package org.osbot.maestro.script.slayer.task;

import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerWornItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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
    PYREFIENDS("Pyrefiend", false, null, SlayerVariables.antipoisonChoice),
    DESERT_LIZARDS("Desert lizard", false, null, SlayerInventoryItem.ICE_COOLER, SlayerInventoryItem.WATERSKIN, SlayerWornItem
            .DESERT_BOOTS, SlayerWornItem.DESERT_ROBE, SlayerWornItem.DESERT_SHIRT),
    BANSHEES("Banshee", false, null, SlayerWornItem.EARMUFFS),
    BATS("Bat", false, null),
    BIRDS("Bird", false, null),
    BEARS("Bear", false, null),
    CAVE_BUGS("Cave bug", false, null, SlayerVariables.antipoisonChoice),
    COWS("Cow", false, null),
    CRAWLING_HANDS("Crawling hand", false, null),
    DOGS("Dog", false, null),
    DWARVES("Dwarf", false, null),
    GHOSTS("Ghost", false, null),
    GOBLINS("Goblin", false, null),
    ICEFIENDS("Icefiend", false, null),
    MINOTAURS("Minotaur", false, null),
    MONKEYS("Monkey", false, null),
    RATS("Rat", false, null),
    SCORPIONS("Scorpion", false, null),
    SKELETONS("Skeleton", false, null),
    SPIDERS("Spider", true, null),
    WOLVES("Wolf", false, null),
    ZOMBIES("Zombie", false, null);

    private final String name;
    private final boolean poisonous;
    private final MonsterMechanic monsterMechanic;
    private final List<SlayerItem> slayerItems;
    private final List<SlayerInventoryItem> inventoryItems;
    private final List<SlayerWornItem> wornItems;

    Monster(String name, boolean poisonous, MonsterMechanic monsterMechanic, SlayerItem... slayerItems) {
        this.name = name;
        this.monsterMechanic = monsterMechanic;
        this.poisonous = poisonous;
        this.slayerItems = Arrays.asList(slayerItems);
        this.inventoryItems = new ArrayList<>();
        this.wornItems = new ArrayList<>();
        this.slayerItems.forEach(new Consumer<SlayerItem>() {
            @Override
            public void accept(SlayerItem slayerItem) {
                if (slayerItem instanceof SlayerWornItem) {
                    wornItems.add((SlayerWornItem) slayerItem);
                } else {
                    inventoryItems.add((SlayerInventoryItem) slayerItem);
                }
            }
        });
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

    public List<SlayerItem> getSlayerItems() {
        return slayerItems;
    }

    public List<SlayerWornItem> getSlayerWornItems() {
        return wornItems;
    }

    public List<SlayerInventoryItem> getSlayerInventoryItems() {
        return inventoryItems;
    }

    public boolean requiresWornItems() {
        return wornItems.size() > 0;
    }

    public boolean requiresInventoryItems() {
        return inventoryItems.size() > 0;
    }
}
