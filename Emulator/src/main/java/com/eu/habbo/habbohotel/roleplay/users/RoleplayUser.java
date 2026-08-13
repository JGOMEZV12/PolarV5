package com.eu.habbo.habbohotel.roleplay.users;

public class RoleplayUser {
    // Persistent Stats / Saved Variables
    private int level;
    private int levelEXP;
    private int jobId;
    private int jobRank;
    private int jobRequest;
    private int maxHealth;
    private int curHealth;
    private int maxEnergy;
    private int curEnergy;
    private int curAlcohol;
    private int maxAlcohol;
    private int armor; // mapping to kevlar in DB
    private int hunger;
    private int sida;
    private int hygiene;
    private int animo;
    private int poop;
    private int intelligence;
    private int strength;
    private int stamina;
    private int intelligenceEXP;
    private int strengthEXP;
    private int staminaEXP;
    private boolean isStun;
    private boolean isDead;
    private int deadTimeLeft;
    private boolean isJailed;
    private int jailedTimeLeft;
    private boolean isWanted;
    private int wantedLevel;
    private int wantedTimeLeft;
    private boolean onProbation;
    private int probationTimeLeft;
    private int sendhomeTimeLeft;
    private boolean isCuffed;
    private int cuffedTimeLeft;

    // Banking variables
    private int bankAccount;
    private int bankTarget;
    private int bankChequings;
    private int bankSavings;

    // Transient memory fields
    private boolean working;
    private boolean policeTrial;
    private boolean disableRadio;
    private boolean jailbroken;
    private boolean drivingCar;
    private int camCargId;
    private int camState;
    private int camDest;
    private int camOwnId;
    private boolean camLoading;
    private boolean camUnLoading;
    private boolean basuChofer;
    private int basuTrashCount;
    private int chalecoPor;
    private boolean paralized;

    // Farming stats
    private com.eu.habbo.habbohotel.roleplay.farming.FarmingStats farmingStats;
    private final com.eu.habbo.habbohotel.roleplay.users.offers.OffersManager offerManager = new com.eu.habbo.habbohotel.roleplay.users.offers.OffersManager();

    public com.eu.habbo.habbohotel.roleplay.farming.FarmingStats getFarmingStats() {
        return farmingStats;
    }

    public void setFarmingStats(com.eu.habbo.habbohotel.roleplay.farming.FarmingStats farmingStats) {
        this.farmingStats = farmingStats;
    }

    public com.eu.habbo.habbohotel.roleplay.users.offers.OffersManager getOfferManager() {
        return offerManager;
    }

    // Cooldown manager
    private com.eu.habbo.habbohotel.roleplay.cooldowns.CooldownManager cooldownManager;

    public com.eu.habbo.habbohotel.roleplay.cooldowns.CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public void setCooldownManager(com.eu.habbo.habbohotel.roleplay.cooldowns.CooldownManager cooldownManager) {
        this.cooldownManager = cooldownManager;
    }

    // Timer manager
    private com.eu.habbo.habbohotel.roleplay.timers.TimerManager timerManager;

    public com.eu.habbo.habbohotel.roleplay.timers.TimerManager getTimerManager() {
        return timerManager;
    }

    public void setTimerManager(com.eu.habbo.habbohotel.roleplay.timers.TimerManager timerManager) {
        this.timerManager = timerManager;
    }

    // Other Transient variables from C# RoleplayUser
    private int userId;
    public int socketChatSpamTicks = -1;
    public int socketChatFloodTime = 0;
    public int socketChatSpamCount = 0;
    private final java.util.concurrent.ConcurrentHashMap<
                    String, com.eu.habbo.habbohotel.roleplay.websocket.chats.WebSocketChatRoom>
            chatRooms = new java.util.concurrent.ConcurrentHashMap<>();
    public int texasHoldEmPlayer = 0;

    private int teleportDestX = 0;
    private int teleportDestY = 0;
    private double teleportDestZ = 0.0;
    private boolean hasTeleportDestination = false;
    private boolean taxiTeleportAuthorized = false;
    private int taxiTargetRoomId = 0;

    public RoleplayUser() {}

    // Getters and Setters for Persistent Stats
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getLevelEXP() {
        return levelEXP;
    }

