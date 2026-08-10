package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Camionero/LeaveCamCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Siendo Camionero, permite abandonar tu carga para poder usar un nuevo camión.
 */
public class LeaveCamCommand extends Command {

    public LeaveCamCommand() {
        super(null, new String[] {"abandonarcarga", "leavecam"});
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

        if (rpUser.getJobId() != 2) { // Camionero / Transportista
            executor.whisper("Debes tener el trabajo de Camionero para usar ese comando.");
            return true;
        }

        if (rpUser.getCamCargId() == 0) {
            executor.whisper("No tienes ninguna carga a tu nombre para abandonar.");
            return true;
        }

        rpUser.setCamCargId(0);
        rpUser.setCamDest(0);
        rpUser.setCamOwnId(0);
        rpUser.setCamState(0);
        rpUser.setDrivingCar(false);

        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(0, 0);
        }

        executor.shout("*Abandona la Carga de su Camión*");
        executor.whisper("Tu Camión ha sido descargado. No has terminado el recorrido, no se te pagará nada.");

        RoleplayUserManager.saveRoleplayUser(rpUser);

        return true;
    }
}
