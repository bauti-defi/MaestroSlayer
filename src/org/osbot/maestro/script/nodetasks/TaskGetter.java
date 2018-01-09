package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.event.InteractionEvent;
import org.osbot.rs07.event.WebWalkEvent;
import org.osbot.rs07.event.webwalk.PathPreferenceProfile;
import org.osbot.rs07.utility.Condition;
import org.osbot.rs07.utility.ConditionalSleep;

public class TaskGetter extends NodeTask {

    public TaskGetter() {
        super(Priority.HIGH);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null) {
            return RuntimeVariables.currentTask.isFinished();
        }
        return RuntimeVariables.currentTask == null;
    }

    @Override
    protected void execute() throws InterruptedException {
        NPC master = getMaster();
        if (master != null) {
            if (provider.getDialogues().inDialogue()) {
                sendBroadcast(new Broadcast("need-first-task", false));
                //TODO: either close dialogues and default to gem or read dialogue widget
                provider.getDialogues().clickContinue();
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
        InteractionEvent talkToMaster = new InteractionEvent(master, "Assignment");
        talkToMaster.setOperateCamera(true);
        talkToMaster.setWalkingDistanceThreshold(5);
        talkToMaster.setWalkTo(true);
        if (provider.execute(talkToMaster).hasFinished()) {
            new ConditionalSleep(5000, 1000) {

                @Override
                public boolean condition() throws InterruptedException {
                    return provider.getDialogues().inDialogue();
                }
            }.sleep();
        }
    }

    private NPC getMaster() {
        return provider.getNpcs().closest(RuntimeVariables.currentMaster.getName());
    }

    private void walkToMaster() {
        WebWalkEvent walkToMaster = new WebWalkEvent(RuntimeVariables.currentMaster.getArea().unwrap());
        walkToMaster.useSimplePath();
        walkToMaster.setPathPreferenceProfile(getToMasterPathPreference());
        walkToMaster.setBreakCondition(new Condition() {
            @Override
            public boolean evaluate() {
                return getMaster() != null || provider.getBank().isOpen();
            }
        });
        provider.execute(walkToMaster);
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
}
