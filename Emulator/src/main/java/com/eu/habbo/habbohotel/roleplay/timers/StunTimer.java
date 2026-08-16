package com.eu.habbo.habbohotel.roleplay.timers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;

public class StunTimer extends RoleplayTimer {
    public StunTimer(RoleplayUser rpUser, int interval) {
        super(rpUser, interval);
    }

    @Override
    public void execute() {
        if (rpUser == null || rpUser.getHabbo() == null) {
            stop();
            return;
        }

        // Execution tick for StunTimer
    }
}
