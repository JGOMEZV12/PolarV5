package com.eu.habbo.habbohotel.roleplay.farming;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FarmingSpace {
    private static final Logger LOGGER = LoggerFactory.getLogger(FarmingSpace.class);

    private int id;
    private int itemId;
    private int roomId;
    private int cost;
    private int x;
    private int y;
    private double z;
    private int ownerId;
    private int expiration;
    private boolean spawned;

    public FarmingSpace(int id, int itemId, int roomId, int cost, int x, int y, double z, int ownerId, int expiration) {
        this.id = id;
        this.itemId = itemId;
        this.roomId = roomId;
        this.cost = cost;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ownerId = ownerId;
        this.expiration = expiration;
        this.spawned = false;
    }

    public void buySpace(GameClient session) {
        if (session == null || this.ownerId > 0) return;

        this.ownerId = session.getHabbo().getHabboInfo().getId();
        this.expiration = 3600;

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE `rp_farming_spaces` SET `expiration` = ?, `owner_id` = ? WHERE `id` = ?")) {
            statement.setInt(1, this.expiration);
            statement.setInt(2, this.ownerId);
            statement.setInt(3, this.id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to buy farming space ID {}", this.id, e);
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public int getExpiration() { return expiration; }
    public void setExpiration(int expiration) { this.expiration = expiration; }

    public boolean isSpawned() { return spawned; }
    public void setSpawned(boolean spawned) { this.spawned = spawned; }
}
