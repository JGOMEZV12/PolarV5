package com.eu.habbo.habbohotel.roleplay.houses;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class House {
    private static final Logger LOGGER = LoggerFactory.getLogger(House.class);

    private int itemId;
    private int roomId;
    private int ownerId;
    private int cost;
    private boolean forSale;
    private int level;
    private String[] upgrades;
    private boolean isLocked;
    private int insideRoomId;
    private int doorX;
    private int doorY;
    private double doorZ;
    private int type; // 1 = Normal | 2 = Robo | 3 = Terreno
    private long lastForcing;
    private String[] space;

    public House(int itemId, int roomId, int ownerId, int cost, boolean forSale, int level, String[] upgrades, boolean isLocked, int insideRoomId, int doorX, int doorY, double doorZ, int type, long lastForcing, String[] space) {
        this.itemId = itemId;
        this.roomId = roomId;
        this.ownerId = ownerId;
        this.cost = cost;
        this.forSale = forSale;
        this.level = level;
        this.upgrades = upgrades;
        this.isLocked = isLocked;
        this.insideRoomId = insideRoomId;
        this.doorX = doorX;
        this.doorY = doorY;
        this.doorZ = doorZ;
        this.type = type;
        this.lastForcing = lastForcing;
        this.space = space;
    }

    public void updateCost(int cost, boolean inDb) {
        this.cost = cost;
        if (inDb) {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("UPDATE `rp_houses` SET `cost` = ? WHERE `owner_id` = ?")) {
                statement.setInt(1, this.cost);
                statement.setInt(2, this.ownerId);
                statement.executeUpdate();
            } catch (SQLException e) {
                LOGGER.error("Failed to update house cost in database", e);
            }
        }
    }

    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public int getCost() { return cost; }
    public void setCost(int cost) { this.cost = cost; }

    public boolean isForSale() { return forSale; }
    public void setForSale(boolean forSale) { this.forSale = forSale; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String[] getUpgrades() { return upgrades; }
    public void setUpgrades(String[] upgrades) { this.upgrades = upgrades; }

    public boolean isLocked() { return isLocked; }
    public void setLocked(boolean locked) { isLocked = locked; }

    public int getInsideRoomId() { return insideRoomId; }
    public void setInsideRoomId(int insideRoomId) { this.insideRoomId = insideRoomId; }

    public int getDoorX() { return doorX; }
    public void setDoorX(int doorX) { this.doorX = doorX; }

    public int getDoorY() { return doorY; }
    public void setDoorY(int doorY) { this.doorY = doorY; }

    public double getDoorZ() { return doorZ; }
    public void setDoorZ(double doorZ) { this.doorZ = doorZ; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public long getLastForcing() { return lastForcing; }
    public void setLastForcing(long lastForcing) { this.lastForcing = lastForcing; }

    public String[] getSpace() { return space; }
    public void setSpace(String[] space) { this.space = space; }
}
