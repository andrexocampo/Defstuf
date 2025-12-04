package com.portfolio.defstuf.services.note;

import com.portfolio.defstuf.models.note.Source;
import com.portfolio.defstuf.repository.note.SourceRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Service for source operations
 * Handles business logic for source management
 */
public class SourceService {
    
    private final SourceRepository sourceRepository;
    
    public SourceService() {
        this.sourceRepository = new SourceRepository();
    }
    
    /**
     * Creates a new source
     * 
     * @param name The source name
     * @param code The source code (optional, will be auto-generated from name if null/empty)
     * @return The created source
     * @throws SourceException If creation fails
     */
    public Source createSource(String name, String code) throws SourceException {
        // Validate input
        validateSourceInput(name);
        
        // Auto-generate code from name if not provided
        String finalCode = (code == null || code.trim().isEmpty()) ? generateCodeFromName(name) : code.trim();
        
        // Check if name already exists
        try {
            if (sourceRepository.nameExists(name.trim())) {
                throw new SourceException("Source name already exists: " + name);
            }
        } catch (SQLException e) {
            throw new SourceException("Error checking name availability: " + e.getMessage(), e);
        }
        
        // Check if code already exists
        try {
            if (sourceRepository.codeExists(finalCode)) {
                throw new SourceException("Source code already exists: " + finalCode);
            }
        } catch (SQLException e) {
            throw new SourceException("Error checking code availability: " + e.getMessage(), e);
        }
        
        // Create and save source
        Source source = new Source(name.trim(), finalCode);
        
        try {
            return sourceRepository.save(source);
        } catch (SQLException e) {
            throw new SourceException("Error creating source: " + e.getMessage(), e);
        }
    }
    
    /**
     * Updates an existing source
     * 
     * @param id The source ID
     * @param name The new source name
     * @param code The new source code (optional, will be auto-generated from name if null/empty)
     * @return The updated source
     * @throws SourceException If update fails
     */
    public Source updateSource(Long id, String name, String code) throws SourceException {
        // Validate input
        validateSourceInput(name);
        
        String finalCode = (code == null || code.trim().isEmpty()) ? generateCodeFromName(name) : code.trim();
        
        // Check if source exists
        Optional<Source> existingSourceOpt;
        try {
            existingSourceOpt = sourceRepository.findById(id);
            if (existingSourceOpt.isEmpty()) {
                throw new SourceException("Source not found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new SourceException("Error finding source: " + e.getMessage(), e);
        }
        
        Source existingSource = existingSourceOpt.get();
        
        // Check if name already exists for another source
        try {
            if (sourceRepository.nameExistsForOtherSource(name.trim(), id)) {
                throw new SourceException("Source name already exists for another source: " + name);
            }
        } catch (SQLException e) {
            throw new SourceException("Error checking name availability: " + e.getMessage(), e);
        }
        
        // Check if code already exists for another source
        try {
            if (sourceRepository.codeExistsForOtherSource(finalCode, id)) {
                throw new SourceException("Source code already exists for another source: " + finalCode);
            }
        } catch (SQLException e) {
            throw new SourceException("Error checking code availability: " + e.getMessage(), e);
        }
        
        // Update source
        existingSource.setName(name.trim());
        existingSource.setCode(finalCode);
        
        try {
            boolean updated = sourceRepository.update(existingSource);
            if (!updated) {
                throw new SourceException("Failed to update source");
            }
            return existingSource;
        } catch (SQLException e) {
            throw new SourceException("Error updating source: " + e.getMessage(), e);
        }
    }
    
    /**
     * Deletes a source
     * 
     * @param id The source ID to delete
     * @throws SourceException If deletion fails
     */
    public void deleteSource(Long id) throws SourceException {
        // Check if source exists
        try {
            Optional<Source> sourceOpt = sourceRepository.findById(id);
            if (sourceOpt.isEmpty()) {
                throw new SourceException("Source not found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new SourceException("Error finding source: " + e.getMessage(), e);
        }
        
        // Delete source
        try {
            boolean deleted = sourceRepository.delete(id);
            if (!deleted) {
                throw new SourceException("Failed to delete source");
            }
        } catch (SQLException e) {
            throw new SourceException("Error deleting source: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets a source by ID
     * 
     * @param id The source ID
     * @return The source if found
     * @throws SourceException If source not found or error occurs
     */
    public Source getSourceById(Long id) throws SourceException {
        try {
            Optional<Source> sourceOpt = sourceRepository.findById(id);
            if (sourceOpt.isEmpty()) {
                throw new SourceException("Source not found with ID: " + id);
            }
            return sourceOpt.get();
        } catch (SQLException e) {
            throw new SourceException("Error finding source: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets a source by code
     * 
     * @param code The source code
     * @return The source if found, empty otherwise
     * @throws SourceException If error occurs
     */
    public Optional<Source> getSourceByCode(String code) throws SourceException {
        try {
            return sourceRepository.findByCode(code);
        } catch (SQLException e) {
            throw new SourceException("Error finding source by code: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets all sources
     * 
     * @return List of all sources
     * @throws SourceException If error occurs
     */
    public List<Source> getAllSources() throws SourceException {
        try {
            return sourceRepository.findAll();
        } catch (SQLException e) {
            throw new SourceException("Error retrieving sources: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generates a code from a name (lowercase, spaces to underscores, special chars removed)
     */
    private String generateCodeFromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "source_" + System.currentTimeMillis();
        }
        
        // Convert to lowercase, replace spaces with underscores, remove special characters
        String code = name.trim()
            .toLowerCase()
            .replaceAll("\\s+", "_")
            .replaceAll("[^a-z0-9_]", "")
            .replaceAll("_+", "_")
            .replaceAll("^_|_$", "");
        
        // Limit to 50 characters
        if (code.length() > 50) {
            code = code.substring(0, 50);
        }
        
        // If code is empty after processing, generate one
        if (code.isEmpty()) {
            code = "source_" + System.currentTimeMillis();
        }
        
        return code;
    }
    
    /**
     * Validates source input
     * 
     * @param name The source name
     * @throws SourceException If validation fails
     */
    private void validateSourceInput(String name) throws SourceException {
        if (name == null || name.trim().isEmpty()) {
            throw new SourceException("Source name cannot be empty");
        }
        
        if (name.trim().length() > 255) {
            throw new SourceException("Source name must be 255 characters or less");
        }
    }
    
    /**
     * Custom exception for source operations
     */
    public static class SourceException extends Exception {
        public SourceException(String message) {
            super(message);
        }
        
        public SourceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

