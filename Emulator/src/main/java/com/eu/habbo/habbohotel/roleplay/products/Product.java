package com.eu.habbo.habbohotel.roleplay.products;

public class Product {
    private int id;
    private String productName;
    private String displayName;
    private int price;
    private String type;
    private boolean canStack;
    private int maxCant;

    public Product(int id, String productName, String displayName, int price, String type, boolean canStack, int maxCant) {
        this.id = id;
        this.productName = productName;
        this.displayName = displayName;
        this.price = price;
        this.type = type;
        this.canStack = canStack;
        this.maxCant = maxCant;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isCanStack() { return canStack; }
    public void setCanStack(boolean canStack) { this.canStack = canStack; }

    public int getMaxCant() { return maxCant; }
    public void setMaxCant(int maxCant) { this.maxCant = maxCant; }
}
