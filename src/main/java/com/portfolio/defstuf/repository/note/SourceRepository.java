package com.portfolio.defstuf.repository.note;

import com.portfolio.defstuf.models.note.Source;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
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

