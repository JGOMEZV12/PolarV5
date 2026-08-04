package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Portado desde: Polar RP/HabboRoleplay/Web/WebEventManager.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Handler de Netty para administrar las conexiones del WebSocket
 * de Roleplay en la ruta /events, procesando mensajes JSON de nxs.js.
 */
@ChannelHandler.Sharable
public class RoleplayWebSocketHandler extends SimpleChannelInboundHandler<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayWebSocketHandler.class);
    private static final Gson GSON = new Gson();

    private static final ConcurrentHashMap<ChannelHandlerContext, Integer> activeConnections = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<ChannelHandlerContext, Integer> getActiveConnections() {
        return activeConnections;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete handshake) {
            String uri = handshake.requestUri();
            if (uri.startsWith("/events")) {
                LOGGER.info("Roleplay WebSocket handshake completed for URI: {}", uri);

                // Configurar dinámicamente la pipeline para el WebSocket de Roleplay (/events)
                // Removemos los decodificadores/encoders binarios del juego principal
                try {
                    if (ctx.pipeline().get("wsCodec") != null) {
                        ctx.pipeline().remove("wsCodec");
                    }
                    if (ctx.pipeline().get("gameMessageHandler") != null) {
                        ctx.pipeline().remove("gameMessageHandler");
                    }
                } catch (Exception e) {
                    LOGGER.warn("Bypassed standard game handlers pipeline cleanup: {}", e.getMessage());
                }

                // Registrar conexión activa
                activeConnections.put(ctx, 0);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof TextWebSocketFrame frame) {
            String text = frame.text();
            if (text == null || text.isEmpty()) return;

            if (text.equalsIgnoreCase("ping")) {
                ctx.writeAndFlush(new TextWebSocketFrame("pong"));
                return;
            }

            try {
                WebEvent event = GSON.fromJson(text, WebEvent.class);
                if (event != null && event.getEventName() != null) {
                    // Actualizar ID del usuario en esta conexión
                    activeConnections.put(ctx, event.getUserId());

                    handleWebEvent(ctx, event);
                }
            } catch (Exception e) {
                LOGGER.debug("Error parsing roleplay web event JSON: " + text, e);
            }
        } else {
            // Pasar otros tipos de mensajes de canal (como BinaryWebSocketFrame si ocurrieran)
            ctx.fireChannelRead(msg);
        }
    }

    private void handleWebEvent(ChannelHandlerContext ctx, WebEvent event) {
        String name = event.getEventName();
        int userId = event.getUserId();

        LOGGER.info("Received roleplay web event: '{}' from userId: {}", name, userId);

        if ("event_pong".equalsIgnoreCase(name)) {
            ctx.writeAndFlush(new TextWebSocketFrame("compose_ping|true"));
        } else if ("event_retrieveconnectingstatistics".equalsIgnoreCase(name)) {
            // Cargar y componer estadísticas de roleplay
            RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
            if (rpUser != null) {
                String statsPayload = "ID:" + rpUser.getUserId() + ";" +
                        "LEVEL:" + rpUser.getLevel() + ";" +
                        "EXP:" + rpUser.getLevelExp() + ";" +
                        "CLASS:" + rpUser.getRpClass() + ";" +
                        "HEALTH:" + rpUser.getCurHealth() + "/" + rpUser.getMaxHealth() + ";" +
                        "ENERGY:" + rpUser.getCurEnergy() + "/" + rpUser.getMaxEnergy() + ";" +
                        "MONEY:" + rpUser.getBankChequings() + ";" +
                        "ARMOR:" + rpUser.getArmor();

                ctx.writeAndFlush(new TextWebSocketFrame("compose_characterbar|" + statsPayload));
            }
        } else {
            // Evento no reconocido o extensible para fases futuras
            ctx.writeAndFlush(new TextWebSocketFrame("compose_alert|Evento '" + name + "' recibido por el servidor."));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        activeConnections.remove(ctx);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("Exception caught on Roleplay WebSocket connection", cause);
        ctx.close();
    }
}
