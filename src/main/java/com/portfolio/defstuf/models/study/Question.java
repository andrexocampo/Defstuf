package com.portfolio.defstuf.models.study;

import java.time.LocalDateTime;

/**
 * Question model representing a question in a study session
 */
public class Question {
    
    private Long id;
    private Long sessionId;
    private Long answerId;
    private Long noteId;
    private LocalDateTime createdAt;
    
    public Question() {
        // Default constructor
    }
    
    public Question(Long sessionId, Long answerId, Long noteId) {
        this.sessionId = sessionId;
        this.answerId = answerId;
        this.noteId = noteId;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
    
    public Long getAnswerId() {
        return answerId;
    }
    
    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }
    
    public Long getNoteId() {
        return noteId;
    }
    
    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", sessionId=" + sessionId +
                ", noteId=" + noteId +
                ", answerId=" + answerId +
                '}';
    }
}

