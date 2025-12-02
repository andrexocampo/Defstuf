package com.portfolio.defstuf.models.note;

import java.time.LocalDateTime;

/**
 * Note model representing a note in the system
 */
public class Note {
    
    private Long id;
    private Long userId;
    private String title;
    private String source;
    private String description;
    private Long areaId;
    private Long noteTypeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Note() {
        // Default constructor
    }
    
    public Note(Long userId, String title, String source, String description, 
                Long areaId, Long noteTypeId) {
        this.userId = userId;
        this.title = title;
        this.source = source;
        this.description = description;
        this.areaId = areaId;
        this.noteTypeId = noteTypeId;
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getAreaId() {
        return areaId;
    }
    
    public void setAreaId(Long areaId) {
        this.areaId = areaId;
    }
    
    public Long getNoteTypeId() {
        return noteTypeId;
    }
    
    public void setNoteTypeId(Long noteTypeId) {
        this.noteTypeId = noteTypeId;
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
        return "Note{" +
                "id=" + id +
                ", userId=" + userId +
                ", title='" + title + '\'' +
                ", source='" + source + '\'' +
                ", areaId=" + areaId +
                ", noteTypeId=" + noteTypeId +
                '}';
    }
}

