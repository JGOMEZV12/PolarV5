package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Bank/CheckBalanceCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Compruebe el saldo de tipo de cuenta del usuario de destino.
 */
public class CheckBalanceCommand extends Command {

    public CheckBalanceCommand() {
        super(null, new String[] {"versaldo"});
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

        if (rpUser.getJobId() != 8) { // Banco / Banquero (jobId = 8)
            executor.whisper("Lo siento, no trabajas en la corporación del banco");
            return true;
        }

        if (params.length == 1) {
            executor.whisper("Por favor ingrese un tipo de banco y destino. (corriente, ahorro)");
            return true;
        }

        Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);
        if (targetHabbo == null) {
            executor.whisper("¡Uy, no pudo encontrar ese usuario!");
            return true;
        }
        GameClient targetClient = targetHabbo.getClient();

        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (targetRp == null) {
            return true;
        }

        String accountType = "corriente";
        if (params.length >= 3) {
            String typeParam = params[2].toLowerCase();
            if (typeParam.equals("savings") || typeParam.equals("ahorro")) {
                accountType = "ahorro";
            }
        }

        if (accountType.equals("corriente")) {
            if (targetRp.getBankAccount() <= 0) {
                executor.whisper("Este usuario no tiene una cuenta corriente");
                return true;
            }

            executor.shout("*Revisa la cuenta de " + targetHabbo.getHabboInfo().getUsername()
                    + "'s Saldo en su Cuenta Corriente*");
            executor.whisper(targetHabbo.getHabboInfo().getUsername() + " Tienen un saldo de: $"
                    + targetRp.getBankChequings() + " En su cuenta Corriente");
        } else {
            if (targetRp.getBankAccount() <= 1) {
                executor.whisper("¡Este usuario no tiene una cuenta de ahorros!");
                return true;
            }

            executor.shout("*Revisa la cuenta de " + targetHabbo.getHabboInfo().getUsername()
                    + "'s Saldo en su cuenta de Ahorros*");
            executor.whisper(targetHabbo.getHabboInfo().getUsername() + " Tiene $" + targetRp.getBankSavings()
                    + " En su cuenta de Ahorros!");
        }

        return true;
    }
}
