package com.eu.habbo.habbohotel.roleplay.gangs;

import com.eu.habbo.habbohotel.roleplay.RpEngine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Portado desde: Polar RP/HabboHotel/Groups/Group.cs (Sección Gangs)
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Representa las estadísticas adicionales de roleplay para una banda (pandilla) en Polaris.
 */
public class Gang {
    private static final Logger LOGGER = LoggerFactory.getLogger(Gang.class);

    private final int id;
    private int kills;
    private int copKills;
    private int deaths;
    private int score;
    private int medipacks;
    private int bankBalance;
    private boolean bankruptcy;
    private int turfsTaken;
    private int turfsDefend;

    public Gang(int id) {
        this.id = id;
        this.kills = 0;
        this.copKills = 0;
        this.deaths = 0;
        this.score = 0;
        this.medipacks = 0;
        this.bankBalance = 0;
        this.bankruptcy = false;
        this.turfsTaken = 0;
        this.turfsDefend = 0;
    }

    public Gang(ResultSet set) throws SQLException {
        this.id = set.getInt("id");
        this.kills = set.getInt("kills");
        this.copKills = set.getInt("cop_kills");
        this.deaths = set.getInt("deaths");
        this.score = set.getInt("score");
        this.medipacks = set.getInt("medipacks");
        this.bankBalance = set.getInt("bank_balance");
        this.bankruptcy = set.getBoolean("bankruptcy");
        this.turfsTaken = set.getInt("turfs_taken");
        this.turfsDefend = set.getInt("turfs_defend");
    }

    public int getId() {
        return id;
    }

    public int getGType() {
        return this.id >= 1000 ? 3 : 2;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getCopKills() {
        return copKills;
    }

    public void setCopKills(int copKills) {
        this.copKills = copKills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMedipacks() {
        return medipacks;
    }

    public void setMedipacks(int medipacks) {
        this.medipacks = medipacks;
    }

    public int getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(int bankBalance) {
        this.bankBalance = bankBalance;
    }

    public boolean isBankruptcy() {
        return bankruptcy;
    }

    public void setBankruptcy(boolean bankruptcy) {
        this.bankruptcy = bankruptcy;
    }

    public int getTurfsTaken() {
        return turfsTaken;
    }

    public void setTurfsTaken(int turfsTaken) {
        this.turfsTaken = turfsTaken;
    }

    public int getTurfsDefend() {
        return turfsDefend;
    }

    public void setTurfsDefend(int turfsDefend) {
        this.turfsDefend = turfsDefend;
    }

    /**
     * Guarda de forma síncrona las estadísticas de la banda en la base de datos.
     */
    public void save() {
        String query =
                "INSERT INTO rp_gangs (id, kills, cop_kills, deaths, score, medipacks, bank_balance, bankruptcy, turfs_taken, turfs_defend) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                        + "ON DUPLICATE KEY UPDATE kills = ?, cop_kills = ?, deaths = ?, score = ?, medipacks = ?, bank_balance = ?, bankruptcy = ?, turfs_taken = ?, turfs_defend = ?";

        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, this.id);
            statement.setInt(2, this.kills);
            statement.setInt(3, this.copKills);
            statement.setInt(4, this.deaths);
            statement.setInt(5, this.score);
            statement.setInt(6, this.medipacks);
            statement.setInt(7, this.bankBalance);
            statement.setBoolean(8, this.bankruptcy);
            statement.setInt(9, this.turfsTaken);
            statement.setInt(10, this.turfsDefend);

            statement.setInt(11, this.kills);
            statement.setInt(12, this.copKills);
            statement.setInt(13, this.deaths);
            statement.setInt(14, this.score);
            statement.setInt(15, this.medipacks);
            statement.setInt(16, this.bankBalance);
            statement.setBoolean(17, this.bankruptcy);
            statement.setInt(18, this.turfsTaken);
            statement.setInt(19, this.turfsDefend);

            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error al guardar estadísticas de la banda #" + this.id, e);
        }
    }

    /**
     * Guarda asíncronamente las estadísticas de la banda.
     */
    public void saveAsync() {
        RpEngine.getThreading().run(this::save);
    }
}
