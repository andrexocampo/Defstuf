package com.portfolio.defstuf.services.area;

import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.repository.area.AreaRepository;

import java.sql.SQLException;
import java.util.List;

/**
 * Service for area operations
 * Handles business logic for area management
 */
public class AreaService {
    
    private final AreaRepository areaRepository;
    
    public AreaService() {
        this.areaRepository = new AreaRepository();
    }
    
    /**
     * Creates a new area
     * 
     * @param name The area name
     * @param code The area code
     * @return The created area
     * @throws AreaException If creation fails
     */
    public Area createArea(String name, String code) throws AreaException {
        // Validate input
        validateAreaInput(name, code);
        
        // Check if code already exists
        try {
            if (areaRepository.codeExists(code)) {
                throw new AreaException("Area code already exists: " + code);
            }
        } catch (SQLException e) {
            throw new AreaException("Error checking code availability: " + e.getMessage());
        }
        
        // Create and save area
        Area area = new Area(name, code);
        
        try {
            return areaRepository.save(area);
        } catch (SQLException e) {
            throw new AreaException("Error creating area: " + e.getMessage());
        }
    }
    
    /**
     * Updates an existing area
     * 
     * @param id The area ID
     * @param name The new area name
     * @param code The new area code
     * @return The updated area
     * @throws AreaException If update fails
     */
    public Area updateArea(Long id, String name, String code) throws AreaException {
        // Validate input
        validateAreaInput(name, code);
        
        // Check if area exists
        Area existingArea;
        try {
            existingArea = areaRepository.findById(id);
            if (existingArea == null) {
                throw new AreaException("Area not found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new AreaException("Error finding area: " + e.getMessage());
        }
        
        // Check if code already exists for another area
        try {
            if (areaRepository.codeExistsForOtherArea(code, id)) {
                throw new AreaException("Area code already exists for another area: " + code);
            }
        } catch (SQLException e) {
            throw new AreaException("Error checking code availability: " + e.getMessage());
        }
        
        // Update area
        existingArea.setName(name);
        existingArea.setCode(code);
        
        try {
            boolean updated = areaRepository.update(existingArea);
            if (!updated) {
                throw new AreaException("Failed to update area");
            }
            return existingArea;
        } catch (SQLException e) {
            throw new AreaException("Error updating area: " + e.getMessage());
        }
    }
    
    /**
     * Deletes an area
     * 
     * @param id The area ID to delete
     * @throws AreaException If deletion fails
     */
    public void deleteArea(Long id) throws AreaException {
        // Check if area exists
        try {
            Area area = areaRepository.findById(id);
            if (area == null) {
                throw new AreaException("Area not found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new AreaException("Error finding area: " + e.getMessage());
        }
        
        // Delete area
        try {
            boolean deleted = areaRepository.delete(id);
            if (!deleted) {
                throw new AreaException("Failed to delete area");
            }
        } catch (SQLException e) {
            throw new AreaException("Error deleting area: " + e.getMessage());
        }
    }
    
    /**
     * Finds an area by ID
     * 
     * @param id The area ID
     * @return The area if found
     * @throws AreaException If area not found or error occurs
     */
    public Area getAreaById(Long id) throws AreaException {
        try {
            Area area = areaRepository.findById(id);
            if (area == null) {
                throw new AreaException("Area not found with ID: " + id);
            }
            return area;
        } catch (SQLException e) {
            throw new AreaException("Error finding area: " + e.getMessage());
        }
    }
    
    /**
     * Finds an area by code
     * 
     * @param code The area code
     * @return The area if found, null otherwise
     * @throws AreaException If error occurs
     */
    public Area getAreaByCode(String code) throws AreaException {
        try {
            return areaRepository.findByCode(code);
        } catch (SQLException e) {
            throw new AreaException("Error finding area by code: " + e.getMessage());
        }
    }
    
    /**
     * Gets all areas
     * 
     * @return List of all areas
     * @throws AreaException If error occurs
     */
    public List<Area> getAllAreas() throws AreaException {
        try {
            return areaRepository.findAll();
        } catch (SQLException e) {
            throw new AreaException("Error retrieving areas: " + e.getMessage());
        }
    }
    
    /**
     * Searches areas by name
     * 
     * @param name The name to search for
     * @return List of matching areas
     * @throws AreaException If error occurs
     */
    public List<Area> searchAreasByName(String name) throws AreaException {
        if (name == null || name.trim().isEmpty()) {
            return getAllAreas();
        }
        
        try {
            return areaRepository.findByName(name.trim());
        } catch (SQLException e) {
            throw new AreaException("Error searching areas: " + e.getMessage());
        }
    }
    
    /**
     * Validates area input
     * 
     * @param name The area name
     * @param code The area code
     * @throws AreaException If validation fails
     */
    private void validateAreaInput(String name, String code) throws AreaException {
        if (name == null || name.trim().isEmpty()) {
            throw new AreaException("Area name cannot be empty");
        }
        
        if (code == null || code.trim().isEmpty()) {
            throw new AreaException("Area code cannot be empty");
        }
        
        // Validate code format (alphanumeric and underscore, max 50 chars)
        String trimmedCode = code.trim();
        if (trimmedCode.length() > 50) {
            throw new AreaException("Area code must be 50 characters or less");
        }
        
        if (!trimmedCode.matches("^[A-Za-z0-9_]+$")) {
            throw new AreaException("Area code can only contain letters, numbers, and underscores");
        }
    }
    
    /**
     * Custom exception for area operations
     */
    public static class AreaException extends Exception {
        public AreaException(String message) {
            super(message);
        }
        
        public AreaException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

