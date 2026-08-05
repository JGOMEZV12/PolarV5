package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Police Related/BailCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite a un ciudadano pagar la fianza de un prisionero para liberarlo de la cárcel.
 */
public class BailCommand extends Command {

    public BailCommand() {
        super(null, new String[] {"fianza"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 2) {
            executor.whisper("Uso correcto: :fianza [usuario] [yes]");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (targetHabbo == null) {
            executor.whisper(
                    "Se ha producido un error al intentar encontrar a ese usuario, tal vez estén sin conexión.");
            return true;
        }

        if (targetUsername.equalsIgnoreCase(executor.getHabboInfo().getUsername())) {
            executor.whisper("¡No puedes pagarte la fianza a ti mismo!");
            return true;
        }

        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());
        if (targetRp == null) {
            executor.whisper("No se pudo cargar el perfil de Roleplay de " + targetUsername + ".");
            return true;
        }

        if (!targetRp.isJailed()) {
            executor.whisper("¡No puedes liberar a alguien que no está en la cárcel!");
            return true;
        }

        int wantedLevel = targetRp.getWantedLevel();
        int bailCost = 5000;
        if (wantedLevel == 2) bailCost = 8000;
        else if (wantedLevel == 3) bailCost = 11000;
        else if (wantedLevel == 4) bailCost = 14000;
        else if (wantedLevel >= 5) bailCost = 17000;

        if (executor.getHabboInfo().getCredits() < bailCost) {
            executor.whisper("No tienes suficiente dinero ($" + bailCost + ") en mano para pagar la fianza de "
                    + targetUsername);
            return true;
        }

        if (params.length < 3 || !params[2].equalsIgnoreCase("yes")) {
            executor.whisper("Si realmente quieres pagar $" + bailCost + " para liberar a " + targetUsername
                    + ", escribe ':fianza " + targetUsername + " yes'.");
            return true;
        }

        // Ejecutar pago y liberación
        executor.giveCredits(-bailCost);
        targetRp.setJailed(false);
        targetRp.setJailedTimeLeft(0);

        RoleplayUserManager.saveRoleplayUser(targetRp);

        executor.shout("*Paga la fianza de " + targetUsername + " por $" + bailCost
                + ", liberándolo de la cárcel en libertad condicional*");
        targetHabbo.whisper("¡" + executor.getHabboInfo().getUsername() + " ha pagado tu fianza de $" + bailCost
                + "! Has sido liberado.");

        return true;
    }
}
