package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayOffer;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Offers/AcceptCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Acepta una oferta pendiente de un tipo específico (ej. fuga, weed, cocaine, etc.), cobrando e intercambiando los recursos correspondientes de forma persistente.
 */
public class AcceptCommand extends Command {

    public AcceptCommand() {
        super(null, new String[] {"aceptar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo acceptor = gameClient.getHabbo();
        int acceptorId = acceptor.getHabboInfo().getId();

        if (params.length < 2) {
            acceptor.whisper("Uso correcto: :aceptar [tipo_oferta] (ej: :aceptar weed, :aceptar fuga)");
            return true;
        }

        String offerType = params[1].toLowerCase();
        RoleplayUser acceptorRp = RoleplayUserManager.getRoleplayUser(acceptorId);

        if (acceptorRp == null) {
            acceptor.whisper("No se pudo cargar tu perfil de Roleplay.");
            return true;
        }

        if (acceptorRp.getOfferManager().getActiveOffers().isEmpty()) {
            acceptor.whisper("¡No tienes ofertas activas que aceptar!");
            return true;
        }

        if (!acceptorRp.getOfferManager().getActiveOffers().containsKey(offerType)) {
            acceptor.whisper("No tienes ninguna oferta activa de tipo '" + offerType + "'.");
            return true;
        }

        RoleplayOffer offer = acceptorRp.getOfferManager().getActiveOffers().get(offerType);
        if (offer == null) {
            acceptor.whisper("Oferta inválida o expirada.");
            return true;
        }

        Habbo offerer = Emulator.getGameEnvironment().getHabboManager().getHabbo(offer.getOffererId());
        if (offerer == null) {
            acceptor.whisper("El usuario ofertante no se encuentra conectado.");
            acceptorRp.getOfferManager().getActiveOffers().remove(offerType);
            return true;
        }

        RoleplayUser offererRp =
                RoleplayUserManager.getRoleplayUser(offerer.getHabboInfo().getId());
        if (offererRp == null) {
            acceptor.whisper("No se pudo cargar el perfil de Roleplay del ofertante.");
            return true;
        }

        // Validaciones generales del aceptante
        if (acceptorRp.isDead() || acceptorRp.isJailed() && !offerType.equals("fuga")) {
            acceptor.whisper("No puedes aceptar ofertas en tu estado actual.");
            return true;
        }

        int price = offer.getCost();
        int quantity = 1;
        if (offer.getParams() != null && offer.getParams().length > 0) {
            quantity = (Integer) offer.getParams()[0];
        }

        // Validar saldo del aceptante
        int acceptorCredits = acceptor.getHabboInfo().getCredits();
        if (acceptorCredits < price) {
            acceptor.whisper("No tienes dinero suficiente ($" + price + ") en mano para aceptar esta oferta.");
            return true;
        }

        // Lógica según tipo de oferta
        boolean completed = false;
        switch (offerType) {
            case "fuga":
                // Descontar fianza/precio y liberar prisionero
                acceptor.giveCredits(-price);
                offerer.giveCredits(price);

                acceptorRp.setJailed(false);
                acceptorRp.setJailedTimeLeft(0);

                acceptor.shout("*Acepta la propuesta de fuga de "
                        + offerer.getHabboInfo().getUsername() + " por $" + price + "*");
                acceptor.whisper("¡Te has fugado con éxito de la cárcel!");
                offerer.whisper("¡" + acceptor.getHabboInfo().getUsername()
                        + " aceptó tu oferta y se fugó de la cárcel! Has recibido $" + price + ".");

                completed = true;
                break;

            case "marihuana":
            case "weed":
                if (offererRp.getWeed() < quantity) {
                    acceptor.whisper("El ofertante ya no tiene suficiente marihuana para venderte.");
                    acceptorRp.getOfferManager().getActiveOffers().remove(offerType);
                    return true;
                }

                acceptor.giveCredits(-price);
                offerer.giveCredits(price);

                offererRp.setWeed(offererRp.getWeed() - quantity);
                acceptorRp.setWeed(acceptorRp.getWeed() + quantity);

                acceptor.shout("*Acepta la oferta de " + quantity + "g de marihuana de "
                        + offerer.getHabboInfo().getUsername() + " por $" + price + "*");
                offerer.whisper("¡" + acceptor.getHabboInfo().getUsername() + " ha comprado " + quantity
                        + "g de marihuana por $" + price + "!");

                completed = true;
                break;

            case "cocaina":
            case "cocaine":
                if (offererRp.getCocaine() < quantity) {
                    acceptor.whisper("El ofertante ya no tiene suficiente cocaína para venderte.");
                    acceptorRp.getOfferManager().getActiveOffers().remove(offerType);
                    return true;
                }

                acceptor.giveCredits(-price);
                offerer.giveCredits(price);

                offererRp.setCocaine(offererRp.getCocaine() - quantity);
                acceptorRp.setCocaine(acceptorRp.getCocaine() + quantity);

                acceptor.shout("*Acepta la oferta de " + quantity + "g de cocaína de "
                        + offerer.getHabboInfo().getUsername() + " por $" + price + "*");
                offerer.whisper("¡" + acceptor.getHabboInfo().getUsername() + " ha comprado " + quantity
                        + "g de cocaína por $" + price + "!");

                completed = true;
                break;

            case "heroina":
            case "heroin":
                if (offererRp.getHeroina() < quantity) {
                    acceptor.whisper("El ofertante ya no tiene suficiente heroína para venderte.");
                    acceptorRp.getOfferManager().getActiveOffers().remove(offerType);
                    return true;
                }

                acceptor.giveCredits(-price);
                offerer.giveCredits(price);

                offererRp.setHeroina(offererRp.getHeroina() - quantity);
                acceptorRp.setHeroina(acceptorRp.getHeroina() + quantity);

                acceptor.shout("*Acepta la oferta de " + quantity + "cc de heroína de "
                        + offerer.getHabboInfo().getUsername() + " por $" + price + "*");
                offerer.whisper("¡" + acceptor.getHabboInfo().getUsername() + " ha comprado " + quantity
                        + "cc de heroína por $" + price + "!");

                completed = true;
                break;

            case "medicina":
            case "medicamento":
            case "medicamentos":
                if (offererRp.getMedicina() < quantity) {
                    acceptor.whisper("El ofertante ya no tiene suficientes medicamentos para venderte.");
                    acceptorRp.getOfferManager().getActiveOffers().remove(offerType);
                    return true;
                }

                acceptor.giveCredits(-price);
                offerer.giveCredits(price);

                offererRp.setMedicina(offererRp.getMedicina() - quantity);
                acceptorRp.setMedicina(acceptorRp.getMedicina() + quantity);

                acceptor.shout("*Acepta la oferta de " + quantity + "x medicamentos de "
                        + offerer.getHabboInfo().getUsername() + " por $" + price + "*");
                offerer.whisper("¡" + acceptor.getHabboInfo().getUsername() + " ha comprado " + quantity
                        + "x medicamentos por $" + price + "!");

                completed = true;
                break;

            case "cigarette":
            case "cigarrillos":
            case "cigarros":
                if (offererRp.getCigarette() < quantity) {
                    acceptor.whisper("El ofertante ya no tiene suficientes cigarrillos para venderte.");
                    acceptorRp.getOfferManager().getActiveOffers().remove(offerType);
                    return true;
                }

                acceptor.giveCredits(-price);
                offerer.giveCredits(price);

                offererRp.setCigarette(offererRp.getCigarette() - quantity);
                acceptorRp.setCigarette(acceptorRp.getCigarette() + quantity);

                acceptor.shout("*Acepta la oferta de " + quantity + "x cigarrillos de "
                        + offerer.getHabboInfo().getUsername() + " por $" + price + "*");
                offerer.whisper("¡" + acceptor.getHabboInfo().getUsername() + " ha comprado " + quantity
                        + "x cigarrillos por $" + price + "!");

                completed = true;
                break;

            case "mamada":
                acceptor.giveCredits(-price);
                offerer.giveCredits(price);

                // Incrementa la felicidad del cliente
                acceptorRp.setAnimo(Math.min(100, acceptorRp.getAnimo() + 50));

                acceptor.shout("*Acepta la oferta de una mamada por parte de "
                        + offerer.getHabboInfo().getUsername() + " por $" + price + "*");
                offerer.whisper("¡" + acceptor.getHabboInfo().getUsername()
                        + " ha aceptado tu servicio y te ha pagado $" + price + "!");

                completed = true;
                break;

            case "mama":
                // Vincular como madre e hijo en memoria y DB
                acceptorRp.setHijo(offerer.getHabboInfo().getId());
                offererRp.setHijo(acceptorId);

                // Resetear estado de embarazo en la madre
                offererRp.setEmbarazo(0);

                acceptor.shout("*¡Acepta que " + offerer.getHabboInfo().getUsername() + " sea su mamá!*");
                offerer.whisper("¡" + acceptor.getHabboInfo().getUsername() + " ahora es tu hij@!");

                completed = true;
                break;

            default:
                acceptor.whisper("El tipo de oferta '" + offerType + "' no es válido o requiere un servicio especial.");
                break;
        }

        if (completed) {
            // Eliminar la oferta procesada
            acceptorRp.getOfferManager().getActiveOffers().remove(offerType);

            // Guardar cambios en DB de forma asíncrona
            RoleplayUserManager.saveRoleplayUserAsync(acceptorRp);
            RoleplayUserManager.saveRoleplayUserAsync(offererRp);
        }

        return true;
    }
}
