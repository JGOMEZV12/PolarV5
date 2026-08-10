package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Criminal Activity/LawsCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra las leyes constitucionales de la ciudad de roleplay al usuario.
 */
public class LawsCommand extends Command {

    public LawsCommand() {
        super(null, new String[] {"leyes"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        executor.whisper("--- Leyes del Servidor ---\n"
                + "1. Queda estrictamente prohibido asesinar civiles en zonas seguras (zonas verdes).\n"
                + "2. No se tolerará la evasión de arrestos de forma abusiva.\n"
                + "3. El robo de bóvedas bancarias requiere nivel 5+ y armas cargadas.\n"
                + "4. Toda transacción de drogas es ilegal bajo las leyes de la policía.");
        return true;
    }
}
