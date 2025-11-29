package com.portfolio.defstuf.session;

import com.portfolio.defstuf.models.user.User;

/**
 * Singleton class for managing the current user session
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    /**
     * Private constructor for Singleton pattern
     */
    private SessionManager() {
        // Private constructor
    }
    
    /**
     * Gets the singleton instance
     * 
     * @return The SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Sets the current logged-in user
     * 
     * @param user The user to set as current
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    /**
     * Gets the current logged-in user
     * 
     * @return The current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is currently logged in
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Logs out the current user
     */
    public void logout() {
        this.currentUser = null;
    }
    
    /**
     * Gets the current user ID
     * 
     * @return The user ID, or null if no user is logged in
     */
    public Long getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
}

