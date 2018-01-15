package org.osbot.maestro.script.slayer.utils;

import org.osbot.maestro.script.slayer.utils.slayeritem.*;
import org.osbot.maestro.script.slayer.utils.templates.InventoryItemTemplate;
import org.osbot.maestro.script.slayer.utils.templates.WornItemTemplate;

import java.util.ArrayList;
import java.util.List;

public class SlayerItemMapper {

    private final List<SlayerItem> slayerItems;

    public SlayerItemMapper() {
        this.slayerItems = new ArrayList<>();
    }

    public InventoryTaskItem get(InventoryItemTemplate template) throws SlayerItemException {
        if (template == null) {
            return null;
        }
        for (SlayerItem item : slayerItems) {
            if (item instanceof SlayerInventoryItem) {
                if (item.getName().equalsIgnoreCase(template.getName())) {
                    return (InventoryTaskItem) item;
                }
            }
        }
        throw new SlayerItemException("Invalid slayer inventory item");
    }


    public WornTaskItem get(WornItemTemplate template) throws SlayerItemException {
        if (template == null) {
            return null;
        }
        for (SlayerItem item : slayerItems) {
            if (item instanceof SlayerWornItem) {
                if (item.getName().equalsIgnoreCase(template.getName())) {
                    return (WornTaskItem) item;
                }
            }
        }
        throw new SlayerItemException("Invalid slayer worn item");
    }

    public void map(SlayerItem item) {
        slayerItems.add(item);
    }
}
