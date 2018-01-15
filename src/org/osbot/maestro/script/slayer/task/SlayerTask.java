package org.osbot.maestro.script.slayer.task;

import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.task.monster.Monster;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanic;
import org.osbot.maestro.script.slayer.utils.slayeritem.InventoryTaskItem;
import org.osbot.maestro.script.slayer.utils.slayeritem.SlayerItem;
import org.osbot.maestro.script.slayer.utils.slayeritem.WornTaskItem;
import org.osbot.maestro.script.slayer.utils.templates.SlayerTaskTemplate;
import org.osbot.rs07.script.MethodProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SlayerTask {

    private final String name;
    private final List<Monster> monsters;
    private final MonsterMechanic monsterMechanic;
    private final List<InventoryTaskItem> slayerInventoryItems;
    private final List<WornTaskItem> slayerWornItems;
    private Monster currentMonster;
    private int killsLeft = 0;

    private SlayerTask(String name, List<Monster> monsters, MonsterMechanic monsterMechanic, List<InventoryTaskItem> slayerInventoryItems,
                       List<WornTaskItem> slayerWornItems) {
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

    public int getKillsLeft() {
        return killsLeft;
    }

    public void setKillsLeft(int killsLeft) {
        this.killsLeft = killsLeft;
    }

    public void registerKill() {
        if (killsLeft == 0) {
            return;
        }
        this.killsLeft--;
    }

    public Monster getCurrentMonster() {
        return currentMonster;
    }

    private void setNewMonster(Comparator<Monster> comparator, int amount) {
        this.killsLeft = amount;
        this.currentMonster = monsters.stream().sorted(comparator).findFirst().orElse(null);
    }

    public boolean isFinished() {
        return killsLeft <= 0;
    }

    public void forceFinish() {
        killsLeft = 0;
    }

    public List<SlayerItem> getAllSlayerItems() {
        List<SlayerItem> slayerItems = new ArrayList<>();
        slayerItems.addAll(getAllInventoryItems());
        slayerItems.addAll(getAllWornItems());
        return slayerItems;
    }

    public List<InventoryTaskItem> getAllInventoryItems() {
        List<InventoryTaskItem> slayerInventoryItems = new ArrayList<>();
        slayerInventoryItems.addAll(getBaseInventoryItems());
        slayerInventoryItems.addAll(currentMonster.getMonsterUniqueInventoryItems());
        return slayerInventoryItems;
    }

    public List<WornTaskItem> getAllWornItems() {
        List<WornTaskItem> slayerWornItems = new ArrayList<>();
        slayerWornItems.addAll(getBaseWornItems());
        slayerWornItems.addAll(currentMonster.getMonsterUniqueWornItems());
        return slayerWornItems;
    }

    public List<InventoryTaskItem> getBaseInventoryItems() {
        return slayerInventoryItems;
    }

    public List<WornTaskItem> getBaseWornItems() {
        return slayerWornItems;
    }

    public boolean haveAllRequiredItems(MethodProvider provider) {
        return haveRequiredWornItems(provider) && haveRequiredInventoryItems(provider);
    }

    public boolean haveRequiredInventoryItems(MethodProvider provider) {
        for (InventoryTaskItem item : getAllInventoryItems()) {
            if (!item.hasInInventory(provider)) {
                return false;
            }
        }
        return true;
    }

    public boolean haveRequiredWornItems(MethodProvider provider) {
        for (WornTaskItem item : getAllWornItems()) {
            if (!item.isWearing(provider) && !item.hasInInventory(provider)) {
                return false;
            }
        }
        return true;
    }

    public static void setCurrentTask(String message) {
        String monsterName;
        int amount = 0;
        if (message.contains("slayer task")) {
            amount = Integer.parseInt(message.split("kill ")[1].split(" ")[0]);
            monsterName = message.split(String.valueOf(amount) + " ")[1].replace(".", "");
        } else {
            monsterName = message.split("assigned to kill ")[1].split(";")[0];
            amount = Integer.parseInt(message.split("only ")[1].split(" more")[0]);
        }
        if (RuntimeVariables.currentTask != null && !RuntimeVariables.currentTask.isFinished()) {
            RuntimeVariables.currentTask.setKillsLeft(amount);
            return;
        }
        for (SlayerTask task : RuntimeVariables.slayerContainer.getTasks()) {
            if (task.getName().equalsIgnoreCase(monsterName)) {
                RuntimeVariables.currentTask = task;
                task.setNewMonster(new Comparator<Monster>() {
                    @Override
                    public int compare(Monster o1, Monster o2) {
                        return o1.getCombatLevel() - o2.getCombatLevel();
                    }
                }, amount);
                return;
            }
        }
        RuntimeVariables.currentTask = null;
    }

    public static SlayerTask wrap(SlayerTaskTemplate template) {
        return new SlayerTask(template.getName(), template.getMonsters(), template.getMonsterMechanic(), template.getSlayerInventoryItems(),
                template.getSlayerWornItems());
    }

    @Override
    public String toString() {
        return getName();
    }
}
