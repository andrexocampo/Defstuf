package com.portfolio.defstuf.repository.note;

import com.portfolio.defstuf.models.note.Note;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Note entity database operations
 */
public class NoteRepository {
    
    /**
     * Saves a new note to the database
     */
    public Note save(Note note) throws SQLException {
        String sql = "INSERT INTO notes (user_id, title, source_id, description, area_id, note_type_id, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, note.getUserId());
            stmt.setString(2, note.getTitle());
            
            if (note.getSourceId() != null) {
                stmt.setLong(3, note.getSourceId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            
            stmt.setString(4, note.getDescription());
            
            if (note.getAreaId() != null) {
                stmt.setLong(5, note.getAreaId());
            } else {
                stmt.setNull(5, Types.BIGINT);
            }
            
            if (note.getNoteTypeId() != null) {
                stmt.setLong(6, note.getNoteTypeId());
            } else {
                stmt.setNull(6, Types.BIGINT);
            }
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating note failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    note.setId(generatedKeys.getLong(1));
                    note.setCreatedAt(now);
                    note.setUpdatedAt(now);
                } else {
                    throw new SQLException("Creating note failed, no ID obtained.");
                }
            }
        }
        return note;
    }
    
    /**
     * Finds a note by ID
     */
    public Note findById(Long id) throws SQLException {
        String sql = "SELECT id, user_id, title, source_id, description, area_id, note_type_id, created_at, updated_at " +
                     "FROM notes WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNote(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all notes for a specific user
     */
    public List<Note> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT id, user_id, title, source_id, description, area_id, note_type_id, created_at, updated_at " +
                     "FROM notes WHERE user_id = ? ORDER BY created_at DESC";
        List<Note> notes = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notes.add(mapResultSetToNote(rs));
                }
            }
        }
        return notes;
    }
    
    /**
     * Maps a ResultSet row to a Note object
     */
    private Note mapResultSetToNote(ResultSet rs) throws SQLException {
        Note note = new Note();
        note.setId(rs.getLong("id"));
        note.setUserId(rs.getLong("user_id"));
        note.setTitle(rs.getString("title"));
        
        Long sourceId = rs.getLong("source_id");
        if (!rs.wasNull()) {
            note.setSourceId(sourceId);
        } else {
            note.setSourceId(null);
        }
        
        note.setDescription(rs.getString("description"));
        
        Long areaId = rs.getLong("area_id");
        if (!rs.wasNull()) {
            note.setAreaId(areaId);
        }
        
        Long noteTypeId = rs.getLong("note_type_id");
        if (!rs.wasNull()) {
            note.setNoteTypeId(noteTypeId);
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            note.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            note.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return note;
    }
}

