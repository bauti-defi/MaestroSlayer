package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.utils.antiban.AntibanFrequency;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SlayerSettings implements Serializable {

    private String slayerMaster, food, antipoisonOption;
    private int minimumLootPrice, baseEatPercentage, maxEatPercentage;
    private boolean eatToLoot, drinkPotion, useCannon;
    private List<String> potions, tasksToSkip;
    private AntibanFrequency antibanFrequency;

    public SlayerSettings() {
        this.potions = new ArrayList<>();
        this.tasksToSkip = new ArrayList<>();
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

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getAntipoisonOption() {
        return antipoisonOption;
    }

    public void setAntipoisonOption(String antipoisonOption) {
        this.antipoisonOption = antipoisonOption;
    }

    public int getMinimumLootPrice() {
        return minimumLootPrice;
    }

    public void setMinimumLootPrice(int minimumLootPrice) {
        this.minimumLootPrice = minimumLootPrice;
    }

    public int getBaseEatPercentage() {
        return baseEatPercentage;
    }

    public void setBaseEatPercentage(int baseEatPercentage) {
        this.baseEatPercentage = baseEatPercentage;
    }

    public int getMaxEatPercentage() {
        return maxEatPercentage;
    }

    public void setMaxEatPercentage(int maxEatPercentage) {
        this.maxEatPercentage = maxEatPercentage;
    }

    public boolean isEatToLoot() {
        return eatToLoot;
    }

    public void setEatToLoot(boolean eatToLoot) {
        this.eatToLoot = eatToLoot;
    }

    public boolean isDrinkPotion() {
        return drinkPotion;
    }

    public void setDrinkPotion(boolean drinkPotion) {
        this.drinkPotion = drinkPotion;
    }

    public boolean isUseCannon() {
        return useCannon;
    }

    public void setUseCannon(boolean useCannon) {
        this.useCannon = useCannon;
    }

    public List<String> getPotions() {
        return potions;
    }

    public void addPotion(String potions) {
        this.potions.add(potions);
    }
}
