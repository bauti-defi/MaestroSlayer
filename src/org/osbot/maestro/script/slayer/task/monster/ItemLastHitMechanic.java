package org.osbot.maestro.script.slayer.task.monster;

import org.osbot.maestro.script.slayer.utils.events.EntityInteractionEvent;
import org.osbot.maestro.script.slayer.utils.templates.MonsterMechanicTemplate;
import org.osbot.rs07.api.model.Item;
import org.osbot.rs07.api.model.NPC;
import org.osbot.rs07.api.ui.Tab;
import org.osbot.rs07.script.MethodProvider;
import org.osbot.rs07.utility.ConditionalSleep;

public class ItemLastHitMechanic implements MonsterMechanic {


    private final String item;
    private final MonsterMechanicTemplate template;

    public ItemLastHitMechanic(MonsterMechanicTemplate template, String item) {
        this.item = item;
        this.template = template;
    }

    @Override
    public MonsterMechanicTemplate getTemplate() {
        return template;
    }

    @Override
    public boolean condition(NPC monster, MethodProvider provider) {
        return monster != null && monster.getHealthPercent() <= 15;
    }

    @Override
    public void execute(NPC monster, MethodProvider provider) throws MonsterMechanicException {
        if (provider.getTabs().open(Tab.INVENTORY)) {
            String selectedItem;
            if ((selectedItem = provider.getInventory().getSelectedItemName()) == null || !selectedItem.equalsIgnoreCase(item)) {
                if (selectedItem != null) {
                    provider.getInventory().deselectItem();
                }
                Item item = provider.getInventory().getItem(this.item);
                if (item != null) {
                    item.interact("Use");
                } else {
                    throw new MonsterMechanicException("Out of " + item + ", stopping...");
                }
            }
            if (monster != null && monster.exists()) {
                EntityInteractionEvent useOnMonster = new EntityInteractionEvent(monster, "Use");
                useOnMonster.setWalkTo(false);
                useOnMonster.setBreakCondition(new ConditionalSleep(4000, 1500) {

                    @Override
                    public boolean condition() throws InterruptedException {
                        return (monster == null || !monster.exists()) && !provider.myPlayer().isAnimating();
                    }
                });
                provider.execute(useOnMonster);
            }
        }
    }
}
