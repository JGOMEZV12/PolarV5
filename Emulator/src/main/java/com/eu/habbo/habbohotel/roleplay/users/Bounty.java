package com.eu.habbo.habbohotel.roleplay.users;

/**
 * Portado desde: Polar RP/HabboRoleplay/Misc/BountyManager.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Estructura de datos para representar una recompensa activa por la cabeza de un usuario.
 */
public class Bounty {

    private final int userId;
    private final int addedBy;
    private final int reward;
    private final long expiryTimeStamp;

    public Bounty(int userId, int addedBy, int reward, long expiryTimeStamp) {
        this.userId = userId;
        this.addedBy = addedBy;
        this.reward = reward;
        this.expiryTimeStamp = expiryTimeStamp;
    }

    public int getUserId() {
        return userId;
    }

    public int getAddedBy() {
        return addedBy;
    }

    public int getReward() {
        return reward;
    }

    public long getExpiryTimeStamp() {
        return expiryTimeStamp;
    }
}
