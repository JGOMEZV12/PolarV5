package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.gangs.Turf;
import com.eu.habbo.habbohotel.roleplay.gangs.TurfManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.generic.alerts.SimpleAlertComposer;
import java.util.Collection;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Gangs/GangTurfsCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra la lista de todos los barrios/territorios capturables de bandas y qué banda los controla.
 */
public class GangTurfsCommand extends Command {

    public GangTurfsCommand() {
        super("command_gang_turfs", new String[] {"gzonas", "gangturfs"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        StringBuilder message = new StringBuilder().append("--- Barrios ---\n\n");

        Collection<Turf> turfs = TurfManager.getAllTurfs();
        if (turfs == null || turfs.isEmpty()) {
            message.append("No hay barrios disponibles.\n");
            gameClient.sendResponse(new SimpleAlertComposer(message.toString()));
            return true;
        }

        for (Turf turf : turfs) {
            if (turf == null) continue;

            Room room = RpEngine.getGameEnvironment().getRoomManager().getRoom(turf.getRoomId());
            if (room != null) {
                String gangName = "Ninguna pandilla";

                if (turf.getGangId() > 0 && turf.getGangId() != 1000) {
                    Guild guild =
                            RpEngine.getGameEnvironment().getGuildManager().getGuild(turf.getGangId());
                    if (guild != null
                            && guild.getName() != null
                            && !guild.getName().isEmpty()) {
                        gangName = guild.getName();
                    } else {
                        gangName = "Pandilla desconocida";
                    }
                }

                message.append(room.getName())
                        .append(" [ID: ")
                        .append(turf.getRoomId())
                        .append("] --- Controlado por: ")
                        .append(gangName)
                        .append("\n\n");
            }
        }

        gameClient.sendResponse(new SimpleAlertComposer(message.toString()));
        return true;
    }
}
