package com.eu.habbo.habbohotel.roleplay.gangs;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Portado desde: Polar RP/HabboHotel/Groups/GroupRank.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Representa un rango de banda con sus límites y comandos asignados.
 */
public class GangRank {
    private final int gangId;
    private final int rank;
    private final String name;
    private final int limit;
    private final Set<String> commands;

    public GangRank(ResultSet set) throws SQLException {
        this.gangId = set.getInt("gang_id");
        this.rank = set.getInt("rank");
        this.name = set.getString("name");
        this.limit = set.getInt("limit");
        this.commands = new HashSet<>();
        String cmds = set.getString("commands");
        if (cmds != null && !cmds.isEmpty()) {
            commands.addAll(Arrays.asList(cmds.toLowerCase().split(",")));
        }
    }

    public int getGangId() {
        return gangId;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getLimit() {
        return limit;
    }

    public Set<String> getCommands() {
        return commands;
    }

    public boolean hasCommand(String command) {
        return commands.contains(command.toLowerCase());
    }
}
