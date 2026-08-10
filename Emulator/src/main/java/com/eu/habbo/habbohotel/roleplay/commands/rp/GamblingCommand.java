package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import java.security.SecureRandom;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Events/GamblingCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite al usuario apostar una cantidad de dinero en un juego de azar (50% de probabilidad de ganar el doble o perder).
 */
public class GamblingCommand extends Command {

    private final SecureRandom random = new SecureRandom();

    public GamblingCommand() {
        super(null, new String[] {"apostar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();

        if (params.length < 2) {
            executor.whisper("Uso correcto: :apostar [cantidad_creditos]");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            executor.whisper("¡Por favor ingrese un número válido de créditos a apostar!");
            return true;
        }

        if (amount <= 0) {
            executor.whisper("La cantidad a apostar debe ser mayor a 0.");
            return true;
        }

        if (executor.getHabboInfo().getCredits() < amount) {
            executor.whisper("¡No tienes suficientes créditos en mano para apostar esa cantidad!");
            return true;
        }

        executor.shout("*Coloca una apuesta de $" + amount + " créditos en un juego de azar*");

        boolean win = random.nextBoolean();
        if (win) {
            executor.giveCredits(amount); // Gana el doble (vuelve a recibir su apuesta como premio extra)
            executor.shout("*¡Ha ganado la apuesta y recibe un total de $" + (amount * 2) + " créditos!*");
        } else {
            executor.giveCredits(-amount); // Pierde la apuesta
            executor.shout("*Ha perdido la apuesta de $" + amount + " créditos*");
        }

        return true;
    }
}
