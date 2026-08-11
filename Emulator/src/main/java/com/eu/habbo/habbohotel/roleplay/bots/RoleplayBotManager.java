package com.eu.habbo.habbohotel.roleplay.bots;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleplayBotManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayBotManager.class);

    public static final ConcurrentHashMap<Integer, RoleplayBot> cachedRoleplayBots = new ConcurrentHashMap<>();

    public static void initialize() {
        cachedRoleplayBots.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("SELECT * FROM rp_bots WHERE spawn_id > '0'")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int id = set.getInt("id");
                    int ownerId = set.getInt("owner_id");
                    String name = set.getString("name");
                    String gender = set.getString("gender");
                    String figure = set.getString("figure");
                    String motto = set.getString("motto");
                    int maxHealth = set.getInt("max_health");
                    int curHealth = set.getInt("cur_health");
                    int strength = set.getInt("str");
                    int level = set.getInt("level");
                    int spawnId = set.getInt("spawn_id");
                    int spawnX = set.getInt("spawn_x");
                    int spawnY = set.getInt("spawn_y");
                    double spawnZ = set.getDouble("spawn_z");
                    int spawnRot = set.getInt("spawn_rot");
                    String aiType = set.getString("ai_type");
                    int roamInterval = set.getInt("roam_interval");
                    int attackInterval = set.getInt("attack_interval");
                    int followInterval = set.getInt("follow_interval");
                    int stayInterval = set.getInt("stay_interval");
                    boolean roamBot = set.getString("roam_bot").equals("1");
                    boolean roamCityBot = set.getString("roam_city_bot").equals("1");
                    boolean addableBot = set.getString("addable_bot").equals("1");
                    int corporationId = set.getInt("corporation_id");
                    String stopworkItem = set.getString("stopwork_item");
                    String workUniform = set.getString("work_uniform");
                    boolean canBeAttacked = set.getString("can_be_attacked").equals("1");
                    int attackPos = set.getInt("attack_pos");
                    String actionOdds = set.getString("action_odds");
                    int speechTimer = set.getInt("speech_timer");
                    String petData = set.getString("pet_data");

                    RoleplayBot bot = new RoleplayBot(
                            id,
                            ownerId,
                            name,
                            gender,
                            figure,
                            motto,
                            maxHealth,
                            curHealth,
                            strength,
                            level,
                            spawnId,
                            spawnX,
                            spawnY,
                            spawnZ,
                            spawnRot,
                            aiType,
                            roamInterval,
                            attackInterval,
                            followInterval,
                            stayInterval,
                            roamBot,
                            roamCityBot,
                            addableBot,
                            corporationId,
                            stopworkItem,
                            workUniform,
                            canBeAttacked,
                            attackPos,
                            actionOdds,
                            speechTimer,
                            petData);

                    cachedRoleplayBots.put(id, bot);
                }
            }

            LOGGER.info("RoleplayBotManager -> Loaded {} roleplay bots.", cachedRoleplayBots.size());

        } catch (SQLException e) {
            LOGGER.error("Failed to load roleplay bots from database", e);
        }
    }
}
