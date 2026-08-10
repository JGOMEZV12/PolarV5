package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/ClearWantedCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Borra toda la lista de peligro.
 */
public class ClearWantedCommand extends Command {

    public ClearWantedCommand() {
        super(null, new String[] {"clearwanted", "limpiarpeligrosos"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        if (rpUser == null) {
            return false;
        }

        if (rpUser.getJobId() != 1 && !rpUser.isPoliceTrial()) { // Policía
            executor.whisper("Sólo un jefe de policía puede usar este comando");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

        executor.shout("*Borra toda la lista de Wanted, quitando a cualquiera que aún estaba en ella*");

        for (GameClient client :
                RpEngine.getGameServer().getGameClientManager().getSessions().values()) {
            if (client.getHabbo() != null) {
                RoleplayUser otherRp = RoleplayUserManager.getRoleplayUser(
                        client.getHabbo().getHabboInfo().getId());
                if (otherRp != null && otherRp.isWanted()) {
                    otherRp.setWanted(false);
                    otherRp.setWantedLevel(0);
                    otherRp.setWantedTimeLeft(0);
                    RoleplayUserManager.saveRoleplayUser(otherRp);
                }
            }
        }

        return true;
    }
}
