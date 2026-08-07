package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Mecanico/ReviewMecCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Siendo Mecánico, revisa el vehículo que tengas en frente. O bien, siendo armero, revisar el arma de alguien a reparar.
 */
public class ReviewMecCommand extends Command {

    public ReviewMecCommand() {
        super(null, new String[] {"revisarmec", "revisarmecanico"});
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

        if (rpUser.getJobId() != 4 && rpUser.getJobId() != 5) { // 4 = Mecánico, 5 = Armero
            executor.whisper("Tu trabajo actual no te permite hacer uso de ese comando. ¡Solo Mecánicos o Armeros!");
            return true;
        }

        if (!rpUser.isWorking()) {
            executor.whisper("Debes estar trabajando para hacer eso.");
            return true;
        }

        if (rpUser.isDead()) {
            executor.whisper("¡No puedes hacer esto mientras estás muert@!");
            return true;
        }

        if (rpUser.isJailed()) {
            executor.whisper("¡No puedes hacer eso mientras estás encarcelad@!");
            return true;
        }

        if (rpUser.isCuffed()) {
            executor.whisper("No puedes hacer eso mientras estás esposad@");
            return true;
        }

        if (rpUser.isDrivingCar()) {
            executor.whisper("¡No puedes hacer eso mientras conduces!");
            return true;
        }

        if (rpUser.getJobId() == 4) { // Mecánico
            executor.shout("*Abre el capó del Vehículo y procede a revisarlo*");
            executor.whisper("Este vehículo requiere 6 repuestos para ser reparado.");
            return true;
        } else { // Armero
            if (params.length < 2) {
                executor.whisper("Comando inválido, escribe :revisarmec [usuario]");
                return true;
            }

            Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);
            if (targetHabbo == null) {
                executor.whisper("No se ha podido encontrar al usuario.");
                return true;
            }
            GameClient targetClient = targetHabbo.getClient();

            RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(
                    targetHabbo.getHabboInfo().getId());

            if (targetRp == null) {
                return true;
            }

            if (targetRp.getEquippedWeapon() == null) {
                executor.whisper("Esa persona no lleva ningún arma Equipada a ser revisada.");
                return true;
            }

            int needPieces = 4;
            rpUser.setArmPiecesTo(needPieces);
            rpUser.setArmUserTo(targetHabbo.getHabboInfo().getId());

            executor.shout(
                    "*Observa el arma de " + targetHabbo.getHabboInfo().getUsername() + " y procede a examinarla*");
            executor.whisper("Esta arma necesita " + needPieces + " pieza(s) para ser reparada.");

            return true;
        }
    }
}
