package com.eu.habbo.habbohotel.roleplay.users.offers;

import java.util.concurrent.ConcurrentHashMap;

public class OffersManager {
    private final ConcurrentHashMap<String, RoleplayOffer> activeOffers = new ConcurrentHashMap<>();

    public OffersManager() {}

    public ConcurrentHashMap<String, RoleplayOffer> getActiveOffers() {
        return activeOffers;
    }

    public void createOffer(String type, int offererId, int cost, Object... params) {
        if (activeOffers.containsKey(type.toLowerCase())) {
            return;
        }
        RoleplayOffer offer = new RoleplayOffer(type.toLowerCase(), offererId, cost, params);
        activeOffers.put(type.toLowerCase(), offer);
    }

    public void endAllOffers() {
        activeOffers.clear();
    }
}
