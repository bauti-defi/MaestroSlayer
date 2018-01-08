package org.osbot.maestro.script.slayer.task.monster;

import org.osbot.maestro.script.slayer.utils.templates.MonsterMechanicTemplate;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.script.MethodProvider;

public interface MonsterMechanic {

    MonsterMechanicTemplate getTemplate();

    boolean condition(String name, NPC monster, MethodProvider provider);

    void execute(NPC monster, MethodProvider provider) throws MonsterMechanicException;
}
