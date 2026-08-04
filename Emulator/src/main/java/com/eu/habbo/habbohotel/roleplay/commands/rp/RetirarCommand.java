package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayCooldowns;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Banking/WithdrawCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite retirar dinero de las cuentas bancarias de corriente o ahorros.
 */
public class RetirarCommand extends Command {

    public RetirarCommand() {
        super(null, new String[] {"retirar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo habbo = gameClient.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        if (params.length < 2) {
            habbo.whisper("Ingrese la cantidad que desea retirar.");
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

        if (cooldowns.hasCooldown("withdraw")) {
            habbo.whisper("Por favor, espera " + cooldowns.getRemainingSeconds("withdraw")
                    + " segundos para retirar de nuevo.");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            habbo.whisper("Por favor ingrese un número válido para retirar.");
            return true;
        }

        if (amount <= 0) {
            habbo.whisper("Por favor ingrese una cantidad mayor que cero para retirar.");
            return true;
        }

        cooldowns.setCooldown("withdraw", 5);

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

            int taxAmount = (int) (amount * 0.05);

            if (amount < 20) {
                habbo.whisper("El monto mínimo que puede retirar de su cuenta de ahorros es $20.");
                return true;
            }

            if (rpUser.getBankSavings() < amount) {
                habbo.whisper("No tienes $" + amount + " en tu cuenta de ahorros para retirar.");
                return true;
            }

            if (params.length < 4 || !params[3].equalsIgnoreCase("yes")) {
                habbo.whisper("Te costará $" + taxAmount + " en impuestos retirar $" + amount
                        + " de tu cuenta de ahorros! Escribe ':retirar " + amount
                        + " ahorro yes' para aceptar el impuesto.");
                return true;
            }

            // Realizar retiro de ahorros
            rpUser.setBankSavings(rpUser.getBankSavings() - amount);
            habbo.giveCredits(amount - taxAmount);

            RoleplayUserManager.saveRoleplayUserAsync(rpUser);

            habbo.shout("*Saca $" + amount + " de su cuenta de ahorros*");
            habbo.whisper("Pagaste un impuesto de $" + taxAmount + " para retirar $" + amount + ".");
        } else {
            if (rpUser.getBankAccount() < 1) {
                habbo.whisper("¡No tienes una cuenta corriente activa! Pide al banco que te abra una.");
                return true;
            }

            if (rpUser.getBankChequings() < amount) {
                habbo.whisper("No tienes $" + amount + " en tu cuenta corriente para retirar.");
                return true;
            }

            // Realizar retiro corriente
            rpUser.setBankChequings(rpUser.getBankChequings() - amount);
            habbo.giveCredits(amount);

            RoleplayUserManager.saveRoleplayUserAsync(rpUser);

            habbo.shout("*Saca $" + amount + " de su cuenta corriente y lo coloca en sus bolsillos*");
        }

        return true;
    }
}
