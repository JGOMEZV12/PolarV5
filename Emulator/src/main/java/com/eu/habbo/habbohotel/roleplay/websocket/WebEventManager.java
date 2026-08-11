package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class WebEventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebEventManager.class);
    private static final Gson GSON = new Gson();

    private static WebEventManager instance;

    public static WebEventManager getInstance() {
        if (instance == null) {
            instance = new WebEventManager();
        }
        return instance;
    }

    private final ConcurrentHashMap<Channel, WebSocketUser> webSockets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, IWebEvent> webEvents = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Channel, Byte>> userSocketIndex = new ConcurrentHashMap<>();

    public WebEventManager() {
        registerIncoming();
        registerOutgoing();
    }

    public void registerIncoming() {
        webEvents.put("event_atm", new ATMWebEvent());
    }

    public void registerOutgoing() {
        // Registered on demand / phases
    }

    public void handleIncomingJson(Channel channel, String json) {
        if (channel == null || json == null || json.isEmpty()) return;
        onSocketMessage(channel, json);
    }

    public void onSocketAdd(Channel channel, int userId) {
        if (channel == null || userId <= 0) return;
        try {
            WebSocketUser wsUser = new WebSocketUser(userId, "", channel);
            webSockets.put(channel, wsUser);

            ConcurrentHashMap<Channel, Byte> set = userSocketIndex.computeIfAbsent(userId,
                    k -> new ConcurrentHashMap<>());
            set.put(channel, (byte) 0);

            LOGGER.info("WebSocket registered: UserId={}", userId);
        } catch (Exception e) {
            LOGGER.error("onSocketAdd error", e);
        }
    }

    public void onSocketRemove(Channel channel) {
        if (channel == null) return;

        WebSocketUser user = webSockets.remove(channel);
        if (user == null) return;

        try {
            removeFromIndex(user.getId(), channel);
            user.setClosing(true);
            user.dispose();
            LOGGER.info("WebSocket removed: UserId={}", user.getId());
        } catch (Exception e) {
            LOGGER.error("onSocketRemove error", e);
        }
    }

    private void removeFromIndex(int userId, Channel channel) {
        ConcurrentHashMap<Channel, Byte> set = userSocketIndex.get(userId);
        if (set != null) {
            set.remove(channel);
            if (set.isEmpty()) {
                userSocketIndex.remove(userId);
            }
        }
    }

    private void onSocketMessage(Channel channel, String data) {
        try {
            if (data == null || data.isEmpty()) return;
            if (data.equalsIgnoreCase("ping") || data.equalsIgnoreCase("pong")) return;

            WebEvent received = GSON.fromJson(data, WebEvent.class);
            if (received == null || received.getEventName() == null || received.getEventName().isEmpty()) return;

            GameClient client = Emulator.getGameServer().getGameClientManager().getHabbo(received.getUserId()).getClient();
            if (client == null || client.getHabbo() == null) return;

            // In our system, verify if the WebSocket is registered
            if (!webSockets.containsKey(channel)) return;

            IWebEvent webEvent = webEvents.get(received.getEventName());
            if (webEvent != null) {
                webEvent.execute(client, data, channel);
            }
        } catch (Exception e) {
            LOGGER.error("onSocketMessage parsing error for data: {}", data, e);
        }
    }

    public void sendData(Channel channel, String json) {
        if (channel != null && channel.isActive() && json != null) {
            channel.writeAndFlush(new TextWebSocketFrame(json));
        }
    }

    public ConcurrentHashMap<Channel, WebSocketUser> getWebSockets() {
        return webSockets;
    }

    public ConcurrentHashMap<String, IWebEvent> getWebEvents() {
        return webEvents;
    }

    public ConcurrentHashMap<Integer, ConcurrentHashMap<Channel, Byte>> getUserSocketIndex() {
        return userSocketIndex;
    }

    public Channel getChannelByUserId(int userId) {
        ConcurrentHashMap<Channel, Byte> set = userSocketIndex.get(userId);
        if (set != null && !set.isEmpty()) {
            return set.keySet().iterator().next();
        }
        return null;
    }
}
