package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Houses/SetPriceCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite al dueño de una casa establecer el precio de venta de su propiedad de roleplay.
 */
public class SetPriceCommand extends Command {

    public SetPriceCommand() {
        super(null, new String[] {"ponerprecio"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();

        if (params.length < 2) {
            executor.whisper("Uso correcto: :ponerprecio [monto]");
            return true;
        }

        int price;
        try {
            price = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
            executor.whisper("Por favor ingrese un número válido de precio.");
            return true;
        }

        if (price < 0) {
            executor.whisper("El precio no puede ser negativo.");
            return true;
        }

        executor.whisper("¡Has establecido el precio de venta de tu propiedad en $" + price + "!");
        executor.shout("*Pone su propiedad en venta por un precio de $" + price + "*");

        return true;
    }
}
