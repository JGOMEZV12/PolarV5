package com.eu.habbo.habbohotel.roleplay.cooldowns;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class Cooldown {
    private static final Logger LOGGER = LoggerFactory.getLogger(Cooldown.class);

    private final String type;
    private final GameClient client;
    private final int interval;
    private int amount;
    private ScheduledFuture<?> task;
    private boolean ended = false;

    public Cooldown(String type, GameClient client, int interval, int amount) {
        this.type = type;
        this.client = client;
        this.interval = interval;
        this.amount = amount;

        // Schedule the execution at fixed rate using Emulator's Threading service
        this.task = Emulator.getThreading().getService().scheduleAtFixedRate(
                this::runTick,
                interval,
                interval,
                TimeUnit.MILLISECONDS
        );
    }

    private void runTick() {
        try {
            if (ended) return;
            execute();
            amount--;
            if (amount <= 0) {
                endCooldown();
            }
        } catch (Exception e) {
            LOGGER.error("Error in cooldown tick execution", e);
            endCooldown();
        }
    }

    public void endCooldown() {
        if (ended) return;
        ended = true;

        if (task != null) {
            task.cancel(true);
            task = null;
        }

        if (client != null && client.getHabbo() != null && client.getHabbo().getRoleplay() != null) {
            client.getHabbo().getRoleplay().getCooldownManager().getActiveCooldowns().remove(type);
        }
    }

    public abstract void execute();

    public String getType() {
        return type;
    }

    public GameClient getClient() {
        return client;
    }

    public int getInterval() {
        return interval;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isEnded() {
        return ended;
    }
}
