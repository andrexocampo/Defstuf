package com.portfolio.defstuf.services.note;

import com.portfolio.defstuf.models.note.Note;
import com.portfolio.defstuf.models.note.NoteImage;
import com.portfolio.defstuf.models.note.NoteType;
import com.portfolio.defstuf.models.note.Source;
import com.portfolio.defstuf.repository.note.ImageRepository;
import com.portfolio.defstuf.repository.note.NoteRepository;
import com.portfolio.defstuf.repository.note.NoteTypeRepository;
import com.portfolio.defstuf.repository.note.SourceRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service for note operations
 * Handles business logic for note management
 */
public class NoteService {
    
    private static final String DEFAULT_SOURCE_CODE = "personal";
    
    private final NoteRepository noteRepository;
    private final NoteTypeRepository noteTypeRepository;
    private final SourceRepository sourceRepository;
    private final ImageRepository imageRepository;
    
    public NoteService() {
        this.noteRepository = new NoteRepository();
        this.noteTypeRepository = new NoteTypeRepository();
        this.sourceRepository = new SourceRepository();
        this.imageRepository = new ImageRepository();
    }
    
    /**
     * Creates a new note with images
     * 
     * @param userId The user ID creating the note
     * @param title The note title (REQUIRED, cannot be empty)
     * @param sourceId The note source ID (optional, defaults to "personal" source if null)
     * @param description The note description (optional, can be empty)
     * @param areaId The area ID (optional)
     * @param noteTypeId The note type ID (optional)
     * @param imagePaths List of image file paths (optional)
     * @param fileSizes List of image file sizes (optional)
     * @param mimeTypes List of image MIME types (optional)
     * @return The created note
     * @throws NoteException If validation fails or database error occurs
     */
    public Note createNote(Long userId, String title, Long sourceId, String description,
                          Long areaId, Long noteTypeId, List<String> imagePaths, 
                          List<Long> fileSizes, List<String> mimeTypes) throws NoteException {
        // Validate title (REQUIRED)
        if (title == null || title.trim().isEmpty()) {
            throw new NoteException("Note title cannot be empty");
        }
        
        // Set default source if null
        Long finalSourceId = sourceId;
        if (finalSourceId == null) {
            try {
                Optional<Source> personalSource = sourceRepository.findByCode(DEFAULT_SOURCE_CODE);
                finalSourceId = personalSource.map(Source::getId).orElse(null);
            } catch (SQLException e) {
                throw new NoteException("Error getting default source: " + e.getMessage(), e);
            }
        }
        
        // Description can be null or empty (no validation needed)
        String finalDescription = (description == null || description.trim().isEmpty()) 
            ? null 
            : description.trim();
        
        // Create note
        Note note = new Note(
            userId, 
            title.trim(), 
            finalSourceId,  // Can be null if "personal" source doesn't exist
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
     * Gets all available sources
     */
    public List<Source> getAllSources() throws NoteException {
        try {
            return sourceRepository.findAll();
        } catch (SQLException e) {
            throw new NoteException("Error retrieving sources: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets a source by code (useful for default values)
     */
    public Optional<Source> getSourceByCode(String code) throws NoteException {
        try {
            return sourceRepository.findByCode(code);
        } catch (SQLException e) {
            throw new NoteException("Error getting source by code: " + e.getMessage(), e);
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

