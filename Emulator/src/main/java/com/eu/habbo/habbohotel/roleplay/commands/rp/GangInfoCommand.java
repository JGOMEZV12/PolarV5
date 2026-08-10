package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.guilds.GuildMember;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.gangs.Gang;
import com.eu.habbo.habbohotel.roleplay.gangs.GangManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.messages.outgoing.generic.alerts.SimpleAlertComposer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Gangs/GangInfoCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra información detallada sobre la banda (pandilla) del usuario, incluyendo clasificaciones y miembros clave.
 */
public class GangInfoCommand extends Command {

    public GangInfoCommand() {
        super("command_gang_info", new String[] {"ginfo", "ganginfo"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(
                gameClient.getHabbo().getHabboInfo().getId());
        if (rpUser == null || rpUser.getGangId() <= 0) {
            gameClient.getHabbo().whisper("No eres parte de una pandilla.");
            return true;
        }

        int gangId = rpUser.getGangId();
        Guild guild = RpEngine.getGameEnvironment().getGuildManager().getGuild(gangId);
        Gang gang = GangManager.getGang(gangId);

        if (guild == null || gang == null) {
            gameClient.getHabbo().whisper("No se pudo cargar la información de tu pandilla.");
            return true;
        }

        List<Gang> allGangs = new ArrayList<>(GangManager.getAllGangs());

        // Clasificación por Score
        allGangs.sort((a, b) -> Integer.compare(b.getScore(), a.getScore()));
        int scoreRanking = allGangs.indexOf(gang) + 1;

        // Clasificación por Kills
        allGangs.sort((a, b) -> Integer.compare(b.getKills(), a.getKills()));
        int killsRanking = allGangs.indexOf(gang) + 1;

        // Clasificación por Deaths
        allGangs.sort((a, b) -> Integer.compare(a.getDeaths(), b.getDeaths()));
        int deathsRanking = allGangs.indexOf(gang) + 1;

        StringBuilder message = new StringBuilder();
        message.append("----- ").append(guild.getName()).append(" -----\n\n");
        message.append("Asesinatos: ")
                .append(String.format("%,d", gang.getKills()))
                .append(" ----- Clasificado ")
                .append(killsRanking)
                .append(" de ")
                .append(allGangs.size())
                .append("\n");
        message.append("Muertes: ")
                .append(String.format("%,d", gang.getDeaths()))
                .append("  ----- Clasificado ")
                .append(deathsRanking)
                .append(" de ")
                .append(allGangs.size())
                .append("\n");
        message.append("Puntuación: ")
                .append(String.format("%,d", gang.getScore()))
                .append(" ----- Clasificado ")
                .append(scoreRanking)
                .append(" de ")
                .append(allGangs.size())
                .append("\n\n");
        message.append("Dinero de pandilla: $").append(gang.getBankBalance()).append("\n");
        message.append("Paquetes médicos: ").append(gang.getMedipacks()).append("\n\n");

        message.append("Fundado por: ").append(guild.getOwnerName()).append("\n\n");

        Set<GuildMember> members =
                RpEngine.getGameEnvironment().getGuildManager().getGuildMembers(guild.getId());
        List<String> coLeaders = new ArrayList<>();
        List<String> medics = new ArrayList<>();
        List<String> dealers = new ArrayList<>();
        List<String> thieves = new ArrayList<>();
        List<String> recruits = new ArrayList<>();

        for (GuildMember member : members) {
            RoleplayUser memberRp = RoleplayUserManager.getRoleplayUser(member.getUserId());
            if (memberRp != null && memberRp.getGangId() == gangId) {
                int rank = memberRp.getGangRank();
                String name = member.getUsername();
                if (rank == 5) coLeaders.add(name);
                else if (rank == 4) medics.add(name);
                else if (rank == 3) dealers.add(name);
                else if (rank == 2) thieves.add(name);
                else if (rank == 1) recruits.add(name);
            }
        }

        message.append("Encargado: ")
                .append(coLeaders.isEmpty() ? "Nadie" : String.join(", ", coLeaders))
                .append("\n");
        message.append("Médicos: ")
                .append(medics.isEmpty() ? "Ninguno" : String.join(", ", medics))
                .append("\n");
        message.append("Traficante de drogas: ")
                .append(dealers.isEmpty() ? "Ninguno" : String.join(", ", dealers))
                .append("\n");
        message.append("Ladrones: ")
                .append(thieves.isEmpty() ? "Ninguno" : String.join(", ", thieves))
                .append("\n");

        if (!recruits.isEmpty()) {
            List<String> topRecruits = recruits.subList(0, Math.min(10, recruits.size()));
            message.append("Primeros 10 novatos: ")
                    .append(String.join(", ", topRecruits))
                    .append("\n");
        } else {
            message.append("Primeros 10 novatos: Ninguno\n");
        }

        gameClient.sendResponse(new SimpleAlertComposer(message.toString()));
        return true;
    }
}
