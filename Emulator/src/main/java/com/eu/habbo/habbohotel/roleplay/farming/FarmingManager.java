package com.eu.habbo.habbohotel.roleplay.farming;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FarmingManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FarmingManager.class);

    public static final ConcurrentHashMap<String, FarmingItem> farmingItems = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, FarmingSpace> farmingSpaces = new ConcurrentHashMap<>();

    public static void initialize() {
        farmingItems.clear();
        farmingSpaces.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            // Load farming items
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_farming");
                    ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String baseItem = set.getString("base_item").toLowerCase();
                    int levelRequired = set.getInt("level_required");
                    int minExp = set.getInt("min_exp");
                    int maxExp = set.getInt("max_exp");
                    int sellPrice = set.getInt("sell_price");
                    int buyPrice = set.getInt("buy_price");

                    FarmingItem item =
                            new FarmingItem(id, baseItem, levelRequired, minExp, maxExp, sellPrice, buyPrice);
                    farmingItems.put(baseItem, item);
                }
            }

            // Load farming spaces
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_farming_spaces");
                    ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    int itemId = set.getInt("item_id");
                    int roomId = set.getInt("room_id");
                    int cost = set.getInt("cost");
                    int x = set.getInt("x");
                    int y = set.getInt("y");
                    double z = set.getDouble("z");
                    int ownerId = set.getInt("owner_id");
                    int expiration = set.getInt("expiration");

                    FarmingSpace space = new FarmingSpace(id, itemId, roomId, cost, x, y, z, ownerId, expiration);
                    farmingSpaces.put(id, space);
                }
            }

            LOGGER.info(
                    "FarmingManager -> Loaded {} farming items and {} farming spaces.",
                    farmingItems.size(),
                    farmingSpaces.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to initialize FarmingManager", e);
        }
    }

    public static FarmingItem getFarmingItem(String baseItem) {
        if (baseItem == null) return null;
        return farmingItems.get(baseItem.toLowerCase());
    }

    public static FarmingItem getFarmingItem(int id) {
        for (FarmingItem item : farmingItems.values()) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public static List<FarmingSpace> getFarmingSpacesByRoomId(int roomId) {
        List<FarmingSpace> list = new ArrayList<>();
        for (FarmingSpace space : farmingSpaces.values()) {
            if (space.getRoomId() == roomId) {
                list.add(space);
            }
        }
        return list;
    }

    public static void updateAllFarmingSpaces() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE `rp_farming_spaces` SET `expiration` = ?, `owner_id` = ? WHERE `id` = ?")) {
            for (FarmingSpace space : farmingSpaces.values()) {
                statement.setInt(1, space.getExpiration());
                statement.setInt(2, space.getOwnerId());
                statement.setInt(3, space.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            LOGGER.error("Failed to update all farming spaces in batch", e);
        }
    }

    public static int sellPlants(GameClient session) {
        if (session == null || session.getHabbo() == null || session.getHabbo().getRoleplay() == null) {
            return 0;
        }

        synchronized (session.getHabbo()) {
            FarmingStats fs = session.getHabbo().getRoleplay().getFarmingStats();
            if (fs == null || fs.getPlantSatchel() == null) return 0;

            PlantSatchel satchel = fs.getPlantSatchel();
            int totalEarnings = 0;

            // Perform standard plant selling calculations...
            if (satchel.getBlueStarflowers() > 0) {
                FarmingItem item = getFarmingItem(11);
                if (item != null) {
                    totalEarnings += satchel.getBlueStarflowers() * item.getSellPrice();
                    satchel.setBlueStarflowers(0);
                }
            }

            if (satchel.getYellowStarflowers() > 0) {
                FarmingItem item = getFarmingItem(10);
                if (item != null) {
                    totalEarnings += satchel.getYellowStarflowers() * item.getSellPrice();
                    satchel.setYellowStarflowers(0);
                }
            }

            // Refund/payment
            if (totalEarnings > 0) {
                session.getHabbo().giveCredits(totalEarnings);
            }

            return totalEarnings;
        }
    }
}
