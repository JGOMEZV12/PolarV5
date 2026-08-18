package com.eu.habbo.habbohotel.roleplay.gambling;

public class TexasHoldEmPlayer {
    public int userId;
    public int currentBet;
    public int totalAmount;

    public TexasHoldEmPlayer(int userId, int currentBet, int totalAmount) {
        this.userId = userId;
        this.currentBet = currentBet;
        this.totalAmount = totalAmount;
    }
}
