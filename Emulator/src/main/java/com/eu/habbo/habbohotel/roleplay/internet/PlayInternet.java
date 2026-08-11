package com.eu.habbo.habbohotel.roleplay.internet;

public class PlayInternet {
    private int id;
    private String url;
    private String name;
    private String description;
    private int authorId;
    private String code;

    public PlayInternet(int id, String url, String name, String description, int authorId, String code) {
        this.id = id;
        this.url = url;
        this.name = name;
        this.description = description;
        this.authorId = authorId;
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
