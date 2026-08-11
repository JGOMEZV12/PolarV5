package com.eu.habbo.habbohotel.roleplay.phones;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PhonesManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhonesManager.class);

    public static final ConcurrentHashMap<Integer, Phones> phones = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, PhonesOwned> phonesOwned = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, PhonesApps> phonesApps = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, PhonesAppsOwned> phonesAppsOwned = new ConcurrentHashMap<>();

    public static void initialize() {
        phones.clear();
        phonesOwned.clear();
        phonesApps.clear();
        phonesAppsOwned.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            // 1. Load rp_phones
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_phones");
                    ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String modelName = set.getString("model_name");
                    String displayName = set.getString("display_name");
                    int price = set.getInt("price");
                    int effectId = set.getInt("effect_id");
                    int screenSlots = set.getInt("screen_slots");
                    int dockSlots = set.getInt("dock_slots");

                    Phones phone = new Phones(id, modelName, displayName, price, effectId, screenSlots, dockSlots);
                    phones.put(id, phone);
                }
            }

            // 2. Load rp_phones_owned
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_phones_owned");
                    ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    int phoneId = set.getInt("phone_id");
                    int ownerId = set.getInt("user_id");
                    String phoneNumber = set.getString("phone_number");

                    PhonesOwned owned = new PhonesOwned(id, phoneId, ownerId, phoneNumber);
                    phonesOwned.put(id, owned);
                }
            }

            // 3. Load rp_phones_apps
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_phones_apps");
                    ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String displayName = set.getString("display_name");
                    String icon = set.getString("icon");
                    String developerName = set.getString("developer_name");
                    String code = set.getString("code");
                    int price = set.getInt("price");
                    String version = set.getString("version");

                    PhonesApps app = new PhonesApps(id, name, displayName, icon, developerName, code, price, version);
                    phonesApps.put(id, app);
                }
            }

            // 4. Load rp_phones_apps_owned
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_phones_apps_owned");
                    ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    int phoneId = set.getInt("phone_id");
                    int appId = set.getInt("app_id");
                    int screenId = set.getInt("screen_id");
                    int slotId = set.getInt("slot_id");
                    String extradata = set.getString("extradata");

                    PhonesAppsOwned ownedApp = new PhonesAppsOwned(id, phoneId, appId, screenId, slotId, extradata);
                    phonesAppsOwned.put(id, ownedApp);
                }
            }

            LOGGER.info(
                    "PhonesManager -> Loaded {} phones, {} owned, {} apps, and {} owned apps.",
                    phones.size(),
                    phonesOwned.size(),
                    phonesApps.size(),
                    phonesAppsOwned.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load phones from database", e);
        }
    }
}
