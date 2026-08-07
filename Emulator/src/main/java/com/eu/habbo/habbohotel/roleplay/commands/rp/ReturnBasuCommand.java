package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Jobs/Types/Basurero/ReturnBasuCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Siendo el conductor del camión de basura, descarga la basura para recibir tu paga.
 */
public class ReturnBasuCommand extends Command {

    public ReturnBasuCommand() {
        super(null, new String[] {"returnbasu", "entregarbasura"});
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

        if (rpUser.getJobId() != 6) { // Basurero / Recolector (jobId = 6)
            executor.whisper("Debes tener el trabajo de Basurero para usar ese comando.");
            return true;
        }

        int basureroRoomId = Emulator.getConfig().getInt("roleplay.basurero.room.id", 6);
        int currentRoomId = executor.getHabboInfo().getCurrentRoom() != null
                ? executor.getHabboInfo().getCurrentRoom().getId()
                : 0;

        if (currentRoomId != basureroRoomId) {
            executor.whisper("¡Debes ir al Basurero de la Ciudad para descargar el camión!");
            return true;
        }

        if (rpUser.isDrivingInCar()) {
            executor.whisper("¡Debes estar conduciendo el camión de basura!");
            return true;
        }

        if (!rpUser.isBasuChofer()) {
            executor.whisper("¡Solo el chofer del Camión puede hacer eso!");
            return true;
        }

        if (rpUser.getBasuTrashCount() < 15) {
            executor.whisper("¡Deben recolectar 15 contedendores de basura para poder descargar el camión!");
            return true;
        }

        int payAmount = rpUser.getBasuLvl() * 25;

        rpUser.setBasuChofer(false);
        rpUser.setBasuTrashCount(0);
        rpUser.setDrivingCar(false);

        rpUser.setBasuXp(rpUser.getBasuXp() + 10);
        if (rpUser.getBasuXp() >= 100) {
            rpUser.setBasuLvl(rpUser.getBasuLvl() + 1);
            rpUser.setBasuXp(0);
            executor.whisper("¡Has subido de nivel de Basurero! Ahora eres Nivel " + rpUser.getBasuLvl());
        }

        executor.giveCredits(payAmount);

        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(0, 0);
        }

        executor.shout("*Descarga el camión de basura completando su trabajo*");
        executor.whisper("¡Buen trabajo! Tus ganancias son: $" + payAmount);

        RoleplayUserManager.saveRoleplayUser(rpUser);

        return true;
    }
}
