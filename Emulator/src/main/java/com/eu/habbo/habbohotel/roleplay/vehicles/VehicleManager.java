package com.eu.habbo.habbohotel.roleplay.vehicles;

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

public class VehicleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleManager.class);

    public static final ConcurrentHashMap<String, Vehicle> vehicles = new ConcurrentHashMap<>();
    public static final List<Integer> enables = new ArrayList<>();

    public static void initialize() {
        vehicles.clear();
        enables.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_vehicles")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    int itemId = set.getInt("item_id");
                    String itemName = set.getString("item_name");
                    int effectId = set.getInt("effect_id");
                    int price = set.getInt("price");
                    String model = set.getString("model");
                    String displayName = set.getString("display_name");
                    int maxFuel = set.getInt("max_fuel");
                    int maxTrunks = set.getInt("max_trunks");
                    int carType = set.getInt("type");
                    int maxDoors = set.getInt("max_passengers");
                    int carCorp = set.getInt("jobid");
                    int fastCar = set.getInt("fast");

                    Vehicle vehicle = new Vehicle(
                            id,
                            itemId,
                            itemName,
                            effectId,
                            price,
                            model,
                            displayName,
                            maxFuel,
                            maxTrunks,
                            carType,
                            maxDoors,
                            carCorp,
                            fastCar);

                    vehicles.put(model, vehicle);
                    enables.add(effectId);
                }
            }

            LOGGER.info("VehicleManager -> Loaded {} roleplay vehicles.", vehicles.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load roleplay vehicles from database", e);
        }
    }

    public static Vehicle getVehicle(String name) {
        if (name == null) return null;
        return vehicles.get(name);
    }

    public static List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }
}
