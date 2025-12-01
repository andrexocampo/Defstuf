package com.portfolio.defstuf.services.area;

import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.repository.DatabaseConnection;
import com.portfolio.defstuf.repository.DatabaseInitializer;

import java.sql.SQLException;
import java.util.List;

/**
 * Test class for Area management functionality
 * Tests all CRUD operations and validations
 */
public class AreaTest {
    
    private AreaService areaService;
    private Area createdArea1;
    private Area createdArea2;
    private Area createdArea3;
    
    public AreaTest() {
        this.areaService = new AreaService();
    }
    
    /**
     * Runs all area management tests
     */
    public void runAllTests() {
        System.out.println("=========================================");
        System.out.println("  AREA MANAGEMENT TESTS");
        System.out.println("=========================================\n");
        
        // Initialize database first
        System.out.println("[SETUP] Initializing database...");
        try {
            DatabaseInitializer.initializeDatabase();
            System.out.println("✓ Database initialized\n");
        } catch (Exception e) {
            System.err.println("✗ Error initializing database: " + e.getMessage());
            return;
        }
        
        // Run tests
        testCreateArea();
        testCreateMultipleAreas();
        testGetAllAreas();
        testGetAreaById();
        testGetAreaByCode();
        testSearchAreasByName();
        testUpdateArea();
        testValidationEmptyName();
        testValidationEmptyCode();
        testValidationInvalidCodeFormat();
        testValidationDuplicateCode();
        testValidationDuplicateCodeOnUpdate();
        testDeleteArea();
        testGetAllAreasAfterDeletion();
        
        System.out.println("\n=========================================");
        System.out.println("  ALL TESTS COMPLETED");
        System.out.println("=========================================");
    }
    
