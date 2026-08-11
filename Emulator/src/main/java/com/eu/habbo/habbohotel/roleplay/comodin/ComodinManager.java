package com.eu.habbo.habbohotel.roleplay.comodin;

import com.eu.habbo.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ComodinManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ComodinManager.class);

    public static final ConcurrentHashMap<Integer, Comodin> comodines = new ConcurrentHashMap<>();

    public static void initialize() {
        comodines.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_comodin");
             ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                int id = set.getInt("id");
                int furniId = set.getInt("furni_id");
                int roomId = set.getInt("room_id");
                String action = set.getString("action");

                Comodin comodin = new Comodin(id, furniId, roomId, action);
                comodines.put(furniId, comodin);
            }

            LOGGER.info("ComodinManager -> Loaded {} roleplay comodines.", comodines.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize ComodinManager", e);
        }
    }

    public static Comodin getComodin(int id) {
        return comodines.get(id);
    }
}
