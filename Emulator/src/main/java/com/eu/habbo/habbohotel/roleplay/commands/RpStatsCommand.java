package com.eu.habbo.habbohotel.roleplay.commands;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Administrators/RPStatsCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra las estadísticas RP actuales del usuario.
 */
public class RpStatsCommand extends Command {

    public RpStatsCommand() {
        super(null, new String[]{"stats", "rpstats"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null || gameClient.getHabbo().getHabboInfo() == null) {
            return false;
        }

        int userId = gameClient.getHabbo().getHabboInfo().getId();
        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);

        if (rpUser == null) {
            gameClient.getHabbo().whisper("No se pudieron cargar tus estadísticas de Roleplay.");
            return true;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== TUS ESTADÍSTICAS ROLEPLAY ===\r\n");
        sb.append("• Nivel: ").append(rpUser.getLevel()).append(" (EXP: ").append(rpUser.getLevelExp()).append(")\r\n");
        sb.append("• Clase: ").append(rpUser.getRpClass().isEmpty() ? "Ninguna" : rpUser.getRpClass()).append("\r\n");
        sb.append("• Salud: ").append(rpUser.getCurHealth()).append("/").append(rpUser.getMaxHealth()).append("\r\n");
        sb.append("• Energía: ").append(rpUser.getCurEnergy()).append("/").append(rpUser.getMaxEnergy()).append("\r\n");
        sb.append("• Hambre: ").append(rpUser.getHunger()).append("% | Higiene: ").append(rpUser.getHygiene()).append("%\r\n");
        sb.append("• Dinero (Banco): $").append(rpUser.getBankChequings()).append("\r\n");
        sb.append("• Armero Rango: ").append(rpUser.getArmLvl()).append(" | Mecánico Rango: ").append(rpUser.getMecLvl()).append("\r\n");
        sb.append("• Trabajo ID: ").append(rpUser.getJobId()).append(" (Rango: ").append(rpUser.getJobRank()).append(")");

        gameClient.getHabbo().whisper(sb.toString());
        return true;
    }
}
