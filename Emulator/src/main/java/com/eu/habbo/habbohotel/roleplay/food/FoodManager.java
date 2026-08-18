package com.eu.habbo.habbohotel.roleplay.food;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FoodManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FoodManager.class);

    public static final ConcurrentHashMap<String, Food> foodList = new ConcurrentHashMap<>();

    public static void initialize() {
        foodList.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_food")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    String name = set.getString("name");
                    String type = set.getString("type");
                    int itemId = set.getInt("item_id");
                    String extraData = set.getString("extra_data");
                    int cost = set.getInt("cost");
                    int health = set.getInt("health");
                    int energy = set.getInt("energy");
                    int alcohol = set.getInt("alcohol");
                    int hunger = set.getInt("hunger");
                    String serveText = set.getString("serve_text");
                    String eatText = set.getString("eat_text");
                    boolean servable = set.getString("servable").equals("1");

                    Food food = new Food(
                            name, type, itemId, extraData, cost, health, energy, alcohol, hunger, serveText, eatText,
                            servable);
                    foodList.put(name, food);
                }
            }

            LOGGER.info("FoodManager -> Loaded {} roleplay food items.", foodList.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load food from database", e);
        }
    }

    public static Food getFood(int itemId) {
        for (Food food : foodList.values()) {
            if (food.getItemId() == itemId) {
                return food;
            }
        }
        return null;
    }

    public static Food getFood(String name) {
        if (name == null) return null;
        Food food = foodList.get(name.toLowerCase());
        if (food != null && food.getType().equalsIgnoreCase("food")) {
            return food;
        }
        return null;
    }

    public static Food getDrink(String name) {
        if (name == null) return null;
        Food food = foodList.get(name.toLowerCase());
        if (food != null && food.getType().equalsIgnoreCase("drink")) {
            return food;
        }
        return null;
    }

    public static Food getFoodAndDrink(String name) {
        if (name == null) return null;
        return foodList.get(name.toLowerCase());
    }

    public static List<Food> getServableBotItems(String type) {
        List<Food> servableItems = new ArrayList<>();
        for (Food food : foodList.values()) {
            if (food.getType().equalsIgnoreCase(type)) {
                servableItems.add(food);
            }
        }
        return servableItems;
    }
}
