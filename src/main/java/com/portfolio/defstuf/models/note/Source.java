package com.portfolio.defstuf.models.note;

import java.time.LocalDateTime;

/**
 * Source model representing a note source in the system
 */
public class Source {
    
    private Long id;
    private String name;
    private String code;
    private LocalDateTime createdAt;
    
    public Source() {
        // Default constructor
    }
    
    public Source(String name, String code) {
        this.name = name;
        this.code = code;
    }
    
    public Source(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return name; // Para mostrar en ComboBox
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Source source = (Source) o;
        return id != null ? id.equals(source.id) : source.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

