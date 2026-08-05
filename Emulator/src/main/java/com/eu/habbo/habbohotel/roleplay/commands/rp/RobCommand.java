package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayCooldowns;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import java.security.SecureRandom;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Criminal Activity/RobCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite robar dinero en mano (créditos), drogas (weed, cocaine, heroina), cigarros o medicinas de otros usuarios a corta distancia.
 */
public class RobCommand extends Command {

    private final SecureRandom random = new SecureRandom();

    public RobCommand() {
        super(null, new String[] {"robar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo habbo = gameClient.getHabbo();
        int userId = habbo.getHabboInfo().getId();

        if (params.length < 2) {
            habbo.whisper("Vaya, se le olvidó ingresar un nombre de usuario!");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (targetHabbo == null) {
            habbo.whisper("El usuario '" + targetUsername + "' no se encuentra conectado.");
            return true;
        }

        int targetId = targetHabbo.getHabboInfo().getId();
        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetId);
        RoleplayCooldowns cooldowns = RoleplayUserManager.getRoleplayCooldowns(userId);

        if (rpUser == null || targetRp == null || cooldowns == null) {
            habbo.whisper("No se pudo cargar el perfil de Roleplay.");
            return true;
        }

        if (rpUser.isDead()) {
            habbo.whisper("¡No puedes robar a alguien mientras estás muerto!");
            return true;
        }

        if (rpUser.isPassiveMode()) {
            habbo.whisper("No puedes realizar acciones ilegales en modo pasivo.");
            return true;
        }

        if (rpUser.isJailed()) {
            habbo.whisper("¡No puedes robar a alguien mientras estás en la cárcel!");
            return true;
        }

        if (rpUser.getJobId() > 1) { // Si tiene algún trabajo activo
            habbo.whisper("No puedes robar, estás trabajando...");
            return true;
        }

        if (targetRp.isDead()) {
            habbo.whisper("¡No puedes robar a alguien que está muerto!");
            return true;
        }

        if (targetRp.isPassiveMode()) {
            habbo.whisper("¡No puedes robar a alguien que está en modo pasivo!");
            return true;
        }

        if (targetRp.isJailed()) {
            habbo.whisper("¡No puedes robar a alguien que está en la cárcel!");
            return true;
        }

        if (targetRp.isNoob()) {
            habbo.whisper("*Este usuario se encuentra bajo inmunidad*");
            return true;
        }

        if (targetHabbo == habbo) {
            habbo.whisper("No se puede robar a sí mismo.");
            return true;
        }

        int levelDiff = Math.abs(rpUser.getLevel() - targetRp.getLevel());
        if (levelDiff > 8) {
            habbo.whisper("¡No puedes robar a este usuario ya que tu diferencia de nivel es mayor de 8!");
            return true;
        }

        if (cooldowns.hasCooldown("rob")) {
            habbo.whisper("Por favor espera un poco para hacer eso nuevamente.");
            return true;
        }

        RoomTile clientTile = habbo.getRoomUnit().getCurrentLocation();
        RoomTile targetTile = targetHabbo.getRoomUnit().getCurrentLocation();

        if (clientTile == null || targetTile == null) {
            habbo.whisper("Ubicación de sala no válida.");
            return true;
        }

        int distanceX = Math.abs(clientTile.x - targetTile.x);
        int distanceY = Math.abs(clientTile.y - targetTile.y);

        if (distanceX > 1 || distanceY > 1) {
            habbo.whisper("Usted necesita acercarse a " + targetUsername + " con el fin de robarlo.");
            return true;
        }

        boolean success = false;
        StringBuilder robbedItems = new StringBuilder();

        // 1. Dinero en mano (Credits)
        int targetCredits = targetHabbo.getHabboInfo().getCredits();
        if (targetCredits > 30) {
            int amountToRob = Math.min(targetCredits, 500);
            int minAmount = amountToRob / 40;
            int maxAmount = amountToRob / 5;
            if (minAmount < 1) minAmount = 1;
            if (maxAmount < minAmount) maxAmount = minAmount;

            int amount = minAmount + random.nextInt((maxAmount - minAmount) + 1);

            habbo.giveCredits(amount);
            targetHabbo.giveCredits(-amount);

            success = true;
            robbedItems.append("$").append(amount).append(", ");
        }

        // 2. Drogas y consumibles (15% probabilidad)
        int drugsChance = random.nextInt(100) + 1;
        if (drugsChance <= 15) {
            if (targetRp.getWeed() > 30) {
                int amountToRob = Math.min(targetRp.getWeed(), 100);
                int minAmount = Math.max(1, amountToRob / 40);
                int maxAmount = Math.max(minAmount, amountToRob / 5);
                int amount = minAmount + random.nextInt((maxAmount - minAmount) + 1);

                rpUser.setWeed(rpUser.getWeed() + amount);
                targetRp.setWeed(targetRp.getWeed() - amount);

                success = true;
                robbedItems
                        .append("una pequeña bolsa que contiene ")
                        .append(amount)
                        .append("g de marihuana, ");
            }

            if (targetRp.getHeroina() > 30) {
                int amountToRob = Math.min(targetRp.getHeroina(), 100);
                int minAmount = Math.max(1, amountToRob / 40);
                int maxAmount = Math.max(minAmount, amountToRob / 5);
                int amount = minAmount + random.nextInt((maxAmount - minAmount) + 1);

                rpUser.setHeroina(rpUser.getHeroina() + amount);
                targetRp.setHeroina(targetRp.getHeroina() - amount);

                success = true;
                robbedItems
                        .append("una pequeña caja que contiene ")
                        .append(amount)
                        .append("cc de heroína, ");
            }

            if (targetRp.getCocaine() > 30) {
                int amountToRob = Math.min(targetRp.getCocaine(), 100);
                int minAmount = Math.max(1, amountToRob / 40);
                int maxAmount = Math.max(minAmount, amountToRob / 5);
                int amount = minAmount + random.nextInt((maxAmount - minAmount) + 1);

                rpUser.setCocaine(rpUser.getCocaine() + amount);
                targetRp.setCocaine(targetRp.getCocaine() - amount);

                success = true;
                robbedItems
                        .append("una pequeña bolsa que contiene ")
                        .append(amount)
                        .append("g de cocaína, ");
            }

            if (targetRp.getCigarette() > 30) {
                int amountToRob = Math.min(targetRp.getCigarette(), 100);
                int minAmount = Math.max(1, amountToRob / 40);
                int maxAmount = Math.max(minAmount, amountToRob / 5);
                int amount = minAmount + random.nextInt((maxAmount - minAmount) + 1);

                rpUser.setCigarette(rpUser.getCigarette() + amount);
                targetRp.setCigarette(targetRp.getCigarette() - amount);

                success = true;
                robbedItems
                        .append("una pequeña bolsa que contiene ")
                        .append(amount)
                        .append("g de cigarrillos, ");
            }

            if (targetRp.getMedicina() > 30) {
                int amountToRob = Math.min(targetRp.getMedicina(), 5);
                int minAmount = Math.max(1, amountToRob / 40);
                int maxAmount = Math.max(minAmount, amountToRob / 5);
                int amount = minAmount + random.nextInt((maxAmount - minAmount) + 1);

                rpUser.setMedicina(rpUser.getMedicina() + amount);
                targetRp.setMedicina(targetRp.getMedicina() - amount);

                success = true;
                robbedItems
                        .append("una pequeña bolsa que contiene ")
                        .append(amount)
                        .append("g de Medicinas, ");
            }
        }

        if (!success) {
            habbo.whisper("Lo siento, pero esta persona es demasiado pobre para robar.");
            return true;
        }

        // Penalizaciones y Wanted
        rpUser.setWanted(true);
        rpUser.setWantedLevel(2);
        rpUser.setWantedTimeLeft(7);
        rpUser.setLevelExp(Math.max(0, rpUser.getLevelExp() - 20));
        rpUser.setCurHealth(Math.max(1, rpUser.getCurHealth() - 20));

        RoleplayUserManager.saveRoleplayUser(rpUser);
        RoleplayUserManager.saveRoleplayUser(targetRp);

        cooldowns.setCooldown("rob", 300); // 300 segundos = 5 minutos de cooldown
        RoleplayUserManager.saveRoleplayCooldowns(cooldowns);

        String itemsText = robbedItems.toString();
        if (itemsText.endsWith(", ")) {
            itemsText = itemsText.substring(0, itemsText.length() - 2);
        }

        habbo.shout("*Agarra a " + targetUsername + " por el cuello y lo apunta para robarle " + itemsText
                + " pero pierde [-20 Exp] y por un golpe [-20 Salud]*");
        targetHabbo.whisper(
                "Te robaron " + itemsText + " fue: " + habbo.getHabboInfo().getUsername() + "!");

        // Aplicar efecto de wanted/arrestado o alerta policial
        habbo.getRoomUnit().setEffectId(59, 100);

        return true;
    }
}
