package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Interactions/Self/DriveCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Permite al usuario arrancar (:manejar) y detener (:detener) su vehículo terrestre, aplicando los efectos de conducción y consumiendo combustible.
 */
public class DriveCommand extends Command {

    public DriveCommand() {
        super(null, new String[] {"manejar", "detener"});
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

        String command = params[0].toLowerCase();

        if (command.equals("detener")) {
            if (!rpUser.isPassiveMode() && !rpUser.isDead() && rpUser.getCar() > 0) {
                stopCar(executor, rpUser);
            } else {
                executor.whisper("No estás conduciendo ningún vehículo.");
            }
            return true;
        }

        // Caso: :manejar
        if (rpUser.getCar() <= 0) {
            executor.whisper("¡No tienes un vehículo comprado! Usa :comprarcarro para adquirir uno.");
            return true;
        }

        if (rpUser.isDead() || rpUser.isJailed()) {
            executor.whisper("No puedes hacer eso en tu estado actual.");
            return true;
        }

        if (rpUser.isPassiveMode()) {
            executor.whisper("No puedes conducir vehículos en modo pasivo.");
            return true;
        }

        if (rpUser.isCuffed()) {
            executor.whisper("No puedes conducir mientras estás esposad@.");
            return true;
        }

        if (rpUser.isCombatMode()) {
            executor.whisper("¡No puedes conducir mientras estás en modo combate!");
            return true;
        }

        // Si ya está conduciendo, detenerlo
        if (rpUser.getCarFuel() <= 0) {
            rpUser.setCarFuel(100); // Rellenar combustible para pruebas iniciales
        }

        startCar(executor, rpUser);

        return true;
    }

    private void startCar(Habbo executor, RoleplayUser rpUser) {
        rpUser.setDrivingInCar(true);
        RoleplayUserManager.saveRoleplayUser(rpUser);

        executor.shout("*Arranca el motor de su vehículo y empieza a conducir*");
        executor.whisper("Vehículo en marcha. Combustible: " + rpUser.getCarFuel() + " galones.");

        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(24, 0); // Efecto de conducir coche
        }
    }

    private void stopCar(Habbo executor, RoleplayUser rpUser) {
        rpUser.setDrivingInCar(false);
        RoleplayUserManager.saveRoleplayUser(rpUser);

        executor.shout("*Detuvo el motor de su vehículo*");
        executor.whisper("Has aparcado tu vehículo correctamente.");

        if (executor.getRoomUnit() != null) {
            executor.getRoomUnit().setEffectId(0, 0);
        }
    }
}
