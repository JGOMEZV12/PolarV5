package com.eu.habbo.habbohotel.roleplay.groups;

import com.eu.habbo.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class RoleplayGroupManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayGroupManager.class);

    public static final ConcurrentHashMap<Integer, RoleplayGroup> jobs = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, RoleplayGroup> gangs = new ConcurrentHashMap<>();

    public static void initialize() {
        jobs.clear();
        gangs.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            // 1. Load rp_jobs
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_jobs");
                 ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("desc");
                    String badge = set.getString("badge");
                    int ownerId = set.getInt("owner_id");
                    int roomId = set.getInt("room_id");
                    int balance = set.getInt("balance");

                    RoleplayGroup group = new RoleplayGroup(id, name, description, badge, ownerId, roomId, balance, 0);
                    jobs.put(id, group);
                }
            }

            // 2. Load rp_gangs
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_gangs");
                 ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String description = set.getString("desc");
                    String badge = set.getString("badge");
                    int ownerId = set.getInt("owner_id");
                    int roomId = set.getInt("room_id");
                    int balance = set.getInt("bank_balance");
                    int medipacks = set.getInt("medipacks");

                    RoleplayGroup group = new RoleplayGroup(id, name, description, badge, ownerId, roomId, balance, medipacks);
                    gangs.put(id, group);
                }
            }

            LOGGER.info("RoleplayGroupManager -> Loaded {} Jobs and {} Gangs.", jobs.size(), gangs.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load roleplay groups from database", e);
        }
    }

    public static RoleplayGroup getGroup(int id) {
        if (id >= 1000) {
            return gangs.get(id);
        } else {
            return jobs.get(id);
        }
    }
}
