package com.eu.habbo.habbohotel.roleplay.weapons;

public class WSkin {
    private int id;
    private String name;
    private String name2;
    private String publicName;
    private int effectId;
    private int handItem;
    private int range;
    private int minDamage;
    private int maxDamage;
    private int clipSize;
    private int cost;
    private int stock;

    public WSkin(int id, String name, String name2, String publicName, int effectId, int handItem, int range, int minDamage, int maxDamage, int clipSize, int cost, int stock) {
        this.id = id;
        this.name = name;
        this.name2 = name2;
        this.publicName = publicName;
        this.effectId = effectId;
        this.handItem = handItem;
        this.range = range;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.clipSize = clipSize;
        this.cost = cost;
        this.stock = stock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getName2() { return name2; }
    public void setName2(String name2) { this.name2 = name2; }

    public String getPublicName() { return publicName; }
    public void setPublicName(String publicName) { this.publicName = publicName; }

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

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}
