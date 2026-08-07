package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

import java.util.Random;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/SearchCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Registrar al ciudadano para ver si tienen alguna droga.
 */
public class SearchCommand extends Command {

    public SearchCommand() {
        super(null, new String[] {"buscar", "catear", "search"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length == 1) {
            executor.whisper("Vaya, te olvidaste de introducir un nombre de usuario es:  :revisar usuario");
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

        if (rpUser.getJobId() != 1 && !rpUser.isPoliceTrial()) { // Policía
            executor.whisper("Sólo un oficial de policía puede utilizar este comando");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

        if (targetRp.isDead()) {
            executor.whisper("¡No puedes buscar a alguien que está muerto!");
            return true;
        }

        if (targetRp.isJailed()) {
            executor.whisper("¡No puedes buscar a alguien que está en la cárcel!");
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
            Random random = new Random();
            int chance = random.nextInt(100) + 1;

            if (chance <= 8) {
                executor.shout("*Revisa a " + targetHabbo.getHabboInfo().getUsername() + " Tratando de encontrar alguna droga, pero parece que no puede encontrar ninguna*");
                return true;
            } else {
                boolean hasWeed = targetRp.getWeed() > 0;
                boolean hasCocaine = targetRp.getCocaine() > 0;
                boolean hasHeroine = targetRp.getHeroina() > 0;

                if (!hasWeed && !hasCocaine && !hasHeroine) {
                    executor.shout("*Revisa a " + targetHabbo.getHabboInfo().getUsername() + " Tratando de encontrar alguna droga, pero parece que no puede encontrar ninguna*");
                    return true;
                } else if (hasWeed && !hasCocaine && !hasHeroine) {
                    executor.shout("*Revisa a " + targetHabbo.getHabboInfo().getUsername() + " y encuentra " + targetRp.getWeed() + "g de marihuana [LEGAL SON: 10g]*");
                    return true;
                } else if (hasCocaine && !hasWeed && !hasHeroine) {
                    executor.shout("*Revisa a " + targetHabbo.getHabboInfo().getUsername() + " y encuentra " + targetRp.getCocaine() + "g de cocaina [LEGAL SON: 8g] *");
                    return true;
                } else if (hasHeroine && !hasCocaine && !hasWeed) {
                    executor.shout("*Revisa a " + targetHabbo.getHabboInfo().getUsername() + " y encuentra " + targetRp.getHeroina() + "g de heroina [LEGAL SON: 20g] *");
                    return true;
                } else {
                    executor.shout("*Revisa a " + targetHabbo.getHabboInfo().getUsername() + " y encuentra " + targetRp.getCocaine() + "g de cocaina [LEGAL SON: 8g] y " + targetRp.getWeed() + "g de marihuana [LEGAL SON: 10g]*");
                    return true;
                }
            }
        } else {
            executor.whisper("¡Debes acercarte a este ciudadano para revisarl!");
            return true;
        }
    }
}
