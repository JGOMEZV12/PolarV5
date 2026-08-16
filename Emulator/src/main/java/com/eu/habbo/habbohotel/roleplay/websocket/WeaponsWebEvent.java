package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public class WeaponsWebEvent implements RoleplayWebEvent {
    @Override
    public String getEventName() {
        return "weapons";
    }

    @Override
    public void handle(GameClient client, String payload) {
        if (client == null || client.getHabbo() == null) {
            return;
        }

        // Handle WebEvent WeaponsWebEvent
    }
}
