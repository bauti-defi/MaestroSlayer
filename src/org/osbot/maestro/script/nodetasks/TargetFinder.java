package org.osbot.maestro.script.nodetasks;

import org.osbot.maestro.framework.Broadcast;
import org.osbot.maestro.framework.BroadcastReceiver;
import org.osbot.maestro.framework.NodeTask;
import org.osbot.maestro.framework.Priority;
import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.NPC;

public class TargetFinder extends NodeTask implements BroadcastReceiver {

    private NPC target;
    private boolean targetRequested;

    public TargetFinder() {
        super(Priority.LOW);
    }

    @Override
    public boolean runnable() throws InterruptedException {
        if (RuntimeVariables.currentTask != null && RuntimeVariables.currentTask.getCurrentMonster().getArea().contains(provider
                .myPosition())) {
            return target == null || !target.exists() || inCombat(target) && !inCombat(provider.myPlayer()) || targetRequested ||
                    target.getHealthPercent() == 0 || inCombat(provider.myPlayer()) && target != null && (!inCombat(target)
                    || !target.isInteracting(provider.myPlayer()));
        }
        return false;
    }

    @Override
    protected void execute() throws InterruptedException {
        if (inCombat(provider.myPlayer()) && target != null && target.exists() && (!target.isInteracting(provider
                .myPlayer()) || !inCombat(target))) {
            provider.log("Updating target");
            target = (NPC) provider.myPlayer().getInteracting();
        } else {
            target = provider.getNpcs().closest(new Filter<NPC>() {
                @Override
                public boolean match(NPC npc) {
                    if (npc.isInteracting(provider.myPlayer())) {
                        return true;
                    }
                    return !inCombat(npc) && npc.hasAction("Attack") && npc.getName().contains(RuntimeVariables.currentMonster.getName())
                            && provider.getMap().canReach(npc);
                }
            });
            provider.log("Target found");
        }
        sendBroadcast(new Broadcast("new-target", target));
        targetRequested = false;
    }

    private boolean inCombat(Character character) {
        if (character != null && character.exists()) {
            return !character.isAttackable() || character.isUnderAttack() || character
                    .getInteracting() != null;
        }
        return false;
    }

    @Override
    public void receivedBroadcast(Broadcast broadcast) {
        switch (broadcast.getKey()) {
            case "request-target":
                targetRequested = true;
                break;
            case "hover-target-antiban":
                if (target != null) {
                    target.hover();
                }
                break;
        }
    }
}
