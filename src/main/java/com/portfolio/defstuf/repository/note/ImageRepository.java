package com.portfolio.defstuf.repository.note;

import com.portfolio.defstuf.models.note.NoteImage;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Image entity database operations
 */
public class ImageRepository {
    
    /**
     * Saves a new image to the database
     */
    public NoteImage save(NoteImage image) throws SQLException {
        String sql = "INSERT INTO images (note_id, image_path, file_size, mime_type, created_at) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, image.getNoteId());
            stmt.setString(2, image.getImagePath());
            
            if (image.getFileSize() != null) {
                stmt.setLong(3, image.getFileSize());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            
            stmt.setString(4, image.getMimeType());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating image failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    image.setId(generatedKeys.getLong(1));
                    image.setCreatedAt(LocalDateTime.now());
                } else {
                    throw new SQLException("Creating image failed, no ID obtained.");
                }
            }
        }
        return image;
    }
    
    /**
     * Finds all images for a specific note
     */
    public List<NoteImage> findByNoteId(Long noteId) throws SQLException {
        String sql = "SELECT id, note_id, image_path, file_size, mime_type, created_at " +
                     "FROM images WHERE note_id = ? ORDER BY created_at";
        List<NoteImage> images = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, noteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    images.add(mapResultSetToImage(rs));
                }
            }
        }
        return images;
    }
    
    /**
     * Maps a ResultSet row to a NoteImage object
     */
    private NoteImage mapResultSetToImage(ResultSet rs) throws SQLException {
        NoteImage image = new NoteImage();
        image.setId(rs.getLong("id"));
        image.setNoteId(rs.getLong("note_id"));
        image.setImagePath(rs.getString("image_path"));
        
        Long fileSize = rs.getLong("file_size");
        if (!rs.wasNull()) {
            image.setFileSize(fileSize);
        }
        
        image.setMimeType(rs.getString("mime_type"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            image.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return image;
    }
}

