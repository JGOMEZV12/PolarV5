package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayCooldowns;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Banking/TanqueCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra la cantidad de gasolina que posee en el tanque de su vehículo.
 */
public class TanqueCommand extends Command {

    public TanqueCommand() {
        super(null, new String[] {"tanque"});
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
            habbo.whisper("No se cargó tu perfil de Roleplay.");
            return true;
        }

        if (rpUser.getCarFuel() <= 0) {
            habbo.whisper("¡No tienes gasolina en el tanque! Vaya en taxi a la 12 y escriba: 'Gasolina cantidad'.");
            return true;
        }

        if (cooldowns.hasCooldown("tanque")) {
            return true;
        }

        cooldowns.setCooldown("tanque", 5);

        // Efecto del celular/teléfono (65)
        if (habbo.getRoomUnit() != null) {
            habbo.getRoomUnit().setEffectId(65, 0);
            Emulator.getThreading().run(() -> {
                try {
                    Thread.sleep(1500);
                    if (habbo.getRoomUnit() != null) {
                        habbo.getRoomUnit().setEffectId(0, 0);
                    }
                } catch (InterruptedException ignored) {
                }
            });
        }

        habbo.whisper("*Saca su teléfono y abre AppCar y tiene: " + rpUser.getCarFuel() + " de gasolina*");
        return true;
    }
}
