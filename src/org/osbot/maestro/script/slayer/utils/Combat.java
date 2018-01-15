package org.osbot.maestro.script.slayer.utils;

import org.osbot.maestro.script.data.RuntimeVariables;
import org.osbot.maestro.script.slayer.task.monster.Monster;
import org.osbot.rs07.api.model.Character;
import org.osbot.rs07.api.model.NPC;

public class Combat {


    public static boolean isTryingToAttack(Character instigator, Character target) {
        if (instigator != null && instigator.exists() && target != null && target.exists()) {
            return instigator.isInteracting(target) && !inCombat(instigator);
        }
        return false;
    }

    public static boolean isAttacking(Character attacker, Character target) {
        if (attacker != null && attacker.exists() && target != null && target.exists()) {
            return target.isUnderAttack() && attacker.isInteracting(target);
        }
        return false;
    }

    public static boolean inCombat(Character character) {
        if (character != null && character.exists()) {
            return !character.isAttackable() || character.isUnderAttack();
        }
        return false;
    }

    public static boolean isPartOfTask(NPC npc) {
        for (Monster monster : RuntimeVariables.currentTask.getMonsters()) {
            if (npc.getName().equalsIgnoreCase(monster.getName())) {
                return true;
            }
        }
        return false;
    }
}
