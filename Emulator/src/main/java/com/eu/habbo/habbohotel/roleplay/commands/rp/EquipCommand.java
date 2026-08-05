package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.roleplay.users.Weapon;
import com.eu.habbo.habbohotel.roleplay.users.WeaponManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Combat/EquipCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite equipar un arma del catálogo de Roleplay, asignándola al estado del usuario y aplicando efectos visuales.
 */
public class EquipCommand extends Command {

    public EquipCommand() {
        super(null, new String[] {"equipar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 2) {
            executor.whisper("¡Vaya, olvidó ingresar un nombre de arma!");
            return true;
        }

        String weaponName = params[1].toLowerCase();
        Weapon baseWeapon = WeaponManager.getWeapon(weaponName);

        if (baseWeapon == null) {
            executor.whisper("¡Esta arma no existe en el catálogo de Roleplay!");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        if (rpUser == null) {
            executor.whisper("No se pudo cargar tu perfil de Roleplay.");
            return true;
        }

        if (rpUser.isDead() || rpUser.isJailed()) {
            executor.whisper("No puedes equipar armas en tu estado actual.");
            return true;
        }

        if (rpUser.isPassiveMode()) {
            executor.whisper("No puedes equipar armas en modo pasivo.");
            return true;
        }

        if (rpUser.isCuffed()) {
            executor.whisper("No puedes sacar un " + baseWeapon.getPublicName() + " con las manos esposadas.");
            return true;
        }

        if (rpUser.getEquippedWeapon() != null
                && rpUser.getEquippedWeapon().getName().equals(baseWeapon.getName())) {
            executor.whisper("Ya tienes esta arma equipada.");
            return true;
        }

        // Equipar el arma en memoria
        rpUser.setEquippedWeapon(baseWeapon);
        rpUser.setBullets(baseWeapon.getClipSize());

        RoleplayUserManager.saveRoleplayUser(rpUser);

        executor.whisper("Has sacado tu " + baseWeapon.getPublicName() + ". Cargador lleno con "
                + baseWeapon.getClipSize() + " balas.");
        executor.shout("*Saca su " + baseWeapon.getPublicName() + "*");

        // Aplicar efectos visuales en el RoomUnit del Habbo
        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(baseWeapon.getEffectId(), 0);
            executor.getRoomUnit().setHandItem(baseWeapon.getHandItem());
        }

        return true;
    }
}
