package com.eu.habbo.habbohotel.roleplay.timers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RoleplayTimer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayTimer.class);

    protected RoleplayUser rpUser;
    private final String type;
    private final GameClient client;
    private final int time;
    private final boolean forever;
    private final Object[] params;
    private int timeLeft = 0;
    private ScheduledFuture<?> task;
    private boolean ended = false;

    public RoleplayTimer(String type, GameClient client, int time, boolean forever, Object[] params) {
        this.type = type;
        this.client = client;
        this.time = time;
        this.forever = forever;
        this.params = params;

        this.task = Emulator.getThreading()
                .getService()
                .scheduleAtFixedRate(this::runTick, time, time, TimeUnit.MILLISECONDS);
    }

    public RoleplayTimer(RoleplayUser rpUser, int time) {
        this.rpUser = rpUser;
        this.type = getClass().getSimpleName();
        this.client = (rpUser != null && rpUser.getHabbo() != null) ? rpUser.getHabbo().getClient() : null;
        this.time = time;
        this.forever = true;
        this.params = new Object[0];

        this.task = Emulator.getThreading()
                .getService()
                .scheduleAtFixedRate(this::runTick, time, time, TimeUnit.MILLISECONDS);
    }

    private void runTick() {
        try {
            if (ended) return;
            execute();
            if (!forever) {
                timeLeft -= (time / 1000);
                if (timeLeft <= 0) {
                    endTimer();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error executing RoleplayTimer {}", type, e);
            endTimer();
        }
    }

    public void stop() {
        endTimer();
    }

    public void endTimer() {
        if (ended) return;
        ended = true;

        if (task != null) {
            task.cancel(true);
            task = null;
        }

        if (client != null && client.getHabbo() != null && client.getHabbo().getRoleplay() != null) {
            client.getHabbo().getRoleplay().getTimerManager().getActiveTimers().remove(type);
        }
    }

    public abstract void execute();

    public String getType() {
        return type;
    }

    public GameClient getClient() {
        return client;
    }

    public int getTime() {
        return time;
    }

    public boolean isForever() {
        return forever;
    }

    public Object[] getParams() {
        return params;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public RoleplayUser getRpUser() {
        return rpUser;
    }
}
