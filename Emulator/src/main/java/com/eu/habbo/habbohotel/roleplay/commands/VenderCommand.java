package com.eu.habbo.habbohotel.roleplay.commands;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.economy.Product;
import com.eu.habbo.habbohotel.roleplay.economy.ProductOwned;
import com.eu.habbo.habbohotel.roleplay.economy.ProductsManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;
import java.util.ArrayList;
import java.util.List;

/**
 * Portado desde: Polar RP/HabboRoleplay/Products/ProductsManager.cs / rp_user_products
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite al usuario vender un producto de su inventario de roleplay de vuelta a la tienda por el 50% de su precio.
 * Mejoras: Lectura de inventario optimizada desde caché local y procesamiento de base de datos asíncrono.
 */
public class VenderCommand extends Command {

    public VenderCommand() {
        super(null, new String[] {"vender"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo habbo = gameClient.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        if (params.length < 2) {
            habbo.whisper("Uso: :vender [nombre_producto] [cantidad]");
            return true;
        }

        String productName = params[1];
        int quantityVal = 1;

        if (params.length >= 3) {
            try {
                quantityVal = Integer.parseInt(params[2]);
            } catch (NumberFormatException ignored) {
            }
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

        // Buscar productos en la caché de inventario local
        List<ProductOwned> myItems = rpUser.getOwnedProducts();
        List<ProductOwned> matchingOwned = new ArrayList<>();

        for (ProductOwned po : myItems) {
            if (po.getProductId() == product.getId()) {
                matchingOwned.add(po);
            }
        }

        if (matchingOwned.size() < quantity) {
            habbo.whisper("No tienes suficiente cantidad de '" + product.getDisplayName()
                    + "' en tu inventario. (Tienes: " + matchingOwned.size() + "x, requerido: " + quantity + "x)");
            return true;
        }

        // Precio de venta: 50% del precio de compra
        int sellPrice = (product.getPrice() / 2) * quantity;

        // Modificar memoria/caché local inmediatamente
        for (int i = 0; i < quantity; i++) {
            ProductOwned po = matchingOwned.get(i);
            rpUser.getOwnedProducts().remove(po);
        }
        rpUser.setBankChequings(rpUser.getBankChequings() + sellPrice);

        // Procesar remoción y guardado en DB de forma asíncrona
        RpEngine.getThreading().run(() -> {
            for (int i = 0; i < quantity; i++) {
                ProductOwned po = matchingOwned.get(i);
                ProductsManager.removeProductOwned(po.getId());
            }
            RoleplayUserManager.saveRoleplayUser(rpUser);
        });

        habbo.whisper("¡Has vendido " + quantity + "x '" + product.getDisplayName() + "' por un total de $" + sellPrice
                + " depositados en tu banco!");
        return true;
    }
}
