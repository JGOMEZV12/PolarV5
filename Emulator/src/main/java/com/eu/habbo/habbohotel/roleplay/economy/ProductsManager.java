package com.eu.habbo.habbohotel.roleplay.economy;

import com.eu.habbo.habbohotel.roleplay.RpEngine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Portado desde: Polar RP/HabboRoleplay/Products/ProductsManager.cs y Polar RP/HabboRoleplay/ProductsOwned/OFF_ProductsOwnedManager.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Administrador del catálogo de productos y del inventario de productos de roleplay de los usuarios.
 */
public class ProductsManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsManager.class);

    private static final ConcurrentHashMap<String, Product> productsByName = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Product> productsById = new ConcurrentHashMap<>();

    /**
     * Inicializa y carga la lista global de productos de Roleplay desde la DB.
     */
    public static void initialize() {
        productsByName.clear();
        productsById.clear();

        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_products");
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                Product product = new Product(set);
                productsByName.put(product.getProductName().toLowerCase(), product);
                productsById.put(product.getId(), product);
            }
            LOGGER.info("Loaded " + productsById.size() + " roleplay products.");
        } catch (SQLException e) {
            LOGGER.error("Error al cargar productos de Roleplay desde rp_products", e);
        }
    }

    public static Product getProduct(String name) {
        if (name == null) return null;
        return productsByName.get(name.toLowerCase());
    }

    public static Product getProduct(int id) {
        return productsById.get(id);
    }

    public static Collection<Product> getAllProducts() {
        return productsById.values();
    }

    public static List<Product> getProductsByType(String type) {
        List<Product> list = new ArrayList<>();
        if (type == null) return list;
        for (Product product : productsById.values()) {
            if (type.equalsIgnoreCase(product.getType())) {
                list.add(product);
            }
        }
        return list;
    }

    /**
     * Obtiene todos los productos de un usuario de la DB.
     */
    public static List<ProductOwned> getMyProductsOwned(int userId) {
        List<ProductOwned> list = new ArrayList<>();
        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("SELECT * FROM rp_user_products WHERE user_id = ?")) {
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    list.add(new ProductOwned(set));
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al cargar inventario de productos para el usuario " + userId, e);
        }
        return list;
    }

    /**
     * Crea y guarda un producto en el inventario del usuario.
     */
    public static ProductOwned createProductOwned(int userId, int productId, String extradata) {
        String query = "INSERT INTO rp_user_products (product_id, user_id, extradata) VALUES (?, ?, ?)";
        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, productId);
            statement.setInt(2, userId);
            statement.setString(3, extradata);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    return new ProductOwned(id, productId, userId, extradata);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al registrar producto " + productId + " en el inventario del usuario " + userId, e);
        }
        return null;
    }

    /**
     * Elimina un producto del inventario del usuario.
     */
    public static void removeProductOwned(int ownedId) {
        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("DELETE FROM rp_user_products WHERE id = ?")) {
            statement.setInt(1, ownedId);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error al eliminar producto " + ownedId + " de rp_user_products", e);
        }
    }
}
