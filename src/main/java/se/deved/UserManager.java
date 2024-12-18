package se.deved;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {

    public static void registerUser() {
        System.out.print("Enter a username: ");
        String username = Main.scanner.nextLine();

        System.out.print("Enter a password: ");
        String password = Main.scanner.nextLine();

        try (PreparedStatement insertUser = Main.connection.prepareStatement("INSERT INTO users (name, password) VALUES (?, ?)")) {
            insertUser.setString(1, username);
            insertUser.setString(2, password);

            if (insertUser.executeUpdate() == 0) {
                System.out.println("Nothing was inserted, perhaps there was a conflict?");
                return;
            }
        } catch (SQLException exception) {
            System.out.println("Failed to save to database.");
            return;
        }

        System.out.println("Registered user! Try logging in.");
    }

    public static boolean loginUser() {
        System.out.print("Enter a username: ");
        String username = Main.scanner.nextLine();

        System.out.print("Enter a password: ");
        String password = Main.scanner.nextLine();

        try (PreparedStatement selectUser = Main.connection.prepareStatement("SELECT * FROM users WHERE name = ? AND password = ?")) {
            selectUser.setString(1, username);
            selectUser.setString(2, password);

            try (ResultSet result = selectUser.executeQuery()) {
                if (result.next()) {
                    // Markera användaren som inloggad.
                    Main.loggedInUserId = result.getInt("id");
                    System.out.println("You have logged in!");
                    return true;
                }
            }
        } catch (SQLException exception) {
            System.out.println("Failed to save to database.");
        }

        System.out.println("Wrong username or password.");
        return false;
    }
}
