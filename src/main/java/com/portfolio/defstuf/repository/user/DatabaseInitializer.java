package com.portfolio.defstuf.repository.user;

import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility class to initialize database tables
 */
public class DatabaseInitializer {
    
    /**
     * Creates the users table if it doesn't exist
     * 
     * @throws Exception If database error occurs
     */
    public static void createUsersTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255) NOT NULL, " +
                     "email VARCHAR(255) NOT NULL UNIQUE, " +
                     "password_hash VARCHAR(255) NOT NULL, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "INDEX idx_email (email)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("Users table created successfully or already exists");
        }
    }
    
    /**
     * Initializes all database tables
     * Call this method when the application starts
     */
    public static void initializeDatabase() {
        try {
            createUsersTable();
            System.out.println("Database initialization completed");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

