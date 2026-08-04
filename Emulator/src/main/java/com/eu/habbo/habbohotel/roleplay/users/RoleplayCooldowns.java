package com.eu.habbo.habbohotel.roleplay.users;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Portado desde: Polar RP/HabboRoleplay/Cooldowns/CooldownManager.cs / rp_stats_cooldowns
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Modela los cooldowns de un usuario de Roleplay.
 * Mejoras: Añadido soporte dinámico para cooldowns temporales en memoria de forma altamente eficiente.
 */
public class RoleplayCooldowns {

    private int id;
    private int robbery = 0;
    private int textCooldown = 0;
    private int robberyBank = 0;
    private int medipacks = 0;
    private int psvmode = 0;

    // Cooldowns temporales en memoria (no persistentes) para comandos rápidos (:saldo, :tanque, etc.)
    private final ConcurrentHashMap<String, Long> tempCooldowns = new ConcurrentHashMap<>();

    public RoleplayCooldowns(int id) {
        this.id = id;
    }

    public RoleplayCooldowns(ResultSet row) throws SQLException {
        this.id = row.getInt("id");
        this.robbery = row.getInt("robbery");
        this.textCooldown = row.getInt("text_cooldown");
        this.robberyBank = row.getInt("robbery_bank");
        this.medipacks = row.getInt("medipacks");
        this.psvmode = row.getInt("psvmode");
    }

    public boolean hasCooldown(String key) {
        if (!tempCooldowns.containsKey(key)) return false;
        return tempCooldowns.get(key) > System.currentTimeMillis();
    }

    public void setCooldown(String key, int seconds) {
        tempCooldowns.put(key, System.currentTimeMillis() + (seconds * 1000L));
    }

    public int getRemainingSeconds(String key) {
        if (!hasCooldown(key)) return 0;
        return (int) ((tempCooldowns.get(key) - System.currentTimeMillis()) / 1000);
    }

    public int getId() {
        return id;
    }

    public int getRobbery() {
        return robbery;
    }

    public void setRobbery(int robbery) {
        this.robbery = robbery;
    }

    public int getTextCooldown() {
        return textCooldown;
    }

    public void setTextCooldown(int textCooldown) {
        this.textCooldown = textCooldown;
    }

    public int getRobberyBank() {
        return robberyBank;
    }

    public void setRobberyBank(int robberyBank) {
        this.robberyBank = robberyBank;
    }

    public int getMedipacks() {
        return medipacks;
    }

    public void setMedipacks(int medipacks) {
        this.medipacks = medipacks;
    }

    public int getPsvmode() {
        return psvmode;
    }

    public void setPsvmode(int psvmode) {
        this.psvmode = psvmode;
    }
}
