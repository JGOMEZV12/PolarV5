package com.eu.habbo.habbohotel.roleplay.websocket.chats;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.websocket.WebEventManager;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class WebSocketChatManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketChatManager.class);
    private static final Gson GSON = new Gson();

    public static final ConcurrentHashMap<String, WebSocketChatRoom> runningChatRooms = new ConcurrentHashMap<>();
    public static final AtomicReference<WebSocketChatManagerMainTimer> webSocketChatManagerMainTimer = new AtomicReference<>(null);

    public static void initialize() {
        if (!runningChatRooms.isEmpty()) {
            stopAllChats();
        }

        runningChatRooms.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_chat_rooms");
             ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                String chatName = set.getString("name");
                int ownerId = set.getInt("owner_id");
                String chatPassword = set.getString("password");
                int gangId = set.getInt("gang_id");
                String lockedCol = set.getString("locked");
                boolean locked = "1".equals(lockedCol) || "true".equalsIgnoreCase(lockedCol);
                String adminsCol = set.getString("admins");

                List<Integer> chatAdmins = new ArrayList<>();
                if (adminsCol != null && !adminsCol.isEmpty() && adminsCol.contains(":")) {
                    String cleaned = adminsCol.startsWith(":") ? adminsCol.substring(1) : adminsCol;
                    for (String part : cleaned.split(":")) {
                        try {
                            chatAdmins.add(Integer.parseInt(part));
                        } catch (NumberFormatException ignored) {}
                    }
                }

                Map<Object, Object> initialValues = new HashMap<>();
                initialValues.put("password", chatPassword);
                initialValues.put("gang", gangId);
                initialValues.put("locked", locked);

                WebSocketChatRoom newChatRoom = new WebSocketChatRoom(chatName, ownerId, initialValues, chatAdmins, true);
                newChatRoom.refreshChatRoomData(); // Get chat bans & mutes

                runningChatRooms.put(newChatRoom.getChatName(), newChatRoom);
            }

        } catch (SQLException e) {
            LOGGER.error("Failed to initialize WebSocketChatManager", e);
        }

        if (webSocketChatManagerMainTimer.get() == null) {
            webSocketChatManagerMainTimer.set(new WebSocketChatManagerMainTimer("websocketchatmanager", 1000, true, null));
        }

        LOGGER.info("WebSocketChatManager -> Loaded {} Chats.", runningChatRooms.size());
    }

    public static void stopAllChats() {
        try {
            int removed = 0;
            int removedUsers = 0;
            synchronized (runningChatRooms) {
                for (WebSocketChatRoom chat : runningChatRooms.values()) {
                    if (chat == null) {
                        continue;
                    }

                    if (chat.isFromDB()) {
                        chat.saveChatRoomData();
                    }

                    new Thread(() -> {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ignored) {}

                        chat.stop("This chat was stopped! Please join a new one!");
                    }).start();

                    removed++;
                }

                saveNewChats();
            }

            LOGGER.info("Removed {} roleplay chat rooms.", removed);
        } catch (Exception e) {
            LOGGER.error("Error stopping all chats", e);
        }
    }

    public static void saveNewChats() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            for (WebSocketChatRoom chatRoom : runningChatRooms.values()) {
                if (chatRoom == null || chatRoom.isFromDB()) {
                    continue;
                }

                boolean exists = false;
                try (PreparedStatement statement = connection.prepareStatement("SELECT 1 FROM rp_chat_rooms WHERE name = ?")) {
                    statement.setString(1, chatRoom.getChatName());
                    try (ResultSet set = statement.executeQuery()) {
                        exists = set.next();
                    }
                }

                if (!exists) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO rp_chat_rooms(owner_id, name, password, locked, gang_id, admins) VALUES(?, ?, ?, ?, ?, ?)")) {
                        statement.setInt(1, chatRoom.getChatOwner());
                        statement.setString(2, chatRoom.getChatName());
                        statement.setString(3, (String) chatRoom.getChatValues().getOrDefault("password", ""));
                        boolean locked = (Boolean) chatRoom.getChatValues().getOrDefault("locked", false);
                        statement.setString(4, locked ? "1" : "0");
                        statement.setInt(5, (Integer) chatRoom.getChatValues().getOrDefault("gang", 0));

                        StringBuilder adminBuilder = new StringBuilder();
                        for (int admin : chatRoom.getChatAdmins()) {
                            adminBuilder.append(admin).append(":");
                        }
                        String adminStr = adminBuilder.toString();
                        if (adminStr.endsWith(":")) {
                            adminStr = adminStr.substring(0, adminStr.length() - 1);
                        }
                        statement.setString(6, adminStr);
                        statement.executeUpdate();

                        chatRoom.setFromDB(true);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error saving new chats", e);
        }
    }

    public static boolean authenticatedInChatRoom(GameClient user, String chatName) {
        if (user == null || user.getHabbo() == null) {
            return false;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("¡Debes estar conectado al websocket para poder unirte a los chats! ¡Comuníquese con un miembro del personal si este problema persiste!");
            return false;
        }

        if (!runningChatRooms.containsKey(chatName.toLowerCase())) {
            user.getHabbo().whisper("¡Este chat no existe!");
            return false;
        }

        WebSocketChatRoom chatRoom = runningChatRooms.get(chatName.toLowerCase());

        if (!chatRoom.getChatUsers().containsKey(user)) {
            user.getHabbo().whisper("No estás en este chat, acceso denegado.");
            return false;
        }

        return true;
    }

    public static WebSocketChatRoom getChatByName(String chatName) {
        if (chatName == null) return null;
        return runningChatRooms.get(chatName.toLowerCase());
    }

    public static void disconnect(GameClient user, String chatName, boolean alertMsg, String msg) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (!runningChatRooms.containsKey(chatName.toLowerCase())) {
            return;
        }

        WebSocketChatRoom chat = getChatByName(chatName);
        if (chat == null) {
            return;
        }

        if (alertMsg && msg != null) {
            chat.sendGreyChatAlert(user, msg);
        }

        if (!chat.getChatUsers().containsKey(user)) {
            return;
        }

        chat.decomposeChatDIV(user);
        chat.onUserLeft(user);

        user.getHabbo().getRoleplay().getChatRooms().remove(chatName.toLowerCase());
        chat.checkDelete();
    }

    public static void alertUser(GameClient user, String msg) {
        if (user == null || user.getHabbo() == null) {
            return;
        }
        Channel channel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (channel != null && channel.isActive()) {
            Map<String, Object> data = new HashMap<>();
            data.put("event", "event_sendjsalert");
            data.put("message", msg);
            WebEventManager.getInstance().sendData(channel, GSON.toJson(data));
        }
    }

    public static void deleteChat(WebSocketChatRoom chatRoom) {
        if (chatRoom == null) return;
        chatRoom.stop("This chat was deleted by a staff member!");

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_chat_rooms WHERE name = ?")) {
                statement.setString(1, chatRoom.getChatName());
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_chat_rooms_data WHERE chat_name = ?")) {
                statement.setString(1, chatRoom.getChatName());
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM rp_chat_rooms_logs WHERE chat_name = ?")) {
                statement.setString(1, chatRoom.getChatName());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            LOGGER.error("Error deleting chat {}", chatRoom.getChatName(), e);
        }
    }
}
