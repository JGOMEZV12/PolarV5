package com.eu.habbo.habbohotel.roleplay.groups;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleplayGroup {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayGroup.class);

    private int id;
    private String name;
    private String description;
    private String badge;
    private int ownerId;
    private int roomId;
    private int balance;
    private int medipacks;

    public RoleplayGroup(
            int id,
            String name,
            String description,
            String badge,
            int ownerId,
            int roomId,
            int balance,
            int medipacks) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.badge = badge;
        this.ownerId = ownerId;
        this.roomId = roomId;
        this.balance = balance;
        this.medipacks = medipacks;
    }

    public boolean isGang() {
        return this.id >= 1000;
    }

    public void updateBalance(int newBalance) {
        this.balance = newBalance;
        String table = isGang() ? "rp_gangs" : "rp_jobs";
        String col = isGang() ? "bank_balance" : "balance"; // assuming balance column name in rp_jobs

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("UPDATE `" + table + "` SET `" + col + "` = ? WHERE `id` = ?")) {
            statement.setInt(1, this.balance);
            statement.setInt(2, this.id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to update group balance for group ID {}", this.id, e);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getMedipacks() {
        return medipacks;
    }

    public void setMedipacks(int medipacks) {
        this.medipacks = medipacks;
    }
}
