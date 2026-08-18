package com.eu.habbo.habbohotel.roleplay.misc;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleplayData {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayData.class);

    private static final ConcurrentHashMap<String, Map<String, String>> data = new ConcurrentHashMap<>();

    public static void initialize() {
        data.clear();
        loadData();
        LOGGER.info("RoleplayData -> Loaded {} entries.", data.size());
    }

    public static void loadData() {
        data.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `rp_data`");
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                String mainKey = set.getString("MainKey");
                String dataString = set.getString("Data");

                if (mainKey == null || dataString == null) {
                    continue;
                }

                mainKey = mainKey.toLowerCase();
                dataString = dataString.replace(" ", "");

                String[] dataArray = dataString.split("\\|");
                Map<String, String> keyValue = new HashMap<>();

                for (String part : dataArray) {
                    if (!part.contains(":")) {
                        continue;
                    }
                    String[] subParts = part.split(":");
                    if (subParts.length < 2) {
                        continue;
                    }
                    String subKey = subParts[0].toLowerCase();
                    String value = subParts[1];

                    if (subKey.isEmpty() || value.isEmpty()) {
                        continue;
                    }

                    keyValue.put(subKey, value);
                }

                data.put(mainKey, keyValue);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to load RoleplayData", e);
        }
    }

    public static String getData(String mainKey, String subKey) {
        if (mainKey == null || subKey == null) return null;
        mainKey = mainKey.toLowerCase();
        subKey = subKey.toLowerCase();

        Map<String, String> subMap = data.get(mainKey);
        if (subMap != null) {
            return subMap.get(subKey);
        }
        return null;
    }
}
