package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/UnStunCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Desparaliza a una persona que ha sufrido alguno de estos ataques.
 */
public class UnStunCommand extends Command {

    public UnStunCommand() {
        super(null, new String[] {"unstun"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length == 1) {
            executor.whisper("Debes ingresar el nombre de la persona.");
            return true;
        }

        Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);
        if (targetHabbo == null) {
            executor.whisper("Ha ocurrido un error al buscar a la persona, probablemente esté desconectada.");
            return true;
        }
        GameClient targetClient = targetHabbo.getClient();

        if (executor.getHabboInfo().getCurrentRoom() == null || targetHabbo.getHabboInfo().getCurrentRoom() == null ||
                executor.getHabboInfo().getCurrentRoom().getId() != targetHabbo.getHabboInfo().getCurrentRoom().getId()) {
            executor.whisper("Ha ocurrido un error al buscar a la persona, probablemente esté desconectada o no está en esta zona.");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (rpUser == null || targetRp == null) {
            return true;
        }

        if (rpUser.getJobId() != 1 && !rpUser.isPoliceTrial()) { // Policía
            executor.whisper("¡Solo un oficial de policía puede hacer eso!");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("Debes estar trabajando para hacer eso.");
            return true;
        }

        if (targetHabbo.getRoomUnit() != null && targetHabbo.getRoomUnit().canWalk()) {
            executor.whisper("No puedes desparalizar a alguien que no está paralizado.");
            return true;
        }

        RoomTile clientTile = executor.getRoomUnit().getCurrentLocation();
        RoomTile targetTile = targetHabbo.getRoomUnit().getCurrentLocation();

        if (clientTile == null || targetTile == null) {
            executor.whisper("Ubicación de sala no válida.");
            return true;
        }

        int distanceX = Math.abs(clientTile.x - targetTile.x);
        int distanceY = Math.abs(clientTile.y - targetTile.y);

        if (distanceX <= 1 && distanceY <= 1) {
            executor.shout("*Ayuda a " + targetHabbo.getHabboInfo().getUsername() + ", dándole tiempo para recuperarse de su aturdimiento*");
            targetRp.setStun(false);
            targetRp.setParalized(false);
            if (targetHabbo.getRoomUnit() != null) {
                targetHabbo.getRoomUnit().setCanWalk(true);
            }

            RoleplayUserManager.saveRoleplayUser(targetRp);
            RoleplayUserManager.saveRoleplayUser(rpUser);
            return true;
        } else {
            executor.whisper("Debes estar más cerca de la persona.");
            return true;
        }
    }
}
