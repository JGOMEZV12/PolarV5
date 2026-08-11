package com.eu.habbo.habbohotel.roleplay.products;

import com.eu.habbo.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class ProductsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductsManager.class);

    public static final ConcurrentHashMap<String, Product> products = new ConcurrentHashMap<>();

    public static void initialize() {
        products.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_products")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String productName = set.getString("name");
                    String displayName = set.getString("display_name");
                    int price = set.getInt("price");
                    String type = set.getString("type");
                    boolean canStack = set.getString("can_stack").equals("1");
                    int maxCant = set.getInt("max_cant");

                    Product product = new Product(id, productName, displayName, price, type, canStack, maxCant);
                    products.put(productName, product);
                }
            }

            LOGGER.info("ProductsManager -> Loaded {} roleplay products.", products.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load products from database", e);
        }
    }

    public static Product getProduct(String name) {
        if (name == null) return null;
        return products.get(name);
    }

    public static List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }

    public static List<Product> getProductsByType(String type) {
        List<Product> list = new ArrayList<>();
        if (type == null) return list;
        for (Product product : products.values()) {
            if (product.getType().equalsIgnoreCase(type)) {
                list.add(product);
            }
        }
        return list;
    }
}
