package com.eu.habbo.habbohotel.roleplay.apartments;

public class Apartment {
    private int id;
    private String modelName;
    private int tiles;
    private String image;
    private int price;

    public Apartment(int id, String modelName, int tiles, String image, int price) {
        this.id = id;
        this.modelName = modelName;
        this.tiles = tiles;
        this.image = image;
        this.price = price;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public int getTiles() { return tiles; }
    public void setTiles(int tiles) { this.tiles = tiles; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
