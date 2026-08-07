package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/UnLawCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Elimina a un ciudadano de la lista de buscados.
 */
public class UnLawCommand extends Command {

    public UnLawCommand() {
        super(null, new String[] {"unlaw", "quitarcargos"});
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
            executor.whisper("Sólo un oficial de policía puede utilizar este comando");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

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

        RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (targetRp == null) {
            return true;
        }

        if (!targetRp.isWanted()) {
            executor.whisper("This citizen is not wanted!");
            return true;
        }

        executor.shout("*Quita a " + targetHabbo.getHabboInfo().getUsername() + " De la Lista de peligrosos, limpia su nombre*");
        targetRp.setWanted(false);
        targetRp.setWantedLevel(0);
        targetRp.setWantedTimeLeft(0);

        RoleplayUserManager.saveRoleplayUser(targetRp);

        return true;
    }
}
