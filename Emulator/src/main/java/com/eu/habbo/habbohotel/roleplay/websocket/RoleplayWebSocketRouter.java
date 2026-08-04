package com.eu.habbo.habbohotel.roleplay.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;

/**
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Intercepta peticiones HTTP de upgrade para WebSocket.
 * Si la URI contiene '/events', reemplaza dinámicamente el protocolo principal
 * de Polaris con un configurador de protocolo para '/events'.
 */
public class RoleplayWebSocketRouter extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest req) {
            String uri = req.uri();
            if (uri != null && uri.startsWith("/events")) {
                // Configurar dinámicamente el protocolo de handshake para la ruta /events
                WebSocketServerProtocolConfig wsConfig = WebSocketServerProtocolConfig.newBuilder()
                        .websocketPath("/events")
                        .checkStartsWith(true)
                        .maxFramePayloadLength(500000)
                        .build();

                if (ctx.pipeline().get("wsProtocolHandler") != null) {
                    ctx.pipeline()
                            .replace(
                                    "wsProtocolHandler",
                                    "wsProtocolHandler",
                                    new WebSocketServerProtocolHandler(wsConfig));
                }
            }
        }
        super.channelRead(ctx, msg);
    }
}
