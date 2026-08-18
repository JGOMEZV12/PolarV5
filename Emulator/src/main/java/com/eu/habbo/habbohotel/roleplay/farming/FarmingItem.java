package com.eu.habbo.habbohotel.roleplay.farming;

public class FarmingItem {
    private int id;
    private String baseItem;
    private int levelRequired;
    private int minExp;
    private int maxExp;
    private int sellPrice;
    private int buyPrice;

    public FarmingItem(
            int id, String baseItem, int levelRequired, int minExp, int maxExp, int sellPrice, int buyPrice) {
        this.id = id;
        this.baseItem = baseItem;
        this.levelRequired = levelRequired;
        this.minExp = minExp;
        this.maxExp = maxExp;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseItem() {
        return baseItem;
    }

    public void setBaseItem(String baseItem) {
        this.baseItem = baseItem;
    }

    public int getLevelRequired() {
        return levelRequired;
    }

    public void setLevelRequired(int levelRequired) {
        this.levelRequired = levelRequired;
    }

    public int getMinExp() {
        return minExp;
    }

    public void setMinExp(int minExp) {
        this.minExp = minExp;
    }

    public int getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(int maxExp) {
        this.maxExp = maxExp;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(int sellPrice) {
        this.sellPrice = sellPrice;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }
}
