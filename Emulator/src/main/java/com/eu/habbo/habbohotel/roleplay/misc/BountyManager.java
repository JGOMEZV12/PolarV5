package com.eu.habbo.habbohotel.roleplay.misc;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class BountyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(BountyManager.class);

    public static final ConcurrentHashMap<Integer, Bounty> bountyUsers = new ConcurrentHashMap<>();

    public static void initialize() {
        bountyUsers.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_bounties");
             ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                int userId = set.getInt("user_id");
                int addedBy = set.getInt("added_by");
                int reward = set.getInt("reward");
                double timeStamp = set.getDouble("timestamp");
                double expiryTimeStamp = set.getDouble("timestamp_expire");

                Bounty bounty = new Bounty(userId, addedBy, reward, timeStamp, expiryTimeStamp);
                bountyUsers.put(userId, bounty);
            }

            LOGGER.info("BountyManager -> Loaded {} active user bounties.", bountyUsers.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize BountyManager", e);
        }
    }

    public static boolean bountyExists(int userId) {
        return bountyUsers.containsKey(userId);
    }

    public static void addBounty(Bounty New) {
        if (bountyUsers.containsKey(New.userId)) {
            return;
        }

        bountyUsers.put(New.userId, New);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO `rp_bounties` (`user_id`, `added_by`, `reward`, `timestamp`, `timestamp_expire`) VALUES (?, ?, ?, ?, ?)")) {
            statement.setInt(1, New.userId);
            statement.setInt(2, New.addedBy);
            statement.setInt(3, New.reward);
            statement.setDouble(4, New.timeStamp);
            statement.setDouble(5, New.expiryTimeStamp);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to add bounty for user ID: {}", New.userId, e);
        }
    }

    public static void removeBounty(int userId, boolean expired) {
        if (!bountyUsers.containsKey(userId)) {
            return;
        }

        Bounty junk = bountyUsers.remove(userId);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM `rp_bounties` WHERE `user_id` = ?")) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to remove bounty for user ID: {}", userId, e);
        }

        if (!expired && junk != null) {
            try {
                Habbo bountyOwner = Emulator.getGameServer().getGameClientManager().getHabbo(junk.addedBy);
                if (bountyOwner != null) {
                    synchronized (bountyOwner) {
                        bountyOwner.giveCredits(junk.reward);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Error rewarding back expired/removed bounty to owner", e);
            }
        }
    }

    public static void checkBounty(GameClient session, int userId) {
        try {
            if (session == null || !bountyUsers.containsKey(userId)) {
                return;
            }

            Bounty junk = bountyUsers.remove(userId);

            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                 PreparedStatement statement = connection.prepareStatement("DELETE FROM `rp_bounties` WHERE `user_id` = ?")) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            }

            double now = System.currentTimeMillis() / 1000.0;
            Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(userId);
            String targetUsername = targetHabbo != null ? targetHabbo.getHabboInfo().getUsername() : "Alguien";

            if (junk != null && now < junk.expiryTimeStamp) {
                session.getHabbo().shout("*Reclamaciones $" + junk.reward + " de la recompensa de " + targetUsername + "*");
                session.getHabbo().whisper("¡Has reclamado con éxito $" + junk.reward + " de la recompensa de " + targetUsername + "!");

                synchronized (session.getHabbo()) {
                    session.getHabbo().giveCredits(junk.reward);
                }
            } else {
                session.getHabbo().whisper("¡Vaya! Has reclamado la recompensa de " + targetUsername + ", pero su recompensa ha expirado!");
            }
        } catch (Exception e) {
            LOGGER.error("Error checking/claiming bounty for user ID: {}", userId, e);
        }
    }

    public static void clearBounties() {
        bountyUsers.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE `rp_bounties`")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to clear bounties from DB", e);
        }
    }

    public static class Bounty {
        public int userId;
        public int addedBy;
        public int reward;
        public double timeStamp;
        public double expiryTimeStamp;

        public Bounty(int userId, int addedBy, int reward, double timeStamp, double expiryTimeStamp) {
            this.userId = userId;
            this.addedBy = addedBy;
            this.reward = reward;
            this.timeStamp = timeStamp;
            this.expiryTimeStamp = expiryTimeStamp;
        }
    }
}
