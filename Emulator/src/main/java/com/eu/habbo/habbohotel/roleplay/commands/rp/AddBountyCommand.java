package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.Bounty;
import com.eu.habbo.habbohotel.roleplay.users.BountyManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Bounties/AddBountyCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite establecer una recompensa económica por la cabeza de un usuario.
 */
public class AddBountyCommand extends Command {

    public AddBountyCommand() {
        super(null, new String[] {"recompensa"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 3) {
            executor.whisper("Uso correcto: :recompensa [usuario] [cantidad]");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (targetHabbo == null) {
            executor.whisper(
                    "Se ha producido un error al intentar encontrar a ese usuario, tal vez esté sin conexión.");
            return true;
        }

        if (targetHabbo == executor) {
            executor.whisper("No puedes establecer una recompensa por ti mismo.");
            return true;
        }

        int reward;
        try {
            reward = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            executor.whisper("Por favor ingrese un número válido de recompensa.");
            return true;
        }

        if (reward < 100) {
            executor.whisper("La cantidad mínima de recompensa es $100.");
            return true;
        }

        if (executor.getHabboInfo().getCredits() < reward) {
            executor.whisper("No tienes suficiente dinero en mano para pagar la recompensa de $" + reward);
            return true;
        }

        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());
        if (targetRp == null) {
            executor.whisper("No se pudo cargar el perfil de roleplay del objetivo.");
            return true;
        }

        if (targetRp.isDead() || targetRp.isJailed()) {
            executor.whisper("No puedes establecer una recompensa por alguien que está muerto o encarcelado.");
            return true;
        }

        // Crear la recompensa
        executor.giveCredits(-reward);

        long expiryTime = System.currentTimeMillis() / 1000L + 3600; // Expira en 1 hora
        Bounty bounty = new Bounty(targetHabbo.getHabboInfo().getId(), userId, reward, expiryTime);
        BountyManager.addBounty(bounty);

        executor.shout("*Coloca una recompensa de $" + reward + " por " + targetUsername + "*");

        // Alerta policial/criminal global
        for (Habbo h : Emulator.getGameEnvironment()
                .getHabboManager()
                .getOnlineHabbos()
                .values()) {
            h.whisper("[RECOMPENSA] Se le dará $" + reward + " a la persona que mate a " + targetUsername
                    + ". ¡Encuéntralo!");
        }

        return true;
    }
}
