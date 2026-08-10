package com.eu.habbo.habbohotel.roleplay.gangs;

import com.eu.habbo.habbohotel.roleplay.RpEngine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Portado desde: Polar RP/HabboRoleplay/Turfs/Turf.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Representa una zona territorial controlable por bandas (pandillas).
 */
public class Turf {
    private static final Logger LOGGER = LoggerFactory.getLogger(Turf.class);

    private final int roomId;
    private int gangId;
    private final int beginX;
    private final int beginY;
    private final int endX;
    private final int endY;
    private final int flagX;
    private final int flagY;

    // Estados de captura en tiempo real
    private transient boolean capturing;
    private transient int captureProgress;
    private transient int capturerId;
    private transient int capturingGangId;

    public Turf(ResultSet set) throws SQLException {
        this.roomId = set.getInt("room_id");
        this.gangId = set.getInt("gang_id");
        this.beginX = set.getInt("begin_x");
        this.beginY = set.getInt("begin_y");
        this.endX = set.getInt("end_x");
        this.endY = set.getInt("end_y");
        this.flagX = set.getInt("flag_x");
        this.flagY = set.getInt("flag_y");

        this.capturing = false;
        this.captureProgress = 0;
        this.capturerId = 0;
        this.capturingGangId = 0;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getGangId() {
        return gangId;
    }

    public void setGangId(int gangId) {
        this.gangId = gangId;
    }

    public int getBeginX() {
        return beginX;
    }

    public int getBeginY() {
        return beginY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public int getFlagX() {
        return flagX;
    }

    public int getFlagY() {
        return flagY;
    }

    public boolean isCapturing() {
        return capturing;
    }

    public void setCapturing(boolean capturing) {
        this.capturing = capturing;
    }

    public int getCaptureProgress() {
        return captureProgress;
    }

    public void setCaptureProgress(int captureProgress) {
        this.captureProgress = captureProgress;
    }

    public int getCapturerId() {
        return capturerId;
    }

    public void setCapturerId(int capturerId) {
        this.capturerId = capturerId;
    }

    public int getCapturingGangId() {
        return capturingGangId;
    }

    public void setCapturingGangId(int capturingGangId) {
        this.capturingGangId = capturingGangId;
    }

    /**
     * Verifica si una coordenada está dentro de los límites del territorio.
     */
    public boolean isInside(int x, int y) {
        return x >= beginX && x <= endX && y >= beginY && y <= endY;
    }

    /**
     * Actualiza la propiedad de la banda de forma síncrona en base de datos.
     */
    public void updateTurf(int newGangId) {
        this.gangId = newGangId;
        String query = "UPDATE rp_gangs_turfs SET gang_id = ? WHERE room_id = ?";
        try (Connection connection = RpEngine.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newGangId);
            statement.setInt(2, this.roomId);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Error al actualizar la banda del territorio #" + this.roomId, e);
        }
    }

    /**
     * Actualiza la propiedad de la banda asíncronamente.
     */
    public void updateTurfAsync(int newGangId) {
        RpEngine.getThreading().run(() -> updateTurf(newGangId));
    }
}
