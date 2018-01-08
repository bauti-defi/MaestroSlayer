package org.osbot.maestro.script.slayer.utils;

import org.osbot.maestro.script.slayer.task.monster.MonsterMechanic;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanicException;
import org.osbot.maestro.script.slayer.utils.templates.MonsterMechanicTemplate;

import java.util.ArrayList;
import java.util.List;

public class MechanicMapper {

    private final List<MonsterMechanic> monsterMechanics;

    public MechanicMapper() {
        monsterMechanics = new ArrayList<>();
    }

    public MonsterMechanic get(MonsterMechanicTemplate template) throws MonsterMechanicException {
        if (template == null) {
            return null;
        }
        for (MonsterMechanic mechanic : monsterMechanics) {
            if (mechanic.getTemplate().equals(template)) {
                return mechanic;
            }
        }
        throw new MonsterMechanicException("Invalid monster mechanic");
    }

    public void map(MonsterMechanic monsterMechanic) {
        this.monsterMechanics.add(monsterMechanic);
    }
}
