package com.eu.habbo.habbohotel.roleplay.vehicles;

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

public class VehiclesOwnedManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(VehiclesOwnedManager.class);

    public static final ConcurrentHashMap<Integer, VehiclesOwned> vehiclesOwned = new ConcurrentHashMap<>();

    public void init() {
        vehiclesOwned.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_vehicles_owned")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    int furniId = set.getInt("furni_id");
                    int itemId = set.getInt("item_id");
                    int ownerId = set.getInt("owner");
                    int lastUserId = set.getInt("last_user");
                    String model = set.getString("model");
                    int fuel = set.getInt("fuel");
                    int km = set.getInt("km");
                    int state = set.getInt("state");
                    boolean traba = set.getString("traba").equals("1");
                    boolean alarm = set.getString("alarm").equals("1");
                    int location = set.getInt("location");
                    int x = set.getInt("x");
                    int y = set.getInt("y");
                    double z = set.getDouble("z");
                    String[] baul = set.getString("baul").split(";");
                    boolean baulOpen = set.getString("baul_state").equals("1");
                    int carLife = set.getInt("life");

                    VehiclesOwned vo = new VehiclesOwned(id, furniId, itemId, ownerId, lastUserId, model, fuel, km, state, traba, alarm, location, x, y, z, baul, baulOpen, carLife);
                    vehiclesOwned.put(id, vo);
                }
            }

            LOGGER.info("VehiclesOwnedManager -> Loaded {} VehiclesOwned from database.", vehiclesOwned.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load VehiclesOwned from database", e);
        }
    }

    public List<VehiclesOwned> getAllVehiclesOwned() {
        return new ArrayList<>(vehiclesOwned.values());
    }

    public VehiclesOwned getVehiclesOwned(int id) {
        return vehiclesOwned.get(id);
    }

    public List<VehiclesOwned> getMyVehiclesOwned(int ownerId) {
        List<VehiclesOwned> list = new ArrayList<>();
        for (VehiclesOwned vo : vehiclesOwned.values()) {
            if (vo.getOwnerId() == ownerId) {
                list.add(vo);
            }
        }
        return list;
    }

    public void deleteVehicleOwned(int id, boolean toDb) {
        vehiclesOwned.remove(id);
        if (toDb) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_vehicles_owned WHERE id = ? LIMIT 1")) {
                statement.setInt(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Failed to delete VehiclesOwned ID {}", id, e);
            }
        }
    }
}
