package com.eu.habbo.habbohotel.roleplay.turfs;

import com.eu.habbo.Emulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Turf {
    private static final Logger LOGGER = LoggerFactory.getLogger(Turf.class);

    private int roomId;
    private int gangId;
    private int beginX;
    private int beginY;
    private int endX;
    private int endY;
    private int flagX;
    private int flagY;
    private boolean flagSpawned;

    public Turf(int roomId, int gangId, int beginX, int beginY, int endX, int endY, int flagX, int flagY) {
        this.roomId = roomId;
        this.gangId = gangId;
        this.beginX = beginX;
        this.beginY = beginY;
        this.endX = endX;
        this.endY = endY;
        this.flagX = flagX;
        this.flagY = flagY;
        this.flagSpawned = false;
    }

    public void updateTurf(int newGangId) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE `rp_gangs_turfs` SET `gang_id` = ? WHERE `gang_id` = ? AND `room_id` = ?")) {
            statement.setInt(1, newGangId);
            statement.setInt(2, this.gangId);
            statement.setInt(3, this.roomId);
            statement.executeUpdate();
            this.gangId = newGangId;
        } catch (SQLException e) {
            LOGGER.error("Failed to update turf for gangId {} in room ID {}", this.gangId, this.roomId, e);
        }
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getGangId() { return gangId; }
    public void setGangId(int gangId) { this.gangId = gangId; }

    public int getBeginX() { return beginX; }
    public void setBeginX(int beginX) { this.beginX = beginX; }

    public int getBeginY() { return beginY; }
    public void setBeginY(int beginY) { this.beginY = beginY; }

    public int getEndX() { return endX; }
    public void setEndX(int endX) { this.endX = endX; }

    public int getEndY() { return endY; }
    public void setEndY(int endY) { this.endY = endY; }

    public int getFlagX() { return flagX; }
    public void setFlagX(int flagX) { this.flagX = flagX; }

    public int getFlagY() { return flagY; }
    public void setFlagY(int flagY) { this.flagY = flagY; }

    public boolean isFlagSpawned() { return flagSpawned; }
    public void setFlagSpawned(boolean flagSpawned) { this.flagSpawned = flagSpawned; }
}
