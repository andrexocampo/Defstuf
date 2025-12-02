package com.portfolio.defstuf.services.note;

import com.portfolio.defstuf.models.note.Note;
import com.portfolio.defstuf.models.note.NoteImage;
import com.portfolio.defstuf.models.note.NoteType;
import com.portfolio.defstuf.repository.note.ImageRepository;
import com.portfolio.defstuf.repository.note.NoteRepository;
import com.portfolio.defstuf.repository.note.NoteTypeRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for note operations
 * Handles business logic for note management
 */
public class NoteService {
    
    private static final String DEFAULT_SOURCE = "personal";
    
    private final NoteRepository noteRepository;
    private final NoteTypeRepository noteTypeRepository;
    private final ImageRepository imageRepository;
    
    public NoteService() {
        this.noteRepository = new NoteRepository();
        this.noteTypeRepository = new NoteTypeRepository();
        this.imageRepository = new ImageRepository();
    }
    
    /**
     * Creates a new note with images
     * 
     * @param userId The user ID creating the note
     * @param title The note title (REQUIRED, cannot be empty)
     * @param source The note source (optional, defaults to "personal" if empty)
     * @param description The note description (optional, can be empty)
     * @param areaId The area ID (optional)
     * @param noteTypeId The note type ID (optional)
     * @param imagePaths List of image file paths (optional)
     * @param fileSizes List of image file sizes (optional)
     * @param mimeTypes List of image MIME types (optional)
     * @return The created note
     * @throws NoteException If validation fails or database error occurs
     */
    public Note createNote(Long userId, String title, String source, String description,
                          Long areaId, Long noteTypeId, List<String> imagePaths, 
                          List<Long> fileSizes, List<String> mimeTypes) throws NoteException {
        // Validate title (REQUIRED)
        if (title == null || title.trim().isEmpty()) {
            throw new NoteException("Note title cannot be empty");
        }
        
        // Set default source if empty or null
        String finalSource = (source == null || source.trim().isEmpty()) 
            ? DEFAULT_SOURCE 
            : source.trim();
        
        // Description can be null or empty (no validation needed)
        String finalDescription = (description == null || description.trim().isEmpty()) 
            ? null 
            : description.trim();
        
        // Create note
        Note note = new Note(
            userId, 
            title.trim(), 
            finalSource,  // Always has a value (default "personal" if empty)
            finalDescription,  // Can be null
            areaId, 
            noteTypeId
        );
        
        try {
            // Save note
            note = noteRepository.save(note);
            
            // Save images if any
            if (imagePaths != null && !imagePaths.isEmpty()) {
                for (int i = 0; i < imagePaths.size(); i++) {
                    NoteImage image = new NoteImage(
                        note.getId(),
                        imagePaths.get(i),
                        fileSizes != null && i < fileSizes.size() ? fileSizes.get(i) : null,
                        mimeTypes != null && i < mimeTypes.size() ? mimeTypes.get(i) : "image/png"
                    );
                    imageRepository.save(image);
                }
            }
            
            return note;
        } catch (SQLException e) {
            throw new NoteException("Error creating note: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets all note types
     */
    public List<NoteType> getAllNoteTypes() throws NoteException {
        try {
            return noteTypeRepository.findAll();
        } catch (SQLException e) {
            throw new NoteException("Error retrieving note types: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets note type by name (creates it if it doesn't exist)
     */
    public NoteType getOrCreateNoteType(String name) throws NoteException {
        try {
            return noteTypeRepository.createIfNotExists(name);
        } catch (SQLException e) {
            throw new NoteException("Error getting/creating note type: " + e.getMessage(), e);
        }
    }
    
    /**
     * Custom exception for note operations
     */
    public static class NoteException extends Exception {
        public NoteException(String message) {
            super(message);
        }
        
        public NoteException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

