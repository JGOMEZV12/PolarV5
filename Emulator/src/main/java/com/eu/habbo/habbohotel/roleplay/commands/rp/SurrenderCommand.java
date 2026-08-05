package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Police Related/SurrenderCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite a un fugitivo entregarse/rendirse voluntariamente a las autoridades legales, yendo directo a prisión con una reducción de condena.
 */
public class SurrenderCommand extends Command {

    public SurrenderCommand() {
        super(null, new String[] {"rendicion"});
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
            executor.whisper("No se pudo cargar tu perfil de Roleplay.");
            return true;
        }

        if (!rpUser.isWanted()) {
            executor.whisper("¡No eres buscado por las autoridades!");
            return true;
        }

        if (params.length < 2 || !params[1].equalsIgnoreCase("yes")) {
            executor.whisper("¿Estás seguro de que quieres rendirte? Serás encarcelado por "
                    + (rpUser.getWantedLevel() * 5) + " minutos!");
            executor.whisper("Escribe ':rendicion yes' si realmente quieres renunciar a tu libertad.");
            return true;
        }

        // Ejecutar entrega
        rpUser.setWanted(false);
        rpUser.setWantedLevel(0);
        rpUser.setWantedTimeLeft(0);
        rpUser.setJailed(true);
        rpUser.setJailedTimeLeft(rpUser.getWantedLevel() * 5); // 5 minutos por cada nivel de búsqueda

        RoleplayUserManager.saveRoleplayUser(rpUser);

        executor.shout("*Se entrega voluntariamente a las autoridades judiciales y es acompañado a prisión*");
        executor.whisper("Te has entregado a las autoridades. Has sido encarcelado.");

        return true;
    }
}
