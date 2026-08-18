package com.eu.habbo.habbohotel.roleplay.timers;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import java.util.concurrent.ConcurrentHashMap;

public class TimerManager {
    private final GameClient client;
    private final ConcurrentHashMap<String, RoleplayTimer> activeTimers = new ConcurrentHashMap<>();

    public TimerManager(GameClient client) {
        this.client = client;
    }

    public void createTimer(String type, int time, boolean forever, Object... params) {
        if (activeTimers.containsKey(type)) {
            return;
        }

        RoleplayTimer timer = new RoleplayTimer(type, client, time, forever, params) {
            @Override
            public void execute() {
                // Default simple timer tick / needs decrease
            }
        };

        activeTimers.put(type, timer);
    }

    public void endTimer(String type) {
        RoleplayTimer timer = activeTimers.remove(type);
        if (timer != null) {
            timer.endTimer();
        }
    }

    public void endAllTimers() {
        for (RoleplayTimer timer : activeTimers.values()) {
            timer.endTimer();
        }
        activeTimers.clear();
    }

    public ConcurrentHashMap<String, RoleplayTimer> getActiveTimers() {
        return activeTimers;
    }
}
