package org.osbot.maestro.script;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.NodeScript;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.nodetasks.*;
import org.osbot.maestro.script.slayer.SlayerMaster;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.task.monster.Monster;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.maestro.script.slayer.utils.SlayerContainer;
import org.osbot.maestro.script.slayer.utils.antiban.AntibanFrequency;
import org.osbot.maestro.script.slayer.utils.consumable.Food;
import org.osbot.maestro.script.util.directory.Directory;
import org.osbot.maestro.script.util.directory.exceptions.InvalidFileNameException;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Predicate;

@ScriptManifest(author = "El Maestro", info = "Slays monsters.", name = "MaestroSlayer", version = 1.0, logo = "")
public class MaestroSlayer extends NodeScript {

    private final Color color1 = new Color(204, 204, 204);
    private final Color color2 = new Color(0, 0, 0);
    private final Font font1 = new Font("Arial", 0, 12);
    private final Font font2 = new Font("Arial", 1, 18);
    private final Font font3 = new Font("Arial", 0, 10);
    private final long startTime;
    private NPC targetToPaint;

    public MaestroSlayer() {
        super();
        this.startTime = System.currentTimeMillis();
        addTask(new BankHandler());
        addTask(new EquipmentHandler());
        addTask(new CombatHandler());
        addTask(new TaskValidator());
        addTask(new TargetFinder());
        addTask(new MonsterMechanicHandler());
        addTask(new AntibanHandler(AntibanFrequency.HIGH));
        addTask(new TaskGetter());
        addTask(new FoodHandler(new Food("Monkfish", 28, RuntimeVariables.minHpPercentToEat, RuntimeVariables.maxHpPercentToEat)));
    }

