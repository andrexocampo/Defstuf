package com.portfolio.defstuf.models.note;

import java.time.LocalDateTime;

/**
 * NoteImage model representing an image associated with a note
 */
public class NoteImage {
    
    private Long id;
    private Long noteId;
    private String imagePath;
    private Long fileSize;
    private String mimeType;
    private LocalDateTime createdAt;
    
    public NoteImage() {
        // Default constructor
    }
    
    public NoteImage(Long noteId, String imagePath, Long fileSize, String mimeType) {
        this.noteId = noteId;
        this.imagePath = imagePath;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.createdAt = LocalDateTime.now();
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
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getMimeType() {
        return mimeType;
    }
    
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

