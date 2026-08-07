package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Restaurant/ServeCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Sirve la comida o la bebida deseada delante de usted.
 */
public class ServeCommand extends Command {

    public ServeCommand() {
        super(null, new String[] {"servir", "serve"});
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

        if (rpUser.getJobId() != 7) { // Restaurante / Cocinero / Camarero (jobId = 7)
            executor.whisper("¡No tienes permiso para usar este comando!");
            return true;
        }

        if (!rpUser.isWorking()) {
            executor.whisper("Usted debe estar trabajando para hacer esto");
            return true;
        }

        if (rpUser.isDead()) {
            executor.whisper("¡No puedes servir comida o bebidas mientras estés muerto!");
            return true;
        }

        if (rpUser.isJailed()) {
            executor.whisper("¡No puedes servir comida o bebidas mientras estás encarcelado!");
            return true;
        }

        if (params.length == 1) {
            executor.whisper("Por favor escriba :servir [item / menu]. Ítems disponibles: Pizza, Hamburguesa, Cerveza, Soda, Agua.");
            return true;
        }

        String targetItem = params[1].toLowerCase();

        if (targetItem.equals("menu")) {
            StringBuilder sb = new StringBuilder();
            sb.append("---------- Menú del Restaurante ----------\n\n");
            sb.append("🍽 Comidas:\n");
            sb.append("  Pizza, Hamburguesa\n\n");
            sb.append("🥤 Bebidas:\n");
            sb.append("  Cerveza, Soda, Agua\n\n");
            sb.append("Usa :servir [nombre] para servir un ítem.");
            executor.alert(sb.toString());
            return true;
        }

        String displayName = "Agua";
        int carryId = 1;

        if (targetItem.equals("pizza")) {
            displayName = "Pizza";
            carryId = 12;
        } else if (targetItem.equals("hamburguesa")) {
            displayName = "Hamburguesa";
            carryId = 11;
        } else if (targetItem.equals("cerveza")) {
            displayName = "Cerveza";
            carryId = 3;
        } else if (targetItem.equals("soda")) {
            displayName = "Soda";
            carryId = 19;
        } else if (targetItem.equals("agua")) {
            displayName = "Agua";
            carryId = 1;
        } else {
            executor.whisper("¡Esto no es un tipo válido de comida o bebida! Ítems disponibles: Pizza, Hamburguesa, Cerveza, Soda, Agua.");
            return true;
        }

        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setHandItem(carryId);
        }

        executor.shout("*Sirve una deliciosa " + displayName + " bien preparada*");
        executor.whisper("¡Has servido '" + displayName + "' con éxito!");

        return true;
    }
}
