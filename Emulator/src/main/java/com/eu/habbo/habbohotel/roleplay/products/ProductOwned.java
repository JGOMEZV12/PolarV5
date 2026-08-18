package com.eu.habbo.habbohotel.roleplay.products;

public class ProductOwned {
    private int id;
    private int productId;
    private int userId;
    private String extradata;

    public ProductOwned(int id, int productId, int userId, String extradata) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
        this.extradata = extradata;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getExtradata() {
        return extradata;
    }

    public void setExtradata(String extradata) {
        this.extradata = extradata;
    }
}
