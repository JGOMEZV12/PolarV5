package com.eu.habbo.habbohotel.roleplay.users;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Portado desde: Polar RP/HabboRoleplay/Misc/BountyManager.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Mantiene y expone la lista de recompensas activas por la cabeza de los usuarios de roleplay.
 */
public class BountyManager {

    private static final ConcurrentHashMap<Integer, Bounty> bountyUsers = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, Bounty> getBountyUsers() {
        return bountyUsers;
    }

    public static void addBounty(Bounty bounty) {
        if (bounty == null) return;
        bountyUsers.put(bounty.getUserId(), bounty);
    }

    public static void removeBounty(int userId) {
        bountyUsers.remove(userId);
    }
}
