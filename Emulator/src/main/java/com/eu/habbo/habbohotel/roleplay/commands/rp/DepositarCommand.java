package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayCooldowns;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Banking/DepositCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite depositar dinero de mano (créditos) en las cuentas bancarias de corriente o ahorros.
 */
public class DepositarCommand extends Command {

    public DepositarCommand() {
        super(null, new String[] {"depositar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo habbo = gameClient.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        if (params.length < 2) {
            habbo.whisper("Introduzca la cantidad que desea depositar.");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayCooldowns cooldowns = RoleplayUserManager.getRoleplayCooldowns(userId);

        if (rpUser == null || cooldowns == null) {
            habbo.whisper("No se cargó tu perfil de Roleplay.");
            return true;
        }

        if (rpUser.getBankAccount() <= 0) {
            habbo.whisper(
                    "¡No tienes cuentas bancarias! Pida a un trabajador bancario que abra una cuenta para usted.");
            return true;
        }

        if (cooldowns.hasCooldown("deposit")) {
            habbo.whisper("Por favor, espera " + cooldowns.getRemainingSeconds("deposit")
                    + " segundos para depositar de nuevo.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            habbo.whisper("Por favor ingrese un número válido para depositar.");
            return true;
        }

        if (amount <= 0) {
            habbo.whisper("Por favor ingrese una cantidad mayor que cero para depositar.");
            return true;
        }

        int handMoney = habbo.getHabboInfo().getCredits();
        if (handMoney < amount) {
            habbo.whisper("No tienes suficientes créditos en mano para depositar $" + amount + " (Tienes: $" + handMoney
                    + ")");
            return true;
        }

        cooldowns.setCooldown("deposit", 5);

        // Cuenta por defecto: corriente/chequings
        String accountType = "corriente";
        if (params.length >= 3) {
            accountType = params[2].toLowerCase();
        }

        if (accountType.equals("2")
                || accountType.equals("savings")
                || accountType.equals("ahorro")
                || accountType.equals("ahorros")) {
            if (rpUser.getBankAccount() < 2) {
                habbo.whisper("¡No tienes una cuenta de ahorros activa! Pide al banco que te abra una.");
                return true;
            }

            // Descontar créditos en mano y sumar en ahorros
            habbo.giveCredits(-amount);

            rpUser.setBankSavings(rpUser.getBankSavings() + amount);
            RoleplayUserManager.saveRoleplayUserAsync(rpUser);

            habbo.shout("*Mete $" + amount + " de su bolsillo y lo deposita en su cuenta de ahorros*");
        } else {
            if (rpUser.getBankAccount() < 1) {
                habbo.whisper("¡No tienes una cuenta corriente activa! Pide al banco que te abra una.");
                return true;
            }

            // Descontar créditos en mano y sumar en corriente
            habbo.giveCredits(-amount);

            rpUser.setBankChequings(rpUser.getBankChequings() + amount);
            RoleplayUserManager.saveRoleplayUserAsync(rpUser);

            habbo.shout("*Mete $" + amount + " de su bolsillo y lo deposita en su Cuenta Corriente*");
        }

        return true;
    }
}
