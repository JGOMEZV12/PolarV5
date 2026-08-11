package com.eu.habbo.habbohotel.roleplay.bots;

public class RoleplayBotFriendship {
    private int botId;
    private int userId;

    public RoleplayBotFriendship(int botId, int userId) {
        this.botId = botId;
        this.userId = userId;
    }

    public int getBotId() {
        return botId;
    }

    public void setBotId(int botId) {
        this.botId = botId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
