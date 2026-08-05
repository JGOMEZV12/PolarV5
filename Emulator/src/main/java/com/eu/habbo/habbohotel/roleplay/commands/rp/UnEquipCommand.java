package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.roleplay.users.Weapon;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Combat/UnEquipCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite desequipar cualquier arma que se tenga equipada, removiendo efectos visuales y reiniciando el estado en memoria.
 */
public class UnEquipCommand extends Command {

    public UnEquipCommand() {
        super(null, new String[] {"desequipar"});
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
            executor.whisper("No se pudo cargar tu perfil de Roleplay.");
            return true;
        }

        Weapon weapon = rpUser.getEquippedWeapon();
        if (weapon == null) {
            executor.whisper("¡No tienes un arma equipada!");
            return true;
        }

        // Desequipar arma
        rpUser.setEquippedWeapon(null);
        rpUser.setBullets(0);

        RoleplayUserManager.saveRoleplayUser(rpUser);

        executor.whisper("Has guardado tu " + weapon.getPublicName() + ".");
        executor.shout("*Guarda su " + weapon.getPublicName() + " de vuelta en su funda*");

        // Remover efectos visuales
        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(0, 0);
            executor.getRoomUnit().setHandItem(0);
        }

        return true;
    }
}
