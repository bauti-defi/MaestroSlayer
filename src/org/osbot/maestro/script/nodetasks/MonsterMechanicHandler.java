package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.slayer.data.SlayerVariables;
import org.osbot.maestro.script.slayer.task.MonsterMechanicException;
import org.osbot.rs07.api.model.NPC;

public class MonsterMechanicHandler extends NodeTask implements BroadcastReceiver {

    private NPC monster;

    public MonsterMechanicHandler() {
        super(Priority.HIGH);
        registerBroadcastReceiver(this);
    }

    @Override
    public boolean runnable() {
        if (monster != null && monster.exists()) {
            return SlayerVariables.currentTask.getMonster().hasMechanic() && SlayerVariables.currentTask.getMonster().getMonsterMechanic().condition(monster, provider);
        }
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (monster.isOnScreen() && monster.isVisible()) {
            try {
                SlayerVariables.currentTask.getMonster().getMonsterMechanic().execute(monster, provider);
            } catch (MonsterMechanicException e) {
                e.printStackTrace();
                stopScript(true);
            }
        }
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        if (broadcast.getKey().equalsIgnoreCase("new-target")) {
            monster = (NPC) broadcast.getMessage();
        }
    }
}
