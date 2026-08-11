package com.eu.habbo.habbohotel.roleplay.weapons;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.misc.RoleplayManager;

public class Weapon {
    private int id;
    private String name;
    private String publicName;
    private String firingText;
    private String equipText;
    private String unEquipText;
    private String reloadText;
    private int energy;
    private int effectId;
    private int handItem;
    private int range;
    private int minDamage;
    private int maxDamage;
    private int clipSize;
    private int reloadTime;
    private int cost;
    private int costFine;
    private int stock;
    private int levelRequirement;
    private boolean canUse;
    private int totalBullets;
    private int wLife;
    private boolean isVip;
    private int baulCar;
    private WeaponCategory category;

    public Weapon(int id, String name, String publicName, String firingText, String equipText, String unEquipText, String reloadText, int energy, int effectId, int handItem, int range, int minDamage, int maxDamage, int clipSize, int reloadTime, int cost, int costFine, int stock, int levelRequirement, boolean canUse, int totalBullets, int wLife, boolean isVip, int baulCar, WeaponCategory category) {
        this.id = id;
        this.name = name;
        this.publicName = publicName;
        this.firingText = firingText;
        this.equipText = equipText;
        this.unEquipText = unEquipText;
        this.reloadText = reloadText;
        this.energy = energy;
        this.effectId = effectId;
        this.handItem = handItem;
        this.range = range;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.clipSize = clipSize;
        this.reloadTime = reloadTime;
        this.cost = cost;
        this.costFine = costFine;
        this.stock = stock;
        this.levelRequirement = levelRequirement;
        this.canUse = canUse;
        this.totalBullets = totalBullets;
        this.wLife = wLife;
        this.isVip = isVip;
        this.baulCar = baulCar;
        this.category = category;
    }

    public boolean reload(GameClient client, GameClient targetClient) {
        if (client == null || client.getHabbo() == null) return false;

        // In Java, we can just print a message or use the bullets
        // For simplicity and matching C#, reload text is printed
        reloadMessage(client, this.clipSize);
        return true;
    }

    public void reloadMessage(GameClient client, int bullets) {
        if (reloadText == null) return;
        String text = reloadText;
        text = text.replace("[NAME]", publicName != null ? publicName : "");
        text = text.replace("[BULLETS]", String.valueOf(bullets));
        RoleplayManager.shout(client, text);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPublicName() { return publicName; }
    public void setPublicName(String publicName) { this.publicName = publicName; }

    public String getFiringText() { return firingText; }
    public void setFiringText(String firingText) { this.firingText = firingText; }

    public String getEquipText() { return equipText; }
    public void setEquipText(String equipText) { this.equipText = equipText; }

    public String getUnEquipText() { return unEquipText; }
    public void setUnEquipText(String unEquipText) { this.unEquipText = unEquipText; }

    public String getReloadText() { return reloadText; }
    public void setReloadText(String reloadText) { this.reloadText = reloadText; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public int getEffectId() { return effectId; }
    public void setEffectId(int effectId) { this.effectId = effectId; }

    public int getHandItem() { return handItem; }
    public void setHandItem(int handItem) { this.handItem = handItem; }

    public int getRange() { return range; }
    public void setRange(int range) { this.range = range; }

    public int getMinDamage() { return minDamage; }
    public void setMinDamage(int minDamage) { this.minDamage = minDamage; }

    public int getMaxDamage() { return maxDamage; }
    public void setMaxDamage(int maxDamage) { this.maxDamage = maxDamage; }

    public int getClipSize() { return clipSize; }
    public void setClipSize(int clipSize) { this.clipSize = clipSize; }

    public int getReloadTime() { return reloadTime; }
    public void setReloadTime(int reloadTime) { this.reloadTime = reloadTime; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public int getCostFine() { return costFine; }
    public void setCostFine(int costFine) { this.costFine = costFine; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getLevelRequirement() { return levelRequirement; }
    public void setLevelRequirement(int levelRequirement) { this.levelRequirement = levelRequirement; }

    public boolean isCanUse() { return canUse; }
    public void setCanUse(boolean canUse) { this.canUse = canUse; }

    public int getTotalBullets() { return totalBullets; }
    public void setTotalBullets(int totalBullets) { this.totalBullets = totalBullets; }

    public int getwLife() { return wLife; }
    public void setwLife(int wLife) { this.wLife = wLife; }

    public boolean isVip() { return isVip; }
    public void setVip(boolean vip) { isVip = vip; }

    public int getBaulCar() { return baulCar; }
    public void setBaulCar(int baulCar) { this.baulCar = baulCar; }

    public WeaponCategory getCategory() { return category; }
    public void setCategory(WeaponCategory category) { this.category = category; }
}
