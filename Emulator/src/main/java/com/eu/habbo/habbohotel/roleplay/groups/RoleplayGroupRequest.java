package com.eu.habbo.habbohotel.roleplay.groups;

public class RoleplayGroupRequest {
    private int groupId; // job_id or gang_id
    private int userId;
    private String description;
    private int hours;
    private String region;
    private long timestamp;

    public RoleplayGroupRequest(int groupId, int userId, String description, int hours, String region, long timestamp) {
        this.groupId = groupId;
        this.userId = userId;
        this.description = description;
        this.hours = hours;
        this.region = region;
        this.timestamp = timestamp;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
