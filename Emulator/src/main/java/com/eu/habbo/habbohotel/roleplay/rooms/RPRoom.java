package com.eu.habbo.habbohotel.roleplay.rooms;

public class RPRoom {
    private int id;
    private String cityRP;
    private boolean hospitalRP;
    private boolean prisonRP;
    private boolean courtRP;
    private boolean prisonBackRP;
    private boolean camioneroRP;
    private boolean mecanicoRP;
    private boolean basureroRP;
    private boolean armeroRP;
    private boolean polStationRP;

    public RPRoom(
            int id,
            String cityRP,
            boolean courtRP,
            boolean hospitalRP,
            boolean prisonRP,
            boolean prisonBRP,
            boolean camioneroRP,
            boolean mecanicoRP,
            boolean basureroRP,
            boolean armeroRP,
            boolean polStationRP) {
        this.id = id;
        this.cityRP = cityRP;
        this.hospitalRP = hospitalRP;
        this.prisonRP = prisonRP;
        this.courtRP = courtRP;
        this.prisonBackRP = prisonBRP;
        this.camioneroRP = camioneroRP;
        this.mecanicoRP = mecanicoRP;
        this.basureroRP = basureroRP;
        this.armeroRP = armeroRP;
        this.polStationRP = polStationRP;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityRP() {
        return cityRP;
    }

    public void setCityRP(String cityRP) {
        this.cityRP = cityRP;
    }

    public boolean isHospitalRP() {
        return hospitalRP;
    }

    public void setHospitalRP(boolean hospitalRP) {
        this.hospitalRP = hospitalRP;
    }

    public boolean isPrisonRP() {
        return prisonRP;
    }

    public void setPrisonRP(boolean prisonRP) {
        this.prisonRP = prisonRP;
    }

    public boolean isCourtRP() {
        return courtRP;
    }

    public void setCourtRP(boolean courtRP) {
        this.courtRP = courtRP;
    }

    public boolean isPrisonBackRP() {
        return prisonBackRP;
    }

    public void setPrisonBackRP(boolean prisonBackRP) {
        this.prisonBackRP = prisonBackRP;
    }

    public boolean isCamioneroRP() {
        return camioneroRP;
    }

    public void setCamioneroRP(boolean camioneroRP) {
        this.camioneroRP = camioneroRP;
    }

    public boolean isMecanicoRP() {
        return mecanicoRP;
    }

    public void setMecanicoRP(boolean mecanicoRP) {
        this.mecanicoRP = mecanicoRP;
    }

    public boolean isBasureroRP() {
        return basureroRP;
    }

    public void setBasureroRP(boolean basureroRP) {
        this.basureroRP = basureroRP;
    }

    public boolean isArmeroRP() {
        return armeroRP;
    }

    public void setArmeroRP(boolean armeroRP) {
        this.armeroRP = armeroRP;
    }

    public boolean isPolStationRP() {
        return polStationRP;
    }

    public void setPolStationRP(boolean polStationRP) {
        this.polStationRP = polStationRP;
    }
}
