package com.portfolio.defstuf.repository.study;

import com.portfolio.defstuf.models.study.Answer;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Answer entity database operations
 * FR-05.4: Handles answer ratings (forgot, hard, good, easy)
 */
public class AnswerRepository {
    
    /**
     * Finds an answer by content (case-insensitive)
     * 
     * @param content The answer content ("forgot", "hard", "good", "easy")
     * @return The Answer if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Answer findByContent(String content) throws SQLException {
        String sql = "SELECT id, content FROM answers WHERE LOWER(content) = LOWER(?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, content);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAnswer(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds or creates an answer by content
     * If the answer doesn't exist, it creates it
     * 
     * @param content The answer content ("forgot", "hard", "good", "easy")
     * @return The Answer (existing or newly created)
     * @throws SQLException If database error occurs
     */
    public Answer findOrCreateByContent(String content) throws SQLException {
        Answer answer = findByContent(content);
        
        if (answer == null) {
            // Create new answer
            answer = new Answer(content);
            answer = save(answer);
        }
        
        return answer;
    }
    
    /**
     * Saves a new answer to the database
     * 
     * @param answer The answer to save
     * @return The saved answer with generated ID
     * @throws SQLException If database error occurs
     */
    public Answer save(Answer answer) throws SQLException {
        String sql = "INSERT INTO answers (content) VALUES (?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, answer.getContent());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating answer failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    answer.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating answer failed, no ID obtained.");
                }
            }
        }
        return answer;
    }
    
    /**
     * Finds an answer by ID
     * 
     * @param id The answer ID
     * @return The Answer if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Answer findById(Long id) throws SQLException {
        String sql = "SELECT id, content FROM answers WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAnswer(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds all answers
     * 
     * @return List of all answers
     * @throws SQLException If database error occurs
     */
    public List<Answer> findAll() throws SQLException {
        String sql = "SELECT id, content FROM answers ORDER BY content";
        List<Answer> answers = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                answers.add(mapResultSetToAnswer(rs));
            }
        }
        return answers;
    }
    
    /**
     * Maps a ResultSet row to an Answer object
     */
    private Answer mapResultSetToAnswer(ResultSet rs) throws SQLException {
        Answer answer = new Answer();
        answer.setId(rs.getLong("id"));
        answer.setContent(rs.getString("content"));
        return answer;
    }
}


