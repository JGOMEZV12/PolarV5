package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.guilds.Guild;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.gangs.GangManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayOffer;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Gangs/GangInviteCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Invita a otro ciudadano a unirse a tu banda (pandilla) mediante una oferta pendiente.
 */
public class GangInviteCommand extends Command {

    public GangInviteCommand() {
        super("command_gang_invite", new String[] {"ginvitar", "ganginvite"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(
                gameClient.getHabbo().getHabboInfo().getId());
        if (rpUser == null || rpUser.getGangId() <= 0) {
            gameClient.getHabbo().whisper("¡No tienes una pandilla para invitar a alguien!");
            return true;
        }

        if (!GangManager.hasGangCommand(gameClient, "ginvite")) {
            gameClient.getHabbo().whisper("¡No tienes permiso para invitar miembros a la pandilla!");
            return true;
        }

        if (rpUser.isDead() || rpUser.isJailed()) {
            gameClient.getHabbo().whisper("No puedes hacer invitaciones en tu estado actual.");
            return true;
        }

        if (params.length < 2) {
            gameClient.getHabbo().whisper("Ingrese el nombre de usuario de la persona que desea invitar.");
            return true;
        }

        Habbo targetHabbo = RpEngine.getGameEnvironment().getHabboManager().getHabbo(params[1]);
        if (targetHabbo == null) {
            gameClient.getHabbo().whisper("No se pudo encontrar a ese usuario, tal vez esté desconectado.");
            return true;
        }

        if (targetHabbo == gameClient.getHabbo()) {
            gameClient.getHabbo().whisper("¡No puedes invitarte a ti mismo!");
            return true;
        }

        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());
        if (targetRp == null) {
            gameClient.getHabbo().whisper("No se pudo cargar el perfil de roleplay del objetivo.");
            return true;
        }

        // Si ya está en una pandilla
        if (targetRp.getGangId() > 0) {
            gameClient.getHabbo().whisper("¡Este usuario ya pertenece a una pandilla!");
            return true;
        }

        // Comprobar oferta activa de pandilla
        if (targetRp.getOfferManager().getActiveOffers().containsKey("pandilla")) {
            RoleplayOffer existing =
                    targetRp.getOfferManager().getActiveOffers().get("pandilla");
            Guild existingGuild =
                    RpEngine.getGameEnvironment().getGuildManager().getGuild(existing.getCost());
            if (existingGuild != null) {
                gameClient
                        .getHabbo()
                        .whisper(
                                "Este usuario ya tiene una invitación pendiente de '" + existingGuild.getName() + "'.");
            } else {
                targetRp.getOfferManager().getActiveOffers().remove("pandilla");
            }
            return true;
        }

        Guild guild = RpEngine.getGameEnvironment().getGuildManager().getGuild(rpUser.getGangId());
        if (guild == null) {
            gameClient.getHabbo().whisper("Error al cargar la pandilla.");
            return true;
        }

        gameClient
                .getHabbo()
                .shout("*Invita a " + targetHabbo.getHabboInfo().getUsername() + " a unirse a la pandilla: '"
                        + guild.getName() + "'*");
        targetHabbo.whisper("Para unirte a la pandilla '" + guild.getName()
                + "' escribe ':aceptar pandilla'. Debes aportar $2,000 para gastos de ingreso.");

        // Creamos la oferta en el gestor de ofertas del objetivo
        // El costo de la oferta será 2000, y pasamos el id de la pandilla en los parámetros adicionales
        RoleplayOffer offer = new RoleplayOffer(
                "pandilla", gameClient.getHabbo().getHabboInfo().getId(), 2000, new Object[] {rpUser.getGangId()});
        targetRp.getOfferManager().getActiveOffers().put("pandilla", offer);

        return true;
    }
}
