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

public class WeaponManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WeaponManager.class);

    public static final ConcurrentHashMap<String, Weapon> weapons = new ConcurrentHashMap<>();
    public static final List<Integer> enables = new ArrayList<>();
    public static final List<Integer> handItems = new ArrayList<>();

    public static void initialize() {
        weapons.clear();
        enables.clear();
        handItems.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_weapons")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    String name = set.getString("name");
                    String publicName = set.getString("publicname");
                    String firingText = set.getString("firingtext");
                    String equipText = set.getString("equiptext");
                    String unEquipText = set.getString("unequiptext");
                    String reloadText = set.getString("reloadtext");
                    int energy = set.getInt("energy");
                    int effectId = set.getInt("effectid");
                    int handItem = set.getInt("handitem");
                    int range = set.getInt("firingrange");
                    int minDamage = set.getInt("mindamage");
                    int maxDamage = set.getInt("maxdamage");
                    int clipSize = set.getInt("clipsize");
                    int reloadTime = set.getInt("reloadtime");
                    int cost = set.getInt("cost");
                    int costFine = set.getInt("costfine");
                    int stock = set.getInt("stock");
                    int levelRequirement = set.getInt("level_requirement");
                    int wLife = set.getInt("life");
                    boolean isVip = set.getString("vip").equals("1");

                    WeaponCategory category = WeaponCategory.fromString(set.getString("category"));

                    Weapon weapon = new Weapon(
                            id,
                            name,
                            publicName,
                            firingText,
                            equipText,
                            unEquipText,
                            reloadText,
                            energy,
                            effectId,
                            handItem,
                            range,
                            minDamage,
                            maxDamage,
                            clipSize,
                            reloadTime,
                            cost,
                            costFine,
                            stock,
                            levelRequirement,
                            true,
                            clipSize,
                            wLife,
                            isVip,
                            0,
                            category);

                    weapons.put(name, weapon);
                    enables.add(effectId);
                    handItems.add(handItem);
                }
            }

            LOGGER.info("WeaponManager -> Cargado(s): {} armas.", weapons.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load weapons from database", e);
        }
    }

    public static Weapon getWeapon(String name) {
        if (name == null) return null;
        return weapons.get(name);
    }
}
