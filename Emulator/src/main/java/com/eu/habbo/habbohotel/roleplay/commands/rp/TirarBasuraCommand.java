package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Banking/TirarBasuraCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite al usuario arrojar la basura de su camión al suelo, ensuciándose y perdiendo higiene a cambio.
 */
public class TirarBasuraCommand extends Command {

    public TirarBasuraCommand() {
        super(null, new String[] {"tirarbasura", "basura"});
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

        // Bajar higiene y vaciar
        rpUser.setHygiene(0);
        RoleplayUserManager.saveRoleplayUser(rpUser);

        executor.shout("*Arroja toda la basura al suelo y se ensució de un líquido hediondo [0% Higiene]*");
        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(10, 30); // Efecto hediondo
        }

        return true;
    }
}
