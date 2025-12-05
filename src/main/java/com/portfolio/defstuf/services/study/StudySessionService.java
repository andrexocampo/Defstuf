package com.portfolio.defstuf.services.study;

import com.portfolio.defstuf.models.study.StudySession;
import com.portfolio.defstuf.repository.study.StudySessionRepository;
import com.portfolio.defstuf.session.SessionManager;

import java.sql.SQLException;

/**
 * Service for study session operations
 * Handles business logic for study session management
 */
public class StudySessionService {
    
    private final StudySessionRepository repository;
    
    public StudySessionService() {
        this.repository = new StudySessionRepository();
    }
    
    /**
     * Creates a new study session with the provided configuration
     * 
     * @param sessionName The name of the session
     * @param areaId The area ID (can be null for multi-area sessions)
     * @param sessionDurationMin Duration in minutes (15, 30, 60, etc.)
     * @param cardsLimit Maximum number of cards/definitions (20, 50, 100, or null for all)
     * @param reviewOrder The order mode (random, oldest_first, hardest_first)
     * @return The created study session
     * @throws StudySessionException If creation fails
     */
    public StudySession createStudySession(
            String sessionName,
            Long areaId,
            Integer sessionDurationMin,
            Integer cardsLimit,
            StudySession.ReviewOrder reviewOrder
    ) throws StudySessionException {
        
        // Validate session name
        if (sessionName == null || sessionName.trim().isEmpty()) {
            throw new StudySessionException("Session name cannot be empty");
        }
        
        // Get current user ID
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) {
            throw new StudySessionException("User must be logged in to create a study session");
        }
        
        // Validate duration (minimum 1 minute)
        if (sessionDurationMin != null && sessionDurationMin < 1) {
            throw new StudySessionException("Session duration must be at least 1 minute");
        }
        
        // Validate cards limit (minimum 1 if not null)
        if (cardsLimit != null && cardsLimit < 1) {
            throw new StudySessionException("Cards limit must be at least 1");
        }
        
        // Set default values if not provided
        int finalDuration = (sessionDurationMin != null) ? sessionDurationMin : 25;
        Integer finalCardsLimit = (cardsLimit != null && cardsLimit > 0) ? cardsLimit : null;
        StudySession.ReviewOrder finalReviewOrder = (reviewOrder != null) ? reviewOrder : StudySession.ReviewOrder.RANDOM;
        
        // Create study session
        StudySession session = new StudySession(userId, sessionName.trim(), areaId);
        session.setSessionDurationMin(finalDuration);
        session.setCardsLimit(finalCardsLimit);
        session.setReviewOrder(finalReviewOrder);
        session.setStatus(StudySession.Status.ACTIVE);
        
        // Save to database
        try {
            return repository.save(session);
        } catch (SQLException e) {
            throw new StudySessionException("Error creating study session: " + e.getMessage(), e);
        }
    }
    
    /**
     * Finds a study session by ID
     * 
     * @param id The session ID
     * @return The study session if found
     * @throws StudySessionException If error occurs
     */
    public StudySession getStudySessionById(Long id) throws StudySessionException {
        try {
            StudySession session = repository.findById(id);
            if (session == null) {
                throw new StudySessionException("Study session not found with ID: " + id);
            }
            return session;
        } catch (SQLException e) {
            throw new StudySessionException("Error finding study session: " + e.getMessage(), e);
        }
    }
    
    /**
     * Updates a study session status
     * 
     * @param sessionId The session ID
     * @param status The new status
     * @throws StudySessionException If update fails
     */
    public void updateSessionStatus(Long sessionId, StudySession.Status status) throws StudySessionException {
        try {
            StudySession session = repository.findById(sessionId);
            if (session == null) {
                throw new StudySessionException("Study session not found with ID: " + sessionId);
            }
            
            session.setStatus(status);
            boolean updated = repository.update(session);
            
            if (!updated) {
                throw new StudySessionException("Failed to update study session status");
            }
        } catch (SQLException e) {
            throw new StudySessionException("Error updating study session: " + e.getMessage(), e);
        }
    }
    
    /**
     * Custom exception for study session operations
     */
    public static class StudySessionException extends Exception {
        public StudySessionException(String message) {
            super(message);
        }
        
        public StudySessionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

