package com.eu.habbo.habbohotel.roleplay.commands;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.roleplay.economy.Product;
import com.eu.habbo.habbohotel.roleplay.economy.ProductOwned;
import com.eu.habbo.habbohotel.roleplay.economy.ProductsManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Portado desde: Polar RP/HabboRoleplay/ProductsOwned/OFF_ProductsOwnedManager.cs / rp_user_products
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Muestra al usuario la lista de productos de roleplay que posee en su inventario.
 * Mejoras: Lectura de inventario optimizada desde caché local.
 */
public class InventarioCommand extends Command {

    public InventarioCommand() {
        super(null, new String[]{"inv", "inventario"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo habbo = gameClient.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        if (rpUser == null) {
            habbo.whisper("No se pudo cargar tus datos de Roleplay.");
            return true;
        }

        List<ProductOwned> myItems = rpUser.getOwnedProducts();

        if (myItems == null || myItems.isEmpty()) {
            habbo.whisper("Tu inventario de Roleplay está vacío.");
            return true;
        }

        // Agrupar productos para mostrarlos de forma limpia (ej. "3x Comida")
        Map<Integer, Integer> productCounts = new HashMap<>();
        for (ProductOwned po : myItems) {
            productCounts.put(po.getProductId(), productCounts.getOrDefault(po.getProductId(), 0) + 1);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== INVENTARIO ROLEPLAY ===\r\n");
        for (Map.Entry<Integer, Integer> entry : productCounts.entrySet()) {
            Product p = ProductsManager.getProduct(entry.getKey());
            if (p != null) {
                sb.append("• ").append(entry.getValue()).append("x ").append(p.getDisplayName())
                        .append(" [").append(p.getProductName()).append("] (Tipo: ").append(p.getType()).append(")\r\n");
            }
        }

        habbo.whisper(sb.toString());
        return true;
    }
}
