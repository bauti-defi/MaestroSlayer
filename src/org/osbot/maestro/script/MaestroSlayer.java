package org.osbot.maestro.script;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.NodeScript;
import org.osbot.maestro.script.data.Cache;
import org.osbot.maestro.script.data.Config;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.nodetasks.*;
import org.osbot.maestro.script.slayer.SlayerMaster;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.maestro.script.ui.MainFrame;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
import org.osbot.rs07.api.util.ExperienceTracker;
import org.osbot.rs07.script.ScriptManifest;

import java.awt.*;

@ScriptManifest(author = "El Maestro", info = "Slays monsters.", name = "MaestroSlayer", version = 1.0, logo = "")
public class MaestroSlayer extends NodeScript {

    private final Color color1 = new Color(204, 204, 204);
    private final Color color2 = new Color(0, 0, 0);
    private final Font font1 = new Font("Arial", 0, 12);
    private final Font font2 = new Font("Arial", 1, 18);
    private final Font font3 = new Font("Arial", 0, 10);
    private final long startTime;
    private MainFrame mainFrame;
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
        addTask(new TaskGetter());
    }

    @Override
    public void onStart() throws InterruptedException {
        log("MaestroSlayer initializing...");
        RuntimeVariables.cache = new Cache(getDirectoryData() + "MaestroSlayer");
        RuntimeVariables.settings = RuntimeVariables.cache.getSettings();
        if (Config.USE_GUI) {
            this.mainFrame = new MainFrame(RuntimeVariables.settings);
            this.mainFrame.setVisible(true);
            while (mainFrame.isVisible()) {
                sleep(random(500, 1000));
            }
        }
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
        if (!RuntimeVariables.settings.getPotions().isEmpty()) {
            log("Adding potion support");
            addTask(new PotionHandler(RuntimeVariables.settings.getPotions()));
        }
        if (RuntimeVariables.settings.isUseCannon()) {
            log("Adding cannon support");
            addTask(new CannonHandler());
        }
        log("Adding eating support");
        addTask(new FoodHandler(RuntimeVariables.settings.getFood()));
        log("Configuring Antiban");
        addTask(new AntibanHandler(RuntimeVariables.settings.getAntibanFrequency()));
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
                    sendBroadcast(new Broadcast("cannon-set", true));
                } else if (message.getMessage().toLowerCase().contains("cannon is out of ammo")) {
                    sendBroadcast(new Broadcast("cannon-reload", true));
                    sendBroadcast(new Broadcast("cannon-set", true));
                } else if (message.getMessage().contains("you repair your cannon")) {
                    sendBroadcast(new Broadcast("cannon-broken", false));
                    sendBroadcast(new Broadcast("cannon-set", true));
                } else if (message.getMessage().contains("load the cannon with")) {
                    sendBroadcast(new Broadcast("cannon-reload", false));
                    sendBroadcast(new Broadcast("cannon-set", true));
                } else if (message.getMessage().contains("cannon already firing")) {
                    sendBroadcast(new Broadcast("cannon-reload", false));
                    sendBroadcast(new Broadcast("cannon-set", true));
                } else if (message.getMessage().contains("you pick up your cannon")) {
                    sendBroadcast(new Broadcast("cannon-set", false));
                } else if (message.getMessage().contains("there isn't enough space to set up here")) {
                    sendBroadcast(new Broadcast("cannon-error"));
                } else if (message.getMessage().toLowerCase().contains("assigned to kill")) {
                    if (RuntimeVariables.currentTask == null || !RuntimeVariables.currentTask.isFinished()) {
                        SlayerTask.setCurrentTask(message.getMessage());
                        if (RuntimeVariables.currentTask != null) {
                            sendBroadcast(new Broadcast("need-slayer-task", false));
                            sendBroadcast(new Broadcast("request-equipment-update"));
                            sendBroadcast(new Broadcast("requires-anti", RuntimeVariables.currentTask.getCurrentMonster().isPoisonous()));
                            log("Current task: " + RuntimeVariables.currentTask.getName());
                            break outter;
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
                    sendBroadcast(new Broadcast("need-slayer-task", true));
                    log("Need new task from " + RuntimeVariables.currentMaster.getName());
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
        g.setColor(Color.RED);
        int mouseX = getMouse().getPosition().x;
        int mouseY = getMouse().getPosition().y;
        g.fillOval(mouseX, mouseY, 6, 6);
        g.drawLine(mouseX - 7, mouseY, mouseX + 7, mouseY);

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
        g.drawString("Kills left: " + formatNumber(RuntimeVariables.currentTask != null ? RuntimeVariables.currentTask.getKillsLeft() : 0), 12, 86);
        g.drawString("Slayer Exp: " + formatNumber(RuntimeVariables.experienceTracker.getGainedXP(Skill.SLAYER)) + "(" + formatNumber
                        (RuntimeVariables.experienceTracker.getGainedXPPerHour(Skill.SLAYER)) + ")", 12,
                102);
        g.drawString("Current Task: " + ((RuntimeVariables.currentTask == null || RuntimeVariables.currentTask.isFinished())
                ? "None" : RuntimeVariables.currentTask.getName()), 12, 118);
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


}
