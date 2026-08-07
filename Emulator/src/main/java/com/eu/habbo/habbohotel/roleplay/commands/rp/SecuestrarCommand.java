package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/SecuestrarCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Secuestra al usuario objetivo.
 */
public class SecuestrarCommand extends Command {

    public SecuestrarCommand() {
        super(null, new String[] {"secuestrar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length == 1) {
            executor.whisper("Vaya, se le olvidó ingresar un nombre de usuario");
            return true;
        }

        Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);
        if (targetHabbo == null) {
            executor.whisper("Se ha producido un error al intentar encontrar a ese usuario, tal vez estén sin conexión.");
            return true;
        }
        GameClient targetClient = targetHabbo.getClient();

        if (executor.getHabboInfo().getCurrentRoom() == null || targetHabbo.getHabboInfo().getCurrentRoom() == null ||
                executor.getHabboInfo().getCurrentRoom().getId() != targetHabbo.getHabboInfo().getCurrentRoom().getId()) {
            executor.whisper("Se ha producido un error al encontrar a ese usuario, tal vez no estén en línea o en esta sala.");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (rpUser == null || targetRp == null) {
            return true;
        }

        if (targetRp.isDead()) {
            executor.whisper("¡No puedes secuestrar a alguien que está muerto!");
            return true;
        }

        if (targetRp.isJailed() && !targetRp.isJailbroken()) {
            executor.whisper("¡No puedes secuestrar a alguien que ya está en la cárcel!");
            return true;
        }

        if (rpUser.isPassiveMode()) {
            executor.whisper("No puedes realizar acciones ilegales en modo pasivo.");
            return true;
        }

        if (targetRp.isPassiveMode()) {
            executor.whisper("¡Este usuario tiene el modo pasivo activo!.");
            return true;
        }

        if (!targetRp.isCuffed()) {
            executor.whisper("¡No puedes arrestar a alguien que no está esponsado!");
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
            if (targetRp.isWorking()) {
                targetRp.setWorking(false);
            }

            executor.shout("*Agarra a " + targetHabbo.getHabboInfo().getUsername() + " le pone un trapo en la boca y nariz para secuestrarlo*");
            targetRp.setCuffed(false);
            if (targetHabbo.getRoomUnit() != null) {
                targetHabbo.getRoomUnit().setEffectId(0, 0);
                targetHabbo.getRoomUnit().setCanWalk(true);
            }

            int secuestroRoomId = Emulator.getConfig().getInt("roleplay.secuestro.room.id", 5);
            targetRp.setJailed(true);
            targetRp.setJailedTimeLeft(10); // 10 minutes default secuestro

            Emulator.getGameEnvironment().getRoomManager().enterRoom(targetHabbo, secuestroRoomId, "", true);
            targetHabbo.alert("Has sido secuestrado por " + executor.getHabboInfo().getUsername() + " por 10 minutos!");

            RoleplayUserManager.saveRoleplayUser(targetRp);
            RoleplayUserManager.saveRoleplayUser(rpUser);

            return true;
        } else {
            executor.whisper("¡Deben acercarse a este ciudadano para detenerlos!");
            return true;
        }
    }
}
