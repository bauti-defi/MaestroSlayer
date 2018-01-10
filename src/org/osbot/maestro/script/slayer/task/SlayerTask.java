package org.osbot.maestro.script.slayer.task;

import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.task.monster.Monster;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanic;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerWornItem;
import org.osbot.maestro.script.slayer.utils.templates.SlayerTaskTemplate;
import org.osbot.rs07.script.MethodProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SlayerTask {

    private final String name;
    private final List<Monster> monsters;
    private final MonsterMechanic monsterMechanic;
    private final List<SlayerInventoryItem> slayerInventoryItems;
    private final List<SlayerWornItem> slayerWornItems;
    private Monster currentMonster;
    private int killsLeft = 0;

    private SlayerTask(String name, List<Monster> monsters, MonsterMechanic monsterMechanic, List<SlayerInventoryItem> slayerInventoryItems,
                       List<SlayerWornItem> slayerWornItems) {
        this.name = name;
        this.monsters = monsters;
        this.monsterMechanic = monsterMechanic;
        this.slayerInventoryItems = slayerInventoryItems;
        this.slayerWornItems = slayerWornItems;
    }

    public String getName() {
        return name;
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public MonsterMechanic getMonsterMechanic() {
        return monsterMechanic;
    }

    public boolean hasMechanic() {
        return monsterMechanic != null;
    }

    public Monster getNewMonster(Comparator<Monster> comparator, int killsLeft) {
        this.killsLeft = killsLeft;
        return currentMonster = monsters.stream().sorted(comparator).findFirst().orElse(null);
    }

    public Monster getCurrentMonster() {
        return currentMonster;
    }

    public int getKillsLeft() {
        return killsLeft;
    }

    public boolean isFinished() {
        return killsLeft <= 0;
    }

    public void forceFinish() {
        killsLeft = 0;
    }

    public List<SlayerItem> getAllSlayerItems() {
        List<SlayerItem> slayerItems = new ArrayList<>();
        slayerItems.addAll(getAllSlayerInventoryItems());
        slayerItems.addAll(getAllSlayerWornItems());
        return slayerItems;
    }

    public List<SlayerInventoryItem> getAllSlayerInventoryItems() {
        List<SlayerInventoryItem> slayerInventoryItems = new ArrayList<>();
        slayerInventoryItems.addAll(getBaseSlayerInventoryItems());
        slayerInventoryItems.addAll(currentMonster.getMonsterUniqueInventoryItems());
        return slayerInventoryItems;
    }

    public List<SlayerWornItem> getAllSlayerWornItems() {
        List<SlayerWornItem> slayerWornItems = new ArrayList<>();
        slayerWornItems.addAll(getBaseSlayerWornItems());
        slayerWornItems.addAll(currentMonster.getMonsterUniqueRequiredWornItems());
        return slayerWornItems;
    }

    public List<SlayerInventoryItem> getBaseSlayerInventoryItems() {
        return slayerInventoryItems;
    }

    public List<SlayerWornItem> getBaseSlayerWornItems() {
        return slayerWornItems;
    }

    public boolean haveAllRequiredItems(MethodProvider provider) {
        return haveRequiredWornItems(provider) && haveRequiredInventoryItems(provider);
    }

    public boolean haveRequiredInventoryItems(MethodProvider provider) {
        for (SlayerInventoryItem item : getAllSlayerInventoryItems()) {
            if (!item.haveItem(provider)) {
                return false;
            }
        }
        return true;
    }

    public boolean haveRequiredWornItems(MethodProvider provider) {
        for (SlayerWornItem item : getAllSlayerWornItems()) {
            if (!item.haveItem(provider)) {
                return false;
            }
        }
        return true;
    }

    public static void setCurrentTask(String message) {
        String monsterName;
        int amount = 0;
        if (message.contains("new task")) {
            amount = Integer.parseInt(message.split("kill ")[1].split(" ")[0]);
            monsterName = message.split(String.valueOf(amount) + " ")[1].replace(".", "");
        } else {
            monsterName = message.split("assigned to kill ")[1].split(";")[0];
            amount = Integer.parseInt(message.split("only ")[1].split(" more")[0]);
        }
        for (SlayerTask task : RuntimeVariables.slayerContainer.getTasks()) {
            if (task.getName().equalsIgnoreCase(monsterName)) {
                RuntimeVariables.currentTask = task;
                RuntimeVariables.currentMonster = task.getNewMonster(new Comparator<Monster>() {
                    @Override
                    public int compare(Monster o1, Monster o2) {
                        return o2.getCombatLevel() - o1.getCombatLevel();
                    }
                }, amount);
                return;
            }
        }
        RuntimeVariables.currentTask = null;
        RuntimeVariables.currentMonster = null;
    }

    public static SlayerTask wrap(SlayerTaskTemplate template) {
        return new SlayerTask(template.getName(), template.getMonsters(), template.getMonsterMechanic(), template.getSlayerInventoryItems(),
                template.getSlayerWornItems());
    }

}
