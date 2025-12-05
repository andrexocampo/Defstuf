package com.portfolio.defstuf.repository.study;

import com.portfolio.defstuf.models.study.ScheduledReview;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for ScheduledReview entity database operations
 */
public class ScheduledReviewRepository {
    
    /**
     * Saves a new scheduled review to the database
     */
    public ScheduledReview save(ScheduledReview review) throws SQLException {
        String sql = "INSERT INTO scheduled_reviews (" +
                     "note_id, user_id, scheduled_date, reviewed_date, ease_factor, " +
                     "current_interval, next_interval, review_status, review_quality, " +
                     "created_at, updated_at" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, review.getNoteId());
            stmt.setLong(2, review.getUserId());
            stmt.setDate(3, Date.valueOf(review.getScheduledDate()));
            
            if (review.getReviewedDate() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(review.getReviewedDate()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            stmt.setBigDecimal(5, review.getEaseFactor() != null ? 
                    review.getEaseFactor() : new java.math.BigDecimal("2.5"));
            
            stmt.setInt(6, review.getCurrentInterval() != null ? review.getCurrentInterval() : 1);
            
            if (review.getNextInterval() != null) {
                stmt.setInt(7, review.getNextInterval());
            } else {
                stmt.setNull(7, Types.INTEGER);
            }
            
            stmt.setString(8, review.getReviewStatus() != null ? 
                    review.getReviewStatus().getValue() : "pending");
            
            if (review.getReviewQuality() != null) {
                stmt.setInt(9, review.getReviewQuality().getValue());
            } else {
                stmt.setNull(9, Types.TINYINT);
            }
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setTimestamp(10, Timestamp.valueOf(review.getCreatedAt() != null ? 
                    review.getCreatedAt() : now));
            stmt.setTimestamp(11, Timestamp.valueOf(review.getUpdatedAt() != null ? 
                    review.getUpdatedAt() : now));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating scheduled review failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.setId(generatedKeys.getLong(1));
                    if (review.getCreatedAt() == null) {
                        review.setCreatedAt(now);
                    }
                    if (review.getUpdatedAt() == null) {
                        review.setUpdatedAt(now);
                    }
                } else {
                    throw new SQLException("Creating scheduled review failed, no ID obtained.");
                }
            }
        }
        return review;
    }
    
    /**
     * Finds a scheduled review by ID
     */
    public ScheduledReview findById(Long id) throws SQLException {
        String sql = "SELECT * FROM scheduled_reviews WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToScheduledReview(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all pending reviews for a user on a specific date
     */
    public List<ScheduledReview> findPendingByUserIdAndDate(Long userId, LocalDate date) throws SQLException {
        String sql = "SELECT * FROM scheduled_reviews " +
                     "WHERE user_id = ? AND scheduled_date = ? AND review_status = 'pending' " +
                     "ORDER BY scheduled_date";
        List<ScheduledReview> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setDate(2, Date.valueOf(date));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToScheduledReview(rs));
                }
            }
        }
        return reviews;
    }
    
    /**
     * Finds all pending reviews for a user
     */
    public List<ScheduledReview> findPendingByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM scheduled_reviews " +
                     "WHERE user_id = ? AND review_status = 'pending' " +
                     "ORDER BY scheduled_date";
        List<ScheduledReview> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToScheduledReview(rs));
                }
            }
        }
        return reviews;
    }
    
    /**
     * Finds scheduled review by note and user
     */
    public ScheduledReview findByNoteIdAndUserId(Long noteId, Long userId) throws SQLException {
        String sql = "SELECT * FROM scheduled_reviews WHERE note_id = ? AND user_id = ? ORDER BY created_at DESC LIMIT 1";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, noteId);
            stmt.setLong(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToScheduledReview(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Updates an existing scheduled review
     */
    public boolean update(ScheduledReview review) throws SQLException {
        String sql = "UPDATE scheduled_reviews SET " +
                     "scheduled_date = ?, reviewed_date = ?, ease_factor = ?, " +
                     "current_interval = ?, next_interval = ?, review_status = ?, " +
                     "review_quality = ?, updated_at = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(review.getScheduledDate()));
            
            if (review.getReviewedDate() != null) {
                stmt.setTimestamp(2, Timestamp.valueOf(review.getReviewedDate()));
            } else {
                stmt.setNull(2, Types.TIMESTAMP);
            }
            
            stmt.setBigDecimal(3, review.getEaseFactor());
            
            stmt.setInt(4, review.getCurrentInterval());
            
            if (review.getNextInterval() != null) {
                stmt.setInt(5, review.getNextInterval());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            
            stmt.setString(6, review.getReviewStatus().getValue());
            
            if (review.getReviewQuality() != null) {
                stmt.setInt(7, review.getReviewQuality().getValue());
            } else {
                stmt.setNull(7, Types.TINYINT);
            }
            
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(9, review.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Maps a ResultSet row to a ScheduledReview object
     */
    private ScheduledReview mapResultSetToScheduledReview(ResultSet rs) throws SQLException {
        ScheduledReview review = new ScheduledReview();
        review.setId(rs.getLong("id"));
        review.setNoteId(rs.getLong("note_id"));
        review.setUserId(rs.getLong("user_id"));
        
        Date scheduledDate = rs.getDate("scheduled_date");
        if (scheduledDate != null) {
            review.setScheduledDate(scheduledDate.toLocalDate());
        }
        
        Timestamp reviewedDate = rs.getTimestamp("reviewed_date");
        if (reviewedDate != null && !rs.wasNull()) {
            review.setReviewedDate(reviewedDate.toLocalDateTime());
        }
        
        review.setEaseFactor(rs.getBigDecimal("ease_factor"));
        review.setCurrentInterval(rs.getInt("current_interval"));
        
        int nextInterval = rs.getInt("next_interval");
        if (!rs.wasNull()) {
            review.setNextInterval(nextInterval);
        }
        
        review.setReviewStatus(ScheduledReview.ReviewStatus.fromString(rs.getString("review_status")));
        
        int reviewQuality = rs.getInt("review_quality");
        if (!rs.wasNull()) {
            review.setReviewQuality(ScheduledReview.ReviewQuality.fromInt(reviewQuality));
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            review.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            review.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return review;
    }
}

