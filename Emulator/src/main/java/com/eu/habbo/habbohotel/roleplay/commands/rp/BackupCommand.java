package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/BackupCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Solicitudes de respaldo / ayuda a todos los oficiales de policía en línea.
 */
public class BackupCommand extends Command {

    public BackupCommand() {
        super(null, new String[] {"backup", "refuerzos"});
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
            executor.whisper("¡Sólo los oficiales de policía pueden usar este comando!");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

        String roomName = executor.getHabboInfo().getCurrentRoom() != null
                ? executor.getHabboInfo().getCurrentRoom().getName()
                : "Unknown";
        int roomId = executor.getHabboInfo().getCurrentRoom() != null
                ? executor.getHabboInfo().getCurrentRoom().getId()
                : 0;

        String formattedMessage = "[RADIO POLICÍA] ¡" + executor.getHabboInfo().getUsername()
                + " está solicitando apoyo en " + roomName + " (ID: " + roomId + "). ¡VE RÁPIDO ALLÍ!";

        for (GameClient client :
                RpEngine.getGameServer().getGameClientManager().getSessions().values()) {
            if (client.getHabbo() != null) {
                RoleplayUser otherRp = RoleplayUserManager.getRoleplayUser(
                        client.getHabbo().getHabboInfo().getId());
                if (otherRp != null
                        && (otherRp.getJobId() == 1 || otherRp.isPoliceTrial())
                        && !otherRp.isDisableRadio()) {
                    client.getHabbo().whisper(formattedMessage);
                }
            }
        }

        return true;
    }
}
