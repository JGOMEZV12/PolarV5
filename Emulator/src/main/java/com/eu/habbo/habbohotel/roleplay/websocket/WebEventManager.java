package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.google.gson.Gson;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Channel, Byte>> userSocketIndex =
            new ConcurrentHashMap<>();

    public WebEventManager() {
        registerIncoming();
        registerOutgoing();
    }

    public void registerIncoming() {
        webEvents.put("atm", new ATMWebEvent());
        webEvents.put("action", new ActionWebEvent());
        webEvents.put("apartments", new ApartmentsWebEvent());
        webEvents.put("armero", new ArmeroWebEvent());
        webEvents.put("basurero", new BasureroWebEvent());
        webEvents.put("bounty", new BountyWebEvent());
        webEvents.put("business", new BusinessWebEvent());
        webEvents.put("camionero", new CamioneroWebEvent());
        webEvents.put("captcha", new CaptchaWebEvent());
        webEvents.put("changename", new ChangeNameWebEvent());
        webEvents.put("commands", new CommandsWebEvent());
        webEvents.put("driving", new DrivingWebEvent());
        webEvents.put("food", new FoodWebEvent());
        webEvents.put("gangs", new GangsWebEvent());
        webEvents.put("gps", new GpsWebEvent());
        webEvents.put("groups", new GroupsWebEvent());
        webEvents.put("hechizos", new HechizosWebEvent());
        webEvents.put("hospital", new HospitalWebEvent());
        webEvents.put("houses", new HousesWebEvent());
        webEvents.put("htmlpage", new HtmlPageWebEvent());
        webEvents.put("item", new ItemWebEvent());
        webEvents.put("job", new JobWebEvent());
        webEvents.put("macro", new MacroWebEvent());
        webEvents.put("manejar", new ManejarWebEvent());
        webEvents.put("mapa", new MapaWebEvent());
        webEvents.put("move", new MoveWebEvent());
        webEvents.put("onlinecount", new OnlineCountWebEvent());
        webEvents.put("phone", new PhoneWebEvent());
        webEvents.put("placehtml", new PlaceHtmlWebEvent());
        webEvents.put("pong", new PongWebEvent());
        webEvents.put("products", new ProductsWebEvent());
        webEvents.put("profile", new ProfileWebEvent());
        webEvents.put("purge", new PurgeWebEvent());
        webEvents.put("purse", new PurseWebEvent());
        webEvents.put("retrievestats", new RetrieveStatsWebEvent());
        webEvents.put("retrieveustats", new RetrieveUStatsWebEvent());
        webEvents.put("sendnotification", new SendNotificationWebEvent());
        webEvents.put("sendwhisper", new SendWhisperWebEvent());
        webEvents.put("stats", new StatsWebEvent());
        webEvents.put("target", new TargetWebEvent());
        webEvents.put("taxi", new TaxiWebEvent());
        webEvents.put("timerdialogue", new TimerDialogueWebEvent());
        webEvents.put("tutorial", new TutorialWebEvent());
        webEvents.put("vip", new VIPWebEvent());
        webEvents.put("vehicles", new VehiclesWebEvent());
        webEvents.put("wskin", new WSkinWebEvent());
        webEvents.put("wanted", new WantedWebEvent());
        webEvents.put("weapons", new WeaponsWebEvent());
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

            ConcurrentHashMap<Channel, Byte> set =
                    userSocketIndex.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());
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
            if (received == null
                    || received.getEventName() == null
                    || received.getEventName().isEmpty()) return;

            GameClient client = Emulator.getGameServer()
                    .getGameClientManager()
                    .getHabbo(received.getUserId())
                    .getClient();
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
