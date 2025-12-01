package com.portfolio.defstuf.models.area;

import java.time.LocalDateTime;

/**
 * Area model representing an area in the system
 */
public class Area {
    
    private Long id;
    private String name;
    private String code;
    private LocalDateTime createdAt;
    
    public Area() {
        // Default constructor
    }
    
    public Area(String name, String code) {
        this.name = name;
        this.code = code;
        this.createdAt = LocalDateTime.now();
    }
    
    public Area(Long id, String name, String code, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.createdAt = createdAt;
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
        return "Area{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Area area = (Area) o;
        
        return id != null ? id.equals(area.id) : area.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

