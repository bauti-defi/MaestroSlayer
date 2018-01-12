package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.utils.antiban.AntibanFrequency;
import org.osbot.maestro.script.slayer.utils.consumable.Food;
import org.osbot.maestro.script.slayer.utils.consumable.Potion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SlayerSettings implements Serializable {

    private String slayerMaster;
    private int minimumLootPrice;
    private boolean eatToLoot, useCannon, debug, useAntidote;
    private List<String> tasksToSkip;
    private List<Potion> potions;
    private Food food;
    private AntibanFrequency antibanFrequency;

    public SlayerSettings() {
        this.potions = new ArrayList<>();
        this.tasksToSkip = new ArrayList<>();
        food = new Food(Foods.MONKFISH.getName(), 22, 30, 50);
        potions.add(new Potion(Potions.SUPER_ATTACK, 1, 0));
        debug = true;
        useCannon = false;
        minimumLootPrice = 3000;
        eatToLoot = false;
        slayerMaster = "Vannaka";
        useAntidote = true;
        antibanFrequency = AntibanFrequency.HIGH;
    }


    public boolean isUseAntidote() {
        return useAntidote;
    }

    public void setUseAntidote(boolean useAntidote) {
        this.useAntidote = useAntidote;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public AntibanFrequency getAntibanFrequency() {
        return antibanFrequency;
    }

    public void setAntibanFrequency(AntibanFrequency antibanFrequency) {
        this.antibanFrequency = antibanFrequency;
    }

    public List<String> getTasksToSkip() {
        return tasksToSkip;
    }

    public void addTaskToSkip(String tasksToSkip) {
        this.tasksToSkip.add(tasksToSkip);
    }

    public void removeTaskToSkip(String task) {
        this.tasksToSkip.remove(task);
    }

    public String getSlayerMaster() {
        return slayerMaster;
    }

    public void setSlayerMaster(String slayerMaster) {
        this.slayerMaster = slayerMaster;
    }

    public int getMinimumLootPrice() {
        return minimumLootPrice;
    }

    public void setMinimumLootPrice(int minimumLootPrice) {
        this.minimumLootPrice = minimumLootPrice;
    }


    public boolean isEatToLoot() {
        return eatToLoot;
    }

    public void setEatToLoot(boolean eatToLoot) {
        this.eatToLoot = eatToLoot;
    }

    public boolean isUseCannon() {
        return useCannon;
    }

    public void setUseCannon(boolean useCannon) {
        this.useCannon = useCannon;
    }

    public List<Potion> getPotions() {
        return potions;
    }

    public void setPotions(List<Potion> potions) {
        this.potions = potions;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }
}
