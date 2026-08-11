package com.eu.habbo.habbohotel.roleplay.vehicles;

public class VehicleJobs {
    private int id;
    private int roomId;
    private int baseItem;
    private int x;
    private int y;
    private double z;
    private int rot;
    private int jobId;

    public VehicleJobs(int id, int roomId, int baseItem, int x, int y, double z, int rot, int jobId) {
        this.id = id;
        this.roomId = roomId;
        this.baseItem = baseItem;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rot = rot;
        this.jobId = jobId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getBaseItem() { return baseItem; }
    public void setBaseItem(int baseItem) { this.baseItem = baseItem; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }

    public int getRot() { return rot; }
    public void setRot(int rot) { this.rot = rot; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
}
