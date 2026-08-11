package com.eu.habbo.habbohotel.roleplay.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.roleplay.farming.FarmingStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDataHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDataHandler.class);

    public UserDataHandler() {
    }

    public static boolean loadData(int userId, RoleplayUser rp) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            // 1. Load rp_stats
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_stats WHERE id = ? LIMIT 1")) {
                statement.setInt(1, userId);
                try (ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        rp.setLevel(set.getInt("level"));
                        rp.setLevelEXP(set.getInt("level_exp"));
                        rp.setJobId(set.getInt("job_id"));
                        rp.setJobRank(set.getInt("job_rank"));
                        rp.setJobRequest(set.getInt("job_request"));
                        rp.setMaxHealth(set.getInt("maxhealth"));
                        rp.setCurHealth(set.getInt("curhealth"));
                        rp.setMaxEnergy(set.getInt("maxenergy"));
                        rp.setCurEnergy(set.getInt("curenergy"));
                        rp.setCurAlcohol(set.getInt("curalcohol"));
                        rp.setMaxAlcohol(set.getInt("maxalcohol"));
                        rp.setArmor(set.getInt("kevlar"));
                        rp.setHunger(set.getInt("hunger"));
                        rp.setSida(set.getInt("sida"));
                        rp.setHygiene(set.getInt("hygiene"));
                        rp.setAnimo(set.getInt("animo"));
                        rp.setPoop(set.getInt("poop"));
                        rp.setIntelligence(set.getInt("intelligence"));
                        rp.setStrength(set.getInt("strength"));
                        rp.setStamina(set.getInt("stamina"));
                        rp.setIntelligenceEXP(set.getInt("intelligence_exp"));
                        rp.setStrengthEXP(set.getInt("strength_exp"));
                        rp.setStaminaEXP(set.getInt("stamina_exp"));
                        rp.setStun(set.getString("is_stun").equals("1"));
                        rp.setDead(set.getString("is_dead").equals("1"));
                        rp.setDeadTimeLeft(set.getInt("dead_time_left"));
                        rp.setJailed(set.getString("is_jailed").equals("1"));
                        rp.setJailedTimeLeft(set.getInt("jailed_time_left"));
                        rp.setWanted(set.getString("is_wanted").equals("1"));
                        rp.setWantedLevel(set.getInt("wanted_level"));
                        rp.setWantedTimeLeft(set.getInt("wanted_time_left"));
                        rp.setOnCheckProbation(set.getString("on_probation").equals("1"));
                        rp.setProbationTimeLeft(set.getInt("probation_time_left"));
                        rp.setSendhomeTimeLeft(set.getInt("sendhome_time_left"));
                        rp.setCuffed(set.getString("is_cuffed").equals("1"));
                        rp.setCuffedTimeLeft(set.getInt("cuffed_time_left"));
                        rp.setBankAccount(set.getInt("bank_account"));
                        rp.setBankTarget(set.getInt("bank_target"));
                        rp.setBankChequings(set.getInt("bank_chequings"));
                        rp.setBankSavings(set.getInt("bank_savings"));
                    } else {
                        LOGGER.warn("No rp_stats row found for user ID: {}", userId);
                        seedDefaultStats(connection, userId, rp);
                    }
                }
            }

            // 2. Ensure rp_stats_cooldowns row exists
            try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM rp_stats_cooldowns WHERE id = ?")) {
                statement.setInt(1, userId);
                try (ResultSet set = statement.executeQuery()) {
                    if (!set.next()) {
                        try (PreparedStatement insert = connection.prepareStatement("INSERT INTO rp_stats_cooldowns (id) VALUES (?)")) {
                            insert.setInt(1, userId);
                            insert.executeUpdate();
                        }
                    }
                }
            }

            // 3. Ensure rp_stats_farming row exists
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_stats_farming WHERE id = ? LIMIT 1")) {
                statement.setInt(1, userId);
                try (ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        FarmingStats farming = new FarmingStats(set);
                        rp.setFarmingStats(farming);
                    } else {
                        try (PreparedStatement insert = connection.prepareStatement(
                                "INSERT INTO rp_stats_farming (id, level, exp, has_seed_satchel, has_plant_satchel, " +
                                        "blue_starflower, yellow_starflower, pink_dahlia, yellow_plumeria, pink_primrose, " +
                                        "blue_primrose, yellow_primrose, yellow_dahlia, blue_plumeria, pink_plumeria, " +
                                        "red_starflower, blue_dahlia) VALUES (?, 1, 0, '0', '0', " +
                                        "'0:0', '0:0', '0:0', '0:0', '0:0', '0:0', '0:0', '0:0', '0:0', '0:0', '0:0', '0:0')")) {
                            insert.setInt(1, userId);
                            insert.executeUpdate();
                        }
                        // Re-query newly created farming stats
                        try (PreparedStatement statement2 = connection.prepareStatement("SELECT * FROM rp_stats_farming WHERE id = ? LIMIT 1")) {
                            statement2.setInt(1, userId);
                            try (ResultSet set2 = statement2.executeQuery()) {
                                if (set2.next()) {
                                    rp.setFarmingStats(new FarmingStats(set2));
                                }
                            }
                        }
                    }
                }
            }

            return true;
        } catch (SQLException e) {
            LOGGER.error("Failed to load RoleplayUser data for user ID: {}", userId, e);
            return false;
        }
    }

    private static void seedDefaultStats(Connection connection, int userId, RoleplayUser rp) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO rp_stats (id, level, level_exp, maxhealth, curhealth, maxenergy, curenergy) VALUES (?, 1, 0, 100, 100, 100, 100)")) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
        rp.setLevel(1);
        rp.setMaxHealth(100);
        rp.setCurHealth(100);
        rp.setMaxEnergy(100);
        rp.setCurEnergy(100);
    }

    public static boolean saveData(int userId, RoleplayUser rp) {
        String query = "UPDATE rp_stats SET " +
                "level = ?, level_exp = ?, job_id = ?, job_rank = ?, job_request = ?, " +
                "maxhealth = ?, curhealth = ?, maxenergy = ?, curenergy = ?, curalcohol = ?, maxalcohol = ?, " +
                "kevlar = ?, hunger = ?, sida = ?, hygiene = ?, animo = ?, poop = ?, " +
                "intelligence = ?, strength = ?, stamina = ?, intelligence_exp = ?, strength_exp = ?, stamina_exp = ?, " +
                "is_stun = ?, is_dead = ?, dead_time_left = ?, is_jailed = ?, jailed_time_left = ?, " +
                "is_wanted = ?, wanted_level = ?, wanted_time_left = ?, on_probation = ?, probation_time_left = ?, " +
                "sendhome_time_left = ?, is_cuffed = ?, cuffed_time_left = ?, " +
                "bank_account = ?, bank_target = ?, bank_chequings = ?, bank_savings = ? " +
                "WHERE id = ?";

        String farmingQuery = "UPDATE rp_stats_farming SET " +
                "level = ?, exp = ?, has_seed_satchel = ?, has_plant_satchel = ?, " +
                "blue_starflower = ?, yellow_starflower = ?, pink_dahlia = ?, yellow_plumeria = ?, " +
                "pink_primrose = ?, blue_primrose = ?, yellow_primrose = ?, yellow_dahlia = ?, " +
                "blue_plumeria = ?, pink_plumeria = ?, red_starflower = ?, blue_dahlia = ? " +
                "WHERE id = ?";

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            // 1. Save rp_stats
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, rp.getLevel());
                statement.setInt(2, rp.getLevelEXP());
                statement.setInt(3, rp.getJobId());
                statement.setInt(4, rp.getJobRank());
                statement.setInt(5, rp.getJobRequest());
                statement.setInt(6, rp.getMaxHealth());
                statement.setInt(7, rp.getCurHealth());
                statement.setInt(8, rp.getMaxEnergy());
                statement.setInt(9, rp.getCurEnergy());
                statement.setInt(10, rp.getCurAlcohol());
                statement.setInt(11, rp.getMaxAlcohol());
                statement.setInt(12, rp.getArmor());
                statement.setInt(13, rp.getHunger());
                statement.setInt(14, rp.getSida());
                statement.setInt(15, rp.getHygiene());
                statement.setInt(16, rp.getAnimo());
                statement.setInt(17, rp.getPoop());
                statement.setInt(18, rp.getIntelligence());
                statement.setInt(19, rp.getStrength());
                statement.setInt(20, rp.getStamina());
                statement.setInt(21, rp.getIntelligenceEXP());
                statement.setInt(22, rp.getStrengthEXP());
                statement.setInt(23, rp.getStaminaEXP());
                statement.setString(24, rp.isStun() ? "1" : "0");
                statement.setString(25, rp.isDead() ? "1" : "0");
                statement.setInt(26, rp.getDeadTimeLeft());
                statement.setString(27, rp.isJailed() ? "1" : "0");
                statement.setInt(28, rp.getJailedTimeLeft());
                statement.setString(29, rp.isWanted() ? "1" : "0");
                statement.setInt(30, rp.getWantedLevel());
                statement.setInt(31, rp.getWantedTimeLeft());
                statement.setString(32, rp.isOnProbation() ? "1" : "0");
                statement.setInt(33, rp.getProbationTimeLeft());
                statement.setInt(34, rp.getSendhomeTimeLeft());
                statement.setString(35, rp.isCuffed() ? "1" : "0");
                statement.setInt(36, rp.getCuffedTimeLeft());
                statement.setInt(37, rp.getBankAccount());
                statement.setInt(38, rp.getBankTarget());
                statement.setInt(39, rp.getBankChequings());
                statement.setInt(40, rp.getBankSavings());
                statement.setInt(41, userId);

                statement.executeUpdate();
            }

            // 2. Save rp_stats_farming
            if (rp.getFarmingStats() != null) {
                try (PreparedStatement statement = connection.prepareStatement(farmingQuery)) {
                    FarmingStats fs = rp.getFarmingStats();
                    statement.setInt(1, fs.getLevel());
                    statement.setInt(2, fs.getExp());
                    statement.setString(3, fs.isHasSeedSatchel() ? "1" : "0");
                    statement.setString(4, fs.isHasPlantSatchel() ? "1" : "0");

                    statement.setString(5, fs.getSeedSatchel().getBlueStarflowerSeeds() + ":" + fs.getPlantSatchel().getBlueStarflowers());
                    statement.setString(6, fs.getSeedSatchel().getYellowStarflowerSeeds() + ":" + fs.getPlantSatchel().getYellowStarflowers());
                    statement.setString(7, fs.getSeedSatchel().getPinkDahliaSeeds() + ":" + fs.getPlantSatchel().getPinkDahlias());
                    statement.setString(8, fs.getSeedSatchel().getYellowPlumeriaSeeds() + ":" + fs.getPlantSatchel().getYellowPlumerias());
                    statement.setString(9, fs.getSeedSatchel().getPinkPrimroseSeeds() + ":" + fs.getPlantSatchel().getPinkPrimroses());
                    statement.setString(10, fs.getSeedSatchel().getBluePrimroseSeeds() + ":" + fs.getPlantSatchel().getBluePrimroses());
                    statement.setString(11, fs.getSeedSatchel().getYellowPrimroseSeeds() + ":" + fs.getPlantSatchel().getYellowPrimroses());
                    statement.setString(12, fs.getSeedSatchel().getYellowDahliaSeeds() + ":" + fs.getPlantSatchel().getYellowDahlias());
                    statement.setString(13, fs.getSeedSatchel().getBluePlumeriaSeeds() + ":" + fs.getPlantSatchel().getBluePlumerias());
                    statement.setString(14, fs.getSeedSatchel().getPinkPlumeriaSeeds() + ":" + fs.getPlantSatchel().getPinkPlumerias());
                    statement.setString(15, fs.getSeedSatchel().getRedStarflowerSeeds() + ":" + fs.getPlantSatchel().getRedStarflowers());
                    statement.setString(16, fs.getSeedSatchel().getBlueDahliaSeeds() + ":" + fs.getPlantSatchel().getBlueDahlias());
                    statement.setInt(17, userId);

                    statement.executeUpdate();
                }
            }

            return true;
        } catch (SQLException e) {
            LOGGER.error("Failed to save RoleplayUser/Farming data for user ID: {}", userId, e);
            return false;
        }
    }
}
