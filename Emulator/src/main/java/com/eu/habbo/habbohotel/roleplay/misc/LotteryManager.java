package com.eu.habbo.habbohotel.roleplay.misc;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LotteryManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotteryManager.class);
    private static final SecureRandom RANDOM = new SecureRandom();

    public static final AtomicInteger ticketLimit = new AtomicInteger(100);
    public static final AtomicInteger prize = new AtomicInteger(5000);
    public static final AtomicInteger cost = new AtomicInteger(50);

    public static final ConcurrentHashMap<Integer, Integer> lotteryTickets = new ConcurrentHashMap<>();

    public static void initialize() {
        try {
            ticketLimit.set(Integer.parseInt(RoleplayData.getData("lottery", "limit")));
            prize.set(Integer.parseInt(RoleplayData.getData("lottery", "prize")));
            cost.set(Integer.parseInt(RoleplayData.getData("lottery", "cost")));
        } catch (Exception e) {
            LOGGER.error("Failed to load lottery config from RoleplayData", e);
            ticketLimit.set(100);
            prize.set(5000);
            cost.set(50);
        }

        lotteryTickets.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_lottery");
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                int userId = set.getInt("user_id");
                int ticket = set.getInt("ticket");

                lotteryTickets.put(userId, ticket);
            }

            LOGGER.info("LotteryManager -> Loaded {} lottery tickets.", lotteryTickets.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize LotteryManager", e);
        }
    }

    public static boolean lotteryFull() {
        return lotteryTickets.size() >= ticketLimit.get();
    }

    public static int getWinner() {
        if (lotteryTickets.isEmpty()) {
            return 0;
        }
        List<Integer> contenders = new ArrayList<>(lotteryTickets.keySet());
        int randomIndex = RANDOM.nextInt(contenders.size());
        return contenders.get(randomIndex);
    }

    public static void givePrize(int winner) {
        if (winner <= 0) return;

        Habbo onlineHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(winner);
        String username = "";

        if (onlineHabbo != null) {
            username = onlineHabbo.getHabboInfo().getUsername();
            synchronized (onlineHabbo) {
                onlineHabbo.giveCredits(prize.get());
            }
        } else {
            try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "UPDATE `users` SET `credits` = (credits + ?) WHERE `id` = ? LIMIT 1")) {
                    statement.setInt(1, prize.get());
                    statement.setInt(2, winner);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement =
                        connection.prepareStatement("SELECT `username` FROM `users` WHERE `id` = ? LIMIT 1")) {
                    statement.setInt(1, winner);
                    try (ResultSet set = statement.executeQuery()) {
                        if (set.next()) {
                            username = set.getString("username");
                        }
                    }
                }
            } catch (SQLException e) {
                LOGGER.error("Failed to give offline lottery prize for user ID: {}", winner, e);
            }
        }

        if (username != null && !username.isEmpty()) {
            sendWinnerAlert(username);
        }
    }

    public static void sendWinnerAlert(String winner) {
        for (GameClient client :
                Emulator.getGameServer().getGameClientManager().getSessions().values()) {
            if (client == null || client.getHabbo() == null) {
                continue;
            }
            client.getHabbo().whisper("[ALERTA] [LOTERÍA] " + winner + " ¡Acaba de ganar la lotería!");
        }
    }

    public static void clearLottery() {
        lotteryTickets.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("TRUNCATE TABLE `rp_lottery`")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to clear lottery from DB", e);
        }
    }
}
