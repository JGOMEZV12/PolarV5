package com.eu.habbo.habbohotel.roleplay.vehicles;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VehicleJobsManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleJobsManager.class);

    public static final ConcurrentHashMap<Integer, VehicleJobs> vehicles = new ConcurrentHashMap<>();

    public static void initialize() {
        vehicles.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_jobs_cars")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    int roomId = set.getInt("room_id");
                    int baseItem = set.getInt("base_item");
                    int x = set.getInt("x");
                    int y = set.getInt("y");
                    double z = set.getDouble("z");
                    int rot = set.getInt("rot");
                    int jobId = set.getInt("job_id");

                    VehicleJobs vj = new VehicleJobs(id, roomId, baseItem, x, y, z, rot, jobId);
                    vehicles.put(id, vj);
                }
            }

            LOGGER.info("VehicleJobsManager -> Loaded {} roleplay jobs vehicles.", vehicles.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load roleplay jobs vehicles from database", e);
        }
    }
}
