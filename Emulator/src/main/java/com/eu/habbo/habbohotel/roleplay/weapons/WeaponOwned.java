package com.eu.habbo.habbohotel.roleplay.weapons;

public class WeaponOwned {
    private String baseWeapon;
    private String name;
    private int minDamage;
    private int maxDamage;
    private int range;
    private int bullets;
    private boolean canUse;
    private int life;
    private int baulCarId;
    private int effectId;

    public WeaponOwned(
            String baseWeapon,
            String name,
            int minDamage,
            int maxDamage,
            int range,
            int bullets,
            boolean canUse,
            int life,
            int baulCarId,
            int effectId) {
        this.baseWeapon = baseWeapon;
        this.name = name;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.range = range;
        this.bullets = bullets;
        this.canUse = canUse;
        this.life = life;
        this.baulCarId = baulCarId;
        this.effectId = effectId;
    }

    public String getBaseWeapon() {
        return baseWeapon;
    }

    public void setBaseWeapon(String baseWeapon) {
        this.baseWeapon = baseWeapon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public int getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getBullets() {
        return bullets;
    }

    public void setBullets(int bullets) {
        this.bullets = bullets;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getBaulCarId() {
        return baulCarId;
    }

    public void setBaulCarId(int baulCarId) {
        this.baulCarId = baulCarId;
    }

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }
}
