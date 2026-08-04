package com.eu.habbo.habbohotel.roleplay.economy;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Portado desde: Polar RP/HabboRoleplay/ProductsOwned/ProductsOwned.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Modela un producto comprado y poseído por un usuario (Inventario RP).
 */
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

    public ProductOwned(ResultSet row) throws SQLException {
        this.id = row.getInt("id");
        this.productId = row.getInt("product_id");
        this.userId = row.getInt("user_id");
        this.extradata = row.getString("extradata");
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

    public int getUserId() {
        return userId;
    }

    public String getExtradata() {
        return extradata;
    }

    public void setExtradata(String extradata) {
        this.extradata = extradata;
    }
}
