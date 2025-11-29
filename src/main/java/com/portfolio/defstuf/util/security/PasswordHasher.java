package com.portfolio.defstuf.util.security;

/**
 * Utility class for password hashing and verification
 * Uses BCrypt for secure password hashing
 * 
 * Note: jbcrypt library is loaded as an automatic module
 */
public class PasswordHasher {
    
    // Number of rounds for BCrypt hashing (higher = more secure but slower)
    private static final int ROUNDS = 12;
    
    /**
     * Hashes a plain text password using BCrypt
     * 
     * @param plainPassword The password in plain text
     * @return The hashed password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        // Use reflection to access BCrypt to avoid module system issues
        try {
            Class<?> bcryptClass = Class.forName("org.mindrot.jbcrypt.BCrypt");
            java.lang.reflect.Method hashpwMethod = bcryptClass.getMethod("hashpw", String.class, String.class);
            java.lang.reflect.Method gensaltMethod = bcryptClass.getMethod("gensalt", int.class);
            
            Object salt = gensaltMethod.invoke(null, ROUNDS);
            return (String) hashpwMethod.invoke(null, plainPassword, salt);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifies if a plain password matches the hashed password
     * 
     * @param plainPassword The password to verify
     * @param hashedPassword The stored hash to compare against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        
        try {
            Class<?> bcryptClass = Class.forName("org.mindrot.jbcrypt.BCrypt");
            java.lang.reflect.Method checkpwMethod = bcryptClass.getMethod("checkpw", String.class, String.class);
            
            Object result = checkpwMethod.invoke(null, plainPassword, hashedPassword);
            return Boolean.TRUE.equals(result);
        } catch (Exception e) {
            return false;
        }
    }
}
