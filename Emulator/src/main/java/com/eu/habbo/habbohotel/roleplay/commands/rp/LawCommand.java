package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Police/LawCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Añade un usuario a la lista deseada para un nivel deseado (1 a 5).
 */
public class LawCommand extends Command {

    public LawCommand() {
        super(null, new String[] {"law", "ley"});
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
            executor.whisper("Sólo un oficial de policía puede utilizar este comando.");
            return true;
        }

        if (!rpUser.isWorking() && !rpUser.isPoliceTrial()) {
            executor.whisper("¡Debes estar trabajando para usar este comando!");
            return true;
        }

        if (params.length != 3) {
            executor.whisper(
                    "Ingrese un nombre de ciudadano y el nivel deseado que desea asignarle. :buscar usuario nivel");
            return true;
        }

        Habbo targetHabbo = RpEngine.getGameServer().getGameClientManager().getHabbo(params[1]);
        if (targetHabbo == null) {
            executor.whisper(
                    "Se ha producido un error al intentar encontrar a ese usuario, tal vez estén sin conexión.");
            return true;
        }
        GameClient targetClient = targetHabbo.getClient();

        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (targetRp == null) {
            return true;
        }

        if (targetRp.isPassiveMode()) {
            executor.whisper("¡No puedes asignarle cargos a una persona que está en modo pasivo!");
            return true;
        }

        if (targetRp.isJailed()) {
            executor.whisper("¡No puedes contratar a alguien que ya está en la cárcel!");
            return true;
        }

        int wantedLevel;
        try {
            wantedLevel = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            executor.whisper("Por favor ingrese un nivel deseado entre 1 y 5!");
            return true;
        }

        if (wantedLevel > 5 || wantedLevel <= 0) {
            executor.whisper("Por favor ingrese un nivel deseado entre 1 y 5!");
            return true;
        }

        targetRp.setWanted(true);
        targetRp.setWantedLevel(wantedLevel);
        targetRp.setWantedTimeLeft(wantedLevel * 2);

        RoleplayUserManager.saveRoleplayUser(targetRp);

        executor.whisper("¡Se ha agregado a " + targetHabbo.getHabboInfo().getUsername()
                + " a la lista de buscados con un nivel de " + wantedLevel + " estrella(s)!");

        for (GameClient client :
                RpEngine.getGameServer().getGameClientManager().getSessions().values()) {
            if (client.getHabbo() != null) {
                client.getHabbo()
                        .whisper("[NOTIFICACIÓN IMPORTANTE] La policia está buscando a: "
                                + targetHabbo.getHabboInfo().getUsername() + ", ¡Ayudanos a encontrarlo!");
            }
        }

        return true;
    }
}
