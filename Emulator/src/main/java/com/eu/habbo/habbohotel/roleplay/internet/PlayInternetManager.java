package com.eu.habbo.habbohotel.roleplay.internet;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayInternetManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayInternetManager.class);

    public static final ConcurrentHashMap<String, PlayInternet> webPages = new ConcurrentHashMap<>();

    public static void initialize() {
        webPages.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_internet");
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                int id = set.getInt("id");
                String url = set.getString("url");
                String name = set.getString("name");
                String description = set.getString("description");
                int authorId = set.getInt("author_id");
                String code = set.getString("code");

                PlayInternet wp = new PlayInternet(id, url, name, description, authorId, code);
                webPages.put(url, wp);
            }

            LOGGER.info("PlayInternetManager -> Loaded {} Web Page(s) from PlayInternet.", webPages.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize PlayInternetManager", e);
        }
    }

    public static int tryToGetWebPage(String url, PlayInternet[] outWp) {
        if (url == null) return 0;
        PlayInternet wp = webPages.get(url);
        if (wp != null) {
            if (outWp != null && outWp.length > 0) {
                outWp[0] = wp;
            }
            return wp.getId();
        }
        return 0;
    }
}
