package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Police Related/WantedListCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra una lista detallada de todos los fugitivos buscados por la policía de la ciudad de roleplay.
 */
public class WantedListCommand extends Command {

    public WantedListCommand() {
        super(null, new String[] {"buscados"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();

        StringBuilder message = new StringBuilder("--- Lista de buscados por la policía ---\n\n");
        int count = 0;

        for (RoleplayUser u : RoleplayUserManager.getUsers().values()) {
            if (u.isWanted()) {
                Habbo target = RpEngine.getGameEnvironment().getHabboManager().getHabbo(u.getUserId());
                if (target != null) {
                    message.append("- ")
                            .append(target.getHabboInfo().getUsername())
                            .append(" (Nivel de búsqueda: ")
                            .append(u.getWantedLevel())
                            .append("★)\n");
                    count++;
                }
            }
        }

        if (count == 0) {
            message.append("Nadie está en esta lista por el momento.\n");
        }

        executor.whisper(message.toString());
        return true;
    }
}
