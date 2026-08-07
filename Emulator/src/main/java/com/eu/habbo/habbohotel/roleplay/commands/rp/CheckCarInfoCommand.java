package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Driving/CheckCarInfoCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite ver la información de un vehículo.
 */
public class CheckCarInfoCommand extends Command {

    public CheckCarInfoCommand() {
        super(null, new String[] {"infocar", "checkcar"});
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

        if (rpUser.getJobId() != 1 && !rpUser.isPoliceTrial()) { // Policía
            executor.whisper("¡Solo un oficial de policía puede hacer eso!");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("Debes estar trabajando para hacer eso.");
            return true;
        }

        if (rpUser.isDead()) {
            executor.whisper("¡No puedes hacer eso mientras estás muert@!");
            return true;
        }

        if (rpUser.isJailed()) {
            executor.whisper("¡No puedes hacer eso mientras estás encarcelad@!");
            return true;
        }

        if (rpUser.isCuffed()) {
            executor.whisper("¡No hacer eso mientras estás esposad@!");
            return true;
        }

        StringBuilder stats = new StringBuilder();
        stats.append("INFORMACIÓN DEL VEHÍCULO\n\n");
        stats.append("Modelo: Polaris Cruiser\n");
        stats.append("Dueño: ").append(executor.getHabboInfo().getUsername()).append("\n");
        stats.append("Última persona en manejarlo: ")
                .append(executor.getHabboInfo().getUsername())
                .append("\n\n");
        stats.append("ESTADÍSTICAS DEL VEHÍCULO\n\n");
        stats.append("Vida: 100/100\n");
        stats.append("Combustible: 50/100\n");
        stats.append("KM: 12.5\n");
        stats.append("Estado: Óptimo & Abierto\n");
        stats.append("Traba: No\n");
        stats.append("Alarma: Sí\n");
        stats.append("Max. Pasajeros: 4\n");

        executor.alert(stats.toString());

        return true;
    }
}
