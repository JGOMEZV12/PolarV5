package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.gangs.Gang;
import com.eu.habbo.habbohotel.roleplay.gangs.GangManager;
import com.eu.habbo.messages.outgoing.generic.alerts.SimpleAlertComposer;
import java.util.ArrayList;
import java.util.List;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Gangs/GangListCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra las 10 mejores bandas (pandillas) ordenadas por puntuación.
 */
public class GangListCommand extends Command {

    public GangListCommand() {
        super("command_gang_list", new String[] {"glist", "ganglist"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        List<Gang> sortedGangs = new ArrayList<>(GangManager.getAllGangs());
        sortedGangs.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));

        List<Gang> top10 = sortedGangs.subList(0, Math.min(10, sortedGangs.size()));

        StringBuilder message = new StringBuilder();
        message.append("---------- Las mejores pandillas ----------\n\n");

        int rank = 1;
        for (Gang gang : top10) {
            Guild guild = RpEngine.getGameEnvironment().getGuildManager().getGuild(gang.getId());
            if (guild != null) {
                message.append("----- ").append(guild.getName()).append(" -----\n");
                message.append("Puesto: #")
                        .append(rank)
                        .append(" de ")
                        .append(sortedGangs.size())
                        .append("\n");
                message.append("Asesinatos: ")
                        .append(String.format("%,d", gang.getKills()))
                        .append("\n");
                message.append("Muertes: ")
                        .append(String.format("%,d", gang.getDeaths()))
                        .append("\n");
                message.append("Puntuación: ")
                        .append(String.format("%,d", gang.getScore()))
                        .append("\n");
                message.append("Fundado por: ").append(guild.getOwnerName()).append("\n\n");
                rank++;
            }
        }

        gameClient.sendResponse(new SimpleAlertComposer(message.toString()));
        return true;
    }
}
