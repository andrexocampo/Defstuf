package com.portfolio.defstuf.repository.study;

import com.portfolio.defstuf.models.study.StudySession;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for StudySession entity database operations
 */
public class StudySessionRepository {
    
    /**
     * Saves a new study session to the database
     */
    public StudySession save(StudySession session) throws SQLException {
        String sql = "INSERT INTO study_sessions (" +
                     "user_id, session_name, area_id, status, created_at, started_at, completed_at, " +
                     "session_duration_min, cards_limit, review_order, show_hints, auto_advance_sec, " +
                     "enable_breaks, break_interval_min, break_duration_min, custom_config" +
                     ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, session.getUserId());
            stmt.setString(2, session.getSessionName());
            
            if (session.getAreaId() != null) {
                stmt.setLong(3, session.getAreaId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            
            stmt.setString(4, session.getStatus() != null ? session.getStatus().getValue() : "active");
            
            LocalDateTime now = LocalDateTime.now();
            stmt.setTimestamp(5, Timestamp.valueOf(session.getCreatedAt() != null ? 
                    session.getCreatedAt() : now));
            
            if (session.getStartedAt() != null) {
                stmt.setTimestamp(6, Timestamp.valueOf(session.getStartedAt()));
            } else {
                stmt.setNull(6, Types.TIMESTAMP);
            }
            
            if (session.getCompletedAt() != null) {
                stmt.setTimestamp(7, Timestamp.valueOf(session.getCompletedAt()));
            } else {
                stmt.setNull(7, Types.TIMESTAMP);
            }
            
            stmt.setInt(8, session.getSessionDurationMin() != null ? session.getSessionDurationMin() : 25);
            stmt.setInt(9, session.getCardsLimit() != null ? session.getCardsLimit() : 20);
            stmt.setString(10, session.getReviewOrder() != null ? 
                    session.getReviewOrder().getValue() : "random");
            stmt.setBoolean(11, session.getShowHints() != null ? session.getShowHints() : false);
            stmt.setInt(12, session.getAutoAdvanceSec() != null ? session.getAutoAdvanceSec() : 0);
            stmt.setBoolean(13, session.getEnableBreaks() != null ? session.getEnableBreaks() : false);
            stmt.setInt(14, session.getBreakIntervalMin() != null ? session.getBreakIntervalMin() : 25);
            stmt.setInt(15, session.getBreakDurationMin() != null ? session.getBreakDurationMin() : 5);
            
            if (session.getCustomConfig() != null && !session.getCustomConfig().trim().isEmpty()) {
                stmt.setString(16, session.getCustomConfig());
            } else {
                stmt.setNull(16, Types.VARCHAR);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating study session failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    session.setId(generatedKeys.getLong(1));
                    if (session.getCreatedAt() == null) {
                        session.setCreatedAt(now);
                    }
                } else {
                    throw new SQLException("Creating study session failed, no ID obtained.");
                }
            }
        }
        return session;
    }
    
    /**
     * Finds a study session by ID
     */
    public StudySession findById(Long id) throws SQLException {
        String sql = "SELECT * FROM study_sessions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudySession(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all study sessions for a specific user
     */
    public List<StudySession> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM study_sessions WHERE user_id = ? ORDER BY created_at DESC";
        List<StudySession> sessions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToStudySession(rs));
                }
            }
        }
        return sessions;
    }
    
    /**
     * Finds study sessions by user and status
     */
    public List<StudySession> findByUserIdAndStatus(Long userId, StudySession.Status status) throws SQLException {
        String sql = "SELECT * FROM study_sessions WHERE user_id = ? AND status = ? ORDER BY created_at DESC";
        List<StudySession> sessions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setString(2, status.getValue());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToStudySession(rs));
                }
            }
        }
        return sessions;
    }
    
    /**
     * Updates an existing study session
     */
    public boolean update(StudySession session) throws SQLException {
        String sql = "UPDATE study_sessions SET " +
                     "session_name = ?, area_id = ?, status = ?, started_at = ?, completed_at = ?, " +
                     "session_duration_min = ?, cards_limit = ?, review_order = ?, show_hints = ?, " +
                     "auto_advance_sec = ?, enable_breaks = ?, break_interval_min = ?, " +
                     "break_duration_min = ?, custom_config = ? " +
                     "WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, session.getSessionName());
            
            if (session.getAreaId() != null) {
                stmt.setLong(2, session.getAreaId());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            
            stmt.setString(3, session.getStatus() != null ? session.getStatus().getValue() : "active");
            
            if (session.getStartedAt() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(session.getStartedAt()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            
            if (session.getCompletedAt() != null) {
                stmt.setTimestamp(5, Timestamp.valueOf(session.getCompletedAt()));
            } else {
                stmt.setNull(5, Types.TIMESTAMP);
            }
            
            stmt.setInt(6, session.getSessionDurationMin() != null ? session.getSessionDurationMin() : 25);
            stmt.setInt(7, session.getCardsLimit() != null ? session.getCardsLimit() : 20);
            stmt.setString(8, session.getReviewOrder() != null ? 
                    session.getReviewOrder().getValue() : "random");
            stmt.setBoolean(9, session.getShowHints() != null ? session.getShowHints() : false);
            stmt.setInt(10, session.getAutoAdvanceSec() != null ? session.getAutoAdvanceSec() : 0);
            stmt.setBoolean(11, session.getEnableBreaks() != null ? session.getEnableBreaks() : false);
            stmt.setInt(12, session.getBreakIntervalMin() != null ? session.getBreakIntervalMin() : 25);
            stmt.setInt(13, session.getBreakDurationMin() != null ? session.getBreakDurationMin() : 5);
            
            if (session.getCustomConfig() != null && !session.getCustomConfig().trim().isEmpty()) {
                stmt.setString(14, session.getCustomConfig());
            } else {
                stmt.setNull(14, Types.VARCHAR);
            }
            
            stmt.setLong(15, session.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Deletes a study session
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM study_sessions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Maps a ResultSet row to a StudySession object
     */
    private StudySession mapResultSetToStudySession(ResultSet rs) throws SQLException {
        StudySession session = new StudySession();
        session.setId(rs.getLong("id"));
        session.setUserId(rs.getLong("user_id"));
        session.setSessionName(rs.getString("session_name"));
        
        Long areaId = rs.getLong("area_id");
        if (!rs.wasNull()) {
            session.setAreaId(areaId);
        }
        
        session.setStatus(StudySession.Status.fromString(rs.getString("status")));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            session.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp startedAt = rs.getTimestamp("started_at");
        if (startedAt != null && !rs.wasNull()) {
            session.setStartedAt(startedAt.toLocalDateTime());
        }
        
        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null && !rs.wasNull()) {
            session.setCompletedAt(completedAt.toLocalDateTime());
        }
        
        session.setSessionDurationMin(rs.getInt("session_duration_min"));
        session.setCardsLimit(rs.getInt("cards_limit"));
        session.setReviewOrder(StudySession.ReviewOrder.fromString(rs.getString("review_order")));
        session.setShowHints(rs.getBoolean("show_hints"));
        session.setAutoAdvanceSec(rs.getInt("auto_advance_sec"));
        session.setEnableBreaks(rs.getBoolean("enable_breaks"));
        session.setBreakIntervalMin(rs.getInt("break_interval_min"));
        session.setBreakDurationMin(rs.getInt("break_duration_min"));
        
        String customConfig = rs.getString("custom_config");
        if (customConfig != null) {
            session.setCustomConfig(customConfig);
        }
        
        return session;
    }
}

