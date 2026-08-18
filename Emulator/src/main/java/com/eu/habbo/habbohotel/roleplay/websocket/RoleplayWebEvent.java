package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import io.netty.channel.Channel;

public interface RoleplayWebEvent extends IWebEvent {
    String getEventName();
    void handle(GameClient client, String payload);

    @Override
    default void execute(GameClient client, String data, Channel channel) {
        handle(client, data);
    }
}
