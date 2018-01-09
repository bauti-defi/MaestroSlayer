package org.osbot.maestro.script.slayer.utils.position;

import org.json.simple.JSONObject;
import org.osbot.maestro.script.data.Config;
import org.osbot.rs07.api.map.Position;

public class SlayerPosition {

    private final int x;
    private final int y;
    private final int z;

    public SlayerPosition(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SlayerPosition wrap(Position position) {
        return new SlayerPosition(position.getX(), position.getY(), position.getZ());
    }

    public static SlayerPosition wrap(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        return new SlayerPosition(((Long) jsonObject.get(Config.POSITION_X)).intValue(), ((Long) jsonObject.get(Config.POSITION_Y)).intValue(), ((Long) jsonObject.get(Config.POSITION_Z)).intValue());
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Position unwrap() {
        return new Position(x, y, z);
    }

    @Override
    public boolean equals(final Object obj) {
        Position position = null;
        if (obj instanceof SlayerPosition) {
            position = ((SlayerPosition) obj).unwrap();
        }
        if (position != null) {
            return position.equals(unwrap());
        }
        return false;
    }

    @Override
    public String toString() {
        return unwrap().toString();
    }
}
