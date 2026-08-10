package com.eu.habbo.habbohotel.roleplay.gangs;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Portado desde: Polar RP/HabboHotel/Groups/GroupManager.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Gestiona las bandas (pandillas) de roleplay de forma unificada con el sistema de Guilds de Polaris.
 */
public class GangManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(GangManager.class);

    private static final ConcurrentHashMap<Integer, Gang> gangs = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, GangRank> genericRanks = new ConcurrentHashMap<>();

    public static void initialize() {
        gangs.clear();
        genericRanks.clear();

        loadGangs();
        loadRanks();

        LOGGER.info("GangManager -> Cargadas " + gangs.size() + " bandas y " + genericRanks.size()
                + " rangos genéricos de banda.");
    }

    private static void loadGangs() {
        String query = "SELECT * FROM rp_gangs";
        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                Gang gang = new Gang(set);
                gangs.put(gang.getId(), gang);
            }
        } catch (SQLException e) {
            LOGGER.error("Error al cargar las bandas de roleplay desde rp_gangs", e);
        }
    }

    private static void loadRanks() {
        String query = "SELECT * FROM rp_gangs_ranks WHERE gang_id = 1000";
        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                GangRank rank = new GangRank(set);
                genericRanks.put(rank.getRank(), rank);
            }
        } catch (SQLException e) {
            LOGGER.error("Error al cargar los rangos de bandas de roleplay", e);
        }
    }

    public static Gang getGang(int gangId) {
        if (gangId <= 0) {
            return null;
        }
        return gangs.computeIfAbsent(gangId, id -> {
            Gang g = new Gang(id);
            g.saveAsync();
            return g;
        });
    }

    public static GangRank getGangRank(int rankId) {
        return genericRanks.get(rankId);
    }

    public static Collection<Gang> getAllGangs() {
        return gangs.values();
    }

    public static boolean hasGangCommand(GameClient session, String command) {
        if (session == null || session.getHabbo() == null) {
            return false;
        }
        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(
                session.getHabbo().getHabboInfo().getId());
        if (rpUser == null) {
            return false;
        }

        int gangId = rpUser.getGangId();
        int gangRank = rpUser.getGangRank();

        if (gangId == 0 || gangId == 1000) {
            return false;
        }

        GangRank rankObj = getGangRank(gangRank);
        return rankObj != null && rankObj.hasCommand(command);
    }
}
