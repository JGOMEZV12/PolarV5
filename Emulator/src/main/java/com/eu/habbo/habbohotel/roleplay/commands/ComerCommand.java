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
import java.util.List;

/**
 * Portado desde: Polar RP/HabboRoleplay/Food/Food.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite consumir un alimento del inventario para recuperar energía y reducir el hambre.
 * Mejoras: Lectura de inventario optimizada desde caché local y procesamiento de base de datos asíncrono.
 */
public class ComerCommand extends Command {

    public ComerCommand() {
        super(null, new String[] {"comer"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo habbo = gameClient.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        if (params.length < 2) {
            habbo.whisper("Uso: :comer [nombre_producto]");
            return true;
        }

        String productName = params[1];
        Product product = ProductsManager.getProduct(productName);

        if (product == null) {
            habbo.whisper("El producto '" + productName + "' no existe en el catálogo.");
            return true;
        }

        // Validar si el producto es comestible/comida
        if (!product.getType().equalsIgnoreCase("food") && !product.getType().equalsIgnoreCase("comida")) {
            habbo.whisper("El producto '" + product.getDisplayName() + "' no es un alimento comestible.");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        if (rpUser == null) {
            habbo.whisper("No se pudo cargar tus datos de Roleplay.");
            return true;
        }

        // Buscar producto en la caché de inventario local
        List<ProductOwned> myItems = rpUser.getOwnedProducts();
        ProductOwned targetItem = null;

        for (ProductOwned po : myItems) {
            if (po.getProductId() == product.getId()) {
                targetItem = po;
                break;
            }
        }

        if (targetItem == null) {
            habbo.whisper("No tienes '" + product.getDisplayName() + "' en tu inventario de roleplay.");
            return true;
        }

        final ProductOwned itemToConsume = targetItem;

        // Modificar memoria/caché local inmediatamente
        rpUser.getOwnedProducts().remove(itemToConsume);
        rpUser.setCurEnergy(Math.min(rpUser.getMaxEnergy(), rpUser.getCurEnergy() + 30));
        rpUser.setHunger(Math.max(0, rpUser.getHunger() - 25));

        // Procesar remoción y guardado en DB de forma asíncrona
        RpEngine.getThreading().run(() -> {
            ProductsManager.removeProductOwned(itemToConsume.getId());
            RoleplayUserManager.saveRoleplayUser(rpUser);
        });

        habbo.whisper("¡Has consumido '" + product.getDisplayName()
                + "'! Tu hambre se ha reducido y recuperas +30 de Energía.");
        return true;
    }
}
