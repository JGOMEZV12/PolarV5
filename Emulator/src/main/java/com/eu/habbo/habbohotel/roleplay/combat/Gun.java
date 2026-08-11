package com.eu.habbo.habbohotel.roleplay.combat;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.misc.RoleplayManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gun implements ICombat {
    private static final Logger LOGGER = LoggerFactory.getLogger(Gun.class);

    @Override
    public boolean canCombat(GameClient client, GameClient targetClient) {
        if (client == null || targetClient == null) return false;
        if (client.getHabbo() == null || targetClient.getHabbo() == null) return false;

        RoleplayUser rp = client.getHabbo().getRoleplay();
        RoleplayUser targetRp = targetClient.getHabbo().getRoleplay();

        if (rp.isDead() || rp.isJailed() || targetRp.isDead() || targetRp.isJailed()) {
            return false;
        }
        return true;
    }

    @Override
    public void execute(GameClient client, GameClient targetClient, boolean hitClosest) {
        if (!canCombat(client, targetClient)) return;

        RoleplayUser rp = client.getHabbo().getRoleplay();
        RoleplayUser targetRp = targetClient.getHabbo().getRoleplay();

        // Gun range damage (ranged weapon)
        int damage = 15 + (int) (Math.random() * 16); // 15 to 30 damage

        synchronized (targetClient.getHabbo()) {
            int newHealth = targetRp.getCurHealth() - damage;
            if (newHealth <= 0) {
                targetRp.setCurHealth(0);
                targetRp.setDead(true);
                RoleplayManager.shout(
                        client,
                        "*Le dispara un tiro fatal en el pecho a "
                                + targetClient.getHabbo().getHabboInfo().getUsername() + " dejándolo moribundo*");
                getRewards(client, targetClient, "gun");
            } else {
                targetRp.setCurHealth(newHealth);
                RoleplayManager.shout(
                        client,
                        "*Le dispara a "
                                + targetClient.getHabbo().getHabboInfo().getUsername() + " causándole " + damage
                                + " de daño*");
            }
        }
    }

    @Override
    public int getEXP(GameClient client, GameClient targetClient) {
        return 25 + (int) (Math.random() * 16); // 25 to 40 EXP
    }

    @Override
    public int getCoins(GameClient targetClient) {
        if (targetClient == null || targetClient.getHabbo() == null) return 0;
        return targetClient.getHabbo().getHabboInfo().getCredits() / 3;
    }

    @Override
    public void getRewards(GameClient client, GameClient targetClient, String combatType) {
        if (client == null || targetClient == null) return;

        // Reward EXP
        int exp = getEXP(client, targetClient);
        RoleplayUser rp = client.getHabbo().getRoleplay();
        rp.setLevelEXP(rp.getLevelEXP() + exp);
        client.getHabbo().whisper("¡Has ganado " + exp + " de experiencia de Roleplay por tu disparo!");

        // Steal credits on kill with consistent deterministic deadlock-free locking order (lowest user ID first)
        int coinsToSteal = getCoins(targetClient);
        if (coinsToSteal > 0) {
            int h1Id = client.getHabbo().getHabboInfo().getId();
            int h2Id = targetClient.getHabbo().getHabboInfo().getId();
            Object lock1 = h1Id < h2Id ? client.getHabbo() : targetClient.getHabbo();
            Object lock2 = h1Id < h2Id ? targetClient.getHabbo() : client.getHabbo();

            synchronized (lock1) {
                synchronized (lock2) {
                    if (targetClient.getHabbo().tryTakeCredits(coinsToSteal)) {
                        client.getHabbo().giveCredits(coinsToSteal);
                        client.getHabbo()
                                .whisper("¡Has robado $" + coinsToSteal + " del bolsillo de "
                                        + targetClient.getHabbo().getHabboInfo().getUsername() + "!");
                        targetClient
                                .getHabbo()
                                .whisper("¡Has perdido $" + coinsToSteal + " por ser herido con arma de fuego!");
                    }
                }
            }
        }
    }
}
