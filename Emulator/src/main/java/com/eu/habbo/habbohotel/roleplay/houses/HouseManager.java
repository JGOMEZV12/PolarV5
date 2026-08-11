package com.eu.habbo.habbohotel.roleplay.houses;

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

public class HouseManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HouseManager.class);

    public final ConcurrentHashMap<Integer, House> houseList = new ConcurrentHashMap<>();

    public void init() {
        houseList.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_houses")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int itemId = set.getInt("sign_id");
                    int roomId = set.getInt("room_id");
                    int ownerId = set.getInt("owner_id");
                    int cost = set.getInt("cost");
                    boolean forSale = set.getString("for_sale").equals("1");
                    int level = set.getInt("level");
                    String[] upgrades = set.getString("upgrades").split(",");
                    boolean isLocked = set.getString("is_locked").equals("1");
                    int insideRoomId = set.getInt("inside_room_id");
                    int doorX = set.getInt("door_x");
                    int doorY = set.getInt("door_y");
                    double doorZ = set.getDouble("door_z");
                    int type = set.getInt("type");
                    long lastForcing = set.getLong("last_forcing");
                    String[] space = set.getString("space").split(";");

                    House house = new House(itemId, roomId, ownerId, cost, forSale, level, upgrades, isLocked, insideRoomId, doorX, doorY, doorZ, type, lastForcing, space);
                    houseList.put(itemId, house);
                }
            }

            LOGGER.info("HouseManager -> Loaded {} houses from database.", houseList.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load houses from database", e);
        }
    }

    public List<House> getHouseByOwnerId(int ownerId) {
        if (ownerId == 0) return null;
        List<House> list = new ArrayList<>();
        for (House house : houseList.values()) {
            if (house.getOwnerId() == ownerId) {
                list.add(house);
            }
        }
        return list.isEmpty() ? null : list;
    }

    public House getHouseBySignItem(int itemId) {
        if (itemId == 0) return null;
        return houseList.get(itemId);
    }

    public List<House> getHousesBySignRoomId(int roomId) {
        List<House> list = new ArrayList<>();
        for (House house : houseList.values()) {
            if (house.getRoomId() == roomId) {
                list.add(house);
            }
        }
        return list;
    }

    public List<House> getTerrainsBySignRoomId(int roomId) {
        List<House> list = new ArrayList<>();
        for (House house : houseList.values()) {
            if (house.getRoomId() == roomId && house.getType() == 3) {
                list.add(house);
            }
        }
        return list;
    }

    public House getHouseByPosition(int roomId, int x, int y, double z) {
        for (House house : houseList.values()) {
            if (house.getRoomId() == roomId && house.getDoorX() == x && house.getDoorY() == y && Math.abs(house.getDoorZ() - z) < 0.1) {
                return house;
            }
        }
        return null;
    }

    public House getHouseByInsideRoom(int insideRoomId) {
        for (House house : houseList.values()) {
            if (house.getInsideRoomId() == insideRoomId) {
                return house;
            }
        }
        return null;
    }

    public House getTerrainByInsideRoom(int insideRoomId) {
        for (House house : houseList.values()) {
            if (house.getInsideRoomId() == insideRoomId && house.getType() == 3) {
                return house;
            }
        }
        return null;
    }
}
