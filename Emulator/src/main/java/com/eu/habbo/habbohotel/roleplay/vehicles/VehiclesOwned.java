package com.eu.habbo.habbohotel.roleplay.vehicles;

public class VehiclesOwned {
    private int id;
    private int furniId;
    private int itemId;
    private int ownerId;
    private int lastUserId;
    private String model;
    private int fuel;
    private int km;
    private int state; // 0 = Open | 1 = Locked | 2 = Unlocked damaged | 3 = Locked damaged
    private boolean traba;
    private boolean alarm;
    private int location;
    private int x;
    private int y;
    private double z;
    private String[] baul;
    private boolean baulOpen;
    private int carLife;
    private int camCargId;
    private int camState;
    private int camDest;
    private int camOwnId;

    public VehiclesOwned(
            int id,
            int furniId,
            int itemId,
            int ownerId,
            int lastUserId,
            String model,
            int fuel,
            int km,
            int state,
            boolean traba,
            boolean alarm,
            int location,
            int x,
            int y,
            double z,
            String[] baul,
            boolean baulOpen,
            int carLife) {
        this.id = id;
        this.furniId = furniId;
        this.itemId = itemId;
        this.ownerId = ownerId;
        this.lastUserId = lastUserId;
        this.model = model;
        this.fuel = fuel;
        this.km = km;
        this.state = state;
        this.traba = traba;
        this.alarm = alarm;
        this.location = location;
        this.x = x;
        this.y = y;
        this.z = z;
        this.baul = baul;
        this.baulOpen = baulOpen;
        this.carLife = carLife;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFurniId() {
        return furniId;
    }

    public void setFurniId(int furniId) {
        this.furniId = furniId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getLastUserId() {
        return lastUserId;
    }

    public void setLastUserId(int lastUserId) {
        this.lastUserId = lastUserId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public int getKm() {
        return km;
    }

    public void setKm(int km) {
        this.km = km;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isTraba() {
        return traba;
    }

    public void setTraba(boolean traba) {
        this.traba = traba;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public String[] getBaul() {
        return baul;
    }

    public void setBaul(String[] baul) {
        this.baul = baul;
    }

    public boolean isBaulOpen() {
        return baulOpen;
    }

    public void setBaulOpen(boolean baulOpen) {
        this.baulOpen = baulOpen;
    }

    public int getCarLife() {
        return carLife;
    }

    public void setCarLife(int carLife) {
        this.carLife = carLife;
    }

    public int getCamCargId() {
        return camCargId;
    }

    public void setCamCargId(int camCargId) {
        this.camCargId = camCargId;
    }

    public int getCamState() {
        return camState;
    }

    public void setCamState(int camState) {
        this.camState = camState;
    }

    public int getCamDest() {
        return camDest;
    }

    public void setCamDest(int camDest) {
        this.camDest = camDest;
    }

    public int getCamOwnId() {
        return camOwnId;
    }

    public void setCamOwnId(int camOwnId) {
        this.camOwnId = camOwnId;
    }
}
