package org.osbot.maestro.script;

import org.osbot.maestro.framework.NodeScript;
import org.osbot.maestro.script.nodetasks.*;
import org.osbot.maestro.script.slayer.data.Constants;
import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.task.Monster;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.maestro.script.slayer.utils.consumable.Food;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.script.ScriptManifest;

@ScriptManifest(author = "El Maestro", info = "Slays monsters.", name = "MaestroSlayer", version = 0.1, logo = "")
public class MaestroSlayer extends NodeScript {

    public MaestroSlayer() {
        super();
        if (SlayerVariables.eating) {
            addTask(new FoodHandler(new Food("Monkfish"), 35));
        }
        addTask(new PotionHandler.Builder().addPotion(SlayerVariables.antidote ? "Antidote" : "poison").build());
        addTask(new CombatHandler());
        addTask(new TaskValidator());
        addTask(new TargetFinder());
        addTask(new CannonHandler());
        addTask(new MonsterMechanicHandler());
    }

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        log("MaestroSlayer started.");
        log("Report any bugs to El Maestro.");
        setCombatStyle();
    }

    @Override
    public void onMessage(final Message message) throws InterruptedException {
        switch (message.getType()) {
            case GAME:
                if (message.getMessage().toLowerCase().contains("your cannon has broken")) {
                    //TODO:: cannon broke
                } else if (message.getMessage().toLowerCase().contains("your cannon is out of ammo")) {
                    //TODO:: cannon out of ammo
                } else if (message.getMessage().toLowerCase().contains("assigned to kill")) {
                    String monsterName = message.getMessage().split("kill ")[1].split(";")[0];
                    int amount = Integer.parseInt(message.getMessage().split("only ")[1].split(" more")[0]);
                    for (Monster monster : Monster.values()) {
                        if (monsterName.toLowerCase().contains(monster.getName().toLowerCase())) {
                            SlayerVariables.currentTask = new SlayerTask(monster, amount);
                            log("Current task: " + SlayerVariables.currentTask.getMonster().getName());
                            break;
                        }
                    }
                } else if (message.getMessage().toLowerCase().contains("you've completed")) {
                    log("Task complete.");
                    stop(true);
                }
                break;
        }
    }

    @Override
    public void onExit() throws InterruptedException {
        log("MaestroSlayer stopped.");
        log("Enjoy the gains!");
    }

    private void setCombatStyle() {
        switch (getConfigs().get(Constants.COMBAT_STYLE_ID)) {
            case 0:
                SlayerVariables.combatStyle = CombatStyle.ACCURATE;
                break;
            case 1:
                SlayerVariables.combatStyle = CombatStyle.AGGRESSIVE;
                break;
            case 2:
                SlayerVariables.combatStyle = CombatStyle.CONTROLLED;
                break;
            case 3:
                SlayerVariables.combatStyle = CombatStyle.BLOCK;
                break;
        }
        log("Combat style: " + SlayerVariables.combatStyle.getName());
    }
}
