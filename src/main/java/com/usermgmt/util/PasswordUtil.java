package com.usermgmt.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    // Hashes a plain text password (e.g., "secret123" -> "$2a$10$...")
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    // Checks if a plain text password matches the stored hash
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            return false; // Invalid hash format
        }
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}