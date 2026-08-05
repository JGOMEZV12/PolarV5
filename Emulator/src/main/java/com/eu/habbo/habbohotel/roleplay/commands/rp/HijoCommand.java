package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Interactions/Marriage/HijoCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite a una madre embarazada dar a luz en el hospital y proponer la adopción como hijo a otro usuario.
 */
public class HijoCommand extends Command {

    public HijoCommand() {
        super(null, new String[] {"hijo"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 2) {
            executor.whisper("¡Vaya, se le olvidó ingresar un nombre de usuario!");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (targetHabbo == null) {
            executor.whisper("¡Uy, no pudo encontrar ese usuario!");
            return true;
        }

        if (targetHabbo == executor) {
            executor.whisper("¡No puedes ser hijo de ti mismo!");
            return true;
        }

        // Verificar que estamos en la sala de hospital (ej: sala #2)
        if (executor.getHabboInfo().getCurrentRoom() == null
                || executor.getHabboInfo().getCurrentRoom().getId() != 2) {
            executor.whisper("¡Para dar a luz debes ir al hospital (sala #2)!");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (rpUser == null || targetRp == null) {
            executor.whisper("No se pudo cargar el perfil de Roleplay.");
            return true;
        }

        if (rpUser.getHijo() > 0) {
            executor.whisper("¡Ya tienes un hijo! Debes abandonarlo primero antes de tener uno nuevo ¡MALA MADRE!");
            return true;
        }

        if (rpUser.getEmbarazo() < 1) {
            executor.whisper("¡Para dar a luz necesitas estar embarazada!");
            return true;
        }

        if (targetRp.getHijo() > 0) {
            executor.whisper("Lo sentimos, pero este usuario ya tiene mamá.");
            return true;
        }

        if (executor.getHabboInfo().getCredits() < 10000) {
            executor.whisper("Dar a luz tiene un gasto de $10.000 créditos en mano.");
            return true;
        }

        if (targetRp.getOfferManager().getActiveOffers().containsKey("mama")) {
            executor.whisper("¡Este usuario ya tiene una propuesta de adopción pendiente!");
            return true;
        }

        // Iniciar parto y crear oferta
        executor.giveCredits(-10000);
        targetRp.getOfferManager().createOffer("mama", userId, 0);

        executor.shout(
                "*Comienza los dolores y contracciones para que salga el bebé: " + targetUsername + " ¡¡aaaaaaahh!!*");
        targetHabbo.whisper(executor.getHabboInfo().getUsername()
                + " acaba de darte a luz. ¡Escribe ':aceptar mama' para tener una mamá!");

        return true;
    }
}
