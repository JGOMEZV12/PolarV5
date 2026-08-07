package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/CuffCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Esposar al usuario con el fin de arrestarlo.
 */
public class CuffCommand extends Command {

    public CuffCommand() {
        super(null, new String[] {"esposar"});
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
            executor.whisper(
                    "Se ha producido un error al intentar encontrar a ese usuario, tal vez estén sin conexión.");
            return true;
        }
        GameClient targetClient = targetHabbo.getClient();

        if (executor.getHabboInfo().getCurrentRoom() == null
                || targetHabbo.getHabboInfo().getCurrentRoom() == null
                || executor.getHabboInfo().getCurrentRoom().getId()
                        != targetHabbo.getHabboInfo().getCurrentRoom().getId()) {
            executor.whisper(
                    "Se ha producido un error al encontrar a ese usuario, tal vez no están en línea o en esta habitación.");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (rpUser == null || targetRp == null) {
            return true;
        }

        if (rpUser.getJobId() != 1 && !rpUser.isPoliceTrial()) { // Policía
            executor.whisper("Sólo un oficial de policía puede utilizar este comando");
            return true;
        }

        if (targetRp.isPassiveMode()) {
            executor.whisper("¡No puedes hacerle eso a una persona que está en modo pasivo!");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

        if (targetRp.isDead()) {
            executor.whisper("¡No puedes esponsar a alguien que está muerto!");
            return true;
        }

        if (targetRp.isJailed() && !targetRp.isJailbroken()) {
            executor.whisper("¡No puedes esponsar a alguien que está en la cárcel!");
            return true;
        }

        if (targetHabbo.getRoomUnit() != null && targetHabbo.getRoomUnit().canWalk()) {
            executor.whisper("Usted no puede esposar a alguien que no está aturdido");
            return true;
        }

        if (targetRp.isCuffed()) {
            executor.whisper("Usted no puede esposar a alguien que ya está esposado");
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
            if (targetRp.getEquippedWeapon() != null) {
                executor.shout("*Agarra a " + targetHabbo.getHabboInfo().getUsername() + "'s "
                        + targetRp.getEquippedWeapon().getPublicName() + " y lo golpe para esposarlo*");
                targetRp.setEquippedWeapon(null);
            }

            executor.shout("*Saca las esposas de su cinturón y las envuelve alrededor de las manos de "
                    + targetHabbo.getHabboInfo().getUsername() + "'s para detenerlo*");
            targetRp.setCuffed(true);
            targetRp.setCuffedTimeLeft(8);

            if (targetHabbo.getRoomUnit() != null) {
                targetHabbo.getRoomUnit().setEffectId(0, 590);
            }

            RoleplayUserManager.saveRoleplayUser(targetRp);
            RoleplayUserManager.saveRoleplayUser(rpUser);
            return true;
        } else {
            executor.whisper("Usted debe accederse a este ciudadano con el fin de esposarlos");
            return true;
        }
    }
}
