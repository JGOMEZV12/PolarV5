package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Interactions/Items/KevlarCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Agarra el Kevlar que está frente a ti y se equipa con chaleco antibalas.
 */
public class KevlarCommand extends Command {

    public KevlarCommand() {
        super(null, new String[] {"kevlar"});
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
            executor.whisper("Ocurrió un error al recuperar tus datos de rol.");
            return true;
        }

        if (rpUser.isDead()) {
            executor.whisper("¡No puedes usar esto estando muerto!");
            return true;
        }

        if (rpUser.isJailed()) {
            executor.whisper("¡No puedes usar esto en la cárcel!");
            return true;
        }

        if (rpUser.getChalecoPor() > 0) {
            executor.whisper("¡Ya tienes un chaleco antibalas equipado!");
            return true;
        }

        if (rpUser.getJobId() != 1 && !rpUser.isPoliceTrial()) { // Policía
            executor.whisper("¡Sólo un oficial de policía puede utilizar este comando!");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

        if (executor.getHabboInfo().getCredits() < 500) {
            executor.whisper("¡Necesitas 500 créditos para comprar un chaleco! Trabaja más duro.");
            return true;
        }

        rpUser.setChalecoPor(300);
        rpUser.setArmor(300);
        executor.giveCredits(-500);

        RoleplayUserManager.saveRoleplayUser(rpUser);

        executor.shout("*Se equipa con chaleco antibalas y un casco militar [+300 protección]*");

        return true;
    }
}
