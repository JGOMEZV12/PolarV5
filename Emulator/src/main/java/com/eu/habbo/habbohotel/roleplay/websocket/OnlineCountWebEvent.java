package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public class OnlineCountWebEvent implements RoleplayWebEvent {
    @Override
    public String getEventName() {
        return "onlinecount";
    }

    @Override
    public void handle(GameClient client, String payload) {
        if (client == null || client.getHabbo() == null) {
            return;
        }

        // Handle WebEvent OnlineCountWebEvent
    }
}
