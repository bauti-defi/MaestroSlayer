package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanicException;
import org.osbot.rs07.api.model.NPC;

public class MonsterMechanicHandler extends NodeTask implements BroadcastReceiver {

    private NPC monster;

    public MonsterMechanicHandler() {
        super(Priority.MEDIUM);
    }

    @Override
    public Response runnable() {
        if (monster != null && monster.exists()) {
            if (RuntimeVariables.currentTask.hasMechanic() && RuntimeVariables.currentTask.getMonsterMechanic().condition(monster, provider)) {
                return Response.EXECUTE;
            }
        }
        return Response.CONTINUE;
    }

    @Override
    protected void execute() throws InterruptedException {
        try {
            RuntimeVariables.currentTask.getMonsterMechanic().execute(monster, provider);
        } catch (MonsterMechanicException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        if (broadcast.getKey().equalsIgnoreCase("new-target")) {
            monster = (NPC) broadcast.getMessage();
        }
    }
}
