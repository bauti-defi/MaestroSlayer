package org.osbot.maestro.script.slayer.utils.position;

import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;
import org.osbot.rs07.api.map.Area;
import org.osbot.rs07.api.map.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SlayerArea {

    private final List<SlayerPosition> area;

    public SlayerArea(Area area) {
        this.area = new ArrayList<>();
        for (Position position : area.getPositions()) {
            this.area.add(SlayerPosition.wrap(position));
        }
    }

    public SlayerArea(SlayerPosition sw, SlayerPosition ne) {
        this(new Area(sw.unwrap(), ne.unwrap()));
    }

    public SlayerArea(SlayerPosition... positions) {
        this.area = new ArrayList<>(Arrays.asList(positions));
    }

    public SlayerArea(Position sw, Position ne) {
        this(new Area(sw, ne));
    }

    public SlayerArea(Position... positions) {
        this(new Area(positions));
    }

    public static SlayerArea wrap(Area area) {
        return new SlayerArea(area);
    }

    public static SlayerArea wrap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return new SlayerArea(SlayerPosition.wrap((JSONObject) jsonObject.get(Config.SW_POSITION)), SlayerPosition.wrap((JSONObject) jsonObject.get(Config.NE_POSITION)));
    }

    public Area unwrap() {
        return new Area(area.get(0).unwrap(), area.get(area.size() - 1).unwrap());
    }

    public List<SlayerPosition> getSlayerPositions() {
        return area;
    }

    public boolean contains(SlayerPosition position) {
        return area.contains(position);
    }

    public boolean contains(Position position) {
        return unwrap().contains(position);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof SlayerArea) {
            SlayerArea area = (SlayerArea) obj;
            return unwrap().equals(area.unwrap());
        } else if (obj instanceof Area) {
            return unwrap().equals((Area) obj);
        }
        return false;
    }

    @Override
    public String toString() {
        return unwrap().toString();
    }
}
