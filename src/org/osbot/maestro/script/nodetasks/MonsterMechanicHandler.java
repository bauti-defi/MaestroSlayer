package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanicException;
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
            return RuntimeVariables.currentTask.hasMechanic() && RuntimeVariables.currentTask.getMonsterMechanic().condition
                    (RuntimeVariables.currentMonster.getName(), monster, provider);
        }
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (monster.isOnScreen() && monster.isVisible()) {
            try {
                RuntimeVariables.currentTask.getMonsterMechanic().execute(monster, provider);
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
