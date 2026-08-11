package com.eu.habbo.habbohotel.roleplay.farming;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SeedSatchel {
    private int blueStarflowerSeeds;
    private int yellowStarflowerSeeds;
    private int pinkDahliaSeeds;
    private int yellowPlumeriaSeeds;
    private int pinkPrimroseSeeds;
    private int bluePrimroseSeeds;
    private int yellowPrimroseSeeds;
    private int yellowDahliaSeeds;
    private int bluePlumeriaSeeds;
    private int pinkPlumeriaSeeds;
    private int redStarflowerSeeds;
    private int blueDahliaSeeds;

    public SeedSatchel(ResultSet row) throws SQLException {
        this.blueStarflowerSeeds = parseVal(row.getString("blue_starflower"), 0);
        this.yellowStarflowerSeeds = parseVal(row.getString("yellow_starflower"), 0);
        this.pinkDahliaSeeds = parseVal(row.getString("pink_dahlia"), 0);
        this.yellowPlumeriaSeeds = parseVal(row.getString("yellow_plumeria"), 0);
        this.pinkPrimroseSeeds = parseVal(row.getString("pink_primrose"), 0);
        this.bluePrimroseSeeds = parseVal(row.getString("blue_primrose"), 0);
        this.yellowPrimroseSeeds = parseVal(row.getString("yellow_primrose"), 0);
        this.yellowDahliaSeeds = parseVal(row.getString("yellow_dahlia"), 0);
        this.bluePlumeriaSeeds = parseVal(row.getString("blue_plumeria"), 0);
        this.pinkPlumeriaSeeds = parseVal(row.getString("pink_plumeria"), 0);
        this.redStarflowerSeeds = parseVal(row.getString("red_starflower"), 0);
        this.blueDahliaSeeds = parseVal(row.getString("blue_dahlia"), 0);
    }

    private static int parseVal(String col, int index) {
        if (col == null || !col.contains(":")) return 0;
        try {
            return Integer.parseInt(col.split(":")[index]);
        } catch (Exception e) {
            return 0;
        }
    }

    // Getters and Setters
    public int getBlueStarflowerSeeds() { return blueStarflowerSeeds; }
    public void setBlueStarflowerSeeds(int blueStarflowerSeeds) { this.blueStarflowerSeeds = blueStarflowerSeeds; }

    public int getYellowStarflowerSeeds() { return yellowStarflowerSeeds; }
    public void setYellowStarflowerSeeds(int yellowStarflowerSeeds) { this.yellowStarflowerSeeds = yellowStarflowerSeeds; }

    public int getPinkDahliaSeeds() { return pinkDahliaSeeds; }
    public void setPinkDahliaSeeds(int pinkDahliaSeeds) { this.pinkDahliaSeeds = pinkDahliaSeeds; }

    public int getYellowPlumeriaSeeds() { return yellowPlumeriaSeeds; }
    public void setYellowPlumeriaSeeds(int yellowPlumeriaSeeds) { this.yellowPlumeriaSeeds = yellowPlumeriaSeeds; }

    public int getPinkPrimroseSeeds() { return pinkPrimroseSeeds; }
    public void setPinkPrimroseSeeds(int pinkPrimroseSeeds) { this.pinkPrimroseSeeds = pinkPrimroseSeeds; }

    public int getBluePrimroseSeeds() { return bluePrimroseSeeds; }
    public void setBluePrimroseSeeds(int bluePrimroseSeeds) { this.bluePrimroseSeeds = bluePrimroseSeeds; }

    public int getYellowPrimroseSeeds() { return yellowPrimroseSeeds; }
    public void setYellowPrimroseSeeds(int yellowPrimroseSeeds) { this.yellowPrimroseSeeds = yellowPrimroseSeeds; }

    public int getYellowDahliaSeeds() { return yellowDahliaSeeds; }
    public void setYellowDahliaSeeds(int yellowDahliaSeeds) { this.yellowDahliaSeeds = yellowDahliaSeeds; }

    public int getBluePlumeriaSeeds() { return bluePlumeriaSeeds; }
    public void setBluePlumeriaSeeds(int bluePlumeriaSeeds) { this.bluePlumeriaSeeds = bluePlumeriaSeeds; }

    public int getPinkPlumeriaSeeds() { return pinkPlumeriaSeeds; }
    public void setPinkPlumeriaSeeds(int pinkPlumeriaSeeds) { this.pinkPlumeriaSeeds = pinkPlumeriaSeeds; }

    public int getRedStarflowerSeeds() { return redStarflowerSeeds; }
    public void setRedStarflowerSeeds(int redStarflowerSeeds) { this.redStarflowerSeeds = redStarflowerSeeds; }

    public int getBlueDahliaSeeds() { return blueDahliaSeeds; }
    public void setBlueDahliaSeeds(int blueDahliaSeeds) { this.blueDahliaSeeds = blueDahliaSeeds; }
}
