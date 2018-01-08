package org.osbot.maestro.script.data;

import org.osbot.maestro.script.slayer.task.monster.MonsterMechanic;
import org.osbot.maestro.script.slayer.task.monster.MonsterMechanicException;
import org.osbot.maestro.script.slayer.utils.templates.MonsterMechanicTemplate;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public class MonsterMechanics {


    public static final MonsterMechanic SALT_MONSTER = new MonsterMechanic() {

        @Override
        public MonsterMechanicTemplate getTemplate() {
            return MonsterMechanicTemplate.SALT_MONSTER;
        }

        @Override
        public boolean condition(String name, NPC monster, MethodProvider provider) {
            return monster.getName().contains(name) && monster.getHealthPercent() <= 15;
        }

        @Override
        public void execute(NPC monster, MethodProvider provider) throws MonsterMechanicException {
            if (provider.getTabs().open(Tab.INVENTORY)) {
                String selectedItem;
                if ((selectedItem = provider.getInventory().getSelectedItemName()) == null || !selectedItem.equalsIgnoreCase("Bag of salt")) {
                    if (selectedItem != null) {
                        provider.getInventory().deselectItem();
                    }
                    Item salt = provider.getInventory().getItem("Bag of salt");
                    if (salt != null) {
                        salt.interact("Use");
                    } else {
                        throw new MonsterMechanicException("Out of salt, stopping...");
                    }
                }
                if (monster != null && monster.exists()) {
                    if (!monster.isVisible() && !monster.isOnScreen()) {
                        provider.getCamera().toEntity(monster);
                    }
                    monster.interact("Use");
                    provider.getMouse().moveOutsideScreen();
                    new ConditionalSleep(4000, 1500) {

                        @Override
                        public boolean condition() throws InterruptedException {
                            return (monster == null || !monster.exists()) && !provider.myPlayer().isAnimating();
                        }
                    }.sleep();
                }
            }
        }
    };


}
