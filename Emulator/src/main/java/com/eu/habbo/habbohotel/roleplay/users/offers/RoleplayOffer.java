package com.eu.habbo.habbohotel.roleplay.users.offers;

public class RoleplayOffer {
    private final String type;
    private final int offererId;
    private final int cost;
    private final Object[] params;

    public RoleplayOffer(String type, int offererId, int cost, Object[] params) {
        this.type = type;
        this.offererId = offererId;
        this.cost = cost;
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public int getOffererId() {
        return offererId;
    }

    public int getCost() {
        return cost;
    }

    public Object[] getParams() {
        return params;
    }
}
