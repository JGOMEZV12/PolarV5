package com.eu.habbo.habbohotel.roleplay.users;

import com.eu.habbo.habbohotel.roleplay.economy.ProductOwned;
import com.eu.habbo.habbohotel.roleplay.economy.ProductsManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Portado desde: Polar RP/HabboRoleplay/RoleplayUsers/RoleplayUser.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Gestiona las estadísticas RP del usuario.
 * Cambios: Adaptado de singleton C# a clase de instancia en Java controlada por RoleplayUserManager.
 * Mejoras: Caché de productos del inventario integrada para evitar lecturas concurrentes a la DB.
 */
public class RoleplayUser {

    private int userId;
    private int level = 1;
    private int levelExp = 0;
    private String rpClass = "";
    private boolean permanentClass = false;

    private int jobId = 1;
    private int jobRank = 1;
    private int jobRequest = 0;
    private int sendHomeTimeLeft = 0;

    private int maxHealth = 100;
    private int curHealth = 100;
    private int maxEnergy = 100;
    private int curEnergy = 100;
    private int curAlcohol = 0;
    private int maxAlcohol = 100;
    private int armor = 0;
    private int hunger = 0;
    private int sida = 0;
    private int hygiene = 0;
    private int animo = 100;
    private int poop = 0;

    private int intelligence = 0;
    private int strength = 0;
    private int stamina = 0;
    private int intelligenceExp = 0;
    private int strengthExp = 0;
    private int staminaExp = 0;
    private boolean passiveMode = false;

    private boolean isStun = false;
    private boolean isDead = false;
    private int deadTimeLeft = 0;
    private boolean isJailed = false;
    private int jailedTimeLeft = 0;
    private boolean isWanted = false;
    private int wantedLevel = 0;
    private int wantedTimeLeft = 0;
    private boolean onProbation = false;
    private int probationTimeLeft = 0;
    private boolean isCuffed = false;
    private int cuffedTimeLeft = 0;

    private int punches = 0;
    private int kills = 0;
    private int hitKills = 0;
    private int gunKills = 0;
    private int deaths = 0;
    private int copDeaths = 0;
    private int timeWorked = 0;
    private int arrests = 0;
    private int arrested = 0;
    private int evasions = 0;

    private int bankAccount = 0;
    private int bankTarget = 0;
    private int bankChequings = 0;
    private int bankSavings = 0;
    private int weedBaul = 0;

    private int basuLvl = 1;
    private int basuXp = 0;
    private int huntPoints = 0;
    private String huntSkins = "";
    private int armLvl = 1;
    private int armXp = 0;
    private int mecLvl = 1;
    private int mecXp = 0;
    private int camLvl = 1;
    private int camXp = 0;

    private int lastKilled = 0;
    private int marriedTo = 0;
    private int hijo = 0;

    private int gangId = 0;
    private int gangRank = 0;
    private int gangRequest = 0;
    private int changeNameCount = 0;

    private int car = 0;
    private int carFuel = 0;
    private int weed = 0;
    private int cocaine = 0;
    private int botiquin = 0;
    private int heroina = 0;
    private int caramelos = 0;
    private int medicina = 0;
    private int cigarette = 0;
    private int pildora = 0;
    private int dynamite = 0;
    private int weedmateria = 0;

    private String unlockedQuests = "";
    private int brawlWins = 0;
    private int cwWins = 0;
    private int mwWins = 0;
    private int soloQueueWins = 0;
    private boolean isNoob = true;
    private int noobTimeLeft = 0;
    private boolean inmunidadActivada = false;
    private int vipBanned = 0;
    private String lastCoordinates = "";

    // Caché local para productos del inventario del usuario
    private List<ProductOwned> ownedProducts = null;

    public RoleplayUser(int userId) {
        this.userId = userId;
    }

