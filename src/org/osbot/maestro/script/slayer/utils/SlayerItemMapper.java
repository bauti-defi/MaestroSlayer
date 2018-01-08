package org.osbot.maestro.script.slayer.utils;

import org.osbot.maestro.script.slayer.utils.requireditem.SlayerInventoryItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItem;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerItemException;
import org.osbot.maestro.script.slayer.utils.requireditem.SlayerWornItem;
import org.osbot.maestro.script.slayer.utils.templates.InventoryItemTemplate;
import org.osbot.maestro.script.slayer.utils.templates.WornItemTemplate;

import java.util.ArrayList;
import java.util.List;

public class SlayerItemMapper {

    private final List<SlayerItem> slayerItems;

    public SlayerItemMapper() {
        this.slayerItems = new ArrayList<>();
    }

    public SlayerInventoryItem get(InventoryItemTemplate template) throws SlayerItemException {
        if (template == null) {
            return null;
        }
        for (SlayerItem item : slayerItems) {
            if (item instanceof SlayerInventoryItem) {
                if (item.getName().equalsIgnoreCase(template.getName())) {
                    return (SlayerInventoryItem) item;
                }
            }
        }
        throw new SlayerItemException("Invalid slayer inventory item");
    }


    public SlayerWornItem get(WornItemTemplate template) throws SlayerItemException {
        if (template == null) {
            return null;
        }
        for (SlayerItem item : slayerItems) {
            if (item instanceof SlayerWornItem) {
                if (item.getName().equalsIgnoreCase(template.getName())) {
                    return (SlayerWornItem) item;
                }
            }
        }
        throw new SlayerItemException("Invalid slayer worn item");
    }

    public void map(SlayerItem item) {
        slayerItems.add(item);
    }
}
