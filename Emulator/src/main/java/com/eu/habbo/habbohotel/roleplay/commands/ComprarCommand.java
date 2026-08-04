package com.eu.habbo.habbohotel.roleplay.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.roleplay.economy.Product;
import com.eu.habbo.habbohotel.roleplay.economy.ProductOwned;
import com.eu.habbo.habbohotel.roleplay.economy.ProductsManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;

/**
 * Portado desde: Polar RP/HabboRoleplay/Products/ProductsManager.cs / rp_user_products
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite al usuario comprar un producto del catálogo de roleplay usando sus fondos del banco.
 * Mejoras: Ejecución de Base de Datos asíncrona no bloqueante y sincronización con el cache local del usuario.
 */
public class ComprarCommand extends Command {

    public ComprarCommand() {
        super(null, new String[]{"comprar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo habbo = gameClient.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        if (params.length < 2) {
            habbo.whisper("Uso: :comprar [nombre_producto] [cantidad]");
            return true;
        }

        String productName = params[1];
        int quantityVal = 1;

        if (params.length >= 3) {
            try {
                quantityVal = Integer.parseInt(params[2]);
            } catch (NumberFormatException ignored) {}
        }

        final int quantity = (quantityVal <= 0) ? 1 : quantityVal;
        Product product = ProductsManager.getProduct(productName);

        if (product == null) {
            habbo.whisper("El producto '" + productName + "' no existe en el catálogo de Roleplay.");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        if (rpUser == null) {
            habbo.whisper("No se pudo cargar tus datos de Roleplay.");
            return true;
        }

        int totalPrice = product.getPrice() * quantity;

        if (rpUser.getBankChequings() < totalPrice) {
            habbo.whisper("No tienes suficiente dinero en tu cuenta bancaria. Costo total: $" + totalPrice + " (Tienes: $" + rpUser.getBankChequings() + ")");
            return true;
        }

        // Deducción en memoria inmediata para feedback instantáneo y prevención de doble gasto (race conditions)
        rpUser.setBankChequings(rpUser.getBankChequings() - totalPrice);

        // Procesamiento en segundo plano asíncrono para no bloquear la ejecución del hilo principal del juego
        Emulator.getThreading().run(() -> {
            for (int i = 0; i < quantity; i++) {
                ProductOwned po = ProductsManager.createProductOwned(userId, product.getId(), "");
                if (po != null) {
                    rpUser.getOwnedProducts().add(po);
                }
            }
            RoleplayUserManager.saveRoleplayUser(rpUser);
        });

        habbo.whisper("¡Has comprado " + quantity + "x '" + product.getDisplayName() + "' por un total de $" + totalPrice + " cobrados del banco!");
        return true;
    }
}
