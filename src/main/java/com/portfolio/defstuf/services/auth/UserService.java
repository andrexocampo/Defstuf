package com.portfolio.defstuf.services.auth;

import com.portfolio.defstuf.models.user.User;
import com.portfolio.defstuf.repository.user.UserRepository;

import java.sql.SQLException;

/**
 * Service for user-related operations
 * Handles user data loading and management
 */
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService() {
        this.userRepository = new UserRepository();
    }
    
    /**
     * Loads user data by ID (FR-01.5)
     * This will be extended to load definitions, statistics, etc.
     * 
     * @param userId The user ID
     * @return The user with all loaded data
     * @throws SQLException If database error occurs
     */
    public User loadUserData(Long userId) throws SQLException {
        User user = userRepository.findById(userId);
        
        if (user != null) {
            // TODO: Load user's definitions
            // TODO: Load user's statistics
            // TODO: Load other user-related data
        }
        
        return user;
    }
    
    /**
     * Gets a user by ID
     * 
     * @param userId The user ID
     * @return The user, or null if not found
     * @throws SQLException If database error occurs
     */
    public User getUserById(Long userId) throws SQLException {
        return userRepository.findById(userId);
    }
}

