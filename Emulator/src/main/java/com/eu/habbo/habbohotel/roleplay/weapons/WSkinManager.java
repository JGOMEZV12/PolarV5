package com.eu.habbo.habbohotel.roleplay.weapons;

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

public class WSkinManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSkinManager.class);

    public static final ConcurrentHashMap<String, WSkin> wSkins = new ConcurrentHashMap<>();
    public static final List<Integer> enables = new ArrayList<>();
    public static final List<Integer> handItems = new ArrayList<>();

    public static void initialize() {
        wSkins.clear();
        enables.clear();
        handItems.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_weapons_skins")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String name2 = set.getString("name2");
                    String publicName = set.getString("publicname");
                    int effectId = set.getInt("effectid");
                    int handItem = set.getInt("handitem");
                    int range = set.getInt("firingrange");
                    int minDamage = set.getInt("mindamage");
                    int maxDamage = set.getInt("maxdamage");
                    int clipSize = set.getInt("clipsize");
                    int cost = set.getInt("cost");
                    int stock = set.getInt("stock");

                    WSkin skin = new WSkin(
                            id,
                            name,
                            name2,
                            publicName,
                            effectId,
                            handItem,
                            range,
                            minDamage,
                            maxDamage,
                            clipSize,
                            cost,
                            stock);

                    wSkins.put(name, skin);
                    enables.add(effectId);
                    handItems.add(handItem);
                }
            }

            LOGGER.info("WSkinManager -> Cargado(s): {} skin de armas.", wSkins.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load weapon skins from database", e);
        }
    }

    public static WSkin getSkin(String name) {
        if (name == null) return null;
        return wSkins.get(name);
    }
}
