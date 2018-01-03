package org.osbot.maestro.script.slayer.task;

import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public interface MonsterMechanic {

    public static final MonsterMechanic ROCK_SLUG_MECHANIC = new MonsterMechanic() {
        @Override
        public boolean condition(NPC monster, MethodProvider provider) {
            return monster.getName().contains(Monster.ROCKSLUGS.getName()) && monster.getHealthPercent() <= 15;
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

    boolean condition(NPC monster, MethodProvider provider);

    void execute(NPC monster, MethodProvider provider) throws MonsterMechanicException;
}
