package com.eu.habbo.habbohotel.roleplay.commands;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayCooldowns;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Medic/CureCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite curarse a sí mismo o a otro usuario si es médico o paga una tarifa de curación.
 */
public class CurarCommand extends Command {

    public CurarCommand() {
        super(null, new String[] {"curar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();
        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayCooldowns cooldowns = RoleplayUserManager.getRoleplayCooldowns(userId);

        if (rpUser == null || cooldowns == null) {
            executor.whisper("No se cargó tu perfil de Roleplay.");
            return true;
        }

        // Caso 1: Curar a otro usuario
        if (params.length > 1) {
            String targetUsername = params[1];
            Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

            if (targetHabbo == null) {
                executor.whisper("El usuario '" + targetUsername + "' no se encuentra conectado.");
                return true;
            }

            // Validar que el ejecutor sea un Médico (job_id = 2) o tenga nivel suficiente
            boolean isMedic = rpUser.getJobId() == 2;
            if (!isMedic) {
                executor.whisper("No eres Médico. Solo los médicos pueden curar a otros usuarios.");
                return true;
            }

            int targetId = targetHabbo.getHabboInfo().getId();
            RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetId);

            if (targetRp == null) {
                executor.whisper("No se pudo cargar el perfil de Roleplay de " + targetUsername);
                return true;
            }

            if (targetRp.getCurHealth() >= targetRp.getMaxHealth()) {
                executor.whisper(targetUsername + " ya goza de excelente salud.");
                return true;
            }

            targetRp.setCurHealth(targetRp.getMaxHealth());
            RoleplayUserManager.saveRoleplayUser(targetRp);

            executor.whisper("¡Has curado con éxito a " + targetUsername + "!");
            targetHabbo.whisper("¡El médico " + executor.getHabboInfo().getUsername() + " te ha curado por completo!");
            return true;
        }

        // Caso 2: Curarse a sí mismo
        if (cooldowns.getMedipacks() > 0) {
            executor.whisper("Estás en cooldown de curación. Espera " + cooldowns.getMedipacks() + " segundos.");
            return true;
        }

        if (rpUser.getCurHealth() >= rpUser.getMaxHealth()) {
            executor.whisper("Ya gozas de excelente salud.");
            return true;
        }

        // Costo de curación por defecto: $15 (los médicos se curan gratis)
        int cureCost = (rpUser.getJobId() == 2) ? 0 : 15;

        if (cureCost > 0 && rpUser.getBankChequings() < cureCost) {
            executor.whisper(
                    "No tienes suficiente dinero en el banco para curarte de emergencia. Requiere $" + cureCost);
            return true;
        }

        if (cureCost > 0) {
            rpUser.setBankChequings(rpUser.getBankChequings() - cureCost);
        }

        rpUser.setCurHealth(rpUser.getMaxHealth());
        cooldowns.setMedipacks(30); // 30 segundos de cooldown

        RoleplayUserManager.saveRoleplayUser(rpUser);
        RoleplayUserManager.saveRoleplayCooldowns(cooldowns);

        if (cureCost > 0) {
            executor.whisper("¡Te has curado por completo! Se han cobrado $" + cureCost + " de tu cuenta bancaria.");
        } else {
            executor.whisper("¡Te has curado por completo gratis gracias a tu profesión de Médico!");
        }

        return true;
    }
}
