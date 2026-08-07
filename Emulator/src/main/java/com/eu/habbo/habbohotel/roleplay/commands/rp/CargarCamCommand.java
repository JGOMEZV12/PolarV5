package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Camionero/CargarCamCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Conduciendo un camión, elije una Carga.
 */
public class CargarCamCommand extends Command {

    public CargarCamCommand() {
        super(null, new String[] {"cargarcamion"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length != 2) {
            executor.whisper("Comando Inválido. ((Usa ':cargarcamion [id]'))");
            return true;
        }

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

        if (rpUser.getCamCargId() > 0) {
            executor.whisper("Ya has cargado un Camión. No puedes hacer más de un recorrido a la vez. Usa ':abandonarcarga' para comenzar uno nuevo.");
            return true;
        }

        if (rpUser.getCamState() > 0) {
            executor.whisper("Tu camión ya fue cargado. ¡Ve a entregar la carga a tu destino!");
            return true;
        }

        if (rpUser.isCamLoading()) {
            executor.whisper("Ya te encuentras cargando el camión. Por favor espera...");
            return true;
        }

        int id;
        try {
            id = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            executor.whisper("Ingresa una ID válida. ((:cargarcamion [ID]))");
            return true;
        }

        if (id < 1 || id > 4) {
            executor.whisper("ID de carga inválida. Usa :cargas para ver un listado de ellas.");
            return true;
        }

        int destRoomId = 10 + id; // Mock destination rooms
        rpUser.setCamCargId(id);
        rpUser.setCamDest(destRoomId);
        rpUser.setCamState(1); // Loaded
        rpUser.setCamOwnId(userId);

        executor.shout("*Comienza a cargar su camión*");
        executor.whisper("Has cargado tu camión con éxito. ¡Lleva la carga al destino room ID " + destRoomId + "!");

        return true;
    }
}
