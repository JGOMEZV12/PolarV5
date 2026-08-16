package com.eu.habbo.habbohotel.commands.roleplay;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.UserDataHandler;

public class BuyTicketCommand extends Command {
    public BuyTicketCommand() {
        super("rp", new String[] {"comprarticket"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        RoleplayUser rpUser = UserDataHandler.getRoleplayUser(gameClient.getHabbo().getHabboInfo().getId());
        if (rpUser == null) {
            return false;
        }

        // Command execution logic for BuyTicketCommand
        gameClient.getHabbo().whisper("Comando BuyTicketCommand ejecutado con éxito.");
        return true;
    }
}
