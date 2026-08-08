package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Bank/OpenAccountCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Se ofrece para abrir el tipo de cuenta bancaria al usuario.
 */
public class OpenAccountCommand extends Command {

    public OpenAccountCommand() {
        super(null, new String[] {"abrircuenta", "openaccount"});
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
            executor.whisper("Por favor ingrese un tipo de cuenta. (corriente, ahorro)");
            return true;
        }

        Habbo targetHabbo = RpEngine.getGameServer().getGameClientManager().getHabbo(params[1]);
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
            if (targetRp.getBankAccount() > 0) {
                executor.whisper("Lo sentimos, pero esta persona ya tiene una cuenta corriente");
                return true;
            }

            executor.shout("*Ofertas para abrir una cuenta corriente para "
                    + targetHabbo.getHabboInfo().getUsername() + " ¡GRATIS!*");
            targetRp.getOfferManager().createOffer("corriente", userId, 0);
            targetHabbo.whisper(
                    "Acaba de recibir una Cuenta corriente gratis DIGA ':aceptar corriente' para activarla!");
        } else {
            int cost = 2500;
            if (targetRp.getBankAccount() > 1) {
                executor.whisper("Lo sentimos, pero esta persona ya tiene una cuenta de ahorro");
                return true;
            }

            if (targetHabbo.getHabboInfo().getCredits() < cost) {
                executor.whisper("¡Este ciudadano no puede pagar una Cuenta de Ahorros!");
                return true;
            }

            executor.shout("*Ofertas para abrir una Cuenta de Ahorro para "
                    + targetHabbo.getHabboInfo().getUsername() + " por $" + cost + "*");
            targetRp.getOfferManager().createOffer("ahorro", userId, cost);
            targetHabbo.whisper("Recién se le ha ofrecido una cuenta de ahorros por $" + cost
                    + " DIGA ':aceptar ahorro' para activarla!");
        }

        return true;
    }
}
