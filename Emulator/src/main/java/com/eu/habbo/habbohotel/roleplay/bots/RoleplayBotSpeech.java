package com.eu.habbo.habbohotel.roleplay.bots;

public class RoleplayBotSpeech {
    private int id;
    private int botId;
    private String speech;
    private boolean shout;

    public RoleplayBotSpeech(int id, int botId, String speech, boolean shout) {
        this.id = id;
        this.botId = botId;
        this.speech = speech;
        this.shout = shout;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBotId() { return botId; }
    public void setBotId(int botId) { this.botId = botId; }

    public String getSpeech() { return speech; }
    public void setSpeech(String speech) { this.speech = speech; }

    public boolean isShout() { return shout; }
    public void setShout(boolean shout) { this.shout = shout; }
}
