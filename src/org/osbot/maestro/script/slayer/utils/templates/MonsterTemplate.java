package org.osbot.maestro.script.slayer.utils.templates;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.slayer.utils.position.SlayerArea;
import org.osbot.maestro.script.slayer.utils.position.SlayerPosition;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItemException;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerWornItem;

import java.util.ArrayList;
import java.util.List;

public class MonsterTemplate {


    private String name;
    private SlayerArea area;
    private int combatLevel;
    private int wildernessLevel;
    private int npcCount;
    private int id;
    private boolean poisonous;
    private boolean cantMelee;
    private boolean multicombat;
    private SlayerPosition safeSpot;
    private SlayerPosition cannonPosition;
    private List<SlayerInventoryItem> requiredInventoryItems;
    private List<SlayerWornItem> requiredWornItems;

    public MonsterTemplate() {
        this.requiredWornItems = new ArrayList<>();
        this.requiredInventoryItems = new ArrayList<>();
    }

    public static MonsterTemplate wrap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        MonsterTemplate monsterTemplate = new MonsterTemplate();
        monsterTemplate.setName((String) jsonObject.get(Config.NAME));
        monsterTemplate.setArea(SlayerArea.wrap((JSONObject) jsonObject.get(Config.AREA)));
        monsterTemplate.setCombatLevel(((Long) jsonObject.get(Config.COMBAT_LEVEL)).intValue());
        monsterTemplate.setWildernessLevel(((Long) jsonObject.get(Config.WILDERNESS_LEVEL)).intValue());
        monsterTemplate.setNpcCount(((Long) jsonObject.get(Config.NPC_COUNT)).intValue());
        monsterTemplate.setId(((Long) jsonObject.get(Config.ID)).intValue());
        monsterTemplate.setPoisonous((boolean) jsonObject.get(Config.POISONOUS));
        monsterTemplate.setCantMelee((boolean) jsonObject.get(Config.CANT_MELEE));
        monsterTemplate.setMulticombat((boolean) jsonObject.get(Config.MULTICOMBAT));
        monsterTemplate.setSafeSpot(SlayerPosition.wrap((JSONObject) jsonObject.getOrDefault(Config.SAFESPOT, null)));
        monsterTemplate.setCannonPosition(SlayerPosition.wrap((JSONObject) jsonObject.getOrDefault(Config.CANNONSPOT, null)));

        if (jsonObject.containsKey(Config.REQUIRED_INVENTORY_ITEMS)) {
            JSONArray invyItems = (JSONArray) jsonObject.get(Config.REQUIRED_INVENTORY_ITEMS);
            for (int i = 0; i < invyItems.size(); i++) {
                try {
                    monsterTemplate.addRequiredSlayerInventoryItem(Config.itemMapper.get(InventoryItemTemplate.wrap((JSONObject) invyItems.get
                            (i))));
                } catch (SlayerItemException e) {
                    e.printStackTrace();
                }
            }
        }

        if (jsonObject.containsKey(Config.REQUIRED_WORN_ITEMS)) {
            JSONArray wornItems = (JSONArray) jsonObject.get(Config.REQUIRED_WORN_ITEMS);
            for (int i = 0; i < wornItems.size(); i++) {
                try {
                    monsterTemplate.addRequiredSlayerWornItem(Config.itemMapper.get(WornItemTemplate.wrap((JSONObject) wornItems.get(i))));
                } catch (SlayerItemException e) {
                    e.printStackTrace();
                }
            }
        }

        return monsterTemplate;
    }

    public SlayerArea getArea() {
        return area;
    }

    private void setArea(final SlayerArea area) {
        this.area = area;
    }

    public SlayerPosition getSafeSpot() {
        return safeSpot;
    }

    private void setSafeSpot(final SlayerPosition safeSpot) {
        this.safeSpot = safeSpot;
    }

    public SlayerPosition getCannonPosition() {
        return cannonPosition;
    }

    private void setCannonPosition(final SlayerPosition cannonPosition) {
        this.cannonPosition = cannonPosition;
    }

    public List<SlayerInventoryItem> getRequiredInventoryItems() {
        return requiredInventoryItems;
    }


    public List<SlayerWornItem> getRequiredWornItems() {
        return requiredWornItems;
    }

    private void addRequiredSlayerWornItem(final SlayerWornItem wornItem) {
        this.requiredWornItems.add(wornItem);
    }

    private void addRequiredSlayerInventoryItem(final SlayerInventoryItem inventoryItem) {
        this.requiredInventoryItems.add(inventoryItem);
    }

    public String getName() {
        return name;
    }

    private void setName(final String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    private void setId(final int id) {
        this.id = id;
    }

    public boolean isPoisonous() {
        return poisonous;
    }

    private void setPoisonous(final boolean poisonous) {
        this.poisonous = poisonous;
    }

    public boolean isCantMelee() {
        return cantMelee;
    }

    private void setCantMelee(final boolean cantMelee) {
        this.cantMelee = cantMelee;
    }

    public boolean isMulticombat() {
        return multicombat;
    }

    private void setMulticombat(final boolean multicombat) {
        this.multicombat = multicombat;
    }

    public int getCombatLevel() {
        return combatLevel;
    }

    private void setCombatLevel(final int combatLevel) {
        this.combatLevel = combatLevel;
    }

    public int getNpcCount() {
        return npcCount;
    }

    private void setNpcCount(final int npcCount) {
        this.npcCount = npcCount;
    }

    public int getWildernessLevel() {
        return wildernessLevel;
    }

    private void setWildernessLevel(final int wildernessLevel) {
        this.wildernessLevel = wildernessLevel;
    }

    @Override
    public String toString() {
        return "[" + combatLevel + "]" + name;
    }
}
