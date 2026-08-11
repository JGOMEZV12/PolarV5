package com.eu.habbo.habbohotel.roleplay.bots;

public class RoleplayBot {
    private int id;
    private int ownerId;
    private String name;
    private String gender;
    private String figure;
    private String motto;
    private int maxHealth;
    private int curHealth;
    private int strength;
    private int level;
    private int spawnId;
    private int spawnX;
    private int spawnY;
    private double spawnZ;
    private int spawnRot;
    private String aiType;
    private int roamInterval;
    private int attackInterval;
    private int followInterval;
    private int stayInterval;
    private boolean roamBot;
    private boolean roamCityBot;
    private boolean addableBot;
    private int corporationId;
    private String stopworkItem;
    private String workUniform;
    private boolean canBeAttacked;
    private int attackPos;
    private String actionOdds;
    private int speechTimer;
    private String petData;

    public RoleplayBot(int id, int ownerId, String name, String gender, String figure, String motto, int maxHealth, int curHealth, int strength, int level, int spawnId, int spawnX, int spawnY, double spawnZ, int spawnRot, String aiType, int roamInterval, int attackInterval, int followInterval, int stayInterval, boolean roamBot, boolean roamCityBot, boolean addableBot, int corporationId, String stopworkItem, String workUniform, boolean canBeAttacked, int attackPos, String actionOdds, int speechTimer, String petData) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.gender = gender;
        this.figure = figure;
        this.motto = motto;
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
        this.strength = strength;
        this.level = level;
        this.spawnId = spawnId;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.spawnZ = spawnZ;
        this.spawnRot = spawnRot;
        this.aiType = aiType;
        this.roamInterval = roamInterval;
        this.attackInterval = attackInterval;
        this.followInterval = followInterval;
        this.stayInterval = stayInterval;
        this.roamBot = roamBot;
        this.roamCityBot = roamCityBot;
        this.addableBot = addableBot;
        this.corporationId = corporationId;
        this.stopworkItem = stopworkItem;
        this.workUniform = workUniform;
        this.canBeAttacked = canBeAttacked;
        this.attackPos = attackPos;
        this.actionOdds = actionOdds;
        this.speechTimer = speechTimer;
        this.petData = petData;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.figure = gender; }

    public String getFigure() { return figure; }
    public void setFigure(String figure) { this.figure = figure; }

    public String getMotto() { return motto; }
    public void setMotto(String motto) { this.motto = motto; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }

    public int getCurHealth() { return curHealth; }
    public void setCurHealth(int curHealth) { this.curHealth = curHealth; }

    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getSpawnId() { return spawnId; }
    public void setSpawnId(int spawnId) { this.spawnId = spawnId; }

    public int getSpawnX() { return spawnX; }
    public void setSpawnX(int spawnX) { this.spawnX = spawnX; }

    public int getSpawnY() { return spawnY; }
    public void setSpawnY(int spawnY) { this.spawnY = spawnY; }

    public double getSpawnZ() { return spawnZ; }
    public void setSpawnZ(double spawnZ) { this.spawnZ = spawnZ; }

    public int getSpawnRot() { return spawnRot; }
    public void setSpawnRot(int spawnRot) { this.spawnRot = spawnRot; }

    public String getAiType() { return aiType; }
    public void setAiType(String aiType) { this.aiType = aiType; }

    public int getRoamInterval() { return roamInterval; }
    public void setRoamInterval(int roamInterval) { this.roamInterval = roamInterval; }

    public int getAttackInterval() { return attackInterval; }
    public void setAttackInterval(int attackInterval) { this.attackInterval = attackInterval; }

    public int getFollowInterval() { return followInterval; }
    public void setFollowInterval(int followInterval) { this.followInterval = followInterval; }

    public int getStayInterval() { return stayInterval; }
    public void setStayInterval(int stayInterval) { this.stayInterval = stayInterval; }

    public boolean isRoamBot() { return roamBot; }
    public void setRoamBot(boolean roamBot) { this.roamBot = roamBot; }

    public boolean isRoamCityBot() { return roamCityBot; }
    public void setRoamCityBot(boolean roamCityBot) { this.roamCityBot = roamCityBot; }

    public boolean isAddableBot() { return addableBot; }
    public void setAddableBot(boolean addableBot) { this.addableBot = addableBot; }

    public int getCorporationId() { return corporationId; }
    public void setCorporationId(int corporationId) { this.corporationId = corporationId; }

    public String getStopworkItem() { return stopworkItem; }
    public void setStopworkItem(String stopworkItem) { this.stopworkItem = stopworkItem; }

    public String getWorkUniform() { return workUniform; }
    public void setWorkUniform(String workUniform) { this.workUniform = workUniform; }

    public boolean isCanBeAttacked() { return canBeAttacked; }
    public void setCanBeAttacked(boolean canBeAttacked) { this.canBeAttacked = canBeAttacked; }

    public int getAttackPos() { return attackPos; }
    public void setAttackPos(int attackPos) { this.attackPos = attackPos; }

    public String getActionOdds() { return actionOdds; }
    public void setActionOdds(String actionOdds) { this.actionOdds = actionOdds; }

    public int getSpeechTimer() { return speechTimer; }
    public void setSpeechTimer(int speechTimer) { this.speechTimer = speechTimer; }

    public String getPetData() { return petData; }
    public void setPetData(String petData) { this.petData = petData; }
}
