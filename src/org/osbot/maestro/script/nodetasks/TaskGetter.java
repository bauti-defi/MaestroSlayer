package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.*;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.task.SlayerTask;
import org.osbot.maestro.script.slayer.utils.events.EntityInteractionEvent;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.RS2Widget;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

public class TaskGetter extends NodeTask implements BroadcastReceiver {

    private boolean needTaskFromMaster;

    public TaskGetter() {
        super(Priority.HIGH);
    }

    @Override
    public Response runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null && RuntimeVariables.currentTask.isFinished() || needTaskFromMaster) {
            return Response.EXECUTE;
        }
        return Response.CONTINUE;
    }

    @Override
    protected void execute() throws InterruptedException {
        NPC master = getMaster();
        if (master != null) {
            if (provider.getDialogues().inDialogue()) {
                RS2Widget taskWidget = provider.getWidgets().get(231, 3);
                if (taskWidget != null) {
                    SlayerTask.setCurrentTask(taskWidget.getMessage());
                    if (RuntimeVariables.currentTask != null) {
                        sendBroadcast(new Broadcast("need-slayer-task", needTaskFromMaster = false));
                        sendBroadcast(new Broadcast("requires-anti", RuntimeVariables.currentTask.getCurrentMonster().isPoisonous()));
                        provider.log("Current task: " + RuntimeVariables.currentTask.getName());
                        provider.getDialogues().clickContinue();
                        return;
                    }
                    provider.log("Task not supported.");
                    stopScript(true);
                }
            } else {
                provider.log("Talking to " + master.getName());
                talkToMaster(master);
            }
        } else {
            provider.log("Walking to " + RuntimeVariables.currentMaster.getName());
            walkToMaster();
        }
    }

    private void talkToMaster(NPC master) {
        EntityInteractionEvent talkToMaster = new EntityInteractionEvent(master, "Assignment");
        talkToMaster.setWalkTo(true);
        talkToMaster.setEnergyThreshold(10, 30);
        talkToMaster.setMinDistanceThreshold(2);
        talkToMaster.setMiniMapDistanceThreshold(8);
        talkToMaster.setBreakCondition(new ConditionalSleep(10000, 1000) {

            @Override
            public boolean condition() throws InterruptedException {
                return provider.getDialogues().inDialogue();
            }
        });
        provider.execute(talkToMaster);
    }

    private NPC getMaster() {
        return provider.getNpcs().closest(RuntimeVariables.currentMaster.getName());
    }

    private void walkToMaster() {
        WebWalkEvent walkToMaster = new WebWalkEvent(RuntimeVariables.currentMaster.getArea().unwrap());
        walkToMaster.setPathPreferenceProfile(getToMasterPathPreference());
        walkToMaster.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return getMaster() != null || provider.getBank().isOpen();
            }
        });
        if (walkToMaster.prefetchRequirements(provider)) {
            provider.execute(walkToMaster);
        } else {
            provider.log("Could not find path to " + RuntimeVariables.currentMaster.getName());
            stopScript(true);
        }
    }


    private PathPreferenceProfile getToMasterPathPreference() {
        PathPreferenceProfile profile = new PathPreferenceProfile();
        profile.checkBankForItems(true);
        profile.checkEquipmentForItems(true);
        profile.checkInventoryForItems(true);
        profile.setAllowGliders(true);
        profile.setAllowCharters(true);
        profile.setAllowObstacles(true);
        profile.setAllowTeleports(true);
        return profile;
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "need-slayer-task":
                needTaskFromMaster = (boolean) broadcast.getMessage();
                break;
        }
    }
}
