package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Roleplay/FugaCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Ofrece a un usuario encarcelado la oportunidad de fugarse de la cárcel.
 */
public class FugaCommand extends Command {

    public FugaCommand() {
        super(null, new String[] {"fuga"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 2) {
            executor.whisper("Uso correcto: :fuga usuario");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (targetHabbo == null) {
            executor.whisper(
                    "Se ha producido un error al intentar encontrar a ese usuario, tal vez esté sin conexión.");
            return true;
        }

        int targetId = targetHabbo.getHabboInfo().getId();
        if (targetId == userId) {
            executor.whisper("¡No puedes ofrecerte una fuga a ti mismo!");
            return true;
        }

        RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetId);
        if (targetRp == null) {
            executor.whisper("No se pudo cargar el perfil de Roleplay de " + targetUsername + ".");
            return true;
        }

        if (!targetRp.isJailed()) {
            executor.whisper("¡Ese usuario no está encarcelado! Solo puedes ofrecer fugas a personas en la cárcel.");
            return true;
        }

        if (targetRp.isPassiveMode()) {
            executor.whisper("¡No puedes ofrecerle una fuga a una persona en modo pasivo!");
            return true;
        }

        if (targetRp.getOfferManager().getActiveOffers().containsKey("fuga")) {
            executor.whisper("¡Esta persona ya tiene una oferta de fuga pendiente!");
            return true;
        }

        // Ejecutar oferta
        executor.shout("*Se acerca sigilosamente y le susurra una propuesta de fuga a " + targetUsername + "*");
        targetRp.getOfferManager().createOffer("fuga", userId, 3000);

        targetHabbo.whisper(
                executor.getHabboInfo().getUsername()
                        + " te ofrece ayudarte a fugarte de la cárcel. Escribe ':aceptar fuga' para aceptar o ':rechazar fuga' para rechazar.");
        executor.whisper("Le has ofrecido una fuga a " + targetUsername + ". Esperando su respuesta...");

        return true;
    }
}