    /**
     * Test creating a single area
     */
    private void testCreateArea() {
        System.out.println("[TEST 1] Create area...");
        try {
            createdArea1 = areaService.createArea("Mathematics", "MATH");
            System.out.println("✓ Area created successfully");
            System.out.println("  - ID: " + createdArea1.getId());
            System.out.println("  - Name: " + createdArea1.getName());
            System.out.println("  - Code: " + createdArea1.getCode());
            System.out.println("  - Created At: " + createdArea1.getCreatedAt());
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error creating area: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test creating multiple areas
     */
    private void testCreateMultipleAreas() {
        System.out.println("[TEST 2] Create multiple areas...");
        try {
            createdArea2 = areaService.createArea("Physics", "PHYS");
            createdArea3 = areaService.createArea("Chemistry", "CHEM");
            System.out.println("✓ Created 2 additional areas");
            System.out.println("  - " + createdArea2.getName() + " (" + createdArea2.getCode() + ")");
            System.out.println("  - " + createdArea3.getName() + " (" + createdArea3.getCode() + ")");
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error creating areas: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test getting all areas
     */
    private void testGetAllAreas() {
        System.out.println("[TEST 3] Get all areas...");
        try {
            List<Area> areas = areaService.getAllAreas();
            System.out.println("✓ Found " + areas.size() + " area(s)");
            for (Area area : areas) {
                System.out.println("  - " + area.getName() + " (" + area.getCode() + ") [ID: " + area.getId() + "]");
            }
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error getting all areas: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test getting area by ID
     */
    private void testGetAreaById() {
        System.out.println("[TEST 4] Get area by ID...");
        if (createdArea1 == null) {
            System.out.println("⚠ Skipped: No area created yet");
            System.out.println();
            return;
        }
        
        try {
            Area foundArea = areaService.getAreaById(createdArea1.getId());
            System.out.println("✓ Area found by ID");
            System.out.println("  - Name: " + foundArea.getName());
            System.out.println("  - Code: " + foundArea.getCode());
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error finding area by ID: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test getting area by code
     */
    private void testGetAreaByCode() {
        System.out.println("[TEST 5] Get area by code...");
        if (createdArea2 == null) {
            System.out.println("⚠ Skipped: No area created yet");
            System.out.println();
            return;
        }
        
        try {
            Area foundArea = areaService.getAreaByCode("PHYS");
            if (foundArea != null) {
                System.out.println("✓ Area found by code");
                System.out.println("  - Name: " + foundArea.getName());
                System.out.println("  - ID: " + foundArea.getId());
            } else {
                System.out.println("✗ Area not found by code");
            }
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error finding area by code: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test searching areas by name
     */
    private void testSearchAreasByName() {
        System.out.println("[TEST 6] Search areas by name...");
        try {
            List<Area> areas = areaService.searchAreasByName("Math");
            System.out.println("✓ Search completed");
            System.out.println("  - Found " + areas.size() + " area(s) matching 'Math'");
            for (Area area : areas) {
                System.out.println("    * " + area.getName() + " (" + area.getCode() + ")");
            }
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error searching areas: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test updating an area
     */
    private void testUpdateArea() {
        System.out.println("[TEST 7] Update area...");
        if (createdArea1 == null) {
            System.out.println("⚠ Skipped: No area created yet");
            System.out.println();
            return;
        }
        
        try {
            Area updatedArea = areaService.updateArea(
                createdArea1.getId(), 
                "Advanced Mathematics", 
                "ADV_MATH"
            );
            System.out.println("✓ Area updated successfully");
            System.out.println("  - Old Name: Mathematics");
            System.out.println("  - New Name: " + updatedArea.getName());
            System.out.println("  - Old Code: MATH");
            System.out.println("  - New Code: " + updatedArea.getCode());
            
            // Update back for consistency
            createdArea1 = updatedArea;
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error updating area: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test validation: empty name
     */
    private void testValidationEmptyName() {
        System.out.println("[TEST 8] Validation: Empty name...");
        try {
            areaService.createArea("", "TEST");
            System.err.println("✗ Validation failed: Should have thrown exception for empty name");
        } catch (AreaService.AreaException e) {
            System.out.println("✓ Validation passed: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test validation: empty code
     */
    private void testValidationEmptyCode() {
        System.out.println("[TEST 9] Validation: Empty code...");
        try {
            areaService.createArea("Test Area", "");
            System.err.println("✗ Validation failed: Should have thrown exception for empty code");
        } catch (AreaService.AreaException e) {
            System.out.println("✓ Validation passed: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test validation: invalid code format
     */
    private void testValidationInvalidCodeFormat() {
        System.out.println("[TEST 10] Validation: Invalid code format...");
        try {
            areaService.createArea("Test Area", "TEST-CODE!"); // Invalid: contains special characters
            System.err.println("✗ Validation failed: Should have thrown exception for invalid code format");
        } catch (AreaService.AreaException e) {
            System.out.println("✓ Validation passed: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test validation: duplicate code
     */
    private void testValidationDuplicateCode() {
        System.out.println("[TEST 11] Validation: Duplicate code...");
        try {
            areaService.createArea("Another Physics", "PHYS"); // Duplicate of createdArea2
            System.err.println("✗ Validation failed: Should have thrown exception for duplicate code");
        } catch (AreaService.AreaException e) {
            System.out.println("✓ Validation passed: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test validation: duplicate code on update
     */
    private void testValidationDuplicateCodeOnUpdate() {
        System.out.println("[TEST 12] Validation: Duplicate code on update...");
        if (createdArea1 == null || createdArea2 == null) {
            System.out.println("⚠ Skipped: Not enough areas created");
            System.out.println();
            return;
        }
        
        try {
            // Try to update area1 with area2's code
            areaService.updateArea(createdArea1.getId(), "Test", createdArea2.getCode());
            System.err.println("✗ Validation failed: Should have thrown exception for duplicate code");
        } catch (AreaService.AreaException e) {
            System.out.println("✓ Validation passed: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test deleting an area
     */
    private void testDeleteArea() {
        System.out.println("[TEST 13] Delete area...");
        if (createdArea3 == null) {
            System.out.println("⚠ Skipped: No area to delete");
            System.out.println();
            return;
        }
        
        try {
            areaService.deleteArea(createdArea3.getId());
            System.out.println("✓ Area deleted successfully");
            System.out.println("  - Deleted: " + createdArea3.getName() + " (" + createdArea3.getCode() + ")");
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error deleting area: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Test getting all areas after deletion
     */
    private void testGetAllAreasAfterDeletion() {
        System.out.println("[TEST 14] Get all areas after deletion...");
        try {
            List<Area> areas = areaService.getAllAreas();
            System.out.println("✓ Found " + areas.size() + " area(s) remaining");
            for (Area area : areas) {
                System.out.println("  - " + area.getName() + " (" + area.getCode() + ") [ID: " + area.getId() + "]");
            }
        } catch (AreaService.AreaException e) {
            System.err.println("✗ Error getting all areas: " + e.getMessage());
        }
        System.out.println();
    }
    
    /**
     * Main method to run tests directly
     */
    public static void main(String[] args) {
        AreaTest tester = new AreaTest();
        tester.runAllTests();
        
        // Close database connection
        try {
            DatabaseConnection.getInstance().closeConnection();
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}

