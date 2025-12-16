package com.portfolio.defstuf.models.study;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ScheduledReview model representing a scheduled review for spaced repetition
 */
public class ScheduledReview {
    
    public enum ReviewStatus {
        PENDING("pending"),
        REVIEWED("reviewed"),
        CANCELLED("cancelled");
        
        private final String value;
        
        ReviewStatus(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static ReviewStatus fromString(String value) {
            if (value == null) return null;
            for (ReviewStatus status : ReviewStatus.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return null;
        }
    }
    
    public enum ReviewQuality {
        FORGOT(1),
        HARD(2),
        GOOD(3),
        EASY(4);
        
        private final int value;
        
        ReviewQuality(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
        public static ReviewQuality fromInt(int value) {
            for (ReviewQuality quality : ReviewQuality.values()) {
                if (quality.value == value) {
                    return quality;
                }
            }
            return null;
        }
    }
    
    private Long id;
    private Long noteId;
    private Long userId;
    private LocalDate scheduledDate;
    private LocalDateTime reviewedDate;
    private java.math.BigDecimal easeFactor;
    private Integer currentInterval;
    private Integer nextInterval;
    private ReviewStatus reviewStatus;
    private ReviewQuality reviewQuality;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public ScheduledReview() {
        // Default constructor
    }
    
    public ScheduledReview(Long noteId, Long userId, LocalDate scheduledDate) {
        this.noteId = noteId;
        this.userId = userId;
        this.scheduledDate = scheduledDate;
        this.easeFactor = new java.math.BigDecimal("2.5");
        this.currentInterval = 1;
        this.reviewStatus = ReviewStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getNoteId() {
        return noteId;
    }
    
    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public LocalDate getScheduledDate() {
        return scheduledDate;
    }
    
    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }
    
    public LocalDateTime getReviewedDate() {
        return reviewedDate;
    }
    
    public void setReviewedDate(LocalDateTime reviewedDate) {
        this.reviewedDate = reviewedDate;
    }
    
    public java.math.BigDecimal getEaseFactor() {
        return easeFactor;
    }
    
    public void setEaseFactor(java.math.BigDecimal easeFactor) {
        this.easeFactor = easeFactor;
    }
    
    public Integer getCurrentInterval() {
        return currentInterval;
    }
    
    public void setCurrentInterval(Integer currentInterval) {
        this.currentInterval = currentInterval;
    }
    
    public Integer getNextInterval() {
        return nextInterval;
    }
    
    public void setNextInterval(Integer nextInterval) {
        this.nextInterval = nextInterval;
    }
    
    public ReviewStatus getReviewStatus() {
        return reviewStatus;
    }
    
    public void setReviewStatus(ReviewStatus reviewStatus) {
        this.reviewStatus = reviewStatus;
    }
    
    public ReviewQuality getReviewQuality() {
        return reviewQuality;
    }
    
    public void setReviewQuality(ReviewQuality reviewQuality) {
        this.reviewQuality = reviewQuality;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "ScheduledReview{" +
                "id=" + id +
                ", noteId=" + noteId +
                ", scheduledDate=" + scheduledDate +
                ", reviewStatus=" + reviewStatus +
                '}';
    }
}







