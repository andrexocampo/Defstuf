package com.portfolio.defstuf.repository.user;

import com.portfolio.defstuf.models.user.User;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Repository for User entity database operations
 */
public class UserRepository {
    
    /**
     * Finds a user by email
     * 
     * @param email The email to search for
     * @return User object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, created_at FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds a user by ID
     * 
     * @param id The user ID
     * @return User object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public User findById(Long id) throws SQLException {
        String sql = "SELECT id, name, email, password_hash, created_at FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Saves a new user to the database
     * 
     * @param user The user to save
     * @return The saved user with generated ID
     * @throws SQLException If database error occurs
     */
    public User save(User user) throws SQLException {
        String sql = "INSERT INTO users (name, email, password_hash, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPasswordHash());
            stmt.setTimestamp(4, Timestamp.valueOf(user.getCreatedAt() != null ? 
                    user.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }
        return user;
    }
    
    /**
     * Checks if an email already exists in the database
     * 
     * @param email The email to check
     * @return true if email exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean emailExists(String email) throws SQLException {
        return findByEmail(email) != null;
    }
    
    /**
     * Maps a ResultSet row to a User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return user;
    }
}

