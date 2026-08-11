package com.eu.habbo.habbohotel.roleplay.websocket;

import com.google.gson.annotations.SerializedName;

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
