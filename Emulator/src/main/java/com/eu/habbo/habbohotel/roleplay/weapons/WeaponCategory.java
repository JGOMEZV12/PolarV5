package com.eu.habbo.habbohotel.roleplay.weapons;

public enum WeaponCategory {
    ArmaDeFuego(1),
    ArmaBlanca(2),
    Utiliario(3);

    private final int value;

    WeaponCategory(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static WeaponCategory fromString(String category) {
        if (category == null) return ArmaDeFuego;
        for (WeaponCategory cat : values()) {
            if (cat.name().equalsIgnoreCase(category)) {
                return cat;
            }
        }
        return ArmaDeFuego;
    }
}
