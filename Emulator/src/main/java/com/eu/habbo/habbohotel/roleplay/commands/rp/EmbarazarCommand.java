package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import java.security.SecureRandom;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Interactions/Marriage/EmbarazarCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite entablar relaciones íntimas con la pareja casada para embarazarla.
 */
public class EmbarazarCommand extends Command {

    private final SecureRandom random = new SecureRandom();

    public EmbarazarCommand() {
        super(null, new String[] {"embarazar"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 2) {
            executor.whisper("¡Vaya, se le olvidó ingresar un nombre de usuario!");
            return true;
        }

        String targetUsername = params[1];
        Habbo targetHabbo = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (targetHabbo == null) {
            executor.whisper(
                    "Se ha producido un error al intentar encontrar a ese usuario, tal vez esté sin conexión.");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        RoleplayUser targetRp =
                RoleplayUserManager.getRoleplayUser(targetHabbo.getHabboInfo().getId());

        if (rpUser == null || targetRp == null) {
            executor.whisper("No se pudo cargar el perfil de Roleplay.");
            return true;
        }

        if (rpUser.getCurEnergy() <= 0) {
            executor.whisper("¡No tienes suficiente energía para tener sexo!");
            return true;
        }

        if (rpUser.getMarriedTo() != targetHabbo.getHabboInfo().getId()) {
            executor.whisper("¡Solo puedes tener un bebé con tu pareja casada!");
            return true;
        }

        if (targetRp.getEmbarazo() > 0) {
            executor.whisper("Lo sentimos, pero tu pareja ya está esperando un bebé.");
            return true;
        }

        if (targetRp.getHijo() > 0) {
            executor.whisper("Lo sentimos, pero este usuario ya tiene hijo.");
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

        if (distanceX > 1 || distanceY > 1) {
            executor.whisper("¡Debes acercarte a este ciudadano para tener relaciones sexuales!");
            return true;
        }

        // Ejecutar embarazo
        executor.shout("*Agarra a " + targetUsername + " por el pecho y la aborda, quitándose la ropa*");
        targetHabbo.shout(
                "*Gime y tiembla porque " + executor.getHabboInfo().getUsername() + " empuja su pene dentro de ella*");

        rpUser.setCurEnergy(Math.max(0, rpUser.getCurEnergy() - 5));
        rpUser.setAnimo(100);
        targetRp.setEmbarazo(1);
        targetRp.setAnimo(100);

        RoleplayUserManager.saveRoleplayUser(rpUser);
        RoleplayUserManager.saveRoleplayUser(targetRp);

        executor.whisper("Gracias al sexo que tuviste tu felicidad está al máximo.");
        targetHabbo.whisper(
                "¡Felicidades estás embarazada! Ahora ve al hospital y escribe :hijo x (Al usuario que quieras solicitar que sea tu hij@)");

        // Aplicar efecto íntimo/abrazo (efecto 507 en Habbo)
        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(507, 15);
        }
        if (targetHabbo.getRoomUnit() != null) {
            targetHabbo.getRoomUnit().setEffectId(507, 15);
        }

        return true;
    }
}
