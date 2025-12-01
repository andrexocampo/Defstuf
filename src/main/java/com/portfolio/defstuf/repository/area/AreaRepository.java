package com.portfolio.defstuf.repository.area;

import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.repository.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Area entity database operations
 */
public class AreaRepository {
    
    /**
     * Finds an area by ID
     * 
     * @param id The area ID
     * @return Area object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Area findById(Long id) throws SQLException {
        String sql = "SELECT id, name, code, created_at FROM areas WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArea(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds an area by code
     * 
     * @param code The area code
     * @return Area object if found, null otherwise
     * @throws SQLException If database error occurs
     */
    public Area findByCode(String code) throws SQLException {
        String sql = "SELECT id, name, code, created_at FROM areas WHERE code = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToArea(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Finds areas by name (partial match, case-insensitive)
     * 
     * @param name The area name to search for
     * @return List of Area objects matching the name
     * @throws SQLException If database error occurs
     */
    public List<Area> findByName(String name) throws SQLException {
        String sql = "SELECT id, name, code, created_at FROM areas WHERE name LIKE ? ORDER BY name";
        List<Area> areas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + name + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    areas.add(mapResultSetToArea(rs));
                }
            }
        }
        return areas;
    }
    
    /**
     * Finds all areas
     * 
     * @return List of all Area objects
     * @throws SQLException If database error occurs
     */
    public List<Area> findAll() throws SQLException {
        String sql = "SELECT id, name, code, created_at FROM areas ORDER BY name";
        List<Area> areas = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                areas.add(mapResultSetToArea(rs));
            }
        }
        return areas;
    }
    
    /**
     * Saves a new area to the database
     * 
     * @param area The area to save
     * @return The saved area with generated ID
     * @throws SQLException If database error occurs
     */
    public Area save(Area area) throws SQLException {
        String sql = "INSERT INTO areas (name, code, created_at) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, area.getName());
            stmt.setString(2, area.getCode());
            stmt.setTimestamp(3, Timestamp.valueOf(area.getCreatedAt() != null ? 
                    area.getCreatedAt() : LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating area failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    area.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating area failed, no ID obtained.");
                }
            }
        }
        return area;
    }
    
    /**
     * Updates an existing area in the database
     * 
     * @param area The area to update
     * @return true if update was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean update(Area area) throws SQLException {
        String sql = "UPDATE areas SET name = ?, code = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, area.getName());
            stmt.setString(2, area.getCode());
            stmt.setLong(3, area.getId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Deletes an area from the database
     * 
     * @param id The area ID to delete
     * @return true if deletion was successful, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean delete(Long id) throws SQLException {
        String sql = "DELETE FROM areas WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    /**
     * Checks if a code already exists in the database
     * 
     * @param code The code to check
     * @return true if code exists, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean codeExists(String code) throws SQLException {
        return findByCode(code) != null;
    }
    
    /**
     * Checks if a code exists for a different area (used for updates)
     * 
     * @param code The code to check
     * @param excludeId The ID to exclude from the check
     * @return true if code exists for another area, false otherwise
     * @throws SQLException If database error occurs
     */
    public boolean codeExistsForOtherArea(String code, Long excludeId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM areas WHERE code = ? AND id != ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            stmt.setLong(2, excludeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Maps a ResultSet row to an Area object
     */
    private Area mapResultSetToArea(ResultSet rs) throws SQLException {
        Area area = new Area();
        area.setId(rs.getLong("id"));
        area.setName(rs.getString("name"));
        area.setCode(rs.getString("code"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            area.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return area;
    }
}

