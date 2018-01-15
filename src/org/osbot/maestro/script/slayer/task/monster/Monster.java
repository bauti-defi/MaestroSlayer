package org.osbot.maestro.script.slayer.task.monster;

import org.osbot.maestro.script.slayer.utils.position.SlayerArea;
import org.osbot.maestro.script.slayer.utils.position.SlayerPosition;
import org.osbot.maestro.script.slayer.utils.slayeritem.InventoryTaskItem;
import org.osbot.maestro.script.slayer.utils.slayeritem.WornTaskItem;
import org.osbot.maestro.script.slayer.utils.templates.MonsterTemplate;

import java.util.List;

public class Monster {

    private final String name;
    private final SlayerArea area;
    private final int combatLevel;
    private final int wildernessLevel;
    private int npcCount;
    private final int id;
    private final boolean poisonous;
    private final boolean cantMelee;
    private final boolean multicombat;
    private final SlayerPosition safeSpot;
    private final SlayerPosition cannonPosition;
    private final List<InventoryTaskItem> requiredInventoryItems;
    private final List<WornTaskItem> requiredWornItems;

    private Monster(String name, SlayerArea area, int combatLevel, int wildernessLevel, int npcCount, int id, boolean poisonous, boolean
            cantMelee, boolean multicombat, SlayerPosition safeSpot, SlayerPosition cannonPosition, List<InventoryTaskItem> requiredInventoryItems,
                    List<WornTaskItem> requiredWornItems) {
        this.name = name;
        this.area = area;
        this.combatLevel = combatLevel;
        this.wildernessLevel = wildernessLevel;
        this.npcCount = npcCount;
        this.id = id;
        this.poisonous = poisonous;
        this.cantMelee = cantMelee;
        this.multicombat = multicombat;
        this.safeSpot = safeSpot;
        this.cannonPosition = cannonPosition;
        this.requiredInventoryItems = requiredInventoryItems;
        this.requiredWornItems = requiredWornItems;
    }

    public boolean canCannon() {
        return cannonPosition != null;
    }

    public boolean isPoisonous() {
        return poisonous;
    }

    public String getName() {
        return name;
    }

    public SlayerArea getArea() {
        return area;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    public int getWildernessLevel() {
        return wildernessLevel;
    }

    public boolean isInWilderness() {
        return wildernessLevel > 0;
    }

    public int getNpcCount() {
        return npcCount;
    }

    public int getId() {
        return id;
    }

    public boolean isCantMelee() {
        return cantMelee;
    }

    public boolean isMulticombat() {
        return multicombat;
    }

    public SlayerPosition getSafeSpot() {
        return safeSpot;
    }

    public SlayerPosition getCannonPosition() {
        return cannonPosition;
    }

    public List<InventoryTaskItem> getMonsterUniqueInventoryItems() {
        return requiredInventoryItems;
    }

    public List<WornTaskItem> getMonsterUniqueRequiredWornItems() {
        return requiredWornItems;
    }

    public static Monster wrap(MonsterTemplate template) {
        return new Monster(template.getName(), template.getArea(), template.getCombatLevel(), template.getWildernessLevel(), template
                .getNpcCount(), template.getId(), template.isPoisonous(), template.isCantMelee(), template.isMulticombat(), template
                .getSafeSpot(), template.getCannonPosition(), template.getRequiredInventoryItems(), template.getRequiredWornItems());
    }
}
