package com.eu.habbo.habbohotel.roleplay.websocket.chats;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.timers.RoleplayTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketChatManagerMainTimer extends RoleplayTimer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketChatManagerMainTimer.class);

    public WebSocketChatManagerMainTimer(String type, int time, boolean forever, Object[] params) {
        super(type, null, time, forever, params);
    }

    @Override
    public void execute() {
        try {
            if (WebSocketChatManager.runningChatRooms.isEmpty()) {
                return;
            }

            for (WebSocketChatRoom chatRoom : WebSocketChatManager.runningChatRooms.values()) {
                if (chatRoom == null) {
                    continue;
                }

                for (GameClient user : chatRoom.getChatUsers().keySet()) {
                    if (user == null || user.getHabbo() == null) {
                        continue;
                    }

                    var rp = user.getHabbo().getRoleplay();
                    if (rp == null) {
                        continue;
                    }

                    if (rp.getSocketChatSpamTicks() >= 0) {
                        rp.setSocketChatSpamTicks(rp.getSocketChatSpamTicks() - 1);

                        if (rp.getSocketChatSpamTicks() == -1) {
                            rp.setSocketChatSpamCount(0);
                        }
                    }

                    if (rp.getSocketChatFloodTime() > 0) {
                        rp.setSocketChatFloodTime(rp.getSocketChatFloodTime() - 1);
                    }

                    if (rp.getSocketChatSpamCount() > 0) {
                        rp.setSocketChatSpamCount(rp.getSocketChatSpamCount() - 1);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error in WebSocketChatManagerMainTimer.execute", e);
            endTimer();
        }
    }
}
