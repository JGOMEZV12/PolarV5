package com.eu.habbo.habbohotel.roleplay.wizards;

public class Hechizos {
    private int id;
    private String name;
    private String publicName;
    private String message;
    private int power;
    private int firingRange;
    private int shields;
    private int firingDamage;
    private int health;
    private int cost;
    private int costFine;
    private int stock;
    private int quantity = 1;

    public Hechizos(
            int id,
            String name,
            String publicName,
            String message,
            int power,
            int firingRange,
            int shields,
            int firingDamage,
            int health,
            int cost,
            int costFine,
            int stock) {
        this.id = id;
        this.name = name;
        this.publicName = publicName;
        this.message = message;
        this.power = power;
        this.firingRange = firingRange;
        this.shields = shields;
        this.firingDamage = firingDamage;
        this.health = health;
        this.cost = cost;
        this.costFine = costFine;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getCostFine() {
        return costFine;
    }

    public void setCostFine(int costFine) {
        this.costFine = costFine;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
