package com.eu.habbo.habbohotel.roleplay.economy;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Portado desde: Polar RP/HabboRoleplay/Products/Products.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Representa la estructura de un Producto de Roleplay.
 */
public class Product {

    private int id;
    private String productName;
    private String displayName;
    private int price;
    private String type;
    private boolean canStack;
    private int maxCant;

    public Product(
            int id, String productName, String displayName, int price, String type, boolean canStack, int maxCant) {
        this.id = id;
        this.productName = productName;
        this.displayName = displayName;
        this.price = price;
        this.type = type;
        this.canStack = canStack;
        this.maxCant = maxCant;
    }

    public Product(ResultSet row) throws SQLException {
        this.id = row.getInt("id");
        this.productName = row.getString("name");
        this.displayName = row.getString("display_name");
        this.price = row.getInt("price");
        this.type = row.getString("type");
        this.canStack = row.getString("can_stack").equals("1");
        this.maxCant = row.getInt("max_cant");
    }

    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    public boolean isCanStack() {
        return canStack;
    }

    public int getMaxCant() {
        return maxCant;
    }
}
