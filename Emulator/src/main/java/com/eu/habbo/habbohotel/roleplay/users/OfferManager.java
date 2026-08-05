package com.eu.habbo.habbohotel.roleplay.users;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Portado desde: Polar RP/HabboRoleplay/RoleplayUsers/Offers/OfferManager.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Gestiona las ofertas activas recibidas por un usuario.
 */
public class OfferManager {

    private final int userId;
    private final ConcurrentHashMap<String, RoleplayOffer> activeOffers;

    public OfferManager(int userId) {
        this.userId = userId;
        this.activeOffers = new ConcurrentHashMap<>();
    }

    public int getUserId() {
        return userId;
    }

    public ConcurrentHashMap<String, RoleplayOffer> getActiveOffers() {
        return activeOffers;
    }

    public void createOffer(String type, int offererId, int cost, Object... params) {
        if (type == null) return;
        String lowerType = type.toLowerCase();
        if (activeOffers.containsKey(lowerType)) {
            return;
        }

        RoleplayOffer offer = new RoleplayOffer(lowerType, offererId, cost, params);
        activeOffers.put(lowerType, offer);
    }

    public void endAllOffers() {
        activeOffers.clear();
    }
}
