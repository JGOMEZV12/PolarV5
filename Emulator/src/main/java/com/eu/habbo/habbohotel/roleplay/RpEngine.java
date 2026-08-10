package com.eu.habbo.habbohotel.roleplay;

import com.eu.habbo.core.ConfigurationManager;
import com.eu.habbo.database.Database;
import com.eu.habbo.habbohotel.GameEnvironment;
import com.eu.habbo.networking.gameserver.GameServer;
import com.eu.habbo.threading.ThreadPooling;
import java.lang.reflect.Method;

public class RpEngine {
    public static GameServer getGameServer() {
        try {
            Class<?> clazz = Class.forName("com.eu.habbo.Emulator");
            Method method = clazz.getMethod("getGameServer");
            return (GameServer) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static GameEnvironment getGameEnvironment() {
        try {
            Class<?> clazz = Class.forName("com.eu.habbo.Emulator");
            Method method = clazz.getMethod("getGameEnvironment");
            return (GameEnvironment) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ThreadPooling getThreading() {
        try {
            Class<?> clazz = Class.forName("com.eu.habbo.Emulator");
            Method method = clazz.getMethod("getThreading");
            return (ThreadPooling) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ConfigurationManager getConfig() {
        try {
            Class<?> clazz = Class.forName("com.eu.habbo.Emulator");
            Method method = clazz.getMethod("getConfig");
            return (ConfigurationManager) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Database getDatabase() {
        try {
            Class<?> clazz = Class.forName("com.eu.habbo.Emulator");
            Method method = clazz.getMethod("getDatabase");
            return (Database) method.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
