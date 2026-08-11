package com.eu.habbo.habbohotel.roleplay.gambling;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.rooms.items.RemoveFloorItemComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TexasHoldEm {
    private static final Logger LOGGER = LoggerFactory.getLogger(TexasHoldEm.class);

    public int gameId;
    public int roomId;

    public final ConcurrentHashMap<Integer, TexasHoldEmPlayer> playerList = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, TexasHoldEmItem> player1;
    public final ConcurrentHashMap<Integer, TexasHoldEmItem> player2;
    public final ConcurrentHashMap<Integer, TexasHoldEmItem> player3;
    public final ConcurrentHashMap<Integer, TexasHoldEmItem> banker;

    public TexasHoldEmItem potSquare;
    public TexasHoldEmItem joinGate;

    private ScheduledFuture<?> timerTask;

    public boolean gameStarted;
    public int joinCost;

    public String[] player1Data;
    public String[] player2Data;
    public String[] player3Data;

    public int gameSequence;
    public int playersTurn;

    public TexasHoldEm(int gameId, int roomId,
                       ConcurrentHashMap<Integer, TexasHoldEmItem> player1,
                       ConcurrentHashMap<Integer, TexasHoldEmItem> player2,
                       ConcurrentHashMap<Integer, TexasHoldEmItem> player3,
                       ConcurrentHashMap<Integer, TexasHoldEmItem> banker,
                       TexasHoldEmItem potSquare, TexasHoldEmItem joinGate,
                       String[] player1Data, String[] player2Data, String[] player3Data, int joinCost) {
        this.gameId = gameId;
        this.roomId = roomId;

        this.playerList.put(1, new TexasHoldEmPlayer(0, 0, 0));
        this.playerList.put(2, new TexasHoldEmPlayer(0, 0, 0));
        this.playerList.put(3, new TexasHoldEmPlayer(0, 0, 0));

        this.player1 = player1;
        this.player2 = player2;
        this.player3 = player3;
        this.banker = banker;

        this.potSquare = potSquare;
        this.joinGate = joinGate;

        this.gameStarted = false;

        this.player1Data = player1Data;
        this.player2Data = player2Data;
        this.player3Data = player3Data;

        this.joinCost = joinCost;

        this.gameSequence = 0;
        this.playersTurn = 0;
    }

    public void addPlayerToGame(int userId) {
        try {
            if (gameStarted) return;

            GameClient player = Emulator.getGameServer().getGameClientManager().getHabbo(userId).getClient();
            if (player == null || player.getHabbo() == null) return;

            if (player.getHabbo().getHabboInfo().getCredits() < this.joinCost) {
                player.getHabbo().whisper("¡No tienes suficiente dinero para jugar! El pote de arranque cuesta $" + this.joinCost + "!");
                return;
            }

            synchronized (playerList) {
                long activeCount = playerList.values().stream().filter(x -> x != null && x.userId > 0).count();
                if (activeCount >= 3) return;

                boolean alreadyIn = playerList.values().stream().anyMatch(x -> x != null && x.userId == userId);
                if (alreadyIn) return;
            }

            int number = 1;
            for (Map.Entry<Integer, TexasHoldEmPlayer> entry : playerList.entrySet()) {
                if (entry.getValue() == null || entry.getValue().userId == 0) {
                    number = entry.getKey();
                    break;
                }
            }

            String[] playerData = (number == 1) ? player1Data : (number == 2 ? player2Data : player3Data);

            player.getHabbo().shout("*Se une al juego Texas Hold'em como jugador " + number + "*");
            playerList.put(number, new TexasHoldEmPlayer(userId, 0, this.joinCost));

            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
            if (room != null) {
                int tx = Integer.parseInt(playerData[0]);
                int ty = Integer.parseInt(playerData[1]);
                room.teleportHabboToLocation(player.getHabbo(), (short) tx, (short) ty);
            }

            player.getHabbo().getRoleplay().setTexasHoldEmPlayer(number);
            spawnStartingBet(number);

            long currentCount = playerList.values().stream().filter(x -> x != null && x.userId > 0).count();
            if (currentCount == 3) {
                if (timerTask == null) {
                    timerTask = Emulator.getThreading().getService().scheduleAtFixedRate(
                            this::runTimerTick, 1, 1, TimeUnit.SECONDS
                    );
                }

                this.playersTurn = 1;
                this.gameStarted = true;
                sendStartMessage("");

                TexasHoldEmPlayer p1 = playerList.get(1);
                if (p1 != null && p1.userId > 0) {
                    Habbo h1 = Emulator.getGameServer().getGameClientManager().getHabbo(p1.userId);
                    if (h1 != null) {
                        sendStartMessage("Por favor " + h1.getHabboInfo().getUsername() + " Haga rodar sus dados, dandole clic dos veces");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in addPlayerToGame() for game ID: {}", gameId, e);
        }
    }

    public void removePlayerFromGame(int userId) {
        try {
            boolean isJoined = playerList.values().stream().anyMatch(x -> x != null && x.userId == userId);
            if (!isJoined) return;

            int key = 1;
            for (Map.Entry<Integer, TexasHoldEmPlayer> entry : playerList.entrySet()) {
                if (entry.getValue() != null && entry.getValue().userId == userId) {
                    key = entry.getKey();
                    break;
                }
            }

            TexasHoldEmPlayer oldPlayer = playerList.get(key);
            playerList.put(key, new TexasHoldEmPlayer(0, oldPlayer.currentBet, oldPlayer.totalAmount));
            removeBetFurni(key);

            ConcurrentHashMap<Integer, TexasHoldEmItem> data = (key == 1) ? player1 : (key == 2 ? player2 : player3);
            if (data != null) {
                Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
                for (TexasHoldEmItem item : data.values()) {
                    if (item.furni != null) {
                        item.furni.setExtradata("0");
                        if (room != null) {
                            room.updateItemState(item.furni);
                        }
                    }
                    item.rolled = false;
                    item.value = 0;
                }
            }

            GameClient player = Emulator.getGameServer().getGameClientManager().getHabbo(userId).getClient();
            if (player != null && player.getHabbo() != null) {
                Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
                if (room != null) {
                    room.teleportHabboToLocation(player.getHabbo(), (short) room.getLayout().getDoorX(), (short) room.getLayout().getDoorY());
                }
                player.getHabbo().getRoleplay().setTexasHoldEmPlayer(0);
            }

            if (this.gameStarted && this.playersTurn == key) {
                changeTurn();
            }
        } catch (Exception e) {
            LOGGER.error("Error in removePlayerFromGame() for game ID: {}", gameId, e);
        }
    }

    public void sendStartMessage(String message) {
        try {
            if (!gameStarted) return;

            for (TexasHoldEmPlayer p : playerList.values()) {
                if (p != null && p.userId > 0) {
                    Habbo h = Emulator.getGameServer().getGameClientManager().getHabbo(p.userId);
                    if (h != null) {
                        h.whisper(message.isEmpty() ? "¡El juego de Texas Hold'em está a punto de comenzar!" : message);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in sendStartMessage()", e);
        }
    }

    public void spawnStartingBet(int number) {
        try {
            TexasHoldEmPlayer player = playerList.get(number);
            if (player == null || player.userId == 0) return;

            removeBetFurni(number);
            spawnBetFurni(number, player.totalAmount);
        } catch (Exception e) {
            LOGGER.error("Error in spawnStartingBet()", e);
        }
    }

    public void spawnBetFurni(int number, int amount) {
        try {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
            if (room == null) return;

            String[] data = (number == 1) ? player1Data : (number == 2 ? player2Data : player3Data);
            int x = Integer.parseInt(data[2]);
            int y = Integer.parseInt(data[3]);

            RoomTile tile = room.getLayout().getTile((short) x, (short) y);
            double height = tile != null ? tile.getStackHeight() : 0.0;

            int hundreds = 0;
            int fifties = 0;
            int tens = 0;
            int fives = 0;

            if (amount >= 100) {
                hundreds = amount / 100;
                amount %= 100;
            }
            if (amount >= 50) {
                fifties = amount / 50;
                amount %= 50;
            }
            if (amount >= 10) {
                tens = amount / 10;
                amount %= 10;
            }
            if (amount >= 5) {
                fives = amount / 5;
                amount %= 5;
            }

            int[] itemsToSpawn = {
                    200054, hundreds, // Diamond
                    187378, fifties,  // Emerald
                    187377, tens,     // Ruby
                    187381, fives     // Sapphire
            };

            for (int i = 0; i < itemsToSpawn.length; i += 2) {
                int baseId = itemsToSpawn[i];
                int count = itemsToSpawn[i+1];
                Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(baseId);
                if (baseItem == null) continue;

                while (count > 0 && tile != null) {
                    HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, baseItem, 0, 0, "0");
                    if (item != null) {
                        item.setRoomId(this.roomId);
                        FurnitureMovementError error = room.placeFloorFurniAt(item, tile, 0, null);
                        if (error != FurnitureMovementError.NONE) {
                            Emulator.getGameEnvironment().getItemManager().deleteItem(item);
                        }
                    }
                    height += 0.5;
                    count--;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in spawnBetFurni()", e);
        }
    }

    public void removeBetFurni(int number) {
        try {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
            if (room == null) return;

            String[] data = (number == 1) ? player1Data : (number == 2 ? player2Data : player3Data);
            int x = Integer.parseInt(data[2]);
            int y = Integer.parseInt(data[3]);

            List<HabboItem> items = room.getFloorItems().stream()
                    .filter(item -> item.getX() == x && item.getY() == y && item.getBaseItem().getName().toLowerCase().contains("cfc"))
                    .collect(Collectors.toList());

            for (HabboItem item : items) {
                room.removeHabboItem(item);
                room.sendComposer(new RemoveFloorItemComposer(item).compose());
                Emulator.getGameEnvironment().getItemManager().deleteItem(item);
            }
        } catch (Exception e) {
            LOGGER.error("Error in removeBetFurni()", e);
        }
    }

    public void placePotFurni(int number, int amount) {
        try {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
            if (room == null) return;

            TexasHoldEmPlayer player = playerList.get(number);
            if (player == null || player.totalAmount - amount < 0) return;

            player.totalAmount -= amount;
            player.currentBet += amount;

            int x = potSquare.x;
            int y = potSquare.y;

            RoomTile tile = room.getLayout().getTile((short) x, (short) y);
            double height = tile != null ? tile.getStackHeight() : 0.0;

            int thousands = 0;
            int hundreds = 0;
            int fifties = 0;
            int tens = 0;
            int fives = 0;

            if (amount >= 1000) {
                thousands = amount / 1000;
                amount %= 1000;
            }
            if (amount >= 100) {
                hundreds = amount / 100;
                amount %= 100;
            }
            if (amount >= 50) {
                fifties = amount / 50;
                amount %= 50;
            }
            if (amount >= 10) {
                tens = amount / 10;
                amount %= 10;
            }
            if (amount >= 5) {
                fives = amount / 5;
                amount %= 5;
            }

            int[] itemsToSpawn = {
                    200059, thousands, // Obsidian
                    200054, hundreds,  // Diamond
                    200062, fifties,   // Emerald
                    200058, tens,      // Ruby
                    200056, fives      // Sapphire
            };

            for (int i = 0; i < itemsToSpawn.length; i += 2) {
                int baseId = itemsToSpawn[i];
                int count = itemsToSpawn[i+1];
                Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(baseId);
                if (baseItem == null) continue;

                while (count > 0 && tile != null) {
                    HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, baseItem, 0, 0, "0");
                    if (item != null) {
                        item.setRoomId(this.roomId);
                        FurnitureMovementError error = room.placeFloorFurniAt(item, tile, 0, null);
                        if (error != FurnitureMovementError.NONE) {
                            Emulator.getGameEnvironment().getItemManager().deleteItem(item);
                        }
                    }
                    height += 0.5;
                    count--;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in placePotFurni()", e);
        }
    }

    public void removePotFurni() {
        try {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
            if (room == null) return;

            int x = potSquare.x;
            int y = potSquare.y;

            List<HabboItem> items = room.getFloorItems().stream()
                    .filter(item -> item.getX() == x && item.getY() == y && item.getBaseItem().getName().toLowerCase().contains("cfc"))
                    .collect(Collectors.toList());

            for (HabboItem item : items) {
                room.removeHabboItem(item);
                room.sendComposer(new RemoveFloorItemComposer(item).compose());
                Emulator.getGameEnvironment().getItemManager().deleteItem(item);
            }
        } catch (Exception e) {
            LOGGER.error("Error in removePotFurni()", e);
        }
    }

    public int minimumBet(int number) {
        TexasHoldEmPlayer player = playerList.get(number);
        int required = playerList.values().stream().mapToInt(x -> x.currentBet).max().orElse(0);
        return required - player.currentBet;
    }

    public void rollDice(GameClient session, HabboItem item, int request) {
        try {
            if (!gameStarted) return;
            if (session == null || item == null) return;

            int number = session.getHabbo().getRoleplay().getTexasHoldEmPlayer();
            if (number <= 0) return;

            if (playersTurn != number) {
                session.getHabbo().whisper("¡No es tu turno de tirar tus dados!");
                return;
            }

            ConcurrentHashMap<Integer, TexasHoldEmItem> data = (number == 1) ? player1 : (number == 2 ? player2 : player3);
            if (data == null || data.isEmpty()) return;

            TexasHoldEmItem texasItem = data.values().stream().filter(x -> x != null && x.furni != null && x.furni.getId() == item.getId()).findFirst().orElse(null);
            if (texasItem == null || texasItem.rolled) return;

            // In Polaris, trigger standard Dice rolling or randomize extra_data from 1 to 6
            int val = 1 + new Random().nextInt(6);
            item.setExtradata(String.valueOf(val));
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
            if (room != null) {
                room.updateItemState(item);
            }
            texasItem.rolled = true;
            texasItem.value = val;

            boolean allRolled = data.values().stream().allMatch(x -> x.rolled);
            if (allRolled) {
                changeTurn();
            }
        } catch (Exception e) {
            LOGGER.error("Error in rollDice()", e);
        }
    }

    public void changeTurn() {
        try {
            this.playersTurn++;

            if (this.playersTurn == 4) {
                this.gameSequence++;
                initiateSequence();
                return;
            }

            for (int i = 0; i < 3; i++) {
                if (this.playersTurn == 1 && playerList.get(1).userId == 0) this.playersTurn++;
                if (this.playersTurn == 2 && playerList.get(2).userId == 0) this.playersTurn++;
                if (this.playersTurn == 3 && playerList.get(3).userId == 0) this.playersTurn++;
            }

            if (this.playersTurn == 4) {
                this.gameSequence++;
                initiateSequence();
                return;
            }

            int playerId = playerList.get(this.playersTurn).userId;
            if (playerId > 0) {
                Habbo client = Emulator.getGameServer().getGameClientManager().getHabbo(playerId);
                if (client != null) {
                    if (gameSequence == 0) {
                        sendStartMessage("Por favor " + client.getHabboInfo().getUsername() + " Gire para tirar sus dados!");
                    } else if (gameSequence == 1 || gameSequence == 2) {
                        sendStartMessage("Por favor " + client.getHabboInfo().getUsername() + ". ¿Quieres ':apostar' o ':pasar' para abandonar ':salirevento (fold)'?");
                        client.whisper("La apuesta mínima actual es de $" + this.minimumBet(this.playersTurn) + "! Solo tienes $" + playerList.get(this.playersTurn).totalAmount + "!");
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in changeTurn()", e);
        }
    }

    public void initiateSequence() {
        try {
            if (banker.containsKey(this.gameSequence)) {
                TexasHoldEmItem bItem = banker.get(this.gameSequence);
                if (bItem != null && bItem.furni != null) {
                    int val = 1 + new Random().nextInt(6);
                    bItem.furni.setExtradata(String.valueOf(val));
                    Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
                    if (room != null) {
                        room.updateItemState(bItem.furni);
                    }
                    bItem.rolled = true;
                    bItem.value = val;
                }
            }

            if (this.gameSequence >= 3) {
                chooseWinner();
            } else {
                this.playersTurn = 1;
                changeTurn();
            }
        } catch (Exception e) {
            LOGGER.error("Error in initiateSequence()", e);
        }
    }

    public void chooseWinner() {
        try {
            ConcurrentHashMap<Integer, List<TexasHoldEmItem>> dice = new ConcurrentHashMap<>();

            synchronized (playerList) {
                long activePlayers = playerList.values().stream().filter(x -> x != null && x.userId > 0).count();
                if (activePlayers == 1) {
                    int winnerId = playerList.entrySet().stream().filter(x -> x.getValue() != null && x.getValue().userId > 0).findFirst().get().getKey();
                    endGame(winnerId);
                    return;
                }

                for (Map.Entry<Integer, TexasHoldEmPlayer> entry : playerList.entrySet()) {
                    int number = entry.getKey();
                    if (entry.getValue() == null || entry.getValue().userId <= 0) continue;

                    ConcurrentHashMap<Integer, TexasHoldEmItem> data = (number == 1) ? player1 : (number == 2 ? player2 : player3);
                    List<TexasHoldEmItem> allDice = new ArrayList<>(data.values());
                    allDice.add(banker.get(1));
                    allDice.add(banker.get(2));
                    allDice.add(banker.get(3));

                    dice.put(number, allDice);
                }
            }

            Map<Integer, int[]> scores = new HashMap<>();
            for (Map.Entry<Integer, List<TexasHoldEmItem>> entry : dice.entrySet()) {
                int number = entry.getKey();
                List<Integer> rolls = entry.getValue().stream().map(x -> x.value).collect(Collectors.toList());

                Map<Integer, Long> grouped = rolls.stream().collect(Collectors.groupingBy(x -> x, Collectors.counting()));

                int scoreKind = 1;
                int scoreCard = 1;

                if (grouped.containsValue(5L)) {
                    scoreKind = 8;
                    scoreCard = grouped.entrySet().stream().filter(x -> x.getValue() == 5L).map(Map.Entry::getKey).findFirst().orElse(1);
                } else if (grouped.containsValue(4L)) {
                    scoreKind = 7;
                    scoreCard = grouped.entrySet().stream().filter(x -> x.getValue() == 4L).map(Map.Entry::getKey).findFirst().orElse(1);
                } else if (grouped.containsValue(3L) && grouped.containsValue(2L)) {
                    scoreKind = 6;
                    scoreCard = grouped.entrySet().stream().filter(x -> x.getValue() == 3L).map(Map.Entry::getKey).findFirst().orElse(1);
                } else if (rolls.containsAll(Arrays.asList(1, 2, 3, 4, 5)) || rolls.containsAll(Arrays.asList(2, 3, 4, 5, 6))) {
                    scoreKind = 5;
                    scoreCard = rolls.contains(6) ? 2 : 1;
                } else if (grouped.containsValue(3L)) {
                    scoreKind = 4;
                    scoreCard = grouped.entrySet().stream().filter(x -> x.getValue() == 3L).map(Map.Entry::getKey).findFirst().orElse(1);
                } else {
                    long doubleCount = grouped.values().stream().filter(x -> x == 2L).count();
                    if (doubleCount >= 2) {
                        scoreKind = 3;
                        scoreCard = grouped.entrySet().stream().filter(x -> x.getValue() == 2L).mapToInt(Map.Entry::getKey).max().orElse(1);
                    } else if (doubleCount == 1) {
                        scoreKind = 2;
                        scoreCard = grouped.entrySet().stream().filter(x -> x.getValue() == 2L).map(Map.Entry::getKey).findFirst().orElse(1);
                    } else {
                        scoreKind = 1;
                        scoreCard = rolls.stream().mapToInt(x -> x).max().orElse(1);
                    }
                }

                scores.put(number, new int[]{scoreKind, scoreCard});
            }

            int bestScoreKind = scores.values().stream().mapToInt(x -> x[0]).max().orElse(1);
            List<Map.Entry<Integer, int[]>> candidates = scores.entrySet().stream().filter(x -> x.getValue()[0] == bestScoreKind).collect(Collectors.toList());

            int winnerKey = candidates.get(0).getKey();
            if (candidates.size() > 1) {
                int maxCard = candidates.stream().mapToInt(x -> x.getValue()[1]).max().orElse(1);
                winnerKey = candidates.stream().filter(x -> x.getValue()[1] == maxCard).findFirst().get().getKey();
            }

            endGame(winnerKey);
        } catch (Exception e) {
            LOGGER.error("Error in chooseWinner()", e);
        }
    }

    public void endGame(int winner) {
        try {
            int prize = playerList.values().stream().mapToInt(x -> x.currentBet).sum();

            TexasHoldEmPlayer winnerPlayer = playerList.get(winner);
            if (winnerPlayer != null && winnerPlayer.userId > 0) {
                Habbo h = Emulator.getGameServer().getGameClientManager().getHabbo(winnerPlayer.userId);
                if (h != null) {
                    sendWinnerMessage("El ganador es: " + h.getHabboInfo().getUsername() + " Quien gana el bote de $" + prize + "!");
                    synchronized (h) {
                        h.giveCredits(prize);
                    }
                }
            }

            removePotFurni();

            synchronized (playerList) {
                for (TexasHoldEmPlayer p : playerList.values()) {
                    if (p != null && p.userId > 0) {
                        removePlayerFromGame(p.userId);
                    }
                }
            }

            resetGame();
        } catch (Exception e) {
            LOGGER.error("Error in endGame()", e);
        }
    }

    public void resetGame() {
        Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);

        for (TexasHoldEmItem item : player1.values()) {
            if (item.furni != null) {
                item.furni.setExtradata("0");
                if (room != null) room.updateItemState(item.furni);
            }
            item.rolled = false;
            item.value = 0;
        }
        for (TexasHoldEmItem item : player2.values()) {
            if (item.furni != null) {
                item.furni.setExtradata("0");
                if (room != null) room.updateItemState(item.furni);
            }
            item.rolled = false;
            item.value = 0;
        }
        for (TexasHoldEmItem item : player3.values()) {
            if (item.furni != null) {
                item.furni.setExtradata("0");
                if (room != null) room.updateItemState(item.furni);
            }
            item.rolled = false;
            item.value = 0;
        }
        for (TexasHoldEmItem item : banker.values()) {
            if (item.furni != null) {
                item.furni.setExtradata("0");
                if (room != null) room.updateItemState(item.furni);
            }
            item.rolled = false;
            item.value = 0;
        }

        playerList.put(1, new TexasHoldEmPlayer(0, 0, 0));
        playerList.put(2, new TexasHoldEmPlayer(0, 0, 0));
        playerList.put(3, new TexasHoldEmPlayer(0, 0, 0));

        this.gameSequence = 0;
        this.playersTurn = 0;
        this.gameStarted = false;

        if (timerTask != null) {
            timerTask.cancel(true);
            timerTask = null;
        }
    }

    private void runTimerTick() {
        try {
            if (!gameStarted) {
                if (timerTask != null) {
                    timerTask.cancel(true);
                    timerTask = null;
                }
                return;
            }

            long activeCount = playerList.values().stream().filter(x -> x != null && x.userId > 0).count();
            if (activeCount <= 0) {
                removePotFurni();
                resetGame();
                return;
            }

            if (activeCount == 1) {
                int winnerId = playerList.entrySet().stream().filter(x -> x.getValue() != null && x.getValue().userId > 0).findFirst().get().getKey();
                endGame(winnerId);
                return;
            }

            // Verify players online and inside room
            for (Map.Entry<Integer, TexasHoldEmPlayer> entry : playerList.entrySet()) {
                if (entry.getValue() == null || entry.getValue().userId <= 0) continue;

                Habbo h = Emulator.getGameServer().getGameClientManager().getHabbo(entry.getValue().userId);
                if (h == null || h.getHabboInfo().getCurrentRoom() == null || h.getHabboInfo().getCurrentRoom().getId() != this.roomId) {
                    removePlayerFromGame(entry.getValue().userId);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in runTimerTick() for game ID: {}", gameId, e);
        }
    }

    public void sendWinnerMessage(String message) {
        try {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
            if (room != null) {
                for (Habbo h : room.getHabbos()) {
                    h.whisper(message);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in sendWinnerMessage()", e);
        }
    }
}
