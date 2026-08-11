package com.eu.habbo.habbohotel.roleplay.websocket;

import io.netty.channel.Channel;

public class WebSocketUser {
    private int id;
    private String username;
    private boolean closing;
    private Channel channel;

    public WebSocketUser(int id, String username, Channel channel) {
        this.id = id;
        this.username = username != null ? username : "";
        this.closing = false;
        this.channel = channel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isClosing() {
        return closing;
    }

    public void setClosing(boolean closing) {
        this.closing = closing;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void dispose() {
        this.id = 0;
        this.username = null;
        this.closing = true;
        this.channel = null;
    }
}
