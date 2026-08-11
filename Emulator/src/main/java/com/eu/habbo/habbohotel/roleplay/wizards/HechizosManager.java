package com.eu.habbo.habbohotel.roleplay.wizards;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HechizosManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HechizosManager.class);

    public static final ConcurrentHashMap<String, Hechizos> hechizos = new ConcurrentHashMap<>();

    public static void initialize() {
        hechizos.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_hechizos")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String publicName = set.getString("publicname");
                    String message = set.getString("message");
                    int power = set.getInt("power");
                    int firingRange = set.getInt("firingrange");
                    int shields = set.getInt("shields");
                    int firingDamage = set.getInt("firingdamage");
                    int health = set.getInt("health");
                    int cost = set.getInt("cost");
                    int costFine = set.getInt("costfine");
                    int stock = set.getInt("stock");

                    Hechizos spell = new Hechizos(
                            id,
                            name,
                            publicName,
                            message,
                            power,
                            firingRange,
                            shields,
                            firingDamage,
                            health,
                            cost,
                            costFine,
                            stock);
                    hechizos.put(name, spell);
                }
            }

            LOGGER.info("HechizosManager -> Loaded {} magic spells.", hechizos.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load magic spells from database", e);
        }
    }

    public static Hechizos getWizard(String name) {
        if (name == null) return null;
        return hechizos.get(name);
    }
}
