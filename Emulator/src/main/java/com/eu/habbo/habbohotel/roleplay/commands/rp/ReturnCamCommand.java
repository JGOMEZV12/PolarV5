package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Camionero/ReturnCamCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Siendo Camionero, entrega tu Camión una vez entregada la mercancía para recibir tu paga.
 */
public class ReturnCamCommand extends Command {

    public ReturnCamCommand() {
        super(null, new String[] {"entregarcamion", "returncam"});
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

        if (!rpUser.isDrivingCar()) {
            executor.whisper("Debes conducir un Camión para hacer eso.");
            return true;
        }

        if (rpUser.getCamOwnId() > 0 && rpUser.getCamOwnId() != userId) {
            executor.whisper("Este camión pertenece a por otra persona. ((Si perdiste el tuyo usa :abandonarcarga))");
            return true;
        }

        if (rpUser.getCamState() == 0) {
            executor.whisper(
                    "El camión no ha sido cargado aún. ¡Ve a cargarlo de mercancía! ((Usa :cargarcamion [ID]))");
            return true;
        }

        if (rpUser.getCamState() != 2) {
            executor.whisper("El camión no ha sido descargado aún. ¡Ve a entregar la mercancía!");
            return true;
        }

        int pay = 13;
        int pieces = 2;

        if (rpUser.getCamLvl() == 2) {
            pay = 16;
            pieces = 5;
        } else if (rpUser.getCamLvl() == 3) {
            pay = 20;
            pieces = 7;
        } else if (rpUser.getCamLvl() == 4) {
            pay = 22;
            pieces = 7;
        } else if (rpUser.getCamLvl() == 5) {
            pay = 25;
            pieces = 7;
        } else if (rpUser.getCamLvl() >= 6) {
            pay = 30;
            pieces = 7;
        }

        String win = "¡Excelente entra! Tus ganancias son: $" + pay;
        if (rpUser.getCamCargId() == 4) { // Armas
            rpUser.setArmXp(rpUser.getArmXp() + pieces);
            win += " y " + pieces + " Piezas de armas.";
        }

        executor.giveCredits(pay);

        rpUser.setCamCargId(0);
        rpUser.setCamDest(0);
        rpUser.setCamOwnId(0);
        rpUser.setCamState(0);
        rpUser.setDrivingCar(false);

        rpUser.setCamXp(rpUser.getCamXp() + 10);
        if (rpUser.getCamXp() >= 100) {
            rpUser.setCamLvl(rpUser.getCamLvl() + 1);
            rpUser.setCamXp(0);
            executor.whisper("¡Has subido de nivel de Camionero! Ahora eres Nivel " + rpUser.getCamLvl());
        }

        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(0, 0);
        }

        executor.shout("*Entrega su camión completando su recorrido*");
        executor.whisper(win);

        RoleplayUserManager.saveRoleplayUser(rpUser);

        return true;
    }
}
