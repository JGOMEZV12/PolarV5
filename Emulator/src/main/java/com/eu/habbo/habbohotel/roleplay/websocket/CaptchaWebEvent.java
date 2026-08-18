package com.eu.habbo.habbohotel.roleplay.websocket;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public class CaptchaWebEvent implements RoleplayWebEvent {
    @Override
    public String getEventName() {
        return "captcha";
    }

    @Override
    public void handle(GameClient client, String payload) {
        if (client == null || client.getHabbo() == null) {
            return;
        }
    }
}
