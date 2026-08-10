package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Combat/HechizosCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite al usuario consultar y consumir hechizos mágicos para aumentar su salud o escudo de combate.
 */
public class HechizosCommand extends Command {

    public HechizosCommand() {
        super(null, new String[] {"hechizo"});
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

        if (params.length < 2 || params[1].equalsIgnoreCase("lista") || params[1].equalsIgnoreCase("list")) {
            StringBuilder message = new StringBuilder("--- TUS HECHIZOS DISPONIBLES ---\n\n");
            message.append("- sanacion: Cura por completo tu vida.\n");
            message.append("- escudo: Restaura tu escudo de combate.\n\n");
            message.append("Usa ':hechizo [nombre]' para consumir un hechizo.");
            executor.whisper(message.toString());
            return true;
        }

        if (rpUser.isDead() || rpUser.isJailed()) {
            executor.whisper("No puedes usar hechizos en tu estado actual.");
            return true;
        }

        String spell = params[1].toLowerCase();

        if (spell.equals("sanacion") || spell.equals("sanar")) {
            rpUser.setCurHealth(rpUser.getMaxHealth());
            executor.shout("*Pronuncia unas palabras mágicas y se cura por completo*");
            executor.whisper("¡Te has curado por completo!");
        } else if (spell.equals("escudo")) {
            rpUser.setAnimo(100); // Rellena felicidad / escudo mental
            executor.shout("*Genera un escudo de energía a su alrededor*");
            executor.whisper("¡Tu escudo de combate ha sido restaurado!");
        } else {
            executor.whisper("Ese hechizo no se encuentra en tu libro de magia.");
            return true;
        }

        RoleplayUserManager.saveRoleplayUser(rpUser);
        return true;
    }
}
