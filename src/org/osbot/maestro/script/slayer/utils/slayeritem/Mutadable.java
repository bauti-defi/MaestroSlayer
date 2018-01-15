package org.osbot.maestro.script.slayer.utils.slayeritem;

import org.osbot.rs07.api.filter.Filter;
import org.osbot.rs07.api.model.Item;

public interface Mutadable {

    Filter<Item> getMutationFilter();
}
