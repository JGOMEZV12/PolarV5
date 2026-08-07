package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/RadioAlertCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Envía un mensaje por radio a todos los oficiales de policía en línea.
 */
public class RadioAlertCommand extends Command {

    public RadioAlertCommand() {
        super(null, new String[] {"radio"});
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

        if (rpUser.isDisableRadio()) {
            executor.whisper("¡Tiene alertas de radio deshabilitadas! Tipo ':togglera' para volver a habilitarlos!");
            return true;
        }

        if (params.length == 1) {
            executor.whisper("Introduzca un mensaje para enviar.");
            return true;
        }

        StringBuilder messageBuilder = new StringBuilder();
        for (int i = 1; i < params.length; i++) {
            messageBuilder.append(params[i]).append(" ");
        }
        String message = messageBuilder.toString().trim();

        String formattedMessage = "[RADIO POLICÍA] " + executor.getHabboInfo().getUsername() + ": " + message;

        for (GameClient client : Emulator.getGameServer().getGameClientManager().getCurrentlyConnectedClients().values()) {
            if (client.getHabbo() != null) {
                RoleplayUser otherRp = RoleplayUserManager.getRoleplayUser(client.getHabbo().getHabboInfo().getId());
                if (otherRp != null && (otherRp.getJobId() == 1 || otherRp.isPoliceTrial()) && !otherRp.isDisableRadio()) {
                    client.getHabbo().whisper(formattedMessage);
                }
            }
        }

        return true;
    }
}
