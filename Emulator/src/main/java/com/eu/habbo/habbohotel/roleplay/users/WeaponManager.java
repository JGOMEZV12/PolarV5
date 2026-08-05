package com.eu.habbo.habbohotel.roleplay.users;

import java.util.HashMap;
import java.util.Map;

/**
 * Portado desde: Polar RP/HabboRoleplay/Weapons/WeaponManager.cs
 * Repositorio origen: https://github.com/JGOMEZV12/EmuPolarV4
 * Repositorio destino: https://github.com/JGOMEZV12/PolarV5
 * Descripción: Mantiene y expone el catálogo de armas disponibles en el sistema de Roleplay.
 */
public class WeaponManager {

    private static final Map<String, Weapon> weapons = new HashMap<>();

    static {
        weapons.put("pistola", new Weapon("pistola", "Pistola 9mm", 15, 25, 6, 15, 4, 3, 1500));
        weapons.put("electrica", new Weapon("electrica", "Pistola Eléctrica", 0, 0, 3, 1, 37, 2, 800));
        weapons.put("ametralladora", new Weapon("ametralladora", "Ametralladora Uzi", 25, 35, 8, 30, 5, 4, 3500));
        weapons.put("escopeta", new Weapon("escopeta", "Escopeta Recortada", 35, 50, 4, 6, 6, 5, 5000));
    }

    public static Weapon getWeapon(String name) {
        if (name == null) return null;
        return weapons.get(name.toLowerCase());
    }

    public static Map<String, Weapon> getWeapons() {
        return weapons;
    }
}
