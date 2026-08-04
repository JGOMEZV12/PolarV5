package com.eu.habbo.habbohotel.roleplay.commands.rp;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.commands.Command;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUserManager;
import com.eu.habbo.habbohotel.users.Habbo;

/**
 * Portado desde: Polar RP/HabboHotel/Rooms/Chat/Commands/Users/Generic/Offers/AtransferirCommand.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Transfiere dinero del banco (Cuenta Corriente) de roleplay del usuario ejecutor al banco de otro usuario.
 */
public class AtransferirCommand extends Command {

    public AtransferirCommand() {
        super(null, new String[] {"atransferir"});
    }

    @Override
    public boolean handle(GameClient gameClient, String[] params) throws Exception {
        if (gameClient == null || gameClient.getHabbo() == null) {
            return false;
        }

        Habbo executor = gameClient.getHabbo();
        int userId = executor.getHabboInfo().getId();

        if (params.length < 3) {
            executor.whisper("Uso: :atransferir [usuario] [cantidad]");
            return true;
        }

        String targetUsername = params[1];
        Habbo target = Emulator.getGameEnvironment().getHabboManager().getHabbo(targetUsername);

        if (target == null) {
            executor.whisper("El usuario '" + targetUsername + "' no se encuentra conectado.");
            return true;
        }

        if (target == executor) {
            executor.whisper("¡No puedes transferirte dinero a ti mismo!");
            return true;
        }

        RoleplayUser rpUser = RoleplayUserManager.getRoleplayUser(userId);
        if (rpUser == null) {
            executor.whisper("No se cargó tu perfil de Roleplay.");
            return true;
        }

        if (rpUser.getBankAccount() <= 0) {
            executor.whisper("¡No tienes una cuenta bancaria abierta para realizar transferencias!");
            return true;
        }

        int amount;
        try {
            amount = Integer.parseInt(params[2]);
        } catch (NumberFormatException e) {
            executor.whisper("Por favor ingrese una cantidad numérica válida.");
            return true;
        }

        if (amount <= 0) {
            executor.whisper("La cantidad a transferir debe ser mayor que cero.");
            return true;
        }

        if (rpUser.getBankChequings() < amount) {
            executor.whisper("No tienes suficiente saldo corriente. (Saldo: $" + rpUser.getBankChequings()
                    + ", requerido: $" + amount + ")");
            return true;
        }

        int targetId = target.getHabboInfo().getId();
        RoleplayUser targetRp = RoleplayUserManager.getRoleplayUser(targetId);

        if (targetRp == null) {
            executor.whisper("No se pudo cargar el perfil de Roleplay del destinatario.");
            return true;
        }

        if (targetRp.getBankAccount() <= 0) {
            executor.whisper("El destinatario no tiene una cuenta bancaria activa.");
            return true;
        }

        // Realizar la transferencia bancaria en memoria
        rpUser.setBankChequings(rpUser.getBankChequings() - amount);
        targetRp.setBankChequings(targetRp.getBankChequings() + amount);

        // Guardar ambos de forma asíncrona no bloqueante
        RoleplayUserManager.saveRoleplayUserAsync(rpUser);
        RoleplayUserManager.saveRoleplayUserAsync(targetRp);

        executor.shout("*Realiza una transferencia bancaria de $" + amount + " a "
                + target.getHabboInfo().getUsername() + "*");
        executor.whisper("[BANCO] Transferencia exitosa. Se descontaron $" + amount + " de tu cuenta corriente.");
        target.whisper("¡Has recibido una transferencia bancaria de $" + amount + " de "
                + executor.getHabboInfo().getUsername() + " depositados en tu cuenta corriente!");

        return true;
    }
}
