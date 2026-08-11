package com.eu.habbo.habbohotel.roleplay.gambling;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.HabboItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TexasHoldEmManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(TexasHoldEmManager.class);

    public static final ConcurrentHashMap<Integer, TexasHoldEm> gameList = new ConcurrentHashMap<>();

    public static void initialize() {
        clearOldData();
        gameList.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM `rp_gambling`");
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                int gameId = set.getInt("id");
                if (gameList.containsKey(gameId)) {
                    continue;
                }

                int roomId = set.getInt("room_id");
                String[] player1Data = set.getString("player_1").split(";");
                String[] player2Data = set.getString("player_2").split(";");
                String[] player3Data = set.getString("player_3").split(";");

                ConcurrentHashMap<Integer, TexasHoldEmItem> player1 = generatePlayer(gameId, 1);
                ConcurrentHashMap<Integer, TexasHoldEmItem> player2 = generatePlayer(gameId, 2);
                ConcurrentHashMap<Integer, TexasHoldEmItem> player3 = generatePlayer(gameId, 3);
                ConcurrentHashMap<Integer, TexasHoldEmItem> banker = generatePlayer(gameId, 0);

                TexasHoldEmItem joinGate = generateJoinGate(set);
                TexasHoldEmItem potSquare = generatePotSquare(set);

                int joinCost = set.getInt("join_cost");

                TexasHoldEm game = new TexasHoldEm(
                        gameId,
                        roomId,
                        player1,
                        player2,
                        player3,
                        banker,
                        potSquare,
                        joinGate,
                        player1Data,
                        player2Data,
                        player3Data,
                        joinCost);

                gameList.put(gameId, game);

                // Spawn starting dices
                if (game.potSquare != null) game.potSquare.spawnDice();
                if (game.joinGate != null) game.joinGate.spawnDice();

                for (TexasHoldEmItem item : game.player1.values()) {
                    if (item != null) item.spawnDice();
                }
                for (TexasHoldEmItem item : game.player2.values()) {
                    if (item != null) item.spawnDice();
                }
                for (TexasHoldEmItem item : game.player3.values()) {
                    if (item != null) item.spawnDice();
                }
                for (TexasHoldEmItem item : game.banker.values()) {
                    if (item != null) item.spawnDice();
                }
            }

            LOGGER.info("TexasHoldEmManager -> Loaded {} Texas Hold 'Em Games!", gameList.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize TexasHoldEmManager", e);
        }
    }

    public static void clearOldData() {
        for (TexasHoldEm game : gameList.values()) {
            if (game != null) {
                game.resetGame();
            }
        }
        gameList.clear();
    }

    public static ConcurrentHashMap<Integer, TexasHoldEmItem> generatePlayer(int gameId, int playerId) {
        ConcurrentHashMap<Integer, TexasHoldEmItem> player = new ConcurrentHashMap<>();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM `rp_gambling_items` WHERE `game_id` = ? AND `player_id` = ?")) {
            statement.setInt(1, gameId);
            statement.setInt(2, playerId);
            try (ResultSet set = statement.executeQuery()) {
                while (set.next()) {
                    int diceId = set.getInt("dice_id");
                    if (player.containsKey(diceId)) {
                        continue;
                    }

                    int itemId = set.getInt("item_id");
                    int roomId = set.getInt("room_id");
                    int x = set.getInt("x");
                    int y = set.getInt("y");
                    double z = set.getDouble("z");
                    int rotation = set.getInt("rotation");

                    TexasHoldEmItem item = new TexasHoldEmItem(roomId, itemId, x, y, z, rotation);
                    player.put(diceId, item);
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to generate player for game ID: {} player ID: {}", gameId, playerId, e);
        }

        return player;
    }

    public static TexasHoldEmItem generatePotSquare(ResultSet set) throws SQLException {
        int itemId = set.getInt("item_id");
        int roomId = set.getInt("room_id");
        int x = set.getInt("x");
        int y = set.getInt("y");
        double z = set.getDouble("z");
        int rotation = set.getInt("rotation");

        return new TexasHoldEmItem(roomId, itemId, x, y, z, rotation);
    }

    public static TexasHoldEmItem generateJoinGate(ResultSet set) throws SQLException {
        // Gates are loaded exactly like pot square or using custom fields
        int itemId = set.getInt("item_id");
        int roomId = set.getInt("room_id");
        int x = set.getInt("x");
        int y = set.getInt("y");
        double z = set.getDouble("z");
        int rotation = set.getInt("rotation");

        return new TexasHoldEmItem(roomId, itemId, x, y, z, rotation);
    }

    public static List<TexasHoldEm> getGamesByRoomId(int roomId) {
        List<TexasHoldEm> list = new ArrayList<>();
        for (TexasHoldEm game : gameList.values()) {
            if (game.roomId == roomId) {
                list.add(game);
            }
        }
        return list;
    }

    public static TexasHoldEm getGameForUser(int userId) {
        for (TexasHoldEm game : gameList.values()) {
            boolean playing = game.playerList.values().stream().anyMatch(p -> p != null && p.userId == userId);
            if (playing) {
                return game;
            }
        }
        return null;
    }

    public static int getPlayerByDice(HabboItem item, TexasHoldEm[] outGame) {
        if (item == null) return 0;

        for (TexasHoldEm game : gameList.values()) {
            boolean matchesP1 = game.player1.values().stream()
                    .anyMatch(x -> x != null && x.furni != null && x.furni.getId() == item.getId());
            if (matchesP1) {
                if (outGame != null && outGame.length > 0) outGame[0] = game;
                return 1;
            }
            boolean matchesP2 = game.player2.values().stream()
                    .anyMatch(x -> x != null && x.furni != null && x.furni.getId() == item.getId());
            if (matchesP2) {
                if (outGame != null && outGame.length > 0) outGame[0] = game;
                return 2;
            }
            boolean matchesP3 = game.player3.values().stream()
                    .anyMatch(x -> x != null && x.furni != null && x.furni.getId() == item.getId());
            if (matchesP3) {
                if (outGame != null && outGame.length > 0) outGame[0] = game;
                return 3;
            }
        }

        return 0;
    }
}
