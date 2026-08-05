package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Offers/SellCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite ofrecer o vender un artículo (drogas, armas, servicios, etc.) a otro usuario por un precio.
 */
public class OfrecerCommand extends Command {

    public OfrecerCommand() {
        super(null, new String[] {"ofrecer"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 4) {
            executor.whisper(
                    "Uso correcto: :ofrecer [usuario] [objeto] [precio] ó :ofrecer [usuario] [objeto] [cantidad] [precio]");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (targetHabbo == null) {
            executor.whisper("No se pudo encontrar al usuario '" + targetUsername + "'.");
            return true;
        }

        if (targetHabbo == executor) {
            executor.whisper("¡No puedes ofrecerte cosas a ti mismo!");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (rpUser == null || targetRp == null) {
            executor.whisper("No se pudo cargar el perfil de Roleplay.");
            return true;
        }

        if (rpUser.isDead() || rpUser.isJailed()) {
            executor.whisper("¡No puedes hacer eso en tu estado actual!");
            return true;
        }

        String item = params[2].toLowerCase();
        int quantity = 1;
        int price = 0;

        if (params.length >= 5) {
            try {
                quantity = Integer.parseInt(params[3]);
                price = Integer.parseInt(params[4]);
            } catch (NumberFormatException e) {
                executor.whisper("Por favor ingrese un número válido de cantidad y precio.");
                return true;
            }
        } else {
            try {
                price = Integer.parseInt(params[3]);
            } catch (NumberFormatException e) {
                executor.whisper("Por favor ingrese un precio válido.");
                return true;
            }
        }

        if (price < 0 || quantity <= 0) {
            executor.whisper("Cantidad o precio inválido.");
            return true;
        }

        // Validaciones según el item que se ofrece
        switch (item) {
            case "marihuana":
            case "weed":
                if (rpUser.getWeed() < quantity) {
                    executor.whisper("No tienes suficiente marihuana. Tienes: " + rpUser.getWeed() + "g");
                    return true;
                }
                break;
            case "cocaina":
            case "cocaine":
                if (rpUser.getCocaine() < quantity) {
                    executor.whisper("No tienes suficiente cocaína. Tienes: " + rpUser.getCocaine() + "g");
                    return true;
                }
                break;
            case "heroina":
            case "heroin":
                if (rpUser.getHeroina() < quantity) {
                    executor.whisper("No tienes suficiente heroína. Tienes: " + rpUser.getHeroina() + "cc");
                    return true;
                }
                break;
            case "medicina":
            case "medicamento":
            case "medicamentos":
                if (rpUser.getMedicina() < quantity) {
                    executor.whisper("No tienes suficientes medicamentos. Tienes: " + rpUser.getMedicina());
                    return true;
                }
                break;
            case "cigarette":
            case "cigarrillos":
            case "cigarros":
                if (rpUser.getCigarette() < quantity) {
                    executor.whisper("No tienes suficientes cigarrillos. Tienes: " + rpUser.getCigarette());
                    return true;
                }
                break;
            case "mamada":
                // Servicio de prostíbulo/social
                break;
            case "reparacion":
            case "reparar":
                // Mecánico / armero
                break;
            case "telefono":
                break;
            default:
                executor.whisper("El item '" + item + "' no es una oferta válida.");
                return true;
        }

        if (targetRp.getOfferManager().getActiveOffers().containsKey(item)) {
            executor.whisper("Ese usuario ya tiene una oferta de '" + item + "' pendiente.");
            return true;
        }

        // Crear la oferta en el gestor de ofertas del destinatario
        targetRp.getOfferManager().createOffer(item, userId, price, quantity);

        executor.shout("*Ofrece " + (quantity > 1 ? quantity + "x " : "") + item + " a " + targetUsername + " por $"
                + price + "*");
        targetHabbo.whisper("Te han ofrecido " + (quantity > 1 ? quantity + "x " : "") + item + " por $" + price
                + ". Escribe ':aceptar " + item + "' para comprarlo, o ':rechazar " + item + "' para rechazar.");

        return true;
    }
}
