package com.eu.habbo.habbohotel.roleplay.wizards;

public class HechizoOwned {
    private int id;
    private int userId;
    private String baseWizard;
    private String name;
    private int power;
    private int firingRange;
    private int shields;
    private int firingDamage;
    private int health;

    public HechizoOwned(
            int id,
            int userId,
            String baseWizard,
            String name,
            int power,
            int firingRange,
            int shields,
            int firingDamage,
            int health) {
        this.id = id;
        this.userId = userId;
        this.baseWizard = baseWizard;
        this.name = name;
        this.power = power;
        this.firingRange = firingRange;
        this.shields = shields;
        this.firingDamage = firingDamage;
        this.health = health;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getBaseWizard() {
        return baseWizard;
    }

    public void setBaseWizard(String baseWizard) {
        this.baseWizard = baseWizard;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getFiringRange() {
        return firingRange;
    }

    public void setFiringRange(int firingRange) {
        this.firingRange = firingRange;
    }

    public int getShields() {
        return shields;
    }

    public void setShields(int shields) {
        this.shields = shields;
    }

    public int getFiringDamage() {
        return firingDamage;
    }

    public void setFiringDamage(int firingDamage) {
        this.firingDamage = firingDamage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
