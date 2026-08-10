package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayOffer;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Offers/DeclineCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Rechaza una oferta de Roleplay específica pendiente.
 */
public class DeclineCommand extends Command {

    public DeclineCommand() {
        super(null, new String[] {"rechazar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 2) {
            executor.whisper("Escribe ':rechazar [tipo_oferta]'. Usa :ofertas para ver qué ofertas tienes.");
            return true;
        }

        String offerType = params[1].toLowerCase();
        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);

        if (rpUser == null) {
            executor.whisper("No se pudo cargar tu perfil de Roleplay.");
            return true;
        }

        if (rpUser.getOfferManager().getActiveOffers().isEmpty()) {
            executor.whisper("¡No tienes ofertas que rechazar!");
            return true;
        }

        if (!rpUser.getOfferManager().getActiveOffers().containsKey(offerType)) {
            executor.whisper("No tienes ninguna oferta de tipo '" + offerType + "' para rechazar.");
            return true;
        }

        RoleplayOffer offer = rpUser.getOfferManager().getActiveOffers().remove(offerType);
        if (offer != null) {
            Habbo offerer = RpEngine.getGameEnvironment().getHabboManager().getHabbo(offer.getOffererId());
            String offererName = (offerer != null) ? offerer.getHabboInfo().getUsername() : "Alguien";

            executor.shout("*Rechaza la oferta de " + offerType + " de " + offererName + "*");
            if (offerer != null) {
                offerer.whisper(
                        "¡" + executor.getHabboInfo().getUsername() + " ha rechazado tu oferta de " + offerType + "!");
            }
        }

        return true;
    }
}
