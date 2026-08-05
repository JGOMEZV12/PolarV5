package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Criminal Activity/BusinfoCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra información sobre el funcionamiento de las líneas y estaciones de autobús en la ciudad de roleplay.
 */
public class BusinfoCommand extends Command {

    public BusinfoCommand() {
        super(null, new String[] {"businfo"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        executor.whisper("--- Rutas de Autobús ---\n"
                + "Usa :bus para viajar entre diferentes paradas de autobús de la ciudad.\n"
                + "Estación Central: Av. Venezuela (Costo de pasaje: $5 créditos).");
        return true;
    }
}
