package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public class BusinessWebEvent implements RoleplayWebEvent {
    @Override
    public String getEventName() {
        return "business";
    }

    @Override
    public void handle(GameClient client, String payload) {
        if (client == null || client.getHabbo() == null) {
            return;
        }

        // Handle WebEvent BusinessWebEvent
    }
}
