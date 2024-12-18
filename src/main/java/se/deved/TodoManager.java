package se.deved;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TodoManager {

    public static void showTodos() {
        try (PreparedStatement selectStatement = Main.connection.prepareStatement("SELECT * FROM todos WHERE user_id = ?")) {
            selectStatement.setInt(1, Main.loggedInUserId);

            try (ResultSet result = selectStatement.executeQuery()) {
                while (result.next()) {
                    displayTodo(result);
                }
            }
        } catch (SQLException exception) {
            System.out.println("Failed to fetch todos from database.");
            return;
        }
    }

    public static void displayTodo(ResultSet result) throws SQLException {
        int id = result.getInt("id");
        String title = result.getString("title");
        Date deadlineDate = result.getDate("deadline_date");
        Date createdDate = result.getDate("created_date");
        boolean completed = result.getBoolean("completed");
        // String username = result.getString("username");

        System.out.println("- (" + id + ") " + title);
        System.out.println("  Deadline: " + deadlineDate.toString());
        System.out.println("  Created: " + createdDate.toString());
        System.out.println("  Completed: " + (completed ? "Yes" : "No"));
        // System.out.println("  User: " + username);
    }

    public static void showTodo() {
        System.out.print("Enter an id: ");
        int todoId = Main.scanner.nextInt();
        Main.scanner.nextLine();

        try (PreparedStatement selectStatement = Main.connection.prepareStatement("SELECT * FROM todos WHERE id = ?")) {
            selectStatement.setInt(1, todoId);
            try (ResultSet result = selectStatement.executeQuery()) {
                if (result.next()) {
                    displayTodo(result);
                } else {
                    System.out.println("There is no todo with id " + todoId);
                }
            }
        } catch (SQLException exception) {
            System.out.println("Failed to fetch todo from database.");
            return;
        }
    }

    public static void createTodo() {
        System.out.print("Enter a title: ");
        String title = Main.scanner.nextLine();

        System.out.print("Enter a deadline date (YYYY-MM-DD): ");
        String deadlineString = Main.scanner.nextLine();

        Date deadlineDate;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            java.util.Date javaDate = dateFormat.parse(deadlineString);
            deadlineDate = new Date(javaDate.getTime());
        } catch (ParseException exception) {
            System.out.println("Could not parse date. Did you enter the correct format?");
            return;
        }

        try (PreparedStatement insertTodo = Main.connection.prepareStatement("INSERT INTO todos (title, deadline_date, user_id) VALUES (?, ?, ?)")) {
            insertTodo.setString(1, title);
            insertTodo.setDate(2, deadlineDate);
            insertTodo.setInt(3, Main.loggedInUserId);

            if (insertTodo.executeUpdate() == 0) {
                System.out.println("Nothing was inserted, perhaps there was a conflict?");
                return;
            }
        } catch (SQLException exception) {
            System.out.println("Failed to save to database.");
            return;
        }

        System.out.println("Saved todo to database.");
    }

    public static void deleteTodo() {
        System.out.print("Enter an id: ");
        int todoId = Main.scanner.nextInt();
        Main.scanner.nextLine();

        try (PreparedStatement deleteStatement = Main.connection.prepareStatement("DELETE FROM todos WHERE id = ?")) {
            deleteStatement.setInt(1, todoId);

            if (deleteStatement.executeUpdate() == 0) {
                System.out.println("There is no todo with id " + todoId);
                return;
            }
        } catch (SQLException exception) {
            System.out.println("Failed to delete todo.");
            return;
        }

        System.out.println("Deleted todo from database.");
    }

    public static void completeTodo() {
        System.out.print("Enter an id: ");
        int todoId = Main.scanner.nextInt();
        Main.scanner.nextLine();

        try (PreparedStatement updateStatement = Main.connection.prepareStatement("UPDATE todos SET completed = true WHERE id = ?")) {
            updateStatement.setInt(1, todoId);

            if (updateStatement.executeUpdate() == 0) {
                System.out.println("There is no todo with id " + todoId);
                return;
            }
        } catch (SQLException exception) {
            System.out.println("Failed to update todo.");
            return;
        }

        System.out.println("Marked todo as completed.");
    }
}
