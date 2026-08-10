package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.RpEngine;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.roleplay.users.Weapon;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import java.security.SecureRandom;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Combat/ShootCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite disparar un arma equipada contra un objetivo, calculando daño, distancia, balas y aplicando estados de muerte o aturdimiento.
 */
public class ShootCommand extends Command {

    private final SecureRandom random = new SecureRandom();

    public ShootCommand() {
        super(null, new String[] {"disparar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 2) {
            executor.whisper("Uso correcto: :disparar [usuario]");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = RpEngine.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (targetHabbo == null) {
            executor.whisper(
                    "Se ha producido un error al intentar encontrar a ese usuario, tal vez esté sin conexión.");
            return true;
        }

        int targetId = targetHabbo.getHabboInfo().getId();
        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetId);

        if (rpUser == null || targetRp == null) {
            executor.whisper("No se pudo cargar el perfil de Roleplay.");
            return true;
        }

        if (rpUser.isDead() || rpUser.isJailed()) {
            executor.whisper("No puedes disparar en tu estado actual.");
            return true;
        }

        if (rpUser.isPassiveMode()) {
            executor.whisper("No puedes disparar en modo pasivo.");
            return true;
        }

        if (targetRp.isPassiveMode()) {
            executor.whisper("No puedes disparar a una persona en modo pasivo.");
            return true;
        }

        if (targetRp.isDead()) {
            executor.whisper("¡No puedes dispararle a una persona muerta!");
            return true;
        }

        if (targetRp.isJailed()) {
            executor.whisper("¡No puedes dispararle a una persona encarcelada!");
            return true;
        }

        Weapon weapon = rpUser.getEquippedWeapon();
        if (weapon == null) {
            executor.whisper("¡No tienes ningún arma Equipada! Usa :equipar [nombre-arma]");
            return true;
        }

        if (rpUser.getBullets() <= 0) {
            executor.whisper("¡Te has quedado sin balas! Usa :recargar para rellenar tu cargador.");
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
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);

        if (distance > weapon.getRange()) {
            executor.shout("*Intentó disparar a " + targetUsername + ", pero falla (fuera de rango)*");
            rpUser.setBullets(rpUser.getBullets() - 1);
            RoleplayUserManager.saveRoleplayUser(rpUser);
            return true;
        }

        // Caso especial: arma eléctrica (Taser)
        if (weapon.getName().equals("electrica")) {
            if (rpUser.getJobId() != 2 && rpUser.getJobId() != 3) { // Ej: Policía / Guardia
                executor.whisper("¡No eres un policía para utilizar esta arma!");
                return true;
            }

            // Aplicar aturdimiento
            int stunChance = random.nextInt(100) + 1;
            if (stunChance <= 8) {
                executor.shout("*Dispara su pistola eléctrica hacia " + targetUsername + ", pero falla*");
            } else {
                executor.shout(
                        "*Dispara su pistola eléctrica hacia " + targetUsername + " inmovilizándolo inmediatamente*");
                targetHabbo.getRoomUnit().setEffectId(53, 15); // Aturdido por 15 segundos
                targetHabbo.whisper("¡Has sido inmovilizado por la pistola eléctrica de "
                        + executor.getHabboInfo().getUsername() + "!");
            }

            rpUser.setBullets(rpUser.getBullets() - 1);
            RoleplayUserManager.saveRoleplayUser(rpUser);
            return true;
        }

        // Lógica de daño estándar
        int damage = weapon.getMinDamage() + random.nextInt((weapon.getMaxDamage() - weapon.getMinDamage()) + 1);

        // Deducción de vida
        int currentHealth = targetRp.getCurHealth();
        if (currentHealth - damage <= 0) {
            targetRp.setCurHealth(0);
            targetRp.setDead(true);
            targetRp.setDeadTimeLeft(300); // Muerte por 5 minutos

            // Incrementar Kills y Deaths
            rpUser.setKills(rpUser.getKills() + 1);
            rpUser.setGunKills(rpUser.getGunKills() + 1);
            targetRp.setDeaths(targetRp.getDeaths() + 1);

            // Recompensa de Exp
            rpUser.setLevelExp(rpUser.getLevelExp() + 50);

            executor.shout("*Dispara su " + weapon.getPublicName() + " contra " + targetUsername + " causándole "
                    + damage + " de daño, dejándolo muerto inmediatamente!*");
            targetHabbo.whisper(
                    "¡Has sido asesinado por " + executor.getHabboInfo().getUsername() + "!");
        } else {
            targetRp.setCurHealth(currentHealth - damage);

            executor.shout("*Dispara su " + weapon.getPublicName() + " contra " + targetUsername + " causándole "
                    + damage + " de daño*");
            targetHabbo.whisper("¡Recibiste " + damage + " de daño de "
                    + executor.getHabboInfo().getUsername() + "!");
        }

        rpUser.setBullets(rpUser.getBullets() - 1);

        RoleplayUserManager.saveRoleplayUser(rpUser);
        RoleplayUserManager.saveRoleplayUser(targetRp);

        return true;
    }
}
