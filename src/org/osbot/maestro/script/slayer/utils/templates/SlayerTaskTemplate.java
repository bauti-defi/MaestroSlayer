package org.osbot.maestro.script.slayer.utils.templates;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.slayer.task.monster.Monster;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanic;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanicException;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItemException;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerWornItem;

import java.util.ArrayList;
import java.util.List;

public class SlayerTaskTemplate {

    private String name;
    private final List<Monster> monsters;
    private MonsterMechanic monsterMechanic;
    private final List<SlayerInventoryItem> slayerInventoryItems;
    private final List<SlayerWornItem> slayerWornItems;

    public SlayerTaskTemplate() {
        monsters = new ArrayList<>();
        slayerInventoryItems = new ArrayList<>();
        slayerWornItems = new ArrayList<>();
    }

    public static SlayerTaskTemplate wrap(JSONObject jsonObject) {
        SlayerTaskTemplate task = new SlayerTaskTemplate();
        task.setName((String) jsonObject.get(Config.NAME));

        JSONObject monsterMechanic = (JSONObject) jsonObject.getOrDefault(Config.MONSTER_MECHANIC, null);
        try {
            task.setMonsterMechanic(Config.mechanicMapper.get(MonsterMechanicTemplate.wrap(monsterMechanic)));
        } catch (MonsterMechanicException e) {
            e.printStackTrace();
        }

        if (jsonObject.containsKey(Config.MONSTERS)) {
            JSONArray monsters = (JSONArray) jsonObject.get(Config.MONSTERS);
            for (int i = 0; i < monsters.size(); i++) {
                task.addMonster(Monster.wrap(MonsterTemplate.wrap((JSONObject) monsters.get(i))));
            }
        }


        if (jsonObject.containsKey(Config.REQUIRED_INVENTORY_ITEMS)) {
            JSONArray invyItems = (JSONArray) jsonObject.get(Config.REQUIRED_INVENTORY_ITEMS);
            for (int i = 0; i < invyItems.size(); i++) {
                try {
                    task.addRequiredSlayerInventoryItem(Config.itemMapper.get(InventoryItemTemplate.wrap((JSONObject) invyItems.get(i))));
                } catch (SlayerItemException e) {
                    e.printStackTrace();
                }
            }
        }

        if (jsonObject.containsKey(Config.REQUIRED_WORN_ITEMS)) {
            JSONArray wornItems = (JSONArray) jsonObject.get(Config.REQUIRED_WORN_ITEMS);
            for (int i = 0; i < wornItems.size(); i++) {
                try {
                    task.addRequiredSlayerWornItem(Config.itemMapper.get(WornItemTemplate.wrap((JSONObject) wornItems.get(i))));
                } catch (SlayerItemException e) {
                    e.printStackTrace();
                }
            }
        }

        return task;
    }

    public String getName() {
        return name;
    }

    private void setName(final String name) {
        this.name = name;
    }

    private void setMonsterMechanic(MonsterMechanic monsterMechanic) {
        this.monsterMechanic = monsterMechanic;
    }

    public List<SlayerInventoryItem> getSlayerInventoryItems() {
        return slayerInventoryItems;
    }

    private void addRequiredSlayerInventoryItem(final SlayerInventoryItem inventoryItem) {
        this.slayerInventoryItems.add(inventoryItem);
    }

    private void addRequiredSlayerWornItem(final SlayerWornItem wornItem) {
        this.slayerWornItems.add(wornItem);
    }

    private void addMonster(Monster monster) {
        monsters.add(monster);
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public MonsterMechanic getMonsterMechanic() {
        return monsterMechanic;
    }

    public List<SlayerWornItem> getSlayerWornItems() {
        return slayerWornItems;
    }

    @Override
    public String toString() {
        return name;
    }
}
