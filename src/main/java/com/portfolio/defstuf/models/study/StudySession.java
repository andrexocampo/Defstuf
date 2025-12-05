package com.portfolio.defstuf.models.study;

import java.time.LocalDateTime;

/**
 * StudySession model representing a study/review session
 */
public class StudySession {
    
    public enum Status {
        ACTIVE("active"),
        COMPLETED("completed"),
        PAUSED("paused"),
        CANCELLED("cancelled");
        
        private final String value;
        
        Status(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static Status fromString(String value) {
            if (value == null) return null;
            for (Status status : Status.values()) {
                if (status.value.equalsIgnoreCase(value)) {
                    return status;
                }
            }
            return null;
        }
    }
    
    public enum ReviewOrder {
        RANDOM("random"),
        OLDEST_FIRST("oldest_first"),
        HARDEST_FIRST("hardest_first");
        
        private final String value;
        
        ReviewOrder(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static ReviewOrder fromString(String value) {
            if (value == null) return null;
            for (ReviewOrder order : ReviewOrder.values()) {
                if (order.value.equalsIgnoreCase(value)) {
                    return order;
                }
            }
            return null;
        }
    }
    
    private Long id;
    private Long userId;
    private String sessionName;
    private Long areaId;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    // Session configurations
    private Integer sessionDurationMin;
    private Integer cardsLimit;
    private ReviewOrder reviewOrder;
    private Boolean showHints;
    private Integer autoAdvanceSec;
    private Boolean enableBreaks;
    private Integer breakIntervalMin;
    private Integer breakDurationMin;
    private String customConfig;  // JSON as String
    
    public StudySession() {
        // Default constructor
    }
    
    public StudySession(Long userId, String sessionName, Long areaId) {
        this.userId = userId;
        this.sessionName = sessionName;
        this.areaId = areaId;
        this.status = Status.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.sessionDurationMin = 25;
        this.cardsLimit = 20;
        this.reviewOrder = ReviewOrder.RANDOM;
        this.showHints = false;
        this.autoAdvanceSec = 0;
        this.enableBreaks = false;
        this.breakIntervalMin = 25;
        this.breakDurationMin = 5;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getSessionName() {
        return sessionName;
    }
    
    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }
    
    public Long getAreaId() {
        return areaId;
    }
    
    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public Integer getSessionDurationMin() {
        return sessionDurationMin;
    }
    
    public void setSessionDurationMin(Integer sessionDurationMin) {
        this.sessionDurationMin = sessionDurationMin;
    }
    
    public Integer getCardsLimit() {
        return cardsLimit;
    }
    
    public void setCardsLimit(Integer cardsLimit) {
        this.cardsLimit = cardsLimit;
    }
    
    public ReviewOrder getReviewOrder() {
        return reviewOrder;
    }
    
    public void setReviewOrder(ReviewOrder reviewOrder) {
        this.reviewOrder = reviewOrder;
    }
    
    public Boolean getShowHints() {
        return showHints;
    }
    
    public void setShowHints(Boolean showHints) {
        this.showHints = showHints;
    }
    
    public Integer getAutoAdvanceSec() {
        return autoAdvanceSec;
    }
    
    public void setAutoAdvanceSec(Integer autoAdvanceSec) {
        this.autoAdvanceSec = autoAdvanceSec;
    }
    
    public Boolean getEnableBreaks() {
        return enableBreaks;
    }
    
    public void setEnableBreaks(Boolean enableBreaks) {
        this.enableBreaks = enableBreaks;
    }
    
    public Integer getBreakIntervalMin() {
        return breakIntervalMin;
    }
    
    public void setBreakIntervalMin(Integer breakIntervalMin) {
        this.breakIntervalMin = breakIntervalMin;
    }
    
    public Integer getBreakDurationMin() {
        return breakDurationMin;
    }
    
    public void setBreakDurationMin(Integer breakDurationMin) {
        this.breakDurationMin = breakDurationMin;
    }
    
    public String getCustomConfig() {
        return customConfig;
    }
    
    public void setCustomConfig(String customConfig) {
        this.customConfig = customConfig;
    }
    
    @Override
    public String toString() {
        return "StudySession{" +
                "id=" + id +
                ", sessionName='" + sessionName + '\'' +
                ", status=" + status +
                ", areaId=" + areaId +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        StudySession that = (StudySession) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

