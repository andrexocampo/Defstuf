package com.portfolio.defstuf.models.note;

/**
 * NoteType model representing a note type in the system
 */
public class NoteType {
    
    private Long id;
    private String name;
    
    public NoteType() {
        // Default constructor
    }
    
    public NoteType(String name) {
        this.name = name;
    }
    
    public NoteType(Long id, String name) {
        this.id = id;
        this.name = name;
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
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        NoteType noteType = (NoteType) o;
        return id != null ? id.equals(noteType.id) : noteType.id == null;
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

