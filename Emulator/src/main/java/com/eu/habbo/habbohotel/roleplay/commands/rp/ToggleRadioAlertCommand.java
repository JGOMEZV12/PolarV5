package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/ToggleRadioAlertCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite ignorar o habilitar las alertas de la radio policial.
 */
public class ToggleRadioAlertCommand extends Command {

    public ToggleRadioAlertCommand() {
        super(null, new String[] {"togglera"});
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
            executor.whisper("¡Sólo los oficiales de policía pueden usar este comando!");
            return true;
        }

        rpUser.setDisableRadio(!rpUser.isDisableRadio());

        executor.whisper(
                "Usted es " + (rpUser.isDisableRadio() ? "now" : "no longer") + " Ignorando las alertas de radio");

        RoleplayUserManager.saveRoleplayUser(rpUser);

        return true;
    }
}
