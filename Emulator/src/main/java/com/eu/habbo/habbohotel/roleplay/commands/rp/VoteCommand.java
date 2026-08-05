package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Police Related/VoteCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite a los miembros del jurado emitir su voto de 'inocente' o 'culpable' durante un juicio activo.
 */
public class VoteCommand extends Command {

    public VoteCommand() {
        super(null, new String[] {"votar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();

        if (params.length < 2) {
            executor.whisper("Uso correcto: :votar [inocente/culpable]");
            return true;
        }

        String vote = params[1].toLowerCase();

        if (vote.equals("inocente") || vote.equals("inno")) {
            executor.shout("*Emite su voto declarando al acusado INOCENTE*");
            executor.whisper("¡Su voto de inocente ha sido emitido!");
        } else if (vote.equals("culpable") || vote.equals("guilty")) {
            executor.shout("*Emite su voto declarando al acusado CULPABLE*");
            executor.whisper("¡Su voto de culpable ha sido emitido!");
        } else {
            executor.whisper(
                    "¡Acción no válida! Usted debe decidir si el acusado es encontrado 'inocente' o 'culpable'.");
        }

        return true;
    }
}
