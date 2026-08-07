package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Camionero/LoadsCamCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Siendo Camionero, permite ver la Lista de Cargas para transportar.
 */
public class LoadsCamCommand extends Command {

    public LoadsCamCommand() {
        super(null, new String[] {"cargas", "cargascamion"});
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

        if (rpUser.getJobId() != 2) { // Camionero / Transportista
            executor.whisper("Debes tener el trabajo de Camionero para usar ese comando.");
            return true;
        }

        int amn = 13;
        int med = 2;
        int crack = 1;
        int piezas = 2;

        if (rpUser.getCamLvl() == 2) {
            amn = 16;
            med = 4;
            crack = 2;
            piezas = 5;
        } else if (rpUser.getCamLvl() == 3) {
            amn = 20;
            med = 6;
            crack = 3;
            piezas = 7;
        } else if (rpUser.getCamLvl() == 4) {
            amn = 22;
            med = 8;
            crack = 4;
            piezas = 7;
        } else if (rpUser.getCamLvl() == 5) {
            amn = 25;
            med = 10;
            crack = 5;
            piezas = 7;
        } else if (rpUser.getCamLvl() >= 6) {
            amn = 30;
            med = 12;
            crack = 6;
            piezas = 7;
        }

        StringBuilder cargas = new StringBuilder();
        cargas.append("==========================\n Cargas de Camionero Nivel ")
                .append(rpUser.getCamLvl())
                .append("\n==========================\n");
        cargas.append("[1] [L] Productos de 24/7 (Ganancias $").append(amn).append(")\n");
        cargas.append("[2] [L] Ropa (Ganancias $").append(amn).append(")\n");
        cargas.append("[3] [I] Drogas (Ganancias $")
                .append(amn)
                .append(" + ")
                .append(med)
                .append(" Medicamentos + ")
                .append(crack)
                .append(" g. de Crack)\n");
        cargas.append("[4] [I] Armas (Ganancias $")
                .append(amn)
                .append(" + ")
                .append(piezas)
                .append(" piezas de armas)\n\n\n");
        cargas.append("[I] = Carga Ilegal\n");
        cargas.append("[L] = Carga Legal\n");

        executor.alert(cargas.toString());

        return true;
    }
}
