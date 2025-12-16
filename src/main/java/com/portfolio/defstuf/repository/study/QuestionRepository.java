package com.portfolio.defstuf.repository.study;

import com.portfolio.defstuf.models.study.Question;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Question entity database operations
 */
public class QuestionRepository {
    
    /**
     * Saves a new question to the database
     */
    public Question save(Question question) throws SQLException {
        String sql = "INSERT INTO questions (session_id, answer_id, note_id, created_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, question.getSessionId());
            stmt.setLong(2, question.getAnswerId());
            stmt.setLong(3, question.getNoteId());
            stmt.setTimestamp(4, Timestamp.valueOf(question.getCreatedAt() != null ? 
                    question.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating question failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    question.setId(generatedKeys.getLong(1));
                    if (question.getCreatedAt() == null) {
                        question.setCreatedAt(LocalDateTime.now());
                    }
                } else {
                    throw new SQLException("Creating question failed, no ID obtained.");
                }
            }
        }
        return question;
    }
    
    /**
     * Finds all questions for a specific session
     */
    public List<Question> findBySessionId(Long sessionId) throws SQLException {
        String sql = "SELECT * FROM questions WHERE session_id = ? ORDER BY created_at";
        List<Question> questions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, sessionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
            }
        }
        return questions;
    }
    
    /**
     * Finds a question by ID
     */
    public Question findById(Long id) throws SQLException {
        String sql = "SELECT * FROM questions WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToQuestion(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Deletes all questions for a specific session
     */
    public boolean deleteBySessionId(Long sessionId) throws SQLException {
        String sql = "DELETE FROM questions WHERE session_id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, sessionId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Maps a ResultSet row to a Question object
     */
    private Question mapResultSetToQuestion(ResultSet rs) throws SQLException {
        Question question = new Question();
        question.setId(rs.getLong("id"));
        question.setSessionId(rs.getLong("session_id"));
        question.setAnswerId(rs.getLong("answer_id"));
        question.setNoteId(rs.getLong("note_id"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            question.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return question;
    }
}







