package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/FlashBangCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Paraliza todos los usuarios deseados en una habitación con el fin de detener en sus pasos.
 */
public class FlashBangCommand extends Command {

    public FlashBangCommand() {
        super(null, new String[] {"flashbang", "cegadora"});
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
            executor.whisper("¡Sólo un teniente de policía puede usar este comando!");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

        Room currentRoom = executor.getHabboInfo().getCurrentRoom();
        if (currentRoom == null) {
            return true;
        }

        RoomTile clientTile = executor.getRoomUnit().getCurrentLocation();
        if (clientTile == null) {
            return true;
        }

        boolean foundWanted = false;

        for (Habbo otherHabbo : currentRoom.getHabbos()) {
            if (otherHabbo.getHabboInfo().getId() == userId) {
                continue;
            }

            RoleplayUser otherRp = RoleplayUserManager.getRoleplayUser(
                    otherHabbo.getHabboInfo().getId());
            if (otherRp != null && otherRp.isWanted() && !otherRp.isDead() && !otherRp.isJailed()) {
                RoomTile targetTile = otherHabbo.getRoomUnit().getCurrentLocation();
                if (targetTile != null) {
                    int distanceX = Math.abs(clientTile.x - targetTile.x);
                    int distanceY = Math.abs(clientTile.y - targetTile.y);
                    if (distanceX <= 10 && distanceY <= 10) {
                        foundWanted = true;
                        otherRp.setStun(true);
                        if (otherHabbo.getRoomUnit() != null) {
                            otherHabbo.getRoomUnit().setCanWalk(false);
                        }
                        otherHabbo.alert("¡Has sido aturdido por una granada cegadora (Flashbang)!");
                        RoleplayUserManager.saveRoleplayUser(otherRp);
                    }
                }
            }
        }

        if (!foundWanted) {
            executor.whisper("No hay ningún usuario deseado en esta sala");
            return true;
        }

        executor.shout("*Lanza una flashbang a todos los sospechosos buscados en la sala y los marea a todos*");

        return true;
    }
}
