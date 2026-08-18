package com.eu.habbo.habbohotel.roleplay.cooldowns;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {
    private final GameClient client;
    private final ConcurrentHashMap<String, Cooldown> activeCooldowns = new ConcurrentHashMap<>();

    public CooldownManager(GameClient client) {
        this.client = client;
    }

    public void createCooldown(String type, int time, int amount) {
        if (activeCooldowns.containsKey(type)) {
            return;
        }

        Cooldown cooldown = new Cooldown(type, client, time, amount) {
            @Override
            public void execute() {
                // Default simple cooldown behavior
            }
        };

        activeCooldowns.put(type, cooldown);
    }

    public boolean tryGetCooldown(String type) {
        Cooldown cooldown = activeCooldowns.get(type);
        if (cooldown != null && !cooldown.isEnded()) {
            return true;
        }
        return false;
    }

    public void endAllCooldowns() {
        for (Cooldown cooldown : activeCooldowns.values()) {
            cooldown.endCooldown();
        }
        activeCooldowns.clear();
    }

    public ConcurrentHashMap<String, Cooldown> getActiveCooldowns() {
        return activeCooldowns;
    }
}
