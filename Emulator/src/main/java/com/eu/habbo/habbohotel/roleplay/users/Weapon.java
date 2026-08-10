package com.eu.habbo.habbohotel.roleplay.users;

/**
 * Portado desde: Polar RP/HabboRoleplay/Weapons/Weapon.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Estructura de datos para representar un arma de roleplay.
 */
public class Weapon {

    private final String name;
    private final String publicName;
    private final int minDamage;
    private final int maxDamage;
    private final int range;
    private final int clipSize;
    private final int effectId;
    private final int handItem;
    private final int cost;

    public Weapon(
            String name,
            String publicName,
            int minDamage,
            int maxDamage,
            int range,
            int clipSize,
            int effectId,
            int handItem,
            int cost) {
        this.name = name;
        this.publicName = publicName;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.range = range;
        this.clipSize = clipSize;
        this.effectId = effectId;
        this.handItem = handItem;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public String getPublicName() {
        return publicName;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public int getRange() {
        return range;
    }

    public int getClipSize() {
        return clipSize;
    }

    public int getEffectId() {
        return effectId;
    }

    public int getHandItem() {
        return handItem;
    }

    public int getCost() {
        return cost;
    }
}
