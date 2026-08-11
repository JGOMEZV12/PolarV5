package com.eu.habbo.habbohotel.roleplay.food;

public class Food {
    private String name;
    private String type;
    private int itemId;
    private String extraData;
    private int cost;
    private int health;
    private int energy;
    private int alcohol;
    private int hunger;
    private String serveText;
    private String eatText;
    private boolean servable;

    public Food(String name, String type, int itemId, String extraData, int cost, int health, int energy, int alcohol, int hunger, String serveText, String eatText, boolean servable) {
        this.name = name;
        this.type = type;
        this.itemId = itemId;
        this.extraData = extraData;
        this.cost = cost;
        this.health = health;
        this.energy = energy;
        this.alcohol = alcohol;
        this.hunger = hunger;
        this.serveText = serveText;
        this.eatText = eatText;
        this.servable = servable;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getExtraData() { return extraData; }
    public void setExtraData(String extraData) { this.extraData = extraData; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public int getAlcohol() { return alcohol; }
    public void setAlcohol(int alcohol) { this.alcohol = alcohol; }

    public int getHunger() { return hunger; }
    public void setHunger(int hunger) { this.hunger = hunger; }

    public String getServeText() { return serveText; }
    public void setServeText(String serveText) { this.serveText = serveText; }

    public String getEatText() { return eatText; }
    public void setEatText(String eatText) { this.eatText = eatText; }

    public boolean isServable() { return servable; }
    public void setServable(boolean servable) { this.servable = servable; }
}
