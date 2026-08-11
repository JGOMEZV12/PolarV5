package com.eu.habbo.habbohotel.roleplay.vehicles;

public class Vehicle {
    private int id;
    private int itemId;
    private String itemName;
    private int effectId;
    private int price;
    private String model;
    private String displayName;
    private int maxFuel;
    private int maxTrunks;
    private int carType;
    private int maxDoors;
    private int carCorp;
    private int fastCar;

    public Vehicle(int id, int itemId, String itemName, int effectId, int price, String model, String displayName, int maxFuel, int maxTrunks, int carType, int maxDoors, int carCorp, int fastCar) {
        this.id = id;
        this.itemId = itemId;
        this.itemName = itemName;
        this.effectId = effectId;
        this.price = price;
        this.model = model;
        this.displayName = displayName;
        this.maxFuel = maxFuel;
        this.maxTrunks = maxTrunks;
        this.carType = carType;
        this.maxDoors = maxDoors;
        this.carCorp = carCorp;
        this.fastCar = fastCar;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public int getEffectId() { return effectId; }
    public void setEffectId(int effectId) { this.effectId = effectId; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getMaxFuel() { return maxFuel; }
    public void setMaxFuel(int maxFuel) { this.maxFuel = maxFuel; }

    public int getMaxTrunks() { return maxTrunks; }
    public void setMaxTrunks(int maxTrunks) { this.maxTrunks = maxTrunks; }

    public int getCarType() { return carType; }
    public void setCarType(int carType) { this.carType = carType; }

    public int getMaxDoors() { return maxDoors; }
    public void setMaxDoors(int maxDoors) { this.maxDoors = maxDoors; }

    public int getCarCorp() { return carCorp; }
    public void setCarCorp(int carCorp) { this.carCorp = carCorp; }

    public int getFastCar() { return fastCar; }
    public void setFastCar(int fastCar) { this.fastCar = fastCar; }
}
