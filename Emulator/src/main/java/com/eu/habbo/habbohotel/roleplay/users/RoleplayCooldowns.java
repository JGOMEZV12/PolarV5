package com.eu.habbo.habbohotel.roleplay.users;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Portado desde: Polar RP/HabboRoleplay/Cooldowns/CooldownManager.cs / rp_stats_cooldowns
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Modela los cooldowns de un usuario de Roleplay.
 */
public class RoleplayCooldowns {

    private int id;
    private int robbery = 0;
    private int textCooldown = 0;
    private int robberyBank = 0;
    private int medipacks = 0;
    private int psvmode = 0;

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

    public int getId() { return id; }
    public int getRobbery() { return robbery; }
    public void setRobbery(int robbery) { this.robbery = robbery; }
    public int getTextCooldown() { return textCooldown; }
    public void setTextCooldown(int textCooldown) { this.textCooldown = textCooldown; }
    public int getRobberyBank() { return robberyBank; }
    public void setRobberyBank(int robberyBank) { this.robberyBank = robberyBank; }
    public int getMedipacks() { return medipacks; }
    public void setMedipacks(int medipacks) { this.medipacks = medipacks; }
    public int getPsvmode() { return psvmode; }
    public void setPsvmode(int psvmode) { this.psvmode = psvmode; }
}
