package com.eu.habbo.habbohotel.roleplay.groups;

public class RoleplayGroupRank {
    private int gangId;
    private int rank;
    private String name;
    private String maleFigure;
    private String femaleFigure;
    private int pay;
    private String commands;
    private String workrooms;
    private int limit;
    private int timer;

    public RoleplayGroupRank(
            int gangId,
            int rank,
            String name,
            String maleFigure,
            String femaleFigure,
            int pay,
            String commands,
            String workrooms,
            int limit,
            int timer) {
        this.gangId = gangId;
        this.rank = rank;
        this.name = name;
        this.maleFigure = maleFigure;
        this.femaleFigure = femaleFigure;
        this.pay = pay;
        this.commands = commands;
        this.workrooms = workrooms;
        this.limit = limit;
        this.timer = timer;
    }

    public int getGangId() {
        return gangId;
    }

    public void setGangId(int gangId) {
        this.gangId = gangId;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaleFigure() {
        return maleFigure;
    }

    public void setMaleFigure(String maleFigure) {
        this.maleFigure = maleFigure;
    }

    public String getFemaleFigure() {
        return femaleFigure;
    }

    public void setFemaleFigure(String femaleFigure) {
        this.femaleFigure = femaleFigure;
    }

    public int getPay() {
        return pay;
    }

    public void setPay(int pay) {
        this.pay = pay;
    }

    public String getCommands() {
        return commands;
    }

    public void setCommands(String commands) {
        this.commands = commands;
    }

    public String getWorkrooms() {
        return workrooms;
    }

    public void setWorkrooms(String workrooms) {
        this.workrooms = workrooms;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }
}
