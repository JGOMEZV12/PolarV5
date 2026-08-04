package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Offers/CtransferirCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Transfiere dinero en mano (créditos) a otro usuario conectado.
 */
public class CtransferirCommand extends Command {

    public CtransferirCommand() {
        super(null, new String[] {"ctransferir"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();

        if (params.length < 3) {
            executor.whisper("Uso: :ctransferir [usuario] [cantidad]");
            return true;
        }

        String targetUsername = params[1];
        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (target == null) {
            executor.whisper("El usuario '" + targetUsername + "' no se encuentra conectado.");
            return true;
        }

        if (target == executor) {
            executor.whisper("¡No puedes transferirte dinero a ti mismo!");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            executor.whisper("Por favor ingrese una cantidad numérica válida.");
            return true;
        }

        if (amount <= 0) {
            executor.whisper("La cantidad a transferir debe ser mayor que cero.");
            return true;
        }

        int handMoney = executor.getHabboInfo().getCredits();
        if (handMoney < amount) {
            executor.whisper("No tienes suficientes créditos en mano para transferir $" + amount);
            return true;
        }

        // Realizar la transferencia de mano (créditos) de forma segura y síncrona
        executor.giveCredits(-amount);
        target.giveCredits(amount);

        executor.shout("*Le entrega $" + amount + " en efectivo de su bolsillo a "
                + target.getHabboInfo().getUsername() + "*");
        executor.whisper(
                "Has transferido $" + amount + " a " + target.getHabboInfo().getUsername() + " con éxito.");
        target.whisper("¡Has recibido $" + amount + " en efectivo de "
                + executor.getHabboInfo().getUsername() + "!");

        return true;
    }
}
