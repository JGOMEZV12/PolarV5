package com.eu.habbo.habbohotel.roleplay.misc;

import com.eu.habbo.Emulator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToDoManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToDoManager.class);

    public static final ConcurrentHashMap<Integer, ToDo> toDoList = new ConcurrentHashMap<>();

    public static void initialize() {
        toDoList.clear();

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT * FROM rp_todo_list");
                ResultSet set = statement.executeQuery()) {

            while (set.next()) {
                int id = set.getInt("id");
                int addedBy = set.getInt("added_by");
                double timeStamp = set.getDouble("timestamp");
                String string = set.getString("todo");

                ToDo toDo = new ToDo(id, string, addedBy, timeStamp);
                toDoList.put(id, toDo);
            }

            LOGGER.info("ToDoManager -> Loaded {} ToDo items.", toDoList.size());
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize ToDoManager", e);
        }
    }

    public static void addNewTodo(ToDo New) {
        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement = connection.prepareStatement(
                        "INSERT INTO `rp_todo_list` (added_by, todo, timestamp) VALUES (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, New.addedBy);
            statement.setString(2, New.string);
            statement.setDouble(3, New.timeStamp);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    New.id = generatedKeys.getInt(1);
                }
            }

            if (!toDoList.containsKey(New.id)) {
                toDoList.put(New.id, New);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to add new ToDo item", e);
        }
    }

    public static void deleteToDo(int id) {
        toDoList.remove(id);

        try (Connection connection = Emulator.getDatabase().getDataSource().getConnection();
                PreparedStatement statement =
                        connection.prepareStatement("DELETE FROM `rp_todo_list` WHERE `id` = ? LIMIT 1")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("Failed to delete ToDo item ID: {}", id, e);
        }
    }

    public static class ToDo {
        public int id;
        public String string;
        public int addedBy;
        public double timeStamp;

        public ToDo(int id, String string, int addedBy, double timeStamp) {
            this.id = id;
            this.string = string;
            this.addedBy = addedBy;
            this.timeStamp = timeStamp;
        }
    }
}
