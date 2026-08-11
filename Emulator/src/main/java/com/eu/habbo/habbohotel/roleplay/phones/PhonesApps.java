package com.eu.habbo.habbohotel.roleplay.phones;

public class PhonesApps {
    private int id;
    private String name;
    private String displayName;
    private String icon;
    private String developerName;
    private String code;
    private int price;
    private String version;

    public PhonesApps(int id, String name, String displayName, String icon, String developerName, String code, int price, String version) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.icon = icon;
        this.developerName = developerName;
        this.code = code;
        this.price = price;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
