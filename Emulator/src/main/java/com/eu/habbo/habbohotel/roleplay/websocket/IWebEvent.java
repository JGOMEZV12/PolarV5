package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import io.netty.channel.Channel;

public interface IWebEvent {
    void execute(GameClient client, String data, Channel channel);
}
