package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public class HechizosWebEvent implements RoleplayWebEvent {
    @Override
    public String getEventName() {
        return "hechizos";
    }

    @Override
    public void handle(GameClient client, String payload) {
        if (client == null || client.getHabbo() == null) {
            return;
        }
    }
}
