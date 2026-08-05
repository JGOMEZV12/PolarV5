package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.Bounty;
import com.eu.habbo.habbohotel.roleplay.users.BountyManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Bounties/BountyListCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra una lista con todas las recompensas de cabezas activas en el roleplay.
 */
public class BountyListCommand extends Command {

    public BountyListCommand() {
        super(null, new String[] {"rlista"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();

        StringBuilder message = new StringBuilder("--- Lista de Recompensas Activas ---\n\n");

        if (BountyManager.getBountyUsers().isEmpty()) {
            message.append("No hay recompensas activas ahora.\n");
        } else {
            long now = System.currentTimeMillis() / 1000L;
            for (Bounty bounty : BountyManager.getBountyUsers().values()) {
                if (now > bounty.getExpiryTimeStamp()) {
                    BountyManager.removeBounty(bounty.getUserId());
                    continue;
                }

                Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(bounty.getUserId());
                if (target != null) {
                    long timeLeftMinutes = (bounty.getExpiryTimeStamp() - now) / 60L;
                    message.append("Fugitivo: ")
                            .append(target.getHabboInfo().getUsername())
                            .append(" - Recompensa: $")
                            .append(bounty.getReward())
                            .append(" - Expira en: ")
                            .append(timeLeftMinutes)
                            .append(" minutos\n");
                }
            }
        }

        executor.whisper(message.toString());
        return true;
    }
}
