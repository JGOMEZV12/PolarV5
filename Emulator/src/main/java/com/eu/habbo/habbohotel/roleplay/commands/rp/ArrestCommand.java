package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/ArrestCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Arresta a una persona según el nivel de búsqueda.
 */
public class ArrestCommand extends Command {

    public ArrestCommand() {
        super(null, new String[] {"arrest", "arrestar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length != 3) {
            executor.whisper("Por favor ingrese un nombre de usuario y el tiempo!");
            return true;
        }

        Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);
        if (targetHabbo == null) {
            executor.whisper("Ha ocurrido un error al buscar a la persona, probablemente esté desconectada.");
            return true;
        }
        GameClient targetClient = targetHabbo.getClient();

        if (executor.getHabboInfo().getCurrentRoom() == null
                || targetHabbo.getHabboInfo().getCurrentRoom() == null
                || executor.getHabboInfo().getCurrentRoom().getId()
                        != targetHabbo.getHabboInfo().getCurrentRoom().getId()) {
            executor.whisper(
                    "Ha ocurrido un error al buscar a la persona, probablemente esté desconectada o no está en esta zona.");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (rpUser == null || targetRp == null) {
            return true;
        }

        if (targetRp.isPassiveMode()) {
            executor.whisper("¡No puedes hacerle eso a una persona que está en modo pasivo!");
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

        if (targetRp.isDead()) {
            executor.whisper("¡No puedes arrestar a una persona muerta!");
            return true;
        }

        if (targetRp.isJailed()) {
            executor.whisper("¡No puedes arrestar a una persona encarcelada!");
            return true;
        }

        if (!targetRp.isCuffed()) {
            executor.whisper("¡Primero debes esposar a la persona!");
            return true;
        }

        int wantedTime;
        try {
            wantedTime = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            executor.whisper("El tiempo debe ser un número válido.");
            return true;
        }

        if (wantedTime <= 0) {
            executor.whisper("El tiempo debe ser mayor a 0 minutos.");
            return true;
        }

        if (wantedTime > 60) {
            executor.whisper("El tiempo máximo de arresto es 60 minutos.");
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

            executor.shout("*Libera las manos de " + targetHabbo.getHabboInfo().getUsername()
                    + " y lo encierra en una celda durante " + wantedTime + " minuto(s)*");
            targetRp.setCuffed(false);
            if (targetHabbo.getRoomUnit() != null) {
                targetHabbo.getRoomUnit().setEffectId(0, 0);
                targetHabbo.getRoomUnit().setCanWalk(true);
            }

            targetRp.setJailed(true);
            targetRp.setJailedTimeLeft(wantedTime);

            int jailRoomId = Emulator.getConfig().getInt("roleplay.jail.room.id", 4);
            targetHabbo.getHabboInfo().setHomeRoom(jailRoomId);
            Emulator.getGameEnvironment().getRoomManager().enterRoom(targetHabbo, jailRoomId, "", true);

            targetHabbo.alert("Has sido arrestad@ por "
                    + executor.getHabboInfo().getUsername() + " por " + wantedTime + " minuto(s)");

            RoleplayUserManager.saveRoleplayUser(targetRp);
            RoleplayUserManager.saveRoleplayUser(rpUser);

            return true;
        } else {
            executor.whisper("Debes estar más cerca de la persona para hacer eso.");
            return true;
        }
    }
}
