package com.eu.habbo.habbohotel.roleplay.comodin;

public class Comodin {
    private int id;
    private int furniId;
    private int roomId;
    private String action;

    public Comodin(int id, int furniId, int roomId, String action) {
        this.id = id;
        this.furniId = furniId;
        this.roomId = roomId;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFurniId() {
        return furniId;
    }

    public void setFurniId(int furniId) {
        this.furniId = furniId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
