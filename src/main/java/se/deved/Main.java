package se.deved;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HexFormat;
import java.util.Scanner;

public class Main {
    public static Scanner scanner = new Scanner(System.in);
    public static Connection connection;
    public static int loggedInUserId = -1;

    static void closeConnection() {
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
    }

    public static void main(String[] args) {

        try {
            connectToDatabase();
        } catch (SQLException ignored) {
            System.out.println("Failed to connect to database. Try again later.");
            return;
        }

        try {
            setupDatabaseTables();
        } catch (SQLException ignored) {
            System.out.println("Failed to create 'todo' table. Fix the SQL statement.");
            closeConnection();
            return;
        }

        try {
            runApp();
        } catch (Exception exception) {
            System.out.println("Something went wrong.");
            closeConnection();
            return;
        }

        closeConnection();
    }

    public static void runApp() {
        System.out.println("Welcome to the todo application!");
        System.out.println("register - Create a user account.");
        System.out.println("login    - Login to the application.");

        String commandName = scanner.nextLine();
        boolean loggedIn = false;
        while (!commandName.equalsIgnoreCase("exit") && !loggedIn) {
            if (commandName.equalsIgnoreCase("register")) {
                UserManager.registerUser();
            } else if (commandName.equalsIgnoreCase("login")) {
                if (UserManager.loginUser()) {
                    break;
                }
            } else {
                System.out.println("There is no such command, please try again.");
            }

            commandName = scanner.nextLine();
        }

        System.out.println("create-todo    - Create and save a todo to database.");
        System.out.println("show-todos     - Fetch and show all saved todos.");
        System.out.println("view-todo      - Fetch and show a specific todo.");
        System.out.println("delete-todo    - Delete a previously saved todo.");
        System.out.println("complete-todo  - Mark a todo as completed.");

        commandName = scanner.nextLine();
        while (!commandName.equalsIgnoreCase("exit")) {
            if (commandName.equalsIgnoreCase("create-todo")) {
                TodoManager.createTodo();
            } else if (commandName.equalsIgnoreCase("show-todos")) {
                TodoManager.showTodos();
            } else if (commandName.equalsIgnoreCase("view-todo")) {
                TodoManager.showTodo();
            } else if (commandName.equalsIgnoreCase("delete-todo")) {
                TodoManager.deleteTodo();
            } else if (commandName.equalsIgnoreCase("complete-todo")) {
                TodoManager.completeTodo();
            } else {
                System.out.println("There is no such command, please try again.");
            }

            commandName = scanner.nextLine();
        }
    }

    public static void setupDatabaseTables() throws SQLException {
        // Gammalt sätt att hantera resource-closing
        Statement createTablesStatement = null;
        try {
            createTablesStatement = connection.createStatement();

            createTablesStatement.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "password TEXT NOT NULL)");

            createTablesStatement.execute("CREATE TABLE IF NOT EXISTS todos (" +
                    "id SERIAL PRIMARY KEY," +
                    "title TEXT NOT NULL," +
                    "deadline_date DATE," +
                    "created_date TIMESTAMP NOT NULL DEFAULT current_timestamp," +
                    "user_id INT REFERENCES users(id)," +
                    "completed BOOLEAN NOT NULL DEFAULT false)");
        } catch (SQLException exception) {
            throw exception;
        } finally {
            try {
                if (createTablesStatement != null) {
                    createTablesStatement.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public static void connectToDatabase() throws SQLException {
        //String connectionString = System.getenv("APP_CONNECTION_STRING");
        String connectionString = "jdbc:postgresql://localhost/todo?user=postgres&password=password";
        connection = DriverManager.getConnection(connectionString);
    }
}