package se.deved;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

public class PasswordHasher {

    public static String hashPassword(String password, String salt) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (Exception ignored) {
            return null;
        }

        digest.update(salt.getBytes());

        byte[] hash = digest.digest(password.getBytes());
        return HexFormat.of().formatHex(hash);
    }

    public static String generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);

        return HexFormat.of().formatHex(salt);
    }
}
