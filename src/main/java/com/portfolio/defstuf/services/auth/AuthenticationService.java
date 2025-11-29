package com.portfolio.defstuf.services.auth;

import com.portfolio.defstuf.models.user.User;
import com.portfolio.defstuf.repository.user.UserRepository;
import com.portfolio.defstuf.util.security.PasswordHasher;

import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Service for authentication operations
 * Handles user registration and login
 */
public class AuthenticationService {
    
    private final UserRepository userRepository;
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }
    
    /**
     * Registers a new user (FR-01.1, FR-01.2, FR-01.4)
     * 
     * @param name User's name
     * @param email User's email
     * @param password User's plain password (will be hashed)
     * @return The created user
     * @throws AuthenticationException If registration fails
     */
    public User register(String name, String email, String password) throws AuthenticationException {
        // Validate input
        validateRegistrationInput(name, email, password);
        
        // Check if email already exists (FR-01.2)
        try {
            if (userRepository.emailExists(email)) {
                throw new AuthenticationException("Email is already in use");
            }
        } catch (SQLException e) {
            throw new AuthenticationException("Error checking email availability: " + e.getMessage());
        }
        
        // Hash password securely (FR-01.4)
        String passwordHash = PasswordHasher.hashPassword(password);
        
        // Create user
        User user = new User(name, email, passwordHash);
        
        // Save to database
        try {
            return userRepository.save(user);
        } catch (SQLException e) {
            throw new AuthenticationException("Error creating user: " + e.getMessage());
        }
    }
    
    /**
     * Authenticates a user login (FR-01.3)
     * 
     * @param email User's email
     * @param password User's plain password
     * @return The authenticated user
     * @throws AuthenticationException If authentication fails
     */
    public User login(String email, String password) throws AuthenticationException {
        // Validate input
        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationException("Email is required");
        }
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password is required");
        }
        
        // Find user by email
        User user;
        try {
            user = userRepository.findByEmail(email.trim());
        } catch (SQLException e) {
            throw new AuthenticationException("Error during login: " + e.getMessage());
        }
        
        // Check if user exists
        if (user == null) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        // Verify password
        if (!PasswordHasher.verifyPassword(password, user.getPasswordHash())) {
            throw new AuthenticationException("Invalid email or password");
        }
        
        return user;
    }
    
    /**
     * Validates email format
     * 
     * @param email Email to validate
     * @return true if email is valid
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
    
    /**
     * Validates registration input
     */
    private void validateRegistrationInput(String name, String email, String password) throws AuthenticationException {
        if (name == null || name.trim().isEmpty()) {
            throw new AuthenticationException("Name is required");
        }
        
        if (email == null || email.trim().isEmpty()) {
            throw new AuthenticationException("Email is required");
        }
        
        if (!isValidEmail(email)) {
            throw new AuthenticationException("Invalid email format");
        }
        
        if (password == null || password.isEmpty()) {
            throw new AuthenticationException("Password is required");
        }
        
        if (password.length() < 6) {
            throw new AuthenticationException("Password must be at least 6 characters long");
        }
    }
    
    /**
     * Custom exception for authentication errors
     */
    public static class AuthenticationException extends Exception {
        public AuthenticationException(String message) {
            super(message);
        }
    }
}

