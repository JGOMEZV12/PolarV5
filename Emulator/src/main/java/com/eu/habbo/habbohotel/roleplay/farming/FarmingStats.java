package com.eu.habbo.habbohotel.roleplay.farming;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FarmingStats {
    private int level;
    private int exp;
    private boolean hasSeedSatchel;
    private boolean hasPlantSatchel;
    private SeedSatchel seedSatchel;
    private PlantSatchel plantSatchel;

    public FarmingStats(ResultSet row) throws SQLException {
        this.level = row.getInt("level");
        this.exp = row.getInt("exp");
        this.hasSeedSatchel = row.getString("has_seed_satchel").equals("1");
        this.hasPlantSatchel = row.getString("has_plant_satchel").equals("1");
        this.seedSatchel = new SeedSatchel(row);
        this.plantSatchel = new PlantSatchel(row);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public boolean isHasSeedSatchel() {
        return hasSeedSatchel;
    }

    public void setHasSeedSatchel(boolean hasSeedSatchel) {
        this.hasSeedSatchel = hasSeedSatchel;
    }

    public boolean isHasPlantSatchel() {
        return hasPlantSatchel;
    }

    public void setHasPlantSatchel(boolean hasPlantSatchel) {
        this.hasPlantSatchel = hasPlantSatchel;
    }

    public SeedSatchel getSeedSatchel() {
        return seedSatchel;
    }

    public void setSeedSatchel(SeedSatchel seedSatchel) {
        this.seedSatchel = seedSatchel;
    }

    public PlantSatchel getPlantSatchel() {
        return plantSatchel;
    }

    public void setPlantSatchel(PlantSatchel plantSatchel) {
        this.plantSatchel = plantSatchel;
    }
}
