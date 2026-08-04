package com.eu.habbo.habbohotel.roleplay.users;

import com.eu.habbo.Emulator;
import com.eu.habbo.plugin.EventHandler;
import com.eu.habbo.plugin.events.users.UserDisconnectEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Portado desde: Polar RP/HabboRoleplay/RoleplayUsers/UserDataHandler.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Administra las sesiones de Roleplay de los usuarios, gestionando
 * lazy loading, guardado persistente asíncrono, y liberación de memoria.
 * Mejoras: Tarea en segundo plano para decrementar cooldowns cada segundo de forma eficiente.
 */
public class RoleplayUserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayUserManager.class);

    private static final ConcurrentHashMap<Integer, RoleplayUser> users = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, RoleplayCooldowns> cooldowns = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "Roleplay-Cooldowns-Thread");
        thread.setDaemon(true);
        return thread;
    });

    static {
        scheduler.scheduleAtFixedRate(RoleplayUserManager::tickCooldowns, 1, 1, TimeUnit.SECONDS);
    }

    private static void tickCooldowns() {
        try {
            for (RoleplayCooldowns cd : cooldowns.values()) {
                if (cd.getRobbery() > 0) {
                    cd.setRobbery(cd.getRobbery() - 1);
                }
                if (cd.getTextCooldown() > 0) {
                    cd.setTextCooldown(cd.getTextCooldown() - 1);
                }
                if (cd.getRobberyBank() > 0) {
                    cd.setRobberyBank(cd.getRobberyBank() - 1);
                }
                if (cd.getMedipacks() > 0) {
                    cd.setMedipacks(cd.getMedipacks() - 1);
                }
                if (cd.getPsvmode() > 0) {
                    cd.setPsvmode(cd.getPsvmode() - 1);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error en la tarea de decremento de cooldowns de roleplay", e);
        }
    }

    public static ConcurrentHashMap<Integer, RoleplayUser> getUsers() {
        return users;
    }

    public static ConcurrentHashMap<Integer, RoleplayCooldowns> getCooldowns() {
        return cooldowns;
    }

    /**
     * Obtiene el RoleplayUser de forma lazy-loading. Si no está en memoria, se lee de la DB.
     * Si no existe en la DB, se registra con valores por defecto.
     */
    public static RoleplayUser getRoleplayUser(int userId) {
        if (users.containsKey(userId)) {
            return users.get(userId);
        }

        RoleplayUser user = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("SELECT * FROM rp_users WHERE id = ? LIMIT 1")) {
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    user = new RoleplayUser(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al cargar estadísticas de roleplay para el usuario " + userId, e);
        }

        if (user == null) {
            // Registrar usuario en rp_users con valores por defecto
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO rp_users (id) VALUES (?)")) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Error al insertar estadísticas de roleplay por defecto para el usuario " + userId, e);
            }

            // Intentar cargar de nuevo
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                    PreparedStatement statement =
                            connection.prepareStatement("SELECT * FROM rp_users WHERE id = ? LIMIT 1")) {
                statement.setInt(1, userId);
                try (ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        user = new RoleplayUser(set);
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Error al re-cargar estadísticas de roleplay para el usuario " + userId, e);
            }
        }

        if (user != null) {
            users.put(userId, user);
        }
        return user;
    }

    /**
     * Obtiene los Cooldowns de forma lazy-loading. Si no está en memoria, se lee de la DB.
     * Si no existe en la DB, se registra con valores por defecto.
     */
    public static RoleplayCooldowns getRoleplayCooldowns(int userId) {
        if (cooldowns.containsKey(userId)) {
            return cooldowns.get(userId);
        }

        RoleplayCooldowns cd = null;
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("SELECT * FROM rp_cooldowns WHERE id = ? LIMIT 1")) {
            statement.setInt(1, userId);
            try (ResultSet set = statement.executeQuery()) {
                if (set.next()) {
                    cd = new RoleplayCooldowns(set);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error al cargar cooldowns de roleplay para el usuario " + userId, e);
        }

        if (cd == null) {
            // Registrar usuario en rp_cooldowns con valores por defecto
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                    PreparedStatement statement =
                            connection.prepareStatement("INSERT INTO rp_cooldowns (id) VALUES (?)")) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Error al insertar cooldowns de roleplay por defecto para el usuario " + userId, e);
            }

            // Intentar cargar de nuevo
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                    PreparedStatement statement =
                            connection.prepareStatement("SELECT * FROM rp_cooldowns WHERE id = ? LIMIT 1")) {
                statement.setInt(1, userId);
                try (ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        cd = new RoleplayCooldowns(set);
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Error al re-cargar cooldowns de roleplay para el usuario " + userId, e);
            }
        }

        if (cd != null) {
            cooldowns.put(userId, cd);
        }
        return cd;
    }

    /**
     * Guarda de forma síncrona/directa los cambios de un usuario (para uso interno o persistencia final).
     */
    public static void saveRoleplayUser(RoleplayUser user) {
        if (user == null) return;

        String query = "UPDATE rp_users SET " + "level = ?, level_exp = ?, class = ?, permanent_class = ?, "
                + "job_id = ?, job_rank = ?, job_request = ?, sendhome_time_left = ?, "
                + "maxhealth = ?, curhealth = ?, maxenergy = ?, curenergy = ?, "
                + "curalcohol = ?, maxalcohol = ?, kevlar = ?, hunger = ?, "
                + "sida = ?, hygiene = ?, animo = ?, poop = ?, "
                + "intelligence = ?, strength = ?, stamina = ?, intelligence_exp = ?, "
                + "strength_exp = ?, stamina_exp = ?, passive_mode = ?, is_stun = ?, "
                + "is_dead = ?, dead_time_left = ?, is_jailed = ?, jailed_time_left = ?, "
                + "is_wanted = ?, wanted_level = ?, wanted_time_left = ?, on_probation = ?, "
                + "probation_time_left = ?, is_cuffed = ?, cuffed_time_left = ?, punches = ?, "
                + "kills = ?, hit_kills = ?, gun_kills = ?, deaths = ?, "
                + "cop_deaths = ?, time_worked = ?, arrests = ?, arrested = ?, "
                + "evasions = ?, bank_account = ?, bank_target = ?, bank_chequings = ?, "
                + "bank_savings = ?, weedbaul = ?, BasuLvl = ?, BasuXP = ?, "
                + "hunt_points = ?, hunt_skins = ?, ArmLvl = ?, ArmXP = ?, "
                + "MecLvl = ?, MecXP = ?, CamLvl = ?, CamXP = ?, "
                + "last_killed = ?, married_to = ?, hijo = ?, gang_id = ?, "
                + "gang_rank = ?, gang_request = ?, changename_count = ?, car = ?, "
                + "car_fuel = ?, weed = ?, cocaine = ?, botiquin = ?, "
                + "heroina = ?, caramelos = ?, medicina = ?, cigarette = ?, "
                + "pildora = ?, dynamite = ?, weedmateria = ?, unlocked_quests = ?, "
                + "brawl_wins = ?, cw_wins = ?, mw_wins = ?, soloqueue_wins = ?, "
                + "is_noob = ?, noob_time_left = ?, inmunidad_activada = ?, vip_banned = ?, "
                + "last_coordinates = ? WHERE id = ?";

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, user.getLevel());
            statement.setInt(2, user.getLevelExp());
            statement.setString(3, user.getRpClass());
            statement.setString(4, user.isPermanentClass() ? "1" : "0");
            statement.setInt(5, user.getJobId());
            statement.setInt(6, user.getJobRank());
            statement.setInt(7, user.getJobRequest());
            statement.setInt(8, user.getSendHomeTimeLeft());
            statement.setInt(9, user.getMaxHealth());
            statement.setInt(10, user.getCurHealth());
            statement.setInt(11, user.getMaxEnergy());
            statement.setInt(12, user.getCurEnergy());
            statement.setInt(13, user.getCurAlcohol());
            statement.setInt(14, user.getMaxAlcohol());
            statement.setInt(15, user.getArmor());
            statement.setInt(16, user.getHunger());
            statement.setInt(17, user.getSida());
            statement.setInt(18, user.getHygiene());
            statement.setInt(19, user.getAnimo());
            statement.setInt(20, user.getPoop());
            statement.setInt(21, user.getIntelligence());
            statement.setInt(22, user.getStrength());
            statement.setInt(23, user.getStamina());
            statement.setInt(24, user.getIntelligenceExp());
            statement.setInt(25, user.getStrengthExp());
            statement.setInt(26, user.getStaminaExp());
            statement.setString(27, user.isPassiveMode() ? "1" : "0");
            statement.setString(28, user.isStun() ? "1" : "0");
            statement.setString(29, user.isDead() ? "1" : "0");
            statement.setInt(30, user.getDeadTimeLeft());
            statement.setString(31, user.isJailed() ? "1" : "0");
            statement.setInt(32, user.getJailedTimeLeft());
            statement.setString(33, user.isWanted() ? "1" : "0");
            statement.setInt(34, user.getWantedLevel());
            statement.setInt(35, user.getWantedTimeLeft());
            statement.setString(36, user.isOnProbation() ? "1" : "0");
            statement.setInt(37, user.getProbationTimeLeft());
            statement.setString(38, user.isCuffed() ? "1" : "0");
            statement.setInt(39, user.getCuffedTimeLeft());
            statement.setInt(40, user.getPunches());
            statement.setInt(41, user.getKills());
            statement.setInt(42, user.getHitKills());
            statement.setInt(43, user.getGunKills());
            statement.setInt(44, user.getDeaths());
            statement.setInt(45, user.getCopDeaths());
            statement.setInt(46, user.getTimeWorked());
            statement.setInt(47, user.getArrests());
            statement.setInt(48, user.getArrested());
            statement.setInt(49, user.getEvasions());
            statement.setInt(50, user.getBankAccount());
            statement.setInt(51, user.getBankTarget());
            statement.setInt(52, user.getBankChequings());
            statement.setInt(53, user.getBankSavings());
            statement.setInt(54, user.getWeedBaul());
            statement.setInt(55, user.getBasuLvl());
            statement.setInt(56, user.getBasuXp());
            statement.setInt(57, user.getHuntPoints());
            statement.setString(58, user.getHuntSkins());
            statement.setInt(59, user.getArmLvl());
            statement.setInt(60, user.getArmXp());
            statement.setInt(61, user.getMecLvl());
            statement.setInt(62, user.getMecXp());
            statement.setInt(63, user.getCamLvl());
            statement.setInt(64, user.getCamXp());
            statement.setInt(65, user.getLastKilled());
            statement.setInt(66, user.getMarriedTo());
            statement.setInt(67, user.getHijo());
            statement.setInt(68, user.getGangId());
            statement.setInt(69, user.getGangRank());
            statement.setInt(70, user.getGangRequest());
            statement.setInt(71, user.getChangeNameCount());
            statement.setInt(72, user.getCar());
            statement.setInt(73, user.getCarFuel());
            statement.setInt(74, user.getWeed());
            statement.setInt(75, user.getCocaine());
            statement.setInt(76, user.getBotiquin());
            statement.setInt(77, user.getHeroina());
            statement.setInt(78, user.getCaramelos());
            statement.setInt(79, user.getMedicina());
            statement.setInt(80, user.getCigarette());
            statement.setInt(81, user.getPildora());
            statement.setInt(82, user.getDynamite());
            statement.setInt(83, user.getWeedmateria());
            statement.setString(84, user.getUnlockedQuests());
            statement.setInt(85, user.getBrawlWins());
            statement.setInt(86, user.getCwWins());
            statement.setInt(87, user.getMwWins());
            statement.setInt(88, user.getSoloQueueWins());
            statement.setString(89, user.isNoob() ? "1" : "0");
            statement.setInt(90, user.getNoobTimeLeft());
            statement.setString(91, user.isInmunidadActivada() ? "1" : "0");
            statement.setInt(92, user.getVipBanned());
            statement.setString(93, user.getLastCoordinates());

            statement.setInt(94, user.getUserId());

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error al guardar estadísticas de roleplay para el usuario " + user.getUserId(), e);
        }
    }

    /**
     * Guarda de forma asíncrona no-bloqueante para optimizar el rendimiento del servidor.
     */
    public static void saveRoleplayUserAsync(RoleplayUser user) {
        if (user != null) {
            Emulator.getThreading().run(() -> saveRoleplayUser(user));
        }
    }

    /**
     * Guarda de forma síncrona los cooldowns de un usuario.
     */
    public static void saveRoleplayCooldowns(RoleplayCooldowns cd) {
        if (cd == null) return;

        String query = "UPDATE rp_cooldowns SET " + "robbery = ?, text_cooldown = ?, robbery_bank = ?, "
                + "medipacks = ?, psvmode = ? WHERE id = ?";

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, cd.getRobbery());
            statement.setInt(2, cd.getTextCooldown());
            statement.setInt(3, cd.getRobberyBank());
            statement.setInt(4, cd.getMedipacks());
            statement.setInt(5, cd.getPsvmode());
            statement.setInt(6, cd.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error al guardar cooldowns de roleplay para el usuario " + cd.getId(), e);
        }
    }

    /**
     * Guarda de forma asíncrona no-bloqueante los cooldowns.
     */
    public static void saveRoleplayCooldownsAsync(RoleplayCooldowns cd) {
        if (cd != null) {
            Emulator.getThreading().run(() -> saveRoleplayCooldowns(cd));
        }
    }

    /**
     * Libera de memoria y guarda los datos de un usuario al desconectar de forma asíncrona.
     */
    public static void unloadUser(int userId) {
        RoleplayUser user = users.remove(userId);
        if (user != null) {
            saveRoleplayUserAsync(user);
        }

        RoleplayCooldowns cd = cooldowns.remove(userId);
        if (cd != null) {
            saveRoleplayCooldownsAsync(cd);
        }
    }

    /**
     * Intercepta el evento de desconexión del usuario para guardar y liberar memoria.
     */
    @EventHandler
    public static void onUserDisconnectEvent(UserDisconnectEvent event) {
        if (event.habbo != null && event.habbo.getHabboInfo() != null) {
            int userId = event.habbo.getHabboInfo().getId();
            LOGGER.info("Guardando asíncronamente y descargando datos de roleplay para el usuario " + userId);
            unloadUser(userId);
        }
    }
}
