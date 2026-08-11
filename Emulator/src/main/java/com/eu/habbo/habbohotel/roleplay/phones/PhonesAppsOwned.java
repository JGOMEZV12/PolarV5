package com.eu.habbo.habbohotel.roleplay.phones;

public class PhonesAppsOwned {
    private int id;
    private int phoneId;
    private int appId;
    private int screenId;
    private int slotId;
    private String extradata;

    public PhonesAppsOwned(int id, int phoneId, int appId, int screenId, int slotId, String extradata) {
        this.id = id;
        this.phoneId = phoneId;
        this.appId = appId;
        this.screenId = screenId;
        this.slotId = slotId;
        this.extradata = extradata;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public void setPhoneId(int phoneId) {
        this.phoneId = phoneId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getScreenId() {
        return screenId;
    }

    public void setScreenId(int screenId) {
        this.screenId = screenId;
    }

    public int getSlotId() {
        return slotId;
    }

    public void setSlotId(int slotId) {
        this.slotId = slotId;
    }

    public String getExtradata() {
        return extradata;
    }

    public void setExtradata(String extradata) {
        this.extradata = extradata;
    }
}
