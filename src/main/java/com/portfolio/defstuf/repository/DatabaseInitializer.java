package com.portfolio.defstuf.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Utility class to initialize database tables
 * Creates all tables in the correct order to respect foreign key dependencies
 */
public class DatabaseInitializer {
    
    /**
     * Creates the users table if it doesn't exist
     * FR-01: User Management
     */
    public static void createUsersTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255) NOT NULL, " +
                     "email VARCHAR(255) NOT NULL UNIQUE, " +
                     "password_hash VARCHAR(255) NOT NULL, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Users table");
    }
    
    /**
     * Creates the statistics_type table (reference table)
     */
    public static void createStatisticsTypeTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS statistics_type (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255) NOT NULL UNIQUE, " +
                     "unit VARCHAR(50), " +
                     "is_numeric BOOLEAN DEFAULT TRUE" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Statistics type table");
    }
    
    /**
     * Creates the user_statistics table
     */
    public static void createUserStatisticsTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS user_statistics (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "user_id BIGINT NOT NULL, " +
                     "statistics_type_id BIGINT NOT NULL, " +
                     "statistics_name VARCHAR(255), " +
                     "statistics_value DECIMAL(15, 4) NOT NULL, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                     "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                     "FOREIGN KEY (statistics_type_id) REFERENCES statistics_type(id) ON DELETE RESTRICT, " +
                     "UNIQUE KEY uk_user_statistics_type (user_id, statistics_type_id), " +
                     "INDEX idx_statistics_type (statistics_type_id)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "User statistics table");
    }
    
    /**
     * Creates the note_type table (reference table)
     */
    public static void createNoteTypeTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS note_type (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255) NOT NULL UNIQUE" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Note type table");
    }
    
    /**
     * Creates the areas table (reference table with created_at)
     */
    public static void createAreasTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS areas (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255) NOT NULL, " +
                     "code VARCHAR(50) UNIQUE, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "INDEX idx_name (name)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Areas table");
    }
    
    /**
     * Creates the sources table (reference table)
     */
    public static void createSourcesTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS sources (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "name VARCHAR(255) NOT NULL, " +
                     "code VARCHAR(50) UNIQUE, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "INDEX idx_name (name)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Sources table");
    }
    
    /**
     * Creates default sources if they don't exist
     */
    public static void createDefaultSources() throws Exception {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Insert "personal" source if it doesn't exist
            String sql = "INSERT IGNORE INTO sources (name, code) VALUES ('Personal', 'personal')";
            stmt.execute(sql);
            System.out.println("✓ Default sources created or already exist");
        }
    }
    
    /**
     * Creates the notes table with user_id for ownership
     */
    public static void createNotesTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS notes (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "user_id BIGINT NOT NULL, " +
                     "title VARCHAR(255) NOT NULL, " +
                     "source_id BIGINT, " +
                     "description TEXT, " +
                     "area_id BIGINT, " +
                     "note_type_id BIGINT, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
                     "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                     "FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE SET NULL, " +
                     "FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE SET NULL, " +
                     "FOREIGN KEY (note_type_id) REFERENCES note_type(id) ON DELETE SET NULL, " +
                     "INDEX idx_user (user_id), " +
                     "INDEX idx_source (source_id), " +
                     "INDEX idx_area (area_id), " +
                     "INDEX idx_note_type (note_type_id), " +
                     "INDEX idx_created_at (created_at)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Notes table");
    }
    
    /**
     * Creates the images table
     */
    public static void createImagesTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS images (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "note_id BIGINT NOT NULL, " +
                     "image_path VARCHAR(1000) NOT NULL, " +
                     "file_size BIGINT, " +
                     "mime_type VARCHAR(50), " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE, " +
                     "INDEX idx_note (note_id)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Images table");
    }
    
    /**
     * Creates the study_sessions table with user_id
     */
    public static void createStudySessionsTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS study_sessions (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "user_id BIGINT NOT NULL, " +
                     "session_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "session_duration INT, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                     "INDEX idx_user (user_id), " +
                     "INDEX idx_session_date (session_date)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Study sessions table");
    }
    
    /**
     * Creates the answers table (reference table)
     */
    public static void createAnswersTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS answers (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "content VARCHAR(255) NOT NULL, " +
                     "INDEX idx_content (content)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Answers table");
    }
    
    /**
     * Creates the questions table
     */
    public static void createQuestionsTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS questions (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "session_id BIGINT NOT NULL, " +
                     "answer_id BIGINT NOT NULL, " +
                     "note_id BIGINT NOT NULL, " +
                     "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "FOREIGN KEY (session_id) REFERENCES study_sessions(id) ON DELETE CASCADE, " +
                     "FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE, " +
                     "FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE, " +
                     "INDEX idx_session (session_id), " +
                     "INDEX idx_answer (answer_id), " +
                     "INDEX idx_note (note_id)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Questions table");
    }
    
    /**
     * Creates the shared_notes table for exporting/sharing notes
     */
    public static void createSharedNotesTable() throws Exception {
        String sql = "CREATE TABLE IF NOT EXISTS shared_notes (" +
                     "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                     "note_id BIGINT NOT NULL, " +
                     "shared_with_user_id BIGINT NOT NULL, " +
                     "permission_type ENUM('VIEW', 'EDIT', 'OWNER') DEFAULT 'VIEW', " +
                     "shared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE, " +
                     "FOREIGN KEY (shared_with_user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                     "UNIQUE KEY uk_note_shared_user (note_id, shared_with_user_id), " +
                     "INDEX idx_note (note_id), " +
                     "INDEX idx_shared_user (shared_with_user_id)" +
                     ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
        
        executeSQL(sql, "Shared notes table");
    }
    
    /**
     * Executes SQL statement and logs the result
     */
    private static void executeSQL(String sql, String tableName) throws Exception {
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(sql);
            System.out.println("✓ " + tableName + " created successfully or already exists");
        }
    }
    
    /**
     * Initializes default data (e.g., default note types)
     */
    private static void initializeDefaultData() {
        try {
            initializeDefaultNoteTypes();
        } catch (Exception e) {
            System.err.println("✗ Error initializing default data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates default note types if they don't exist
     */
    private static void initializeDefaultNoteTypes() throws Exception {
        String[] defaultNoteTypes = {"Definition"};
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection()) {
            for (String noteTypeName : defaultNoteTypes) {
                // Check if note type already exists
                String checkSql = "SELECT COUNT(*) FROM note_type WHERE name = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, noteTypeName);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next() && rs.getInt(1) == 0) {
                            // Note type doesn't exist, create it
                            String insertSql = "INSERT INTO note_type (name) VALUES (?)";
                            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                                insertStmt.setString(1, noteTypeName);
                                insertStmt.executeUpdate();
                                System.out.println("✓ Default note type '" + noteTypeName + "' created");
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Initializes all database tables in the correct order
     * Order is important to respect foreign key dependencies
     */
    public static void initializeDatabase() {
        try {
            System.out.println("=========================================");
            System.out.println("  INITIALIZING DATABASE SCHEMA");
            System.out.println("=========================================\n");
            
            // Step 1: Create reference tables (no dependencies)
            createUsersTable();
            createStatisticsTypeTable();
            createNoteTypeTable();
            createAreasTable();
            createSourcesTable();
            createDefaultSources();  // Create default source "personal"
            createAnswersTable();
            
            // Step 2: Create tables that depend on reference tables
            createUserStatisticsTable();
            createNotesTable();
            createStudySessionsTable();
            
            // Step 3: Create tables that depend on previous tables
            createImagesTable();
            createQuestionsTable();
            createSharedNotesTable();
            
            // Step 4: Initialize default data
            initializeDefaultData();
            
            System.out.println("\n=========================================");
            System.out.println("  DATABASE INITIALIZATION COMPLETED");
            System.out.println("=========================================");
        } catch (Exception e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

