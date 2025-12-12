package com.portfolio.defstuf.models.study;

import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.models.note.Note;

import java.util.ArrayList;
import java.util.List;

/**
 * NoteGroup model for grouping notes by title and area
 * Temporary model used during study sessions (not persisted)
 * FR-05.3: Groups notes with the same title in the same area
 */
public class NoteGroup {
    
    private String sharedTitle;  // Shared title for all notes in this group
    private List<Note> notes;    // All notes with this title and area
    private Area area;           // Area (must be the same for all notes)
    
    public NoteGroup() {
        this.notes = new ArrayList<>();
    }
    
    public NoteGroup(String sharedTitle, List<Note> notes, Area area) {
        this.sharedTitle = sharedTitle;
        this.notes = notes != null ? notes : new ArrayList<>();
        this.area = area;
    }
    
    // Getters and Setters
    public String getSharedTitle() {
        return sharedTitle;
    }
    
    public void setSharedTitle(String sharedTitle) {
        this.sharedTitle = sharedTitle;
    }
    
    public List<Note> getNotes() {
        return notes;
    }
    
    public void setNotes(List<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
    }
    
    public Area getArea() {
        return area;
    }
    
    public void setArea(Area area) {
        this.area = area;
    }
    
    /**
     * Gets the number of notes in this group
     */
    public int getNoteCount() {
        return notes != null ? notes.size() : 0;
    }
    
    /**
     * Checks if this group contains multiple notes
     */
    public boolean hasMultipleNotes() {
        return notes != null && notes.size() > 1;
    }
    
    @Override
    public String toString() {
        return "NoteGroup{" +
                "sharedTitle='" + sharedTitle + '\'' +
                ", noteCount=" + getNoteCount() +
                ", area=" + (area != null ? area.getName() : "null") +
                '}';
    }
}


