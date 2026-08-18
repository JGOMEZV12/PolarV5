package com.eu.habbo.habbohotel.roleplay.phones;

public class PhonesOwned {
    private int id;
    private int phoneId;
    private int userId;
    private String phoneNumber;

    public PhonesOwned(int id, int phoneId, int userId, String phoneNumber) {
        this.id = id;
        this.phoneId = phoneId;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
