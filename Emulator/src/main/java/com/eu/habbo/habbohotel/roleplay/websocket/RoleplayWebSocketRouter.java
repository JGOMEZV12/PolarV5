package com.eu.habbo.habbohotel.roleplay.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleplayWebSocketRouter extends ChannelInboundHandlerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayWebSocketRouter.class);
    private boolean isRoleplayPath = false;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest req = (HttpRequest) msg;
            String uri = req.uri();
            if (uri != null && uri.startsWith("/events")) {
                isRoleplayPath = true;
                LOGGER.info("Detected Roleplay WebSocket handshake on /events from {}", ctx.channel().remoteAddress());

                // Swap default wsProtocolHandler with one configured for /events
                WebSocketServerProtocolConfig rpWsConfig = WebSocketServerProtocolConfig.newBuilder()
                        .websocketPath("/events")
                        .checkStartsWith(true)
                        .maxFramePayloadLength(500000)
                        .build();

                if (ctx.pipeline().get("wsProtocolHandler") != null) {
                    ctx.pipeline().replace("wsProtocolHandler", "wsProtocolHandler", new WebSocketServerProtocolHandler(rpWsConfig));
                }

                // Add our custom roleplay frame handler after the protocol handler or aggregator
                if (ctx.pipeline().get("wsFrameAggregator") != null) {
                    if (ctx.pipeline().get("roleplayWsHandler") == null) {
                        ctx.pipeline().addAfter("wsFrameAggregator", "roleplayWsHandler", new RoleplayWebSocketHandler());
                    }
                }
            }
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof HandshakeComplete) {
            if (!isRoleplayPath) {
                // If this handshake is NOT for roleplay, remove any roleplay handler that could be in the pipeline
                if (ctx.pipeline().get("roleplayWsHandler") != null) {
                    ctx.pipeline().remove("roleplayWsHandler");
                }
                ctx.pipeline().remove(this);
            }
        }
        super.userEventTriggered(ctx, evt);
    }
}
