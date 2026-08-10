package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Criminal Activity/TutorialCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra un tutorial introductorio y noticias informativas al usuario sobre el funcionamiento del sistema de roleplay.
 */
public class TutorialCommand extends Command {

    public TutorialCommand() {
        super(null, new String[] {"noticias", "tutorial"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        executor.whisper("--- Guía Introductoria y Noticias ---\n"
                + "¡Bienvenido al sistema RP de Polaris!\n"
                + "Usa los siguientes comandos básicos para interactuar:\n"
                + "- :saldo -> Consulta tus estadísticas financieras.\n"
                + "- :equipar [arma] -> Saca tu arma defensiva o de ataque.\n"
                + "- :manejar -> Enciende el motor de tu vehículo comprado.\n"
                + "- :fianza [usuario] -> Paga la fianza de un prisionero.");
        return true;
    }
}
