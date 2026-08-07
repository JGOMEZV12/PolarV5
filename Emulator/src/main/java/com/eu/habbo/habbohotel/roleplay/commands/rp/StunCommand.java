package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/StunCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Paraliza a una persona para detenerla.
 */
public class StunCommand extends Command {

    public StunCommand() {
        super(null, new String[] {"stun"});
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

        if (targetRp.isCuffed()) {
            executor.whisper("Esta persona se encuentra esposada. No hace falta paralizarla.");
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
            executor.whisper("¡No puedes hacerle eso a una persona muert@!");
            return true;
        }

        if (targetRp.isJailed()) {
            executor.whisper("¡No puedes hacerle eso a una persona encarcelad@!");
            return true;
        }

        if (targetHabbo.getRoomUnit() != null && !targetHabbo.getRoomUnit().canWalk()) {
            executor.whisper("¡No puedes aturdir a alguien que ya está aturdido, debes esposarlo!");
            return true;
        }

        if (rpUser.getEquippedWeapon() == null) {
            executor.whisper("¡Debes equiparte el arma!");
            return true;
        }

        if (!rpUser.getEquippedWeapon().getName().equalsIgnoreCase("electrica")) {
            executor.whisper("¡Debes equiparte la pistola electrica!");
            return true;
        }

        RoomTile clientTile = executor.getRoomUnit().getCurrentLocation();
        RoomTile targetTile = targetHabbo.getRoomUnit().getCurrentLocation();

        if (clientTile == null || targetTile == null) {
            executor.whisper("Ubicación de sala no válida.");
            return true;
        }

        int dx = Math.abs(clientTile.x - targetTile.x);
        int dy = Math.abs(clientTile.y - targetTile.y);

        if ((dx == 0 || dy == 0) && (dx > 7 || dy > 7)) {
            executor.whisper("¡El objetivo está demasiado lejos! (Máx. 7 en línea recta)");
            return true;
        }
        if ((dx != 0 && dy != 0) && (dx > 4 || dy > 4)) {
            executor.whisper("¡El objetivo está demasiado lejos! (Máx. 4 en diagonal)");
            return true;
        }

        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(0, 536);
        }

        executor.shout("*Dispara su pistola electrica hacia " + targetHabbo.getHabboInfo().getUsername() + " inmovilizándolo inmediatamente*");

        targetRp.setStun(true);
        if (targetHabbo.getRoomUnit() != null) {
            targetHabbo.getRoomUnit().setCanWalk(false);
        }

        if (targetRp.getEquippedWeapon() != null) {
            targetHabbo.shout("*Guarda su " + targetRp.getEquippedWeapon().getPublicName() + "*");
            targetRp.setEquippedWeapon(null);
        }

        RoleplayUserManager.saveRoleplayUser(targetRp);
        RoleplayUserManager.saveRoleplayUser(rpUser);

        return true;
    }
}
