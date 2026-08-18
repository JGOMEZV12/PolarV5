package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public class MoveWebEvent implements RoleplayWebEvent {
    @Override
    public String getEventName() {
        return "move";
    }

    @Override
    public void handle(GameClient client, String payload) {
        if (client == null || client.getHabbo() == null) {
            return;
        }
    }
}
