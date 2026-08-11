package com.eu.habbo.habbohotel.roleplay.rooms;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RPRoomManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RPRoomManager.class);
    private static final Random RNG = new Random();

    public final Map<String, List<RPRoom>> hospitalRooms = new HashMap<>();
    public final Map<String, List<RPRoom>> jailRooms = new HashMap<>();
    public final Map<String, List<RPRoom>> jailBack = new HashMap<>();
    public final Map<String, List<RPRoom>> courtRooms = new HashMap<>();
    public final Map<String, List<RPRoom>> polStationRooms = new HashMap<>();
    public final Map<String, List<RPRoom>> camionerosRooms = new HashMap<>();
    public final Map<String, List<RPRoom>> basurerosRooms = new HashMap<>();
    public final Map<String, List<RPRoom>> armerosRooms = new HashMap<>();

    public RPRoomManager() {}

    public void init() {
        hospitalRooms.clear();
        jailRooms.clear();
        jailBack.clear();
        courtRooms.clear();
        polStationRooms.clear();
        camionerosRooms.clear();
        basurerosRooms.clear();
        armerosRooms.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `rp_rooms` "
                        + "WHERE `is_hospital` = '1' OR `is_prison` = '1' OR `is_prisonback` = '1' "
                        + "   OR `is_court` = '1' OR `is_camionero` = '1' OR `is_basurero` = '1' "
                        + "   OR `is_polstation` = '1' OR `is_armero` = '1'")) {

            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    RPRoom room = buildRoom(set);
                    String city = set.getString("city");

                    if (set.getString("is_hospital").equals("1")) addToDict(hospitalRooms, city, room);
                    if (set.getString("is_prison").equals("1")) addToDict(jailRooms, city, room);
                    if (set.getString("is_prisonback").equals("1")) addToDict(jailBack, city, room);
                    if (set.getString("is_court").equals("1")) addToDict(courtRooms, city, room);
                    if (set.getString("is_camionero").equals("1")) addToDict(camionerosRooms, city, room);
                    if (set.getString("is_basurero").equals("1")) addToDict(basurerosRooms, city, room);
                    if (set.getString("is_polstation").equals("1")) addToDict(polStationRooms, city, room);
                    if (set.getString("is_armero").equals("1")) addToDict(armerosRooms, city, room);
                }
            }

            LOGGER.info("{} Hospital(es) -> CARGADO", totalRooms(hospitalRooms));
            LOGGER.info("{} Prision(es) -> CARGADO", totalRooms(jailRooms));
            LOGGER.info("{} Prisonback(es) -> CARGADO", totalRooms(jailBack));
            LOGGER.info("{} Police Station(es) -> CARGADO", totalRooms(polStationRooms));
            LOGGER.info("{} Camioneros Zone(es) -> CARGADO", totalRooms(camionerosRooms));
            LOGGER.info("{} Basureros Zone(es) -> CARGADO", totalRooms(basurerosRooms));

        } catch (SQLException e) {
            LOGGER.error("Error al inicializar RPRoomManager", e);
        }
    }

    public int tryToGetHospital(String city, RPRoom[] outRoom) {
        return tryGetRandom(hospitalRooms, city, outRoom);
    }

    public int tryToGetJail(String city, RPRoom[] outRoom) {
        return tryGetRandom(jailRooms, city, outRoom);
    }

    public int tryToGetJailBack(String city, RPRoom[] outRoom) {
        return tryGetRandom(jailBack, city, outRoom);
    }

    public int tryToGetCourt(String city, RPRoom[] outRoom) {
        return tryGetRandom(courtRooms, city, outRoom);
    }

    public int tryToGetCamioneros(String city, RPRoom[] outRoom) {
        return tryGetRandom(camionerosRooms, city, outRoom);
    }

    public int tryToGetPolStation(String city, RPRoom[] outRoom) {
        return tryGetRandom(polStationRooms, city, outRoom);
    }

    public int tryToGetBasureros(String city, RPRoom[] outRoom) {
        return tryGetRandom(basurerosRooms, city, outRoom);
    }

    public int tryToGetArmeros(String city, RPRoom[] outRoom) {
        return tryGetRandom(armerosRooms, city, outRoom);
    }

    private static int tryGetRandom(Map<String, List<RPRoom>> dict, String city, RPRoom[] outRoom) {
        List<RPRoom> rooms = dict.get(city);
        if (rooms != null && !rooms.isEmpty()) {
            RPRoom room = rooms.get(RNG.nextInt(rooms.size()));
            if (outRoom != null && outRoom.length > 0) {
                outRoom[0] = room;
            }
            return room.getId();
        }
        if (outRoom != null && outRoom.length > 0) {
            outRoom[0] = null;
        }
        return 0;
    }

    private static void addToDict(Map<String, List<RPRoom>> dict, String city, RPRoom room) {
        dict.computeIfAbsent(city, k -> new ArrayList<>()).add(room);
    }

    private static RPRoom buildRoom(ResultSet set) throws SQLException {
        return new RPRoom(
                set.getInt("id"),
                set.getString("city"),
                set.getString("is_court").equals("1"),
                set.getString("is_hospital").equals("1"),
                set.getString("is_prison").equals("1"),
                set.getString("is_prisonback").equals("1"),
                set.getString("is_camionero").equals("1"),
                set.getString("is_mecanico").equals("1"),
                set.getString("is_basurero").equals("1"),
                set.getString("is_armero").equals("1"),
                set.getString("is_polstation").equals("1"));
    }

    private static int totalRooms(Map<String, List<RPRoom>> dict) {
        return dict.values().stream().mapToInt(List::size).sum();
    }
}
