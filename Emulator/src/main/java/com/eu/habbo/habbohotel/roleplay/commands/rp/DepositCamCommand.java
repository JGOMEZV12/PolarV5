package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Camionero/DepositCamCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Siendo Camionero, entrega la carga de tu Camión a tu respectivo destino.
 */
public class DepositCamCommand extends Command {

    public DepositCamCommand() {
        super(null, new String[] {"depositarcarga"});
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

        if (!rpUser.isDrivingCar()) {
            executor.whisper("Debes conducir un Camión para hacer eso.");
            return true;
        }

        if (rpUser.getCamOwnId() > 0 && rpUser.getCamOwnId() != userId) {
            executor.whisper("Este camión no ha sido cargado bajo tu nombre. No puedes hacer recorridos ajenos.");
            return true;
        }

        if (rpUser.getCamState() != 1) {
            executor.whisper("El camión no ha sido cargado aún.");
            return true;
        }

        int currentRoomId = executor.getHabboInfo().getCurrentRoom() != null ? executor.getHabboInfo().getCurrentRoom().getId() : 0;
        if (rpUser.getCamDest() != currentRoomId) {
            executor.whisper("¡Debes ir al room ID " + rpUser.getCamDest() + " para entregar la mercancía!");
            return true;
        }

        if (rpUser.isCamUnLoading()) {
            executor.whisper("Ya te encuentras descargando el camión. Por favor espera...");
            return true;
        }

        rpUser.setCamState(2); // Unloaded

        executor.shout("*Comienza a descargar su camión*");
        executor.whisper("Has descargado el camión con éxito. ¡Vuelve a la sede de transportistas para entregar el camión y recibir tu paga! ((Usa :entregarcamion))");

        RoleplayUserManager.saveRoleplayUser(rpUser);

        return true;
    }
}
