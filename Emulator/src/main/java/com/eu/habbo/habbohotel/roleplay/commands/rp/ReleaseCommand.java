package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/ReleaseCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Libera a un convicto de la cárcel.
 */
public class ReleaseCommand extends Command {

    public ReleaseCommand() {
        super(null, new String[] {"release", "liberar"});
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

        Habbo targetHabbo = RpEngine.getGameServer().getGameClientManager().getHabbo(params[1]);
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
                    targetHabbo.getHabboInfo().getUsername() + " ¡Ni siquiera está en la misma habitación que tú!");
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

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

        if (!targetRp.isJailed()) {
            executor.whisper("¡No puedes liberar a alguien que ya no está en la cárcel!");
            return true;
        }

        executor.shout(
                "*Libera a " + targetHabbo.getHabboInfo().getUsername() + " De la cárcel en libertad condicional*");
        targetRp.setJailed(false);
        targetRp.setStun(false);
        targetRp.setParalized(false);
        targetRp.setJailedTimeLeft(0);

        RoleplayUserManager.saveRoleplayUser(targetRp);
        RoleplayUserManager.saveRoleplayUser(rpUser);

        return true;
    }
}
