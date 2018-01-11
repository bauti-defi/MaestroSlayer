package org.osbot.maestro.script.data;

public enum Foods {
    MONKFISH("Monkfish"), TROUT("Trout");

    private final String name;

    Foods(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Foods getByName(String name) {
        for (Foods foods : values()) {
            if (foods.getName().equalsIgnoreCase(name)) {
                return foods;
            }
        }
        return null;
    }
}
