package com.eu.habbo.habbohotel.roleplay.combat;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CombatManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CombatManager.class);

    public static final ConcurrentHashMap<String, ICombat> combatTypes = new ConcurrentHashMap<>();

    public static void initialize() {
        combatTypes.clear();
        combatTypes.put("fist", new Fist());
        combatTypes.put("gun", new Gun());

        LOGGER.info("CombatManager -> CARGADO ({} tipos de combate)", combatTypes.size());
    }

    public static ICombat getCombatType(String type) {
        if (type == null) return null;
        return combatTypes.get(type.toLowerCase());
    }
}
