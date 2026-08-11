package com.eu.habbo.habbohotel.roleplay.combat;

import com.eu.habbo.habbohotel.gameclients.GameClient;

public interface ICombat {
    boolean canCombat(GameClient client, GameClient targetClient);
    void execute(GameClient client, GameClient targetClient, boolean hitClosest);
    int getEXP(GameClient client, GameClient targetClient);
    int getCoins(GameClient targetClient);
    void getRewards(GameClient client, GameClient targetClient, String combatType);
}
