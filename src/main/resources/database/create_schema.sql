-- ============================================
-- DefStuf Database Schema
-- Complete database structure
-- ============================================

-- ============================================
-- 1. USERS TABLE
-- FR-01: User Management
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,  -- UNIQUE already creates index
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    -- WITHOUT: INDEX idx_email (email)  -- Redundant!
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 2. STATISTICS TYPE TABLE (Reference Table)
-- ============================================
CREATE TABLE IF NOT EXISTS statistics_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,  -- UNIQUE already creates index
    unit VARCHAR(50),
    is_numeric BOOLEAN DEFAULT TRUE
    -- WITHOUT: INDEX idx_name (name)  -- Redundant!
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 3. USER STATISTICS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS user_statistics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    statistics_type_id BIGINT NOT NULL,
    statistics_name VARCHAR(255),
    statistics_value DECIMAL(15, 4) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (statistics_type_id) REFERENCES statistics_type(id) ON DELETE RESTRICT,
    UNIQUE KEY uk_user_statistics_type (user_id, statistics_type_id),  -- This creates index
    INDEX idx_statistics_type (statistics_type_id)  -- Only necessary if you search by type
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- NOTE: idx_user is not necessary because uk_user_statistics_type already indexes (user_id, statistics_type_id)
-- and the first column (user_id) can be used for searches by user_id only

-- ============================================
-- 4. NOTE TYPE TABLE (Reference Table)
-- ============================================
CREATE TABLE IF NOT EXISTS note_type (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE  -- UNIQUE already creates index
    -- WITHOUT: INDEX idx_name (name)  -- Redundant!
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 5. AREAS TABLE (Reference Table with created_at)
-- ============================================
CREATE TABLE IF NOT EXISTS areas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE,  -- UNIQUE already creates index
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_name (name)  -- Necessary because it's not UNIQUE
    -- WITHOUT: INDEX idx_code (code)  -- Redundant!
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 6. SOURCES TABLE (Reference Table)
-- ============================================
CREATE TABLE IF NOT EXISTS sources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE,  -- UNIQUE already creates index
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_name (name)  -- Necessary because it's not UNIQUE
    -- WITHOUT: INDEX idx_code (code)  -- Redundant!
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 7. NOTES TABLE (With user_id for ownership)
-- ============================================
CREATE TABLE IF NOT EXISTS notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,  -- Owner/creator of the note
    title VARCHAR(255) NOT NULL,
    source_id BIGINT,  -- Foreign key to sources table
    description TEXT,
    area_id BIGINT,
    note_type_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (source_id) REFERENCES sources(id) ON DELETE SET NULL,
    FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE SET NULL,
    FOREIGN KEY (note_type_id) REFERENCES note_type(id) ON DELETE SET NULL,
    INDEX idx_user (user_id),  -- To filter notes by owner
    INDEX idx_source (source_id),  -- Necessary for JOINs and WHERE
    INDEX idx_area (area_id),  -- Necessary for JOINs and WHERE
    INDEX idx_note_type (note_type_id),  -- Necessary for JOINs and WHERE
    INDEX idx_created_at (created_at)  -- Useful for sorting by date
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 8. IMAGES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,
    image_path VARCHAR(1000) NOT NULL,
    file_size BIGINT,
    mime_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
    INDEX idx_note (note_id)  -- Necessary for JOINs and WHERE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 9. SESSIONS TABLE (With user_id)
-- ============================================
CREATE TABLE IF NOT EXISTS sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,  -- Session belongs to a user
    session_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    session_duration INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user (user_id),  -- To filter sessions by user
    INDEX idx_session_date (session_date)  -- Useful for filtering by date
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 10. ANSWERS TABLE (Reference Table)
-- ============================================
CREATE TABLE IF NOT EXISTS answers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    
    INDEX idx_name (name)  -- Necessary if you search by name (not UNIQUE)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 11. QUESTIONS TABLE (WITH AUTO_INCREMENT ID)
-- ============================================
CREATE TABLE IF NOT EXISTS questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,  -- Artificial key
    session_id BIGINT NOT NULL,
    answer_id BIGINT NOT NULL,
    note_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    
    FOREIGN KEY (session_id) REFERENCES sessions(id) ON DELETE CASCADE,
    FOREIGN KEY (answer_id) REFERENCES answers(id) ON DELETE CASCADE,
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
    INDEX idx_session (session_id),
    INDEX idx_answer (answer_id),
    INDEX idx_note (note_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- 12. SHARED NOTES TABLE (For exporting/sharing notes)
-- ============================================
CREATE TABLE IF NOT EXISTS shared_notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    note_id BIGINT NOT NULL,  -- Note that is being shared
    shared_with_user_id BIGINT NOT NULL,  -- User with whom the note is shared
    permission_type ENUM('VIEW', 'EDIT', 'OWNER') DEFAULT 'VIEW',  -- Permission type
    shared_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (note_id) REFERENCES notes(id) ON DELETE CASCADE,
    FOREIGN KEY (shared_with_user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_note_shared_user (note_id, shared_with_user_id),  -- Prevents duplicates
    INDEX idx_note (note_id),
    INDEX idx_shared_user (shared_with_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

