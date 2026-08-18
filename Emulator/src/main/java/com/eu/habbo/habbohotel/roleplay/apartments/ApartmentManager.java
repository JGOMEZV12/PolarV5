package com.eu.habbo.habbohotel.roleplay.apartments;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApartmentManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApartmentManager.class);

    public static final ConcurrentHashMap<Integer, Apartment> apartments = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, ApartmentOwned> apartmentsOwned = new ConcurrentHashMap<>();

    public static void initialize() {
        apartments.clear();
        apartmentsOwned.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            // 1. Load rp_apartments
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_apartments");
                    ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String modelName = set.getString("model_name");
                    int tiles = set.getInt("tiles");
                    String image = set.getString("image");
                    int price = set.getInt("price");

                    Apartment apt = new Apartment(id, modelName, tiles, image, price);
                    apartments.put(id, apt);
                }
            }

            // 2. Load rp_apartments_owned
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_apartments_owned");
                    ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    int apartId = set.getInt("apart_id");
                    int roomId = set.getInt("room_id");
                    int lobbyId = set.getInt("lobby_id");
                    int owner = set.getInt("owner");
                    boolean forSale = set.getString("for_sale").equals("1");
                    int price = set.getInt("price");
                    String paymentType = set.getString("payment_type");
                    boolean floorEditor = set.getString("floor_editor").equals("1");

                    ApartmentOwned owned = new ApartmentOwned(
                            id, apartId, roomId, lobbyId, owner, forSale, price, paymentType, floorEditor);
                    apartmentsOwned.put(id, owned);
                }
            }

            LOGGER.info(
                    "ApartmentManager -> Loaded {} apartments and {} owned apartments.",
                    apartments.size(),
                    apartmentsOwned.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load apartments from database", e);
        }
    }
}