    public RoleplayUser(ResultSet row) throws SQLException {
        this.userId = row.getInt("id");
        this.level = row.getInt("level");
        this.levelExp = row.getInt("level_exp");
        this.rpClass = row.getString("class");
        this.permanentClass = row.getString("permanent_class").equals("1");

        this.jobId = row.getInt("job_id");
        this.jobRank = row.getInt("job_rank");
        this.jobRequest = row.getInt("job_request");
        this.sendHomeTimeLeft = row.getInt("sendhome_time_left");

        this.maxHealth = row.getInt("maxhealth");
        this.curHealth = row.getInt("curhealth");
        this.maxEnergy = row.getInt("maxenergy");
        this.curEnergy = row.getInt("curenergy");
        this.curAlcohol = row.getInt("curalcohol");
        this.maxAlcohol = row.getInt("maxalcohol");
        this.armor = row.getInt("kevlar");
        this.hunger = row.getInt("hunger");
        this.sida = row.getInt("sida");
        this.hygiene = row.getInt("hygiene");
        this.animo = row.getInt("animo");
        this.poop = row.getInt("poop");

        this.intelligence = row.getInt("intelligence");
        this.strength = row.getInt("strength");
        this.stamina = row.getInt("stamina");
        this.intelligenceExp = row.getInt("intelligence_exp");
        this.strengthExp = row.getInt("strength_exp");
        this.staminaExp = row.getInt("stamina_exp");
        this.passiveMode = row.getString("passive_mode").equals("1");

        this.isStun = row.getString("is_stun").equals("1");
        this.isDead = row.getString("is_dead").equals("1");
        this.deadTimeLeft = row.getInt("dead_time_left");
        this.isJailed = row.getString("is_jailed").equals("1");
        this.jailedTimeLeft = row.getInt("jailed_time_left");
        this.isWanted = row.getString("is_wanted").equals("1");
        this.wantedLevel = row.getInt("wanted_level");
        this.wantedTimeLeft = row.getInt("wanted_time_left");
        this.onProbation = row.getString("on_probation").equals("1");
        this.probationTimeLeft = row.getInt("probation_time_left");
        this.isCuffed = row.getString("is_cuffed").equals("1");
        this.cuffedTimeLeft = row.getInt("cuffed_time_left");

        this.punches = row.getInt("punches");
        this.kills = row.getInt("kills");
        this.hitKills = row.getInt("hit_kills");
        this.gunKills = row.getInt("gun_kills");
        this.deaths = row.getInt("deaths");
        this.copDeaths = row.getInt("cop_deaths");
        this.timeWorked = row.getInt("time_worked");
        this.arrests = row.getInt("arrests");
        this.arrested = row.getInt("arrested");
        this.evasions = row.getInt("evasions");

        this.bankAccount = row.getInt("bank_account");
        this.bankTarget = row.getInt("bank_target");
        this.bankChequings = row.getInt("bank_chequings");
        this.bankSavings = row.getInt("bank_savings");
        this.weedBaul = row.getInt("weedbaul");

        this.basuLvl = row.getInt("BasuLvl");
        this.basuXp = row.getInt("BasuXP");
        this.huntPoints = row.getInt("hunt_points");
        this.huntSkins = row.getString("hunt_skins");
        this.armLvl = row.getInt("ArmLvl");
        this.armXp = row.getInt("ArmXP");
        this.mecLvl = row.getInt("MecLvl");
        this.mecXp = row.getInt("MecXP");
        this.camLvl = row.getInt("CamLvl");
        this.camXp = row.getInt("CamXP");

        this.lastKilled = row.getInt("last_killed");
        this.marriedTo = row.getInt("married_to");
        this.hijo = row.getInt("hijo");

        this.gangId = row.getInt("gang_id");
        this.gangRank = row.getInt("gang_rank");
        this.gangRequest = row.getInt("gang_request");
        this.changeNameCount = row.getInt("changename_count");

        this.car = row.getInt("car");
        this.carFuel = row.getInt("car_fuel");
        this.weed = row.getInt("weed");
        this.cocaine = row.getInt("cocaine");
        this.botiquin = row.getInt("botiquin");
        this.heroina = row.getInt("heroina");
        this.caramelos = row.getInt("caramelos");
        this.medicina = row.getInt("medicina");
        this.cigarette = row.getInt("cigarette");
        this.pildora = row.getInt("pildora");
        this.dynamite = row.getInt("dynamite");
        this.weedmateria = row.getInt("weedmateria");

        this.unlockedQuests = row.getString("unlocked_quests");
        this.brawlWins = row.getInt("brawl_wins");
        this.cwWins = row.getInt("cw_wins");
        this.mwWins = row.getInt("mw_wins");
        this.soloQueueWins = row.getInt("soloqueue_wins");
        this.isNoob = row.getString("is_noob").equals("1");
        this.noobTimeLeft = row.getInt("noob_time_left");
        this.inmunidadActivada = row.getString("inmunidad_activada").equals("1");
        this.vipBanned = row.getInt("vip_banned");
        this.lastCoordinates = row.getString("last_coordinates");
    }

