package com.portfolio.defstuf.repository.note;

import com.portfolio.defstuf.models.note.Source;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Source entity database operations
 */
public class SourceRepository {
    
    /**
     * Finds all sources
     */
    public List<Source> findAll() throws SQLException {
        String sql = "SELECT id, name, code, created_at FROM sources ORDER BY name";
        List<Source> sources = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                sources.add(mapResultSetToSource(rs));
            }
        }
        return sources;
    }
    
    /**
     * Finds a source by code
     */
    public Optional<Source> findByCode(String code) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = "SELECT id, name, code, created_at FROM sources WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSource(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    /**
     * Finds a source by ID
     */
    public Optional<Source> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, code, created_at FROM sources WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSource(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    /**
     * Finds a source by name (exact match)
     */
    public Optional<Source> findByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String sql = "SELECT id, name, code, created_at FROM sources WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name.trim());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToSource(rs));
                }
            }
        }
        return Optional.empty();
    }
    
    /**
     * Saves a new source to the database
     * 
     * @param source The source to save
     * @return The saved source with generated ID
     * @throws SQLException If database error occurs
     */
    public Source save(Source source) throws SQLException {
        String sql = "INSERT INTO sources (name, code, created_at) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, source.getName());
            if (source.getCode() != null && !source.getCode().trim().isEmpty()) {
                stmt.setString(2, source.getCode());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            stmt.setTimestamp(3, Timestamp.valueOf(source.getCreatedAt() != null ? 
                    source.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating source failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    source.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating source failed, no ID obtained.");
                }
            }
        }
        return source;
    }
    
    /**
     * Updates an existing source in the database
     * 
     * @param source The source to update
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean update(Source source) throws SQLException {
        String sql = "UPDATE sources SET name = ?, code = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, source.getName());
            if (source.getCode() != null && !source.getCode().trim().isEmpty()) {
                stmt.setString(2, source.getCode());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }
            stmt.setLong(3, source.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Deletes a source from the database
     * 
     * @param id The source ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM sources WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Checks if a name already exists in the database
     * 
     * @param name The name to check
     * @return true if name exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean nameExists(String name) throws SQLException {
        return findByName(name).isPresent();
    }
    
    /**
     * Checks if a code already exists in the database
     * 
     * @param code The code to check
     * @return true if code exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean codeExists(String code) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        return findByCode(code).isPresent();
    }
    
    /**
     * Checks if a name exists for a different source (used for updates)
     * 
     * @param name The name to check
     * @param excludeId The ID to exclude from the check
     * @return true if name exists for another source, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean nameExistsForOtherSource(String name, Long excludeId) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM sources WHERE name = ? AND id != ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name.trim());
            stmt.setLong(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Checks if a code exists for a different source (used for updates)
     * 
     * @param code The code to check
     * @param excludeId The ID to exclude from the check
     * @return true if code exists for another source, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean codeExistsForOtherSource(String code, Long excludeId) throws SQLException {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        
        String sql = "SELECT COUNT(*) FROM sources WHERE code = ? AND id != ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            stmt.setLong(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Maps a ResultSet row to a Source object
     */
    private Source mapResultSetToSource(ResultSet rs) throws SQLException {
        Source source = new Source();
        source.setId(rs.getLong("id"));
        source.setName(rs.getString("name"));
        source.setCode(rs.getString("code"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            source.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return source;
    }
}