    @Override
    public void onStart() throws InterruptedException {
        log("MaestroSlayer initializing...");
        RuntimeVariables.saveDirectory = new Directory(getDirectoryData() + "MaestroSlayer");
        if (!RuntimeVariables.saveDirectory.exists()) {
            try {
                if (RuntimeVariables.saveDirectory.create()) {
                    log("Script directory created at: " + RuntimeVariables.saveDirectory.getPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadSlayerDataLocal();
        //TODO:START GUI
        for (SlayerMaster master : RuntimeVariables.slayerContainer.getMasters()) {
            if (master.getName().equalsIgnoreCase("vannaka")) {
                log("Slayer master set to: " + master.getName());
                RuntimeVariables.currentMaster = master;
                break;
            }
        }
        log("Initializing trackers...");
        RuntimeVariables.experienceTracker = new ExperienceTracker();
        RuntimeVariables.experienceTracker.exchangeContext(getBot());
        RuntimeVariables.experienceTracker.start(Skill.SLAYER);
        RuntimeVariables.experienceTracker.start(Skill.ATTACK);
        RuntimeVariables.experienceTracker.start(Skill.STRENGTH);
        RuntimeVariables.experienceTracker.start(Skill.HITPOINTS);
        RuntimeVariables.experienceTracker.start(Skill.RANGED);
        RuntimeVariables.experienceTracker.start(Skill.MAGIC);
        RuntimeVariables.experienceTracker.start(Skill.DEFENCE);
        if (RuntimeVariables.drinkPotions) {
            log("Adding potion support");
            addTask(new PotionHandler.Builder().addPotion("Super attack", 1, Skill.ATTACK, 0, false).addPotion(RuntimeVariables
                    .antipoisonType, 1, true).build());
        }
        if (RuntimeVariables.cannon) {
            log("Adding cannon support");
            addTask(new CannonHandler());
        }
        RuntimeVariables.combatStyle = CombatStyle.getCurrentCombatStyle(this);
        log("Combat style set to: " + RuntimeVariables.combatStyle.getName());
        if (!RuntimeVariables.currentMaster.hasRequirements(this)) {
            log("You don't have the requirements for this slayer master, Stopping...");
            forceStopScript(true);
        }
        log("MaestroSlayer started!");
        log("Please report any bugs to El Maestro.");
        super.onStart();
    }

    @Override
    public void onMessage(final Message message) throws InterruptedException {
        outter:
        switch (message.getType()) {
            case GAME:
                if (message.getMessage().toLowerCase().contains("your cannon has broken")) {
                    sendBroadcast(new Broadcast("cannon-broken", true));
                } else if (message.getMessage().toLowerCase().contains("cannon is out of ammo")) {
                    sendBroadcast(new Broadcast("cannon-reload", true));
                } else if (message.getMessage().contains("you repair your cannon")) {
                    sendBroadcast(new Broadcast("cannon-broken", false));
                } else if (message.getMessage().contains("load the cannon with")) {
                    sendBroadcast(new Broadcast("cannon-reload", false));
                } else if (message.getMessage().contains("cannon already firing")) {
                    sendBroadcast(new Broadcast("cannon-reload", false));
                } else if (message.getMessage().contains("you pick up your cannon")) {
                    sendBroadcast(new Broadcast("cannon-set", false));
                } else if (message.getMessage().contains("there isn't enough space to set up here")) {
                    sendBroadcast(new Broadcast("cannon-error"));
                } else if (message.getMessage().toLowerCase().contains("assigned to " + "kill")) {
                    if (RuntimeVariables.currentTask == null || !RuntimeVariables.currentTask.isFinished()) {
                        String monsterName = message.getMessage().split("kill ")[1].split(";")[0];
                        int amount = Integer.parseInt(message.getMessage().split("only ")[1].split(" more")[0]);
                        for (SlayerTask task : RuntimeVariables.slayerContainer.getTasks()) {
                            if (task.getName().equalsIgnoreCase(monsterName)) {
                                RuntimeVariables.currentTask = task;
                                RuntimeVariables.currentMonster = task.getNewMonster(new Predicate<Monster>() {
                                    @Override
                                    public boolean test(Monster monster) {
                                        return monster.getCombatLevel() > 1;
                                    }
                                }, amount);
                                sendBroadcast(new Broadcast("requires-anti", RuntimeVariables.currentMonster.isPoisonous()));
                                log("Current task: " + task.getName());
                                break outter;
                            }
                        }
                        log("Task not supported.");
                        forceStopScript(true);
                    }
                } else if (message.getMessage().toLowerCase().contains("you've completed")) {
                    log("Task complete.");
                    RuntimeVariables.tasksFinished++;
                    RuntimeVariables.currentTask.forceFinish();
                } else if (message.getMessage().toLowerCase().contains("you need something new to hunt.")) {
                    if (RuntimeVariables.currentTask != null) {
                        RuntimeVariables.currentTask.forceFinish();
                        break;
                    }
                    sendBroadcast(new Broadcast("need-first-task", true));
                }
                break;
        }
    }

    @Override
    public void onExit() throws InterruptedException {
        log("MaestroSlayer stopped.");
        log("Enjoy the gains!");
    }

    @Override
    public void onPaint(Graphics2D g) {
        g.setColor(color1);
        g.fillRoundRect(7, 10, 178, 113, 16, 16);
        g.setFont(font1);
        g.setColor(color2);
        g.drawString("Time: " + getRuntimeFormat(System.currentTimeMillis() - startTime), 12, 54);
        g.setFont(font2);
        g.drawString("MaestroSlayer", 12, 26);
        g.setFont(font3);
        g.drawString("Version: " + getVersion(), 12, 36);
        g.setFont(font1);
        g.drawString("Kills left: ", 12, 86);
        g.drawString("Slayer Exp: " + formatNumber(RuntimeVariables.experienceTracker.getGainedXP(Skill.SLAYER)) + "(" + formatNumber
                        (RuntimeVariables.experienceTracker.getGainedXPPerHour(Skill.SLAYER)) + ")", 12,
                102);
        g.drawString("Current Task: " + ((RuntimeVariables.currentTask == null || RuntimeVariables.currentTask.isFinished())
                        ? "None" : RuntimeVariables.currentTask.getCurrentMonster().getName()),
                12, 118);
        g.drawString("Tasks: " + RuntimeVariables.tasksFinished, 12, 70);
        if (targetToPaint != null && targetToPaint.exists()) {
            g.setColor(Color.RED);
            g.drawPolygon(targetToPaint.getPosition().getPolygon(getBot()));
        }
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "new-target":
                targetToPaint = (NPC) broadcast.getMessage();
                break;
        }
    }

    private void downloadSlayerData() {
        log("Downloading latest version...");
        //logic
        log("Download finished.");
    }

    private void loadSlayerDataLocal() {
        log("Loading slayer data...");
        try {
            if (!RuntimeVariables.saveDirectory.getFile(Config.SLAYER_DATA_FILE_NAME).exists()) {
                warn("NO LOCAL SLAYER DATA FOUND!");
                downloadSlayerData();
                loadSlayerDataLocal();
            }
        } catch (InvalidFileNameException e) {
            e.printStackTrace();
        }
        JSONParser parser = new JSONParser();
        JSONObject slayerData = null;
        try (FileReader reader = new FileReader(RuntimeVariables.saveDirectory.getFile(Config.SLAYER_DATA_FILE_NAME))) {
            slayerData = (JSONObject) parser.parse(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidFileNameException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (slayerData == null) {
            warn("ERROR LOADING LOCAL SLAYER DATA!");
            forceStopScript(true);
        }
        RuntimeVariables.slayerContainer = SlayerContainer.wrap(slayerData);
        log("Slayer data loaded.");
    }
}
