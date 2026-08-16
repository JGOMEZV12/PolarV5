package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public interface RoleplayWebEvent {
    String getEventName();
    void handle(GameClient client, String payload);
}
