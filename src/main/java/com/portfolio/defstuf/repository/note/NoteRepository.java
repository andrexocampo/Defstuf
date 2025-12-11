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
     * Counts notes by area ID and user ID
     * 
     * @param areaId The area ID
     * @param userId The user ID
     * @return Number of notes for the specified area and user
     * @throws SQLException If database error occurs
     */
    public int countByAreaIdAndUserId(Long areaId, Long userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notes WHERE area_id = ? AND user_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, areaId);
            stmt.setLong(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Gets distinct source IDs used in notes for a specific area and user
     * 
     * @param areaId The area ID
     * @param userId The user ID
     * @return List of source IDs (may include null for notes without source)
     * @throws SQLException If database error occurs
     */
    public List<Long> getDistinctSourceIdsByAreaIdAndUserId(Long areaId, Long userId) throws SQLException {
        String sql = "SELECT DISTINCT source_id FROM notes WHERE area_id = ? AND user_id = ? AND source_id IS NOT NULL";
        List<Long> sourceIds = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, areaId);
            stmt.setLong(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sourceIds.add(rs.getLong("source_id"));
                }
            }
        }
        return sourceIds;
    }
    
    /**
     * Counts notes by area ID, user ID, and list of source IDs
     * If sourceIds is null or empty, counts all notes for the area and user
     * 
     * @param areaId The area ID
     * @param userId The user ID
     * @param sourceIds List of source IDs to filter by (null or empty means all sources)
     * @return Number of notes matching the criteria
     * @throws SQLException If database error occurs
     */
    public int countByAreaIdAndUserIdAndSourceIds(Long areaId, Long userId, List<Long> sourceIds) throws SQLException {
        String sql;
        if (sourceIds == null || sourceIds.isEmpty()) {
            // Count all notes for area and user
            sql = "SELECT COUNT(*) FROM notes WHERE area_id = ? AND user_id = ?";
        } else {
            // Count notes matching specific sources
            String placeholders = String.join(",", java.util.Collections.nCopies(sourceIds.size(), "?"));
            sql = "SELECT COUNT(*) FROM notes WHERE area_id = ? AND user_id = ? AND source_id IN (" + placeholders + ")";
        }
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, areaId);
            stmt.setLong(2, userId);
            
            if (sourceIds != null && !sourceIds.isEmpty()) {
                for (int i = 0; i < sourceIds.size(); i++) {
                    stmt.setLong(3 + i, sourceIds.get(i));
                }
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Counts notes by area ID, user ID, and source ID
     * 
     * @param areaId The area ID
     * @param userId The user ID
     * @param sourceId The source ID
     * @return Number of notes for the specified area, user, and source
     * @throws SQLException If database error occurs
     */
    public int countByAreaIdAndUserIdAndSourceId(Long areaId, Long userId, Long sourceId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM notes WHERE area_id = ? AND user_id = ? AND source_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, areaId);
            stmt.setLong(2, userId);
            stmt.setLong(3, sourceId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
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