    public void setLevelEXP(int levelEXP) {
        this.levelEXP = levelEXP;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getJobRank() {
        return jobRank;
    }

    public void setJobRank(int jobRank) {
        this.jobRank = jobRank;
    }

    public int getJobRequest() {
        return jobRequest;
    }

    public void setJobRequest(int jobRequest) {
        this.jobRequest = jobRequest;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getCurHealth() {
        return curHealth;
    }

    public void setCurHealth(int curHealth) {
        this.curHealth = curHealth;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(int maxEnergy) {
        this.maxEnergy = maxEnergy;
    }

    public int getCurEnergy() {
        return curEnergy;
    }

    public void setCurEnergy(int curEnergy) {
        this.curEnergy = curEnergy;
    }

    public int getCurAlcohol() {
        return curAlcohol;
    }

    public void setCurAlcohol(int curAlcohol) {
        this.curAlcohol = curAlcohol;
    }

    public int getMaxAlcohol() {
        return maxAlcohol;
    }

    public void setMaxAlcohol(int maxAlcohol) {
        this.maxAlcohol = maxAlcohol;
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getHunger() {
        return hunger;
    }

    public void setHunger(int hunger) {
        this.hunger = hunger;
    }

    public int getSida() {
        return sida;
    }

    public void setSida(int sida) {
        this.sida = sida;
    }

    public int getHygiene() {
        return hygiene;
    }

    public void setHygiene(int hygiene) {
        this.hygiene = hygiene;
    }

    public int getAnimo() {
        return animo;
    }

    public void setAnimo(int animo) {
        this.animo = animo;
    }

    public int getPoop() {
        return poop;
    }

    public void setPoop(int poop) {
        this.poop = poop;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public void setIntelligence(int intelligence) {
        this.intelligence = intelligence;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getStamina() {
        return stamina;
    }

    public void setStamina(int stamina) {
        this.stamina = stamina;
    }

    public int getIntelligenceEXP() {
        return intelligenceEXP;
    }

    public void setIntelligenceEXP(int intelligenceEXP) {
        this.intelligenceEXP = intelligenceEXP;
    }

    public int getStrengthEXP() {
        return strengthEXP;
    }

    public void setStrengthEXP(int strengthEXP) {
        this.strengthEXP = strengthEXP;
    }

    public int getStaminaEXP() {
        return staminaEXP;
    }

    public void setStaminaEXP(int staminaEXP) {
        this.staminaEXP = staminaEXP;
    }

    public boolean isStun() {
        return isStun;
    }

    public void setStun(boolean stun) {
        isStun = stun;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public int getDeadTimeLeft() {
        return deadTimeLeft;
    }

    public void setDeadTimeLeft(int deadTimeLeft) {
        this.deadTimeLeft = deadTimeLeft;
    }

    public boolean isJailed() {
        return isJailed;
    }

    public void setJailed(boolean jailed) {
        isJailed = jailed;
    }

    public int getJailedTimeLeft() {
        return jailedTimeLeft;
    }

    public void setJailedTimeLeft(int jailedTimeLeft) {
        this.jailedTimeLeft = jailedTimeLeft;
    }

    public boolean isWanted() {
        return isWanted;
    }

    public void setWanted(boolean wanted) {
        isWanted = wanted;
    }

    public int getWantedLevel() {
        return wantedLevel;
    }

    public void setWantedLevel(int wantedLevel) {
        this.wantedLevel = wantedLevel;
    }

    public int getWantedTimeLeft() {
        return wantedTimeLeft;
    }

    public void setWantedTimeLeft(int wantedTimeLeft) {
        this.wantedTimeLeft = wantedTimeLeft;
    }

    public boolean isOnProbation() {
        return onProbation;
    }

    public void setOnCheckProbation(boolean onCheckProbation) {
        this.onProbation = onCheckProbation;
    }

    public int getProbationTimeLeft() {
        return probationTimeLeft;
    }

    public void setProbationTimeLeft(int probationTimeLeft) {
        this.probationTimeLeft = probationTimeLeft;
    }

    public int getSendhomeTimeLeft() {
        return sendhomeTimeLeft;
    }

    public void setSendhomeTimeLeft(int sendhomeTimeLeft) {
        this.sendhomeTimeLeft = sendhomeTimeLeft;
    }

    public boolean isCuffed() {
        return isCuffed;
    }

    public void setCuffed(boolean cuffed) {
        isCuffed = cuffed;
    }

    public int getCuffedTimeLeft() {
        return cuffedTimeLeft;
    }

    public void setCuffedTimeLeft(int cuffedTimeLeft) {
        this.cuffedTimeLeft = cuffedTimeLeft;
    }

    // Getters and Setters for Banking variables
    public int getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(int bankAccount) {
        this.bankAccount = bankAccount;
    }

    public int getBankTarget() {
        return bankTarget;
    }

    public void setBankTarget(int bankTarget) {
        this.bankTarget = bankTarget;
    }

    public int getBankChequings() {
        return bankChequings;
    }

    public void setBankChequings(int bankChequings) {
        this.bankChequings = bankChequings;
    }

    public int getBankSavings() {
        return bankSavings;
    }

    public void setBankSavings(int bankSavings) {
        this.bankSavings = bankSavings;
    }

    // Getters and Setters for Transient fields
    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public boolean isPoliceTrial() {
        return policeTrial;
    }

    public void setPoliceTrial(boolean policeTrial) {
        this.policeTrial = policeTrial;
    }

    public boolean isDisableRadio() {
        return disableRadio;
    }

    public void setDisableRadio(boolean disableRadio) {
        this.disableRadio = disableRadio;
    }

    public boolean isJailbroken() {
        return jailbroken;
    }

    public void setJailbroken(boolean jailbroken) {
        this.jailbroken = jailbroken;
    }

    public boolean isDrivingCar() {
        return drivingCar;
    }

    public void setDrivingCar(boolean drivingCar) {
        this.drivingCar = drivingCar;
    }

    public int getCamCargId() {
        return camCargId;
    }

    public void setCamCargId(int camCargId) {
        this.camCargId = camCargId;
    }

    public int getCamState() {
        return camState;
    }

    public void setCamState(int camState) {
        this.camState = camState;
    }

    public int getCamDest() {
        return camDest;
    }

    public void setCamDest(int camDest) {
        this.camDest = camDest;
    }

    public int getCamOwnId() {
        return camOwnId;
    }

    public void setCamOwnId(int camOwnId) {
        this.camOwnId = camOwnId;
    }

    public boolean isCamLoading() {
        return camLoading;
    }

    public void setCamLoading(boolean camLoading) {
        this.camLoading = camLoading;
    }

    public boolean isCamUnLoading() {
        return camUnLoading;
    }

    public void setCamUnLoading(boolean camUnLoading) {
        this.camUnLoading = camUnLoading;
    }

    public boolean isBasuChofer() {
        return basuChofer;
    }

    public void setBasuChofer(boolean basuChofer) {
        this.basuChofer = basuChofer;
    }

    public int getBasuTrashCount() {
        return basuTrashCount;
    }

    public void setBasuTrashCount(int basuTrashCount) {
        this.basuTrashCount = basuTrashCount;
    }

    public int getChalecoPor() {
        return chalecoPor;
    }

    public void setChalecoPor(int chalecoPor) {
        this.chalecoPor = chalecoPor;
    }

    public boolean isParalized() {
        return paralized;
    }

    public void setParalized(boolean paralized) {
        this.paralized = paralized;
    }

    public int getTeleportDestX() {
        return teleportDestX;
    }

    public void setTeleportDestX(int teleportDestX) {
        this.teleportDestX = teleportDestX;
    }

    public int getTeleportDestY() {
        return teleportDestY;
    }

    public void setTeleportDestY(int teleportDestY) {
        this.teleportDestY = teleportDestY;
    }

    public double getTeleportDestZ() {
        return teleportDestZ;
    }

    public void setTeleportDestZ(double teleportDestZ) {
        this.teleportDestZ = teleportDestZ;
    }

    public boolean isHasTeleportDestination() {
        return hasTeleportDestination;
    }

    public void setHasTeleportDestination(boolean hasTeleportDestination) {
        this.hasTeleportDestination = hasTeleportDestination;
    }

    public boolean isTaxiTeleportAuthorized() {
        return taxiTeleportAuthorized;
    }

    public void setTaxiTeleportAuthorized(boolean taxiTeleportAuthorized) {
        this.taxiTeleportAuthorized = taxiTeleportAuthorized;
    }

    public int getTaxiTargetRoomId() {
        return taxiTargetRoomId;
    }

    public void setTaxiTargetRoomId(int taxiTargetRoomId) {
        this.taxiTargetRoomId = taxiTargetRoomId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSocketChatSpamTicks() {
        return socketChatSpamTicks;
    }

    public void setSocketChatSpamTicks(int socketChatSpamTicks) {
        this.socketChatSpamTicks = socketChatSpamTicks;
    }

    public int getSocketChatFloodTime() {
        return socketChatFloodTime;
    }

    public void setSocketChatFloodTime(int socketChatFloodTime) {
        this.socketChatFloodTime = socketChatFloodTime;
    }

    public int getSocketChatSpamCount() {
        return socketChatSpamCount;
    }

    public void setSocketChatSpamCount(int socketChatSpamCount) {
        this.socketChatSpamCount = socketChatSpamCount;
    }

    public java.util.concurrent.ConcurrentHashMap<
                    String, com.eu.habbo.habbohotel.roleplay.websocket.chats.WebSocketChatRoom>
            getChatRooms() {
        return chatRooms;
    }

    public io.netty.channel.Channel getWebSocketConnection() {
        return com.eu.habbo.habbohotel.roleplay.websocket.WebEventManager.getInstance()
                .getChannelByUserId(this.userId);
    }

    public int getTexasHoldEmPlayer() {
        return texasHoldEmPlayer;
    }

    public void setTexasHoldEmPlayer(int texasHoldEmPlayer) {
        this.texasHoldEmPlayer = texasHoldEmPlayer;
    }
}
