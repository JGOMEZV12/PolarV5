package com.eu.habbo.habbohotel.roleplay.farming;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlantSatchel {
    private int blueStarflowers;
    private int yellowStarflowers;
    private int pinkDahlias;
    private int yellowPlumerias;
    private int pinkPrimroses;
    private int bluePrimroses;
    private int yellowPrimroses;
    private int yellowDahlias;
    private int bluePlumerias;
    private int pinkPlumerias;
    private int redStarflowers;
    private int blueDahlias;

    public PlantSatchel(ResultSet row) throws SQLException {
        this.blueStarflowers = parseVal(row.getString("blue_starflower"), 1);
        this.yellowStarflowers = parseVal(row.getString("yellow_starflower"), 1);
        this.pinkDahlias = parseVal(row.getString("pink_dahlia"), 1);
        this.yellowPlumerias = parseVal(row.getString("yellow_plumeria"), 1);
        this.pinkPrimroses = parseVal(row.getString("pink_primrose"), 1);
        this.bluePrimroses = parseVal(row.getString("blue_primrose"), 1);
        this.yellowPrimroses = parseVal(row.getString("yellow_primrose"), 1);
        this.yellowDahlias = parseVal(row.getString("yellow_dahlia"), 1);
        this.bluePlumerias = parseVal(row.getString("blue_plumeria"), 1);
        this.pinkPlumerias = parseVal(row.getString("pink_plumeria"), 1);
        this.redStarflowers = parseVal(row.getString("red_starflower"), 1);
        this.blueDahlias = parseVal(row.getString("blue_dahlia"), 1);
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
    public int getBlueStarflowers() {
        return blueStarflowers;
    }

    public void setBlueStarflowers(int blueStarflowers) {
        this.blueStarflowers = blueStarflowers;
    }

    public int getYellowStarflowers() {
        return yellowStarflowers;
    }

    public void setYellowStarflowers(int yellowStarflowers) {
        this.yellowStarflowers = yellowStarflowers;
    }

    public int getPinkDahlias() {
        return pinkDahlias;
    }

    public void setPinkDahlias(int pinkDahlias) {
        this.pinkDahlias = pinkDahlias;
    }

    public int getYellowPlumerias() {
        return yellowPlumerias;
    }

    public void setYellowPlumerias(int yellowPlumerias) {
        this.yellowPlumerias = yellowPlumerias;
    }

    public int getPinkPrimroses() {
        return pinkPrimroses;
    }

    public void setPinkPrimroses(int pinkPrimroses) {
        this.pinkPrimroses = pinkPrimroses;
    }

    public int getBluePrimroses() {
        return bluePrimroses;
    }

    public void setBluePrimroses(int bluePrimroses) {
        this.bluePrimroses = bluePrimroses;
    }

    public int getYellowPrimroses() {
        return yellowPrimroses;
    }

    public void setYellowPrimroses(int yellowPrimroses) {
        this.yellowPrimroses = yellowPrimroses;
    }

    public int getYellowDahlias() {
        return yellowDahlias;
    }

    public void setYellowDahlias(int yellowDahlias) {
        this.yellowDahlias = yellowDahlias;
    }

    public int getBluePlumerias() {
        return bluePlumerias;
    }

    public void setBluePlumerias(int bluePlumerias) {
        this.bluePlumerias = bluePlumerias;
    }

    public int getPinkPlumerias() {
        return pinkPlumerias;
    }

    public void setPinkPlumerias(int pinkPlumerias) {
        this.pinkPlumerias = pinkPlumerias;
    }

    public int getRedStarflowers() {
        return redStarflowers;
    }

    public void setRedStarflowers(int redStarflowers) {
        this.redStarflowers = redStarflowers;
    }

    public int getBlueDahlias() {
        return blueDahlias;
    }

    public void setBlueDahlias(int blueDahlias) {
        this.blueDahlias = blueDahlias;
    }
}
