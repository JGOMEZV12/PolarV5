package com.eu.habbo.habbohotel.roleplay.gangs;

import com.eu.habbo.habbohotel.roleplay.RpEngine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Portado desde: Polar RP/HabboRoleplay/Turfs/TurfManager.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Gestiona todos los territorios y barrios de las bandas (pandillas).
 */
public class TurfManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TurfManager.class);

    private static final ConcurrentHashMap<Integer, Turf> turfs = new ConcurrentHashMap<>();

    public static void initialize() {
        turfs.clear();

        String query = "SELECT * FROM rp_gangs_turfs";
        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                Turf turf = new Turf(set);
                turfs.put(turf.getRoomId(), turf);
            }
        } catch (SQLException e) {
            LOGGER.error("Error al cargar territorios de bandas", e);
        }

        LOGGER.info("TurfManager -> Cargados " + turfs.size() + " territorios de bandas.");
    }

    public static Turf getTurf(int roomId) {
        return turfs.get(roomId);
    }

    public static Collection<Turf> getAllTurfs() {
        return turfs.values();
    }

    public static List<Turf> getTurfsByGang(int gangId) {
        List<Turf> gangTurfs = new ArrayList<>();
        for (Turf turf : turfs.values()) {
            if (turf.getGangId() == gangId) {
                gangTurfs.add(turf);
            }
        }
        return gangTurfs;
    }
}
