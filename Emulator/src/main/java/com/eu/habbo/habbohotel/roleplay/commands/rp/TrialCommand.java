package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Police Related/TrialCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite a un recluso con suficiente tiempo de condena solicitar un juicio en la corte.
 */
public class TrialCommand extends Command {

    public TrialCommand() {
        super(null, new String[] {"juicio"});
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
            executor.whisper("No se pudo cargar tu perfil de Roleplay.");
            return true;
        }

        if (!rpUser.isJailed()) {
            executor.whisper("¡Usted no puede solicitar un juicio en el tribunal mientras no está encarcelado!");
            return true;
        }

        if (rpUser.getJailedTimeLeft() < 11) {
            executor.whisper(
                    "Lo siento, solo los reclusos encarcelados que tienen 11 o más minutos restantes en prisión pueden solicitar un juicio.");
            return true;
        }

        executor.shout("*Solicita un juicio formal en la corte de justicia*");
        executor.whisper("Usted ha solicitado un juicio. Por favor espera a que se inicie la sesión de tribunal.");

        return true;
    }
}
