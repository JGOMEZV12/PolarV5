package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayCooldowns;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Banking/BalanceCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra el saldo de las cuentas bancarias de roleplay del usuario.
 */
public class SaldoCommand extends Command {

    public SaldoCommand() {
        super(null, new String[] {"saldo"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo habbo = gameClient.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayCooldowns cooldowns = RoleplayUserManager.getRoleplayCooldowns(userId);

        if (rpUser == null || cooldowns == null) {
            habbo.whisper("No se pudo cargar tus datos de Roleplay.");
            return true;
        }

        if (rpUser.getBankAccount() <= 0) {
            habbo.whisper(
                    "¡No tienes cuentas bancarias! Pida a un trabajador bancario que abra una cuenta para usted.");
            return true;
        }

        if (cooldowns.hasCooldown("balance")) {
            habbo.whisper("Espera " + cooldowns.getRemainingSeconds("balance")
                    + " segundos antes de verificar tu saldo nuevamente.");
            return true;
        }

        boolean balanceHide = false;
        if (params.length > 1) {
            if (params[1].equalsIgnoreCase("hide")) {
                balanceHide = true;
            }
        }

        cooldowns.setCooldown("balance", 5);

        // Dinero en mano actual (credits en Polaris)
        int handMoney = habbo.getHabboInfo().getCredits();

        if (!balanceHide) {
            // Efecto de celular/teléfono (efecto 65 es el teléfono en Habbo)
            if (habbo.getRoomUnit() != null) {
                habbo.getRoomUnit().setEffectId(65, 0);
                RpEngine.getThreading().run(() -> {
                    try {
                        Thread.sleep(1500);
                        if (habbo.getRoomUnit() != null) {
                            habbo.getRoomUnit().setEffectId(0, 0);
                        }
                    } catch (InterruptedException ignored) {
                    }
                });
            }
            habbo.whisper("*Saca su teléfono y abre aplicación de banco para ver su saldo. Tienes: $"
                    + rpUser.getBankChequings() + " (Corriente) y $" + handMoney + " (Mano)*");
        } else {
            if (habbo.getRoomUnit() != null) {
                habbo.getRoomUnit().setEffectId(65, 0);
                RpEngine.getThreading().run(() -> {
                    try {
                        Thread.sleep(1500);
                        if (habbo.getRoomUnit() != null) {
                            habbo.getRoomUnit().setEffectId(0, 0);
                        }
                    } catch (InterruptedException ignored) {
                    }
                });
            }
            habbo.whisper(
                    "Tienes $" + rpUser.getBankChequings() + " En tu cuenta corriente y $" + handMoney + " en mano.");
        }

        if (rpUser.getBankAccount() > 1) {
            habbo.whisper("También tienes $" + rpUser.getBankSavings() + " En tu cuenta de ahorros.");
        }

        return true;
    }
}
