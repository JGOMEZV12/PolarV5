package com.eu.habbo.habbohotel.roleplay.misc;

import com.eu.habbo.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlackListManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlackListManager.class);

    public static final List<Integer> blackList = new CopyOnWriteArrayList<>();

    public static void initialize() {
        blackList.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_blacklist");
             ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                blackList.add(set.getInt("id"));
            }

            LOGGER.info("BlackListManager -> Loaded {} blacklisted users.", blackList.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize BlackListManager", e);
        }
    }

    public static void addBlackList(int id) {
        if (blackList.contains(id)) {
            return;
        }

        blackList.add(id);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO `rp_blacklist` (`id`) VALUES (?)")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to add blacklist for ID: {}", id, e);
        }
    }

    public static void removeBlackList(int id) {
        if (!blackList.contains(id)) {
            return;
        }

        blackList.remove(Integer.valueOf(id));

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM `rp_blacklist` WHERE `id` = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to remove blacklist for ID: {}", id, e);
        }
    }
}
