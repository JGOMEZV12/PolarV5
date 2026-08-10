package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.messages.outgoing.guilds.GuildBuyRoomsComposer;
import java.util.ArrayList;
import java.util.List;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Gangs/GangCreateCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Abre la ventana nativa de creación de grupos (bandas) filtrando solo las salas válidas del usuario.
 */
public class GangCreateCommand extends Command {

    public GangCreateCommand() {
        super("command_gang_create", new String[] {"gcreate", "gcrear", "gangcreate"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(
                gameClient.getHabbo().getHabboInfo().getId());
        if (rpUser == null) {
            return true;
        }

        if (rpUser.getGangId() > 0) {
            gameClient.getHabbo().whisper("Por favor borre a su pandilla antes de intentar hacer una nueva!");
            return true;
        }

        List<Room> myRooms = RpEngine.getGameEnvironment().getRoomManager().getRoomsForHabbo(gameClient.getHabbo());
        List<Room> validRooms = new ArrayList<>();

        for (Room room : myRooms) {
            if (room.getGuildId() <= 0) {
                validRooms.add(room);
            }
        }

        gameClient.sendResponse(new GuildBuyRoomsComposer(validRooms));
        return true;
    }
}
