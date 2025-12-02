package com.portfolio.defstuf.repository.note;

import com.portfolio.defstuf.models.note.NoteType;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for NoteType entity database operations
 */
public class NoteTypeRepository {
    
    /**
     * Finds all note types
     */
    public List<NoteType> findAll() throws SQLException {
        String sql = "SELECT id, name FROM note_type ORDER BY name";
        List<NoteType> noteTypes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                noteTypes.add(mapResultSetToNoteType(rs));
            }
        }
        return noteTypes;
    }
    
    /**
     * Finds a note type by name
     */
    public NoteType findByName(String name) throws SQLException {
        String sql = "SELECT id, name FROM note_type WHERE name = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNoteType(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds a note type by ID
     */
    public NoteType findById(Long id) throws SQLException {
        String sql = "SELECT id, name FROM note_type WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNoteType(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Creates a note type if it doesn't exist
     */
    public NoteType createIfNotExists(String name) throws SQLException {
        NoteType existing = findByName(name);
        if (existing != null) {
            return existing;
        }
        
        String sql = "INSERT INTO note_type (name) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, name);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating note type failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    NoteType noteType = new NoteType();
                    noteType.setId(generatedKeys.getLong(1));
                    noteType.setName(name);
                    return noteType;
                } else {
                    throw new SQLException("Creating note type failed, no ID obtained.");
                }
            }
        }
    }
    
    /**
     * Maps a ResultSet row to a NoteType object
     */
    private NoteType mapResultSetToNoteType(ResultSet rs) throws SQLException {
        NoteType noteType = new NoteType();
        noteType.setId(rs.getLong("id"));
        noteType.setName(rs.getString("name"));
        return noteType;
    }
}

