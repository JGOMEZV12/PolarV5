package com.eu.habbo.habbohotel.roleplay.timers;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.misc.RoleplayManager;
import com.eu.habbo.habbohotel.roleplay.users.RoleplayUser;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TurfCaptureTimer extends RoleplayTimer {
    private static final Logger LOGGER = LoggerFactory.getLogger(TurfCaptureTimer.class);

    private int timeCount = 0;

    public TurfCaptureTimer(String type, GameClient client, int time, boolean forever, Object[] params) {
        super(type, client, time, false, params); // Force forever = false to let base class manage decrement
        setTimeLeft(300); // 300 seconds
    }

    @Override
    public void execute() {
        GameClient client = getClient();
        if (client == null || client.getHabbo() == null) {
            endTimer();
            return;
        }

        RoleplayUser rp = client.getHabbo().getRoleplay();
        if (rp == null || rp.isDead() || rp.isJailed() || rp.isParalized()) {
            cancelTurfCapture();
            return;
        }

        timeCount += (getTime() / 1000);

        if (getTimeLeft() > 0) {
            if (timeCount >= 60) {
                RoleplayManager.shout(
                        client,
                        "*Se acerca a capturar el barrio de pandillas [" + (getTimeLeft() / 60)
                                + " Minutos restantes]*");
                timeCount = 0;
            }
            return;
        }

        completeTurfCapture();
    }

    private void cancelTurfCapture() {
        GameClient client = getClient();
        if (client != null) {
            client.getHabbo().whisper("¡Se ha cancelado la captura del territorio!");
        }
        endTimer();
    }

    private void completeTurfCapture() {
        GameClient client = getClient();
        if (client == null || client.getHabbo() == null) {
            endTimer();
            return;
        }

        RoleplayUser rp = client.getHabbo().getRoleplay();

        // Parameterized update to the gang score
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "UPDATE `rp_gangs` SET `gang_score` = `gang_score` + 15 WHERE `id` = ?")) {
            statement.setInt(1, rp.getJobId()); // assuming jobId holds gangId or group ID
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to update gang score for gang ID {}", rp.getJobId(), e);
        }

        RoleplayManager.shout(client, "*Captura con éxito el barrio de pandillas*");
        client.getHabbo().whisper("¡Ganaste la captura del barrio!");
        endTimer();
    }
}
