package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Interactions/Marriage/AbandonarCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite abandonar la relación paterno-filial (hijo) con otro usuario.
 */
public class AbandonarCommand extends Command {

    public AbandonarCommand() {
        super(null, new String[] {"abandonar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 2) {
            executor.whisper("Uso correcto: :abandonar [usuario]");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        if (rpUser == null) {
            executor.whisper("No se pudo cargar tu perfil de Roleplay.");
            return true;
        }

        if (rpUser.getHijo() <= 0) {
            executor.whisper("¡No tienes ningún hij@ registrado!");
            return true;
        }

        if (targetHabbo == null) {
            // Unilateral offline
            rpUser.setHijo(0);
            RoleplayUserManager.saveRoleplayUser(rpUser);
            executor.shout("*Le dice a " + targetUsername + " que lo abandona definitivamente*");
            return true;
        }

        int targetId = targetHabbo.getHabboInfo().getId();
        RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetId);

        if (targetRp == null || targetRp.getHijo() != userId) {
            executor.whisper("¡Este usuario no es tu hij@!");
            return true;
        }

        rpUser.setHijo(0);
        targetRp.setHijo(0);

        RoleplayUserManager.saveRoleplayUser(rpUser);
        RoleplayUserManager.saveRoleplayUser(targetRp);

        executor.shout("*Abandona la relación familiar con " + targetUsername + "*");
        targetHabbo.whisper("¡" + executor.getHabboInfo().getUsername() + " te ha abandonado de su familia!");

        return true;
    }
}
