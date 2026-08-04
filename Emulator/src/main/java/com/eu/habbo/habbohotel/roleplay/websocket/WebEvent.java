package com.eu.habbo.habbohotel.roleplay.websocket;

import com.google.gson.annotations.SerializedName;

/**
 * Portado desde: Polar RP/HabboRoleplay/Web/WebEvent.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Estructura de un Evento de WebSocket de Roleplay (JSON).
 */
public class WebEvent {

    @SerializedName("UserId")
    private int userId;

    @SerializedName("Token")
    private int token;

    @SerializedName("EventName")
    private String eventName;

    @SerializedName("ExtraData")
    private String extraData;

    @SerializedName("Bypass")
    private boolean bypass;

    @SerializedName("JSON")
    private boolean isJSON;

    public WebEvent() {}

    public WebEvent(int userId, int token, String eventName, String extraData, boolean bypass, boolean isJSON) {
        this.userId = userId;
        this.token = token;
        this.eventName = eventName;
        this.extraData = extraData;
        this.bypass = bypass;
        this.isJSON = isJSON;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getExtraData() {
        return extraData;
    }

    public void setExtraData(String extraData) {
        this.extraData = extraData;
    }

    public boolean isBypass() {
        return bypass;
    }

    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

    public boolean isJSON() {
        return isJSON;
    }

    public void setJSON(boolean JSON) {
        isJSON = JSON;
    }
}
