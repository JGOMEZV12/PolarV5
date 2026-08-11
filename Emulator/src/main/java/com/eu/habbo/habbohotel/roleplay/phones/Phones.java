package com.eu.habbo.habbohotel.roleplay.phones;

public class Phones {
    private int id;
    private String modelName;
    private String displayName;
    private int price;
    private int effectId;
    private int screenSlots;
    private int dockSlots;

    public Phones(
            int id, String modelName, String displayName, int price, int effectId, int screenSlots, int dockSlots) {
        this.id = id;
        this.modelName = modelName;
        this.displayName = displayName;
        this.price = price;
        this.effectId = effectId;
        this.screenSlots = screenSlots;
        this.dockSlots = dockSlots;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getEffectId() {
        return effectId;
    }

    public void setEffectId(int effectId) {
        this.effectId = effectId;
    }

    public int getScreenSlots() {
        return screenSlots;
    }

    public void setScreenSlots(int screenSlots) {
        this.screenSlots = screenSlots;
    }

    public int getDockSlots() {
        return dockSlots;
    }

    public void setDockSlots(int dockSlots) {
        this.dockSlots = dockSlots;
    }
}
