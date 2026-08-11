package com.eu.habbo.habbohotel.roleplay.websocket.chats;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.websocket.WebEventManager;
import com.eu.habbo.habbohotel.roleplay.websocket.IWebEvent;
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

public class WebSocketChatRoom {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketChatRoom.class);
    private static final Gson GSON = new Gson();

    private String chatName;
    private int chatOwner;
    private final ConcurrentHashMap<GameClient, Integer> chatUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Object, Object> chatValues = new ConcurrentHashMap<>();
    private boolean fromDB;
    private List<Integer> chatAdmins = new ArrayList<>();
    private final ConcurrentHashMap<Integer, Double> bannedUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Double> mutedUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Integer> unbannedUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Integer> unmutedUsers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> chatLogs = new ConcurrentHashMap<>();
    private boolean mutedRoom = false;

    public WebSocketChatRoom(String chatName, int chatOwner, Map<Object, Object> initialValues, List<Integer> chatAdmins, boolean fromDB) {
        this.chatName = chatName.toLowerCase();
        this.chatOwner = chatOwner;
        if (initialValues != null) {
            this.chatValues.putAll(initialValues);
        }
        this.chatAdmins = chatAdmins != null ? new ArrayList<>(chatAdmins) : new ArrayList<>();
        this.fromDB = fromDB;

        if (WebSocketChatManager.runningChatRooms.containsKey(this.chatName)) {
            WebSocketChatRoom existing = WebSocketChatManager.runningChatRooms.get(this.chatName);
            if (existing != null) {
                existing.stop("¡Este chat fue actualizado! Por favor, vuelve a reunirte de nuevo.");
            }
        }

        WebSocketChatManager.runningChatRooms.put(this.chatName, this);
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }

    public int getChatOwner() {
        return chatOwner;
    }

    public void setChatOwner(int chatOwner) {
        this.chatOwner = chatOwner;
    }

    public ConcurrentHashMap<GameClient, Integer> getChatUsers() {
        return chatUsers;
    }

    public ConcurrentHashMap<Object, Object> getChatValues() {
        return chatValues;
    }

    public boolean isFromDB() {
        return fromDB;
    }

    public void setFromDB(boolean fromDB) {
        this.fromDB = fromDB;
    }

    public List<Integer> getChatAdmins() {
        return chatAdmins;
    }

    public void setChatAdmins(List<Integer> chatAdmins) {
        this.chatAdmins = chatAdmins;
    }

    public ConcurrentHashMap<Integer, Double> getBannedUsers() {
        return bannedUsers;
    }

    public ConcurrentHashMap<Integer, Double> getMutedUsers() {
        return mutedUsers;
    }

    public ConcurrentHashMap<Integer, Integer> getUnbannedUsers() {
        return unbannedUsers;
    }

    public ConcurrentHashMap<Integer, Integer> getUnmutedUsers() {
        return unmutedUsers;
    }

    public ConcurrentHashMap<Integer, ConcurrentHashMap<String, String>> getChatLogs() {
        return chatLogs;
    }

    public boolean isMutedRoom() {
        return mutedRoom;
    }

    public void setMutedRoom(boolean mutedRoom) {
        this.mutedRoom = mutedRoom;
    }

    public void refreshChatRoomData() {
        this.chatValues.clear();
        this.bannedUsers.clear();
        this.mutedUsers.clear();
        this.chatAdmins.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT owner_id, password, locked, admins, gang_id from `rp_chat_rooms` WHERE name = ?")) {
                statement.setString(1, this.chatName);
                try (ResultSet set = statement.executeQuery()) {
                    if (set.next()) {
                        int ownerId = set.getInt("owner_id");
                        int gangId = set.getInt("gang_id");
                        String password = set.getString("password");
                        String adminsCol = set.getString("admins");
                        String lockedCol = set.getString("locked");

                        List<Integer> adminsList = new ArrayList<>();
                        if (adminsCol != null && !adminsCol.isEmpty() && adminsCol.contains(":")) {
                            String cleaned = adminsCol.startsWith(":") ? adminsCol.substring(1) : adminsCol;
                            for (String part : cleaned.split(":")) {
                                try {
                                    adminsList.add(Integer.parseInt(part));
                                } catch (NumberFormatException ignored) {}
                            }
                        }
                        this.chatAdmins = adminsList;
                        this.chatOwner = ownerId;
                        this.setChatPassword(password);
                        this.setChatGang(gangId);
                        this.setLockStatus("1".equals(lockedCol) || "true".equalsIgnoreCase(lockedCol));
                    }
                }
            }

            double now = System.currentTimeMillis() / 1000.0;
            try (PreparedStatement statement = connection.prepareStatement("SELECT * from `rp_chat_rooms_data` WHERE chat_name = ?")) {
                statement.setString(1, this.chatName);
                try (ResultSet set = statement.executeQuery()) {
                    while (set.next()) {
                        String dataType = set.getString("data_type");
                        String dataValue = set.getString("data_value");
                        double dataExpire = set.getDouble("data_timestamp_expire");

                        if (dataExpire <= now) {
                            try (PreparedStatement delStatement = connection.prepareStatement(
                                    "DELETE FROM rp_chat_rooms_data WHERE chat_name = ? AND data_type = ? AND data_value = ?")) {
                                delStatement.setString(1, this.chatName);
                                delStatement.setString(2, dataType);
                                delStatement.setString(3, dataValue);
                                delStatement.executeUpdate();
                            }
                            continue;
                        }

                        if ("ban".equals(dataType)) {
                            this.insertBanData(dataValue, dataExpire);
                        } else if ("mute".equals(dataType)) {
                            this.insertMuteData(dataValue, dataExpire);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error refreshing chatroom data for {}", this.chatName, e);
        }
    }

    public boolean exploitingAction(GameClient user) {
        if (!this.chatUsers.containsKey(user)) {
            if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
                user.getHabbo().whisper("No se puede hacer esto ya que no estás en este chat");
            } else {
                sendTopAlert(user, "No se puede hacer esto ya que no estás en este chat");
            }
            return true;
        }
        return false;
    }

    private void sendTopAlert(GameClient user, String msg) {
        Map<String, Object> data = new HashMap<>();
        data.put("event", "top_alert");
        data.put("message", msg);
        Channel channel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (channel != null) {
            WebEventManager.getInstance().sendData(channel, GSON.toJson(data));
        }
    }

    public boolean canBypassRestrictions(GameClient user, boolean chatOwnerCheckOnly) {
        if (user.getHabbo().getHabboInfo().getId() == this.chatOwner) {
            return true;
        }
        // In Polaris, we check if the permissions are staff/vip
        if (user.getHabbo().hasPermission("events_staff")) {
            return true;
        }
        if (!chatOwnerCheckOnly) {
            if (this.chatAdmins.contains(user.getHabbo().getHabboInfo().getId())) {
                return true;
            }
        }
        return false;
    }

    public void addChatLog(GameClient user, String message) {
        ConcurrentHashMap<String, String> logEntry = new ConcurrentHashMap<>();
        logEntry.put("chat_name", this.chatName);
        logEntry.put("user_id", String.valueOf(user.getHabbo().getHabboInfo().getId()));
        logEntry.put("chat_message", message);
        logEntry.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000L));
        this.chatLogs.put(this.chatLogs.size() + 1, logEntry);
    }

    public void saveChatLogs() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            for (Map.Entry<Integer, ConcurrentHashMap<String, String>> entry : this.chatLogs.entrySet()) {
                ConcurrentHashMap<String, String> logData = entry.getValue();
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO `rp_chat_rooms_logs` (`chat_name`, `chat_message`, `user_id`, `timestamp`) VALUES (?, ?, ?, ?)")) {
                    statement.setString(1, logData.get("chat_name"));
                    statement.setString(2, logData.get("chat_message"));
                    statement.setInt(3, Integer.parseInt(logData.get("user_id")));
                    statement.setLong(4, Long.parseLong(logData.get("timestamp")));
                    statement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error saving chat logs for {}", this.chatName, e);
        }
    }

    public void saveChatRoomData() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE `rp_chat_rooms` SET `owner_id` = ?, `password` = ?, `gang_id` = ?, `locked` = ? WHERE `name` = ?")) {
                statement.setInt(1, this.chatOwner);
                statement.setString(2, (String) this.chatValues.getOrDefault("password", ""));
                statement.setInt(3, (Integer) this.chatValues.getOrDefault("gang", 0));
                boolean locked = (Boolean) this.chatValues.getOrDefault("locked", false);
                statement.setString(4, locked ? "1" : "0");
                statement.setString(5, this.chatName);
                statement.executeUpdate();
            }

            StringBuilder adminBuilder = new StringBuilder();
            for (int admin : this.chatAdmins) {
                adminBuilder.append(admin).append(",");
            }
            String adminStr = adminBuilder.toString();
            if (adminStr.endsWith(",")) {
                adminStr = adminStr.substring(0, adminStr.length() - 1);
            }

            try (PreparedStatement statement = connection.prepareStatement(
                    "UPDATE `rp_chat_rooms` SET `admins` = ? WHERE `name` = ?")) {
                statement.setString(1, adminStr);
                statement.setString(2, this.chatName);
                statement.executeUpdate();
            }

            for (Map.Entry<Integer, Double> entry : this.bannedUsers.entrySet()) {
                int bannedUserId = entry.getKey();
                double expire = entry.getValue();

                boolean exists = false;
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT 1 FROM `rp_chat_rooms_data` WHERE `chat_name` = ? AND `data_type` = 'ban' AND `data_value` = ?")) {
                    statement.setString(1, this.chatName);
                    statement.setString(2, String.valueOf(bannedUserId));
                    try (ResultSet set = statement.executeQuery()) {
                        exists = set.next();
                    }
                }

                if (exists) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "UPDATE `rp_chat_rooms_data` SET `data_timestamp_expire` = ? WHERE `chat_name` = ? AND `data_type` = 'ban' AND `data_value` = ?")) {
                        statement.setDouble(1, expire);
                        statement.setString(2, this.chatName);
                        statement.setString(3, String.valueOf(bannedUserId));
                        statement.executeUpdate();
                    }
                } else {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO `rp_chat_rooms_data` (`chat_name`, `data_type`, `data_value`, `data_timestamp_expire`) VALUES (?, 'ban', ?, ?)")) {
                        statement.setString(1, this.chatName);
                        statement.setString(2, String.valueOf(bannedUserId));
                        statement.setDouble(3, expire);
                        statement.executeUpdate();
                    }
                }
            }

            for (Map.Entry<Integer, Integer> entry : this.unbannedUsers.entrySet()) {
                int unbannedUserId = entry.getKey();
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM `rp_chat_rooms_data` WHERE `chat_name` = ? AND `data_type` = 'ban' AND `data_value` = ?")) {
                    statement.setString(1, this.chatName);
                    statement.setString(2, String.valueOf(unbannedUserId));
                    statement.executeUpdate();
                }
            }

            for (Map.Entry<Integer, Double> entry : this.mutedUsers.entrySet()) {
                int mutedUserId = entry.getKey();
                double expire = entry.getValue();

                boolean exists = false;
                try (PreparedStatement statement = connection.prepareStatement(
                        "SELECT 1 FROM `rp_chat_rooms_data` WHERE `chat_name` = ? AND `data_type` = 'mute' AND `data_value` = ?")) {
                    statement.setString(1, this.chatName);
                    statement.setString(2, String.valueOf(mutedUserId));
                    try (ResultSet set = statement.executeQuery()) {
                        exists = set.next();
                    }
                }

                if (exists) {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "UPDATE `rp_chat_rooms_data` SET `data_timestamp_expire` = ? WHERE `chat_name` = ? AND `data_type` = 'mute' AND `data_value` = ?")) {
                        statement.setDouble(1, expire);
                        statement.setString(2, this.chatName);
                        statement.setString(3, String.valueOf(mutedUserId));
                        statement.executeUpdate();
                    }
                } else {
                    try (PreparedStatement statement = connection.prepareStatement(
                            "INSERT INTO `rp_chat_rooms_data` (`chat_name`, `data_type`, `data_value`, `data_timestamp_expire`) VALUES (?, 'mute', ?, ?)")) {
                        statement.setString(1, this.chatName);
                        statement.setString(2, String.valueOf(mutedUserId));
                        statement.setDouble(3, expire);
                        statement.executeUpdate();
                    }
                }
            }

            for (Map.Entry<Integer, Integer> entry : this.unmutedUsers.entrySet()) {
                int unmutedUserId = entry.getKey();
                try (PreparedStatement statement = connection.prepareStatement(
                        "DELETE FROM `rp_chat_rooms_data` WHERE `chat_name` = ? AND `data_type` = 'mute' AND `data_value` = ?")) {
                    statement.setString(1, this.chatName);
                    statement.setString(2, String.valueOf(unmutedUserId));
                    statement.executeUpdate();
                }
            }

            this.saveChatLogs();

        } catch (SQLException e) {
            LOGGER.error("Error saving chatroom data for {}", this.chatName, e);
        }
    }

    public void insertMuteData(String userId, double muteExpire) {
        try {
            this.mutedUsers.put(Integer.parseInt(userId), muteExpire);
        } catch (NumberFormatException ignored) {}
    }

    public void insertBanData(String userId, double banExpire) {
        try {
            this.bannedUsers.put(Integer.parseInt(userId), banExpire);
        } catch (NumberFormatException ignored) {}
    }

    public boolean onUserJoin(GameClient user) {
        if (user == null || user.getHabbo() == null) {
            return false;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return false;
        }

        this.chatUsers.put(user, user.getHabbo().getHabboInfo().getId());
        return true;
    }

    public boolean onUserLeft(GameClient user) {
        if (user == null) {
            return false;
        }

        if (!this.chatUsers.containsKey(user)) {
            return false;
        }

        synchronized (this.chatUsers) {
            if (user.getHabbo() == null) {
                this.chatUsers.remove(user);
                return false;
            }

            this.chatUsers.remove(user);
            this.decomposeChatDIV(user);

            Map<String, Object> data = new HashMap<>();
            data.put("event", "chatManager");
            data.put("chatname", chatName);
            data.put("action", "newleftchat");
            data.put("chatusername", user.getHabbo().getHabboInfo().getUsername());
            data.put("chatuserfigure", user.getHabbo().getHabboInfo().getLook());

            this.broadCastChatData(user, GSON.toJson(data), false);
        }

        return true;
    }

    public void warnUser(GameClient user, String message) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("event", "chatManager");
        data.put("action", "newwarnchat");
        data.put("chatname", this.chatName);
        data.put("chatmessage", message);

        Channel channel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (channel != null) {
            WebEventManager.getInstance().sendData(channel, GSON.toJson(data));
        }
    }

    public void sendGreyChatAlert(GameClient user, String message) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("event", "chatManager");
        data.put("action", "newgreymsg");
        data.put("chatname", this.chatName);
        data.put("chatmessage", message);

        Channel channel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (channel != null) {
            WebEventManager.getInstance().sendData(channel, GSON.toJson(data));
        }
    }

    public void beginChatJoin(GameClient user) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("chatname", chatName);
        data.put("action", "joinedchat");

        // Executes it
        IWebEvent event = WebEventManager.getInstance().getWebEvents().get("event_chatroom");
        if (event != null) {
            event.execute(user, GSON.toJson(data), user.getHabbo().getRoleplay().getWebSocketConnection());
        }
    }

    public void authoriseChatJoin(GameClient user) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("event", "chatManager");
        data.put("action", "authorisechat");
        data.put("chatname", this.chatName);

        Channel channel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (channel != null) {
            WebEventManager.getInstance().sendData(channel, GSON.toJson(data));
        }
    }

    public void buildChatDIV(GameClient user) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("event", "chatManager");
        data.put("action", "buildchat");
        data.put("chatname", this.chatName);
        data.put("chatownerfigure", this.returnOwnersFigure());

        Channel channel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (channel != null) {
            WebEventManager.getInstance().sendData(channel, GSON.toJson(data));
        }
    }

    public String returnOwnersFigure() {
        var habitant = Emulator.getGameServer().getGameClientManager().getHabbo(this.chatOwner);
        if (habitant != null) {
            return habitant.getHabboInfo().getLook();
        }
        return "";
    }

    public void decomposeChatDIV(GameClient user) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("event", "chatManager");
        data.put("action", "leavechat");
        data.put("chatname", this.chatName);

        Channel channel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (channel != null) {
            WebEventManager.getInstance().sendData(channel, GSON.toJson(data));
        }
    }

    public void broadCastNewChat(GameClient user, Map<Object, Object> params) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return;
        }

        Map<String, Object> selfMsg = new HashMap<>();
        selfMsg.put("event", "chatManager");
        selfMsg.put("action", "onsendchat");
        selfMsg.put("chatname", this.chatName);
        selfMsg.put("chatmessage", String.valueOf(params.get("chatmessage")));

        Channel selfChannel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (selfChannel != null) {
            WebEventManager.getInstance().sendData(selfChannel, GSON.toJson(selfMsg));
        }

        Map<String, Object> bcMsg = new HashMap<>();
        bcMsg.put("event", "chatManager");
        bcMsg.put("action", "onchat");
        bcMsg.put("chatname", this.chatName);
        bcMsg.put("chatusername", String.valueOf(params.get("chatusername")));
        bcMsg.put("chatmessage", String.valueOf(params.get("chatmessage")));
        bcMsg.put("chatfigure", user.getHabbo().getHabboInfo().getLook());

        this.broadCastChatData(user, GSON.toJson(bcMsg), false);

        // Notify text message to other users in the chat room
        for (GameClient chatUser : this.chatUsers.keySet()) {
            if (chatUser == null || chatUser == user || chatUser.getHabbo() == null) {
                continue;
            }
            // In Polaris, we can send a localized or whisper or standard notification message to mimic original
            chatUser.getHabbo().whisper("Recibió un nuevo mensaje del '" + this.chatName + "' grupo de whatsapp");
        }
    }

    public void broadCastChatHTML(GameClient user, Map<Object, Object> params) {
        if (user == null || user.getHabbo() == null) {
            return;
        }

        if (user.getHabbo().getRoleplay().getWebSocketConnection() == null) {
            user.getHabbo().whisper("Tu conexión de websocket está desconectada. Póngase en contacto con un miembro del personal si el problema persiste");
            return;
        }

        Map<String, Object> selfMsg = new HashMap<>();
        selfMsg.put("event", "chatManager");
        selfMsg.put("action", "specialembed");
        selfMsg.put("chatname", this.chatName);
        selfMsg.put("chatmessage", String.valueOf(params.get("chatmessage")));
        selfMsg.put("chatembedtype", params.get("chatEmbedType"));
        selfMsg.put("chatembedlink", params.get("chatEmbedLink"));
        selfMsg.put("chatembedextra", params.get("chatEmbedExtra"));
        selfMsg.put("chatembedaction", "send");

        Channel selfChannel = user.getHabbo().getRoleplay().getWebSocketConnection();
        if (selfChannel != null) {
            WebEventManager.getInstance().sendData(selfChannel, GSON.toJson(selfMsg));
        }

        Map<String, Object> bcMsg = new HashMap<>();
        bcMsg.put("event", "chatManager");
        bcMsg.put("action", "specialembed");
        bcMsg.put("chatname", this.chatName);
        bcMsg.put("chatmessage", String.valueOf(params.get("chatmessage")));
        bcMsg.put("chatembedtype", params.get("chatEmbedType"));
        bcMsg.put("chatembedlink", params.get("chatEmbedLink"));
        bcMsg.put("chatembedextra", params.get("chatEmbedExtra"));
        bcMsg.put("chatembedaction", "receive");
        bcMsg.put("chatusername", String.valueOf(params.get("chatusername")));
        bcMsg.put("chatfigure", user.getHabbo().getHabboInfo().getLook());

        this.broadCastChatData(user, GSON.toJson(bcMsg), false);
    }

    public void broadCastChatData(GameClient user, String data, boolean sendToMe) {
        for (GameClient chatUser : this.chatUsers.keySet()) {
            if (chatUser == null || chatUser.getHabbo() == null) {
                continue;
            }

            if (!sendToMe && chatUser == user) {
                continue;
            }

            Channel channel = chatUser.getHabbo().getRoleplay().getWebSocketConnection();
            if (channel != null && channel.isActive()) {
                WebEventManager.getInstance().sendData(channel, data);
            }
        }
    }

    public void broadCastChatWarning(GameClient user, String msg) {
        Map<String, Object> data = new HashMap<>();
        data.put("event", "chatManager");
        data.put("action", "newwarnchat");
        data.put("chatname", this.chatName);
        data.put("chatmessage", msg);

        this.broadCastChatData(user, GSON.toJson(data), true);
    }

    public boolean setChatGang(int gangId) {
        this.chatValues.put("gang", gangId);
        return true;
    }

    public boolean setLockStatus(boolean locked) {
        this.chatValues.put("locked", locked);
        return true;
    }

    public boolean setChatPassword(String password) {
        this.chatValues.put("password", password != null ? password : "");
        return true;
    }

    public boolean checkDelete() {
        if (this.fromDB) {
            return false;
        }

        if (this.chatUsers.isEmpty()) {
            this.saveNewChat();
        }

        return false;
    }

    public void saveNewChat() {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection()) {
            boolean exists = false;
            try (PreparedStatement statement = connection.prepareStatement(
                    "SELECT 1 FROM rp_chat_rooms WHERE name = ?")) {
                statement.setString(1, this.chatName);
                try (ResultSet set = statement.executeQuery()) {
                    exists = set.next();
                }
            }

            if (!exists) {
                try (PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO rp_chat_rooms(owner_id, name, password, locked, gang_id, admins) VALUES(?, ?, ?, ?, ?, ?)")) {
                    statement.setInt(1, this.chatOwner);
                    statement.setString(2, this.chatName);
                    statement.setString(3, (String) this.chatValues.getOrDefault("password", ""));
                    boolean locked = (Boolean) this.chatValues.getOrDefault("locked", false);
                    statement.setString(4, locked ? "1" : "0");
                    statement.setInt(5, (Integer) this.chatValues.getOrDefault("gang", 0));

                    StringBuilder adminBuilder = new StringBuilder();
                    for (int admin : this.chatAdmins) {
                        adminBuilder.append(admin).append(":");
                    }
                    String adminStr = adminBuilder.toString();
                    if (adminStr.endsWith(":")) {
                        adminStr = adminStr.substring(0, adminStr.length() - 1);
                    }
                    statement.setString(6, adminStr);
                    statement.executeUpdate();

                    this.fromDB = false;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error saving new chat {}", this.chatName, e);
        }
    }

    public void stop(String stopMsg) {
        synchronized (this.chatUsers) {
            WebSocketChatManager.runningChatRooms.remove(this.chatName);

            for (GameClient user : this.chatUsers.keySet()) {
                if (user == null || user.getHabbo() == null) {
                    continue;
                }

                if (user.getHabbo().getRoleplay().getWebSocketConnection() != null) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("event", "chatManager");
                    data.put("action", "disconnectchat");
                    data.put("chatname", this.chatName);

                    Channel channel = user.getHabbo().getRoleplay().getWebSocketConnection();
                    if (channel != null) {
                        WebEventManager.getInstance().sendData(channel, GSON.toJson(data));
                    }
                    this.sendGreyChatAlert(user, stopMsg);
                }

                WebSocketChatManager.disconnect(user, this.chatName, true, stopMsg);
            }
        }

        new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {}
            this.dispose();
        }).start();
    }

    public void dispose() {
        chatUsers.clear();
        chatValues.clear();
        chatAdmins.clear();
        WebSocketChatManager.runningChatRooms.remove(this.chatName);
    }

    public String getChatType() {
        if (!this.chatValues.isEmpty()) {
            int gang = (Integer) this.chatValues.getOrDefault("gang", 0);
            if (gang > 0) {
                return "gang";
            }

            String password = (String) this.chatValues.getOrDefault("password", "");
            if (password != null && !password.isEmpty()) {
                return "password";
            }

            boolean locked = (Boolean) this.chatValues.getOrDefault("locked", false);
            if (locked) {
                return "locked";
            }
        }

        return "available";
    }

    public boolean incrementAndCheckFlood(GameClient user, int[] muteTimeOut) {
        muteTimeOut[0] = 0;

        if (user == null || user.getHabbo() == null) {
            return false;
        }

        var rp = user.getHabbo().getRoleplay();
        rp.socketChatSpamCount++;

        if (rp.socketChatSpamTicks == -1) {
            rp.socketChatSpamTicks = 8;
        } else if (rp.socketChatSpamCount >= 8) {
            int muteTime;
            if (user.getHabbo().hasPermission("events_staff")) {
                muteTime = 3;
            } else if (user.getHabbo().hasPermission("gold_vip")) {
                muteTime = 7;
            } else if (user.getHabbo().hasPermission("silver_vip")) {
                muteTime = 10;
            } else {
                muteTime = 15;
            }

            rp.socketChatFloodTime = muteTime;
            rp.socketChatSpamCount = 0;
            muteTimeOut[0] = muteTime;
            return true;
        }

        return false;
    }
}