    public List<ProductOwned> getOwnedProducts() {
        if (this.ownedProducts == null) {
            this.ownedProducts = ProductsManager.getMyProductsOwned(this.userId);
            if (this.ownedProducts == null) {
                this.ownedProducts = new ArrayList<>();
            }
        }
        return this.ownedProducts;
    }

    public int getUserId() { return userId; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public int getLevelExp() { return levelExp; }
    public void setLevelExp(int levelExp) { this.levelExp = levelExp; }
    public String getRpClass() { return rpClass; }
    public void setRpClass(String rpClass) { this.rpClass = rpClass; }
    public boolean isPermanentClass() { return permanentClass; }
    public void setPermanentClass(boolean permanentClass) { this.permanentClass = permanentClass; }

    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public int getJobRank() { return jobRank; }
    public void setJobRank(int jobRank) { this.jobRank = jobRank; }
    public int getJobRequest() { return jobRequest; }
    public void setJobRequest(int jobRequest) { this.jobRequest = jobRequest; }
    public int getSendHomeTimeLeft() { return sendHomeTimeLeft; }
    public void setSendHomeTimeLeft(int sendHomeTimeLeft) { this.sendHomeTimeLeft = sendHomeTimeLeft; }

    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    public int getCurHealth() { return curHealth; }
    public void setCurHealth(int curHealth) { this.curHealth = curHealth; }
    public int getMaxEnergy() { return maxEnergy; }
    public void setMaxEnergy(int maxEnergy) { this.maxEnergy = maxEnergy; }
    public int getCurEnergy() { return curEnergy; }
    public void setCurEnergy(int curEnergy) { this.curEnergy = curEnergy; }
    public int getCurAlcohol() { return curAlcohol; }
    public void setCurAlcohol(int curAlcohol) { this.curAlcohol = curAlcohol; }
    public int getMaxAlcohol() { return maxAlcohol; }
    public void setMaxAlcohol(int maxAlcohol) { this.maxAlcohol = maxAlcohol; }
    public int getArmor() { return armor; }
    public void setArmor(int armor) { this.armor = armor; }
    public int getHunger() { return hunger; }
    public void setHunger(int hunger) { this.hunger = hunger; }
    public int getSida() { return sida; }
    public void setSida(int sida) { this.sida = sida; }
    public int getHygiene() { return hygiene; }
    public void setHygiene(int hygiene) { this.hygiene = hygiene; }
    public int getAnimo() { return animo; }
    public void setAnimo(int animo) { this.animo = animo; }
    public int getPoop() { return poop; }
    public void setPoop(int poop) { this.poop = poop; }

    public int getIntelligence() { return intelligence; }
    public void setIntelligence(int intelligence) { this.intelligence = intelligence; }
    public int getStrength() { return strength; }
    public void setStrength(int strength) { this.strength = strength; }
    public int getStamina() { return stamina; }
    public void setStamina(int stamina) { this.stamina = stamina; }
    public int getIntelligenceExp() { return intelligenceExp; }
    public void setIntelligenceExp(int intelligenceExp) { this.intelligenceExp = intelligenceExp; }
    public int getStrengthExp() { return strengthExp; }
    public void setStrengthExp(int strengthExp) { this.strengthExp = strengthExp; }
    public int getStaminaExp() { return staminaExp; }
    public void setStaminaExp(int staminaExp) { this.staminaExp = staminaExp; }
    public boolean isPassiveMode() { return passiveMode; }
    public void setPassiveMode(boolean passiveMode) { this.passiveMode = passiveMode; }

    public boolean isStun() { return isStun; }
    public void setStun(boolean isStun) { this.isStun = isStun; }
    public boolean isDead() { return isDead; }
    public void setDead(boolean isDead) { this.isDead = isDead; }
    public int getDeadTimeLeft() { return deadTimeLeft; }
    public void setDeadTimeLeft(int deadTimeLeft) { this.deadTimeLeft = deadTimeLeft; }
    public boolean isJailed() { return isJailed; }
    public void setJailed(boolean isJailed) { this.isJailed = isJailed; }
    public int getJailedTimeLeft() { return jailedTimeLeft; }
    public void setJailedTimeLeft(int jailedTimeLeft) { this.jailedTimeLeft = jailedTimeLeft; }
    public boolean isWanted() { return isWanted; }
    public void setWanted(boolean isWanted) { this.isWanted = isWanted; }
    public int getWantedLevel() { return wantedLevel; }
    public void setWantedLevel(int wantedLevel) { this.wantedLevel = wantedLevel; }
    public int getWantedTimeLeft() { return wantedTimeLeft; }
    public void setWantedTimeLeft(int wantedTimeLeft) { this.wantedTimeLeft = wantedTimeLeft; }
    public boolean isOnProbation() { return onProbation; }
    public void setOnProbation(boolean onProbation) { this.onProbation = onProbation; }
    public int getProbationTimeLeft() { return probationTimeLeft; }
    public void setProbationTimeLeft(int probationTimeLeft) { this.probationTimeLeft = probationTimeLeft; }
    public boolean isCuffed() { return isCuffed; }
    public void setCuffed(boolean isCuffed) { this.isCuffed = isCuffed; }
    public int getCuffedTimeLeft() { return cuffedTimeLeft; }
    public void setCuffedTimeLeft(int cuffedTimeLeft) { this.cuffedTimeLeft = cuffedTimeLeft; }

    public int getPunches() { return punches; }
    public void setPunches(int punches) { this.punches = punches; }
    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    public int getHitKills() { return hitKills; }
    public void setHitKills(int hitKills) { this.hitKills = hitKills; }
    public int getGunKills() { return gunKills; }
    public void setGunKills(int gunKills) { this.gunKills = gunKills; }
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public int getCopDeaths() { return copDeaths; }
    public void setCopDeaths(int copDeaths) { this.copDeaths = copDeaths; }
    public int getTimeWorked() { return timeWorked; }
    public void setTimeWorked(int timeWorked) { this.timeWorked = timeWorked; }
    public int getArrests() { return arrests; }
    public void setArrests(int arrests) { this.arrests = arrests; }
    public int getArrested() { return arrested; }
    public void setArrested(int arrested) { this.arrested = arrested; }
    public int getEvasions() { return evasions; }
    public void setEvasions(int evasions) { this.evasions = evasions; }

    public int getBankAccount() { return bankAccount; }
    public void setBankAccount(int bankAccount) { this.bankAccount = bankAccount; }
    public int getBankTarget() { return bankTarget; }
    public void setBankTarget(int bankTarget) { this.bankTarget = bankTarget; }
    public int getBankChequings() { return bankChequings; }
    public void setBankChequings(int bankChequings) { this.bankChequings = bankChequings; }
    public int getBankSavings() { return bankSavings; }
    public void setBankSavings(int bankSavings) { this.bankSavings = bankSavings; }
    public int getWeedBaul() { return weedBaul; }
    public void setWeedBaul(int weedBaul) { this.weedBaul = weedBaul; }

    public int getBasuLvl() { return basuLvl; }
    public void setBasuLvl(int basuLvl) { this.basuLvl = basuLvl; }
    public int getBasuXp() { return basuXp; }
    public void setBasuXp(int basuXp) { this.basuXp = basuXp; }
    public int getHuntPoints() { return huntPoints; }
    public void setHuntPoints(int huntPoints) { this.huntPoints = huntPoints; }
    public String getHuntSkins() { return huntSkins; }
    public void setHuntSkins(String huntSkins) { this.huntSkins = huntSkins; }
    public int getArmLvl() { return armLvl; }
    public void setArmLvl(int armLvl) { this.armLvl = armLvl; }
    public int getArmXp() { return armXp; }
    public void setArmXp(int armXp) { this.armXp = armXp; }
    public int getMecLvl() { return mecLvl; }
    public void setMecLvl(int mecLvl) { this.mecLvl = mecLvl; }
    public int getMecXp() { return mecXp; }
    public void setMecXp(int mecXp) { this.mecXp = mecXp; }
    public int getCamLvl() { return camLvl; }
    public void setCamLvl(int camLvl) { this.camLvl = camLvl; }
    public int getCamXp() { return camXp; }
    public void setCamXp(int camXp) { this.camXp = camXp; }

    public int getLastKilled() { return lastKilled; }
    public void setLastKilled(int lastKilled) { this.lastKilled = lastKilled; }
    public int getMarriedTo() { return marriedTo; }
    public void setMarriedTo(int marriedTo) { this.marriedTo = marriedTo; }
    public int getHijo() { return hijo; }
    public void setHijo(int hijo) { this.hijo = hijo; }

    public int getGangId() { return gangId; }
    public void setGangId(int gangId) { this.gangId = gangId; }
    public int getGangRank() { return gangRank; }
    public void setGangRank(int gangRank) { this.gangRank = gangRank; }
    public int getGangRequest() { return gangRequest; }
    public void setGangRequest(int gangRequest) { this.gangRequest = gangRequest; }
    public int getChangeNameCount() { return changeNameCount; }
    public void setChangeNameCount(int changeNameCount) { this.changeNameCount = changeNameCount; }

    public int getCar() { return car; }
    public void setCar(int car) { this.car = car; }
    public int getCarFuel() { return carFuel; }
    public void setCarFuel(int carFuel) { this.carFuel = carFuel; }
    public int getWeed() { return weed; }
    public void setWeed(int weed) { this.weed = weed; }
    public int getCocaine() { return cocaine; }
    public void setCocaine(int cocaine) { this.cocaine = cocaine; }
    public int getBotiquin() { return botiquin; }
    public void setBotiquin(int botiquin) { this.botiquin = botiquin; }
    public int getHeroina() { return heroina; }
    public void setHeroina(int heroina) { this.heroina = heroina; }
    public int getCaramelos() { return caramelos; }
    public void setCaramelos(int caramelos) { this.caramelos = caramelos; }
    public int getMedicina() { return medicina; }
    public void setMedicina(int medicina) { this.medicina = medicina; }
    public int getCigarette() { return cigarette; }
    public void setCigarette(int cigarette) { this.cigarette = cigarette; }
    public int getPildora() { return pildora; }
    public void setPildora(int pildora) { this.pildora = pildora; }
    public int getDynamite() { return dynamite; }
    public void setDynamite(int dynamite) { this.dynamite = dynamite; }
    public int getWeedmateria() { return weedmateria; }
    public void setWeedmateria(int weedmateria) { this.weedmateria = weedmateria; }

    public String getUnlockedQuests() { return unlockedQuests; }
    public void setUnlockedQuests(String unlockedQuests) { this.unlockedQuests = unlockedQuests; }
    public int getBrawlWins() { return brawlWins; }
    public void setBrawlWins(int brawlWins) { this.brawlWins = brawlWins; }
    public int getCwWins() { return cwWins; }
    public void setCwWins(int cwWins) { this.cwWins = cwWins; }
    public int getMwWins() { return mwWins; }
    public void setMwWins(int mwWins) { this.mwWins = mwWins; }
    public int getSoloQueueWins() { return soloQueueWins; }
    public void setSoloQueueWins(int soloQueueWins) { this.soloQueueWins = soloQueueWins; }
    public boolean isNoob() { return isNoob; }
    public void setNoob(boolean isNoob) { this.isNoob = isNoob; }
    public int getNoobTimeLeft() { return noobTimeLeft; }
    public void setNoobTimeLeft(int noobTimeLeft) { this.noobTimeLeft = noobTimeLeft; }
    public boolean isInmunidadActivada() { return inmunidadActivada; }
    public void setInmunidadActivada(boolean inmunidadActivada) { this.inmunidadActivada = inmunidadActivada; }
    public int getVipBanned() { return vipBanned; }
    public void setVipBanned(int vipBanned) { this.vipBanned = vipBanned; }
    public String getLastCoordinates() { return lastCoordinates; }
    public void setLastCoordinates(String lastCoordinates) { this.lastCoordinates = lastCoordinates; }
}
