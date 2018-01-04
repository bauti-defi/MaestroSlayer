package org.osbot.maestro.script;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.NodeScript;
import org.osbot.maestro.script.nodetasks.*;
import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.task.Monster;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.utils.AntibanFrequency;
import org.osbot.maestro.script.slayer.utils.CombatStyle;
import org.osbot.maestro.script.slayer.utils.SkillTimer;
import org.osbot.maestro.script.slayer.utils.consumable.Food;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Message;
import org.osbot.rs07.api.ui.Skill;
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
    private SkillTimer slayerTimer;
    private int tasksFinished = 0;
    private NPC targetToPaint;

    public MaestroSlayer() {
        super();
        this.startTime = System.currentTimeMillis();
        if (SlayerVariables.eating) {
            addTask(new FoodHandler(new Food("Monkfish"), 50, 30));
        }
        if (SlayerVariables.cannon) {
            addTask(new CannonHandler());
        }
        addTask(new PotionHandler.Builder().addPotion("Super attack", Skill.ATTACK, 0).addPotion(SlayerVariables
                .antidote ? "Antidote" : "poison").build());
        addTask(new CombatHandler());
        addTask(new TaskValidator());
        addTask(new TargetFinder());
        addTask(new MonsterMechanicHandler());
        addTask(new AntibanHandler(AntibanFrequency.HIGH));
    }

    @Override
    public void onStart() throws InterruptedException {
        super.onStart();
        this.slayerTimer = new SkillTimer(this, Skill.SLAYER);
        log("MaestroSlayer started.");
        log("Report any bugs to El Maestro.");
        SlayerVariables.combatStyle = CombatStyle.getCurrentCombatStyle(this);
        log("Combat style: " + SlayerVariables.combatStyle.getName());
    }

    @Override
    public void onMessage(final Message message) throws InterruptedException {
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
                    String monsterName = message.getMessage().split("kill ")[1].split(";")[0];
                    int amount = Integer.parseInt(message.getMessage().split("only ")[1].split(" more")[0]);
                    for (Monster monster : Monster.values()) {
                        if (monsterName.toLowerCase().contains(monster.getName().toLowerCase())) {
                            SlayerVariables.currentTask = new SlayerTask(monster, amount);
                            log("Current task: " + SlayerVariables.currentTask.getMonster().getName());
                            break;
                        }
                    }
                } else if (message.getMessage().toLowerCase().contains("you've completed") || message.getMessage()
                        .toLowerCase().contains("you need something new to hunt.")) {
                    log("Task complete.");
                    tasksFinished++;
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
        g.drawString("Version: " + this.getVersion(), 12, 36);
        g.setFont(font1);
        g.drawString("Kills left: ", 12, 86);
        g.drawString("Slayer Exp: " + (slayerTimer == null ? "" : formatNumber(slayerTimer.getXpGained())), 12, 102);
        g.drawString("Current Task: " + ((SlayerVariables.currentTask == null || SlayerVariables.currentTask.isFinished())
                        ? "None" : SlayerVariables.currentTask.getMonster().getName()),
                12, 118);
        g.drawString("Tasks: " + tasksFinished, 12, 70);
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
