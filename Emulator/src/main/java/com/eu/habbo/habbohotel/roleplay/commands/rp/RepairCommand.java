package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Offers/SellCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite a un mecánico o armero reparar un vehículo o arma de un ciudadano cobrando un precio.
 */
public class RepairCommand extends Command {

    public RepairCommand() {
        super(null, new String[] {"reparar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        if (rpUser == null) {
            return false;
        }

        if (rpUser.getJobId() != 4 && rpUser.getJobId() != 5) { // 4 = Mecánico, 5 = Armero
            executor.whisper("Tu trabajo actual no te permite hacer uso de ese comando. ¡Solo Mecánicos o Armeros!");
            return true;
        }

        if (!rpUser.isWorking()) {
            executor.whisper("Debes estar trabajando para hacer eso.");
            return true;
        }

        if (params.length != 3) {
            executor.whisper("Comando inválido, escribe :reparar [cliente] [precio]");
            return true;
        }

        Habbo targetHabbo = Emulator.getGameServer().getGameClientManager().getHabbo(params[1]);
        if (targetHabbo == null) {
            executor.whisper("No se ha podido encontrar al usuario.");
            return true;
        }
        GameClient targetClient = targetHabbo.getClient();

        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (targetRp == null) {
            return true;
        }

        int price;
        try {
            price = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            executor.whisper("El precio debe ser un número válido.");
            return true;
        }

        if (price <= 0) {
            executor.whisper("El precio debe ser mayor a 0.");
            return true;
        }

        if (targetHabbo.getHabboInfo().getCredits() < price) {
            executor.whisper("El cliente no tiene suficiente dinero para pagar por la reparación.");
            return true;
        }

        RoomTile clientTile = executor.getRoomUnit().getCurrentLocation();
        RoomTile targetTile = targetHabbo.getRoomUnit().getCurrentLocation();

        if (clientTile == null || targetTile == null) {
            executor.whisper("Ubicación de sala no válida.");
            return true;
        }

        int distanceX = Math.abs(clientTile.x - targetTile.x);
        int distanceY = Math.abs(clientTile.y - targetTile.y);

        if (distanceX > 2 || distanceY > 2) {
            executor.whisper("Debes acercarte al cliente para ofrecer la reparación.");
            return true;
        }

        if (rpUser.getJobId() == 4) { // Mecánico
            executor.shout("*Saca sus herramientas y comienza a reparar el vehículo de "
                    + targetHabbo.getHabboInfo().getUsername() + "*");
            executor.whisper("Has reparado el vehículo del cliente por un precio de $" + price);
            targetHabbo.whisper("¡El mecánico " + executor.getHabboInfo().getUsername()
                    + " ha reparado tu vehículo por un precio de $" + price + "!");
        } else { // Armero
            if (targetRp.getEquippedWeapon() == null) {
                executor.whisper("Esa persona no lleva ningún arma Equipada a ser reparada.");
                return true;
            }

            executor.shout("*Usa su destornillador para reparar el arma de "
                    + targetHabbo.getHabboInfo().getUsername() + "*");
            executor.whisper("Has reparado el arma del cliente por un precio de $" + price);
            targetHabbo.whisper("¡El armero " + executor.getHabboInfo().getUsername()
                    + " ha reparado tu arma por un precio de $" + price + "!");
        }

        executor.giveCredits(price);
        targetHabbo.giveCredits(-price);

        RoleplayUserManager.saveRoleplayUser(rpUser);
        RoleplayUserManager.saveRoleplayUser(targetRp);

        return true;
    }
}
