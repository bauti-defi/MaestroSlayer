package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.task.monster.ItemLastHitMechanic;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanic;
import org.osbot.maestro.script.slayer.utils.templates.MonsterMechanicTemplate;

public class MonsterMechanics {


    public static final MonsterMechanic SALT_MONSTER = new ItemLastHitMechanic(MonsterMechanicTemplate.SALT_MONSTER, "Bag of salt");
    public static final MonsterMechanic ICE_MONSTER = new ItemLastHitMechanic(MonsterMechanicTemplate.ICE_MONSTER, "Ice cooler");
}
