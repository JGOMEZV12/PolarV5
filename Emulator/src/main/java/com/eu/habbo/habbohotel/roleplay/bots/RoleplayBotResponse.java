package com.eu.habbo.habbohotel.roleplay.bots;

public class RoleplayBotResponse {
    private String message;
    private String response;
    private int bubble;
    private String type;

    public RoleplayBotResponse(String message, String response, int bubble, String type) {
        this.message = message;
        this.response = response;
        this.bubble = bubble;
        this.type = type;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public int getBubble() { return bubble; }
    public void setBubble(int bubble) { this.bubble = bubble; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
