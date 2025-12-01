package com.portfolio.defstuf.controllers.area;

import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.services.area.AreaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for Edit Area view
 * RF-02.3: Form to edit existing area with pre-filled data
 */
public class EditAreaController {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField codeField;
    
    @FXML
    private Label nameErrorLabel;
    
    @FXML
    private Label codeErrorLabel;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private Stage primaryStage;
    private AreaService areaService;
    private Area currentArea;
    
    @FXML
    private void initialize() {
        areaService = new AreaService();
        clearErrors();
    }
    
    /**
     * Sets the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Sets the area to edit and pre-fills the form
     */
    public void setArea(Area area) {
        this.currentArea = area;
        if (area != null) {
            nameField.setText(area.getName());
            codeField.setText(area.getCode());
        }
    }
    
    /**
     * Handles the Save Changes button click
     */
    @FXML
    private void handleSave() {
        if (currentArea == null) {
            showError("No area selected for editing");
            return;
        }
        
        clearErrors();
        
        String name = nameField.getText().trim();
        String code = codeField.getText().trim();
        
        // Validate inputs
        boolean isValid = true;
        
        if (name.isEmpty()) {
            nameErrorLabel.setText("Name is required");
            isValid = false;
        }
        
        if (code.isEmpty()) {
            codeErrorLabel.setText("Code is required");
            isValid = false;
        }
        
        if (!isValid) {
            return;
        }
        
        // Check if nothing changed
        if (name.equals(currentArea.getName()) && code.equals(currentArea.getCode())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("No changes detected.");
            alert.showAndWait();
            handleCancel();
            return;
        }
        
        // Try to update the area
        try {
            Area updatedArea = areaService.updateArea(currentArea.getId(), name, code);
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Area updated successfully!");
            alert.showAndWait();
            
            // Navigate back to Area Detail view
            navigateBackToDetail(updatedArea);
            
        } catch (AreaService.AreaException e) {
            // Show specific error message
            if (e.getMessage().contains("already exists")) {
                codeErrorLabel.setText("Code already exists for another area. Please use a different code.");
            } else if (e.getMessage().contains("cannot be empty")) {
                if (e.getMessage().contains("name")) {
                    nameErrorLabel.setText("Name cannot be empty");
                } else if (e.getMessage().contains("code")) {
                    codeErrorLabel.setText("Code cannot be empty");
                }
            } else if (e.getMessage().contains("characters or less") || 
                      e.getMessage().contains("only contain")) {
                codeErrorLabel.setText(e.getMessage());
            } else if (e.getMessage().contains("not found")) {
                showError(e.getMessage());
                handleCancel();
            } else {
                showError("Error updating area: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handles the Cancel button click
     */
    @FXML
    private void handleCancel() {
        if (currentArea != null) {
            navigateBackToDetail(currentArea);
        } else {
            // Fallback: navigate to Manage Areas
            navigateToManageAreas();
        }
    }
    
    /**
     * Navigates back to Area Detail view
     */
    private void navigateBackToDetail(Area area) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/area/AreaDetailView.fxml")
            );
            Parent root = loader.load();
            
            AreaDetailController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setArea(area);
            
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            primaryStage.setTitle("DefStuf - Area Detail");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error navigating to area detail: " + e.getMessage());
        }
    }
    
    /**
     * Navigates to Manage Areas view (fallback)
     */
    private void navigateToManageAreas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/area/ManageAreasView.fxml")
            );
            Parent root = loader.load();
            
            ManageAreasController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.refreshAreas();
            
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            primaryStage.setTitle("DefStuf - Manage Areas");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error navigating to manage areas: " + e.getMessage());
        }
    }
    
    /**
     * Clears all error labels
     */
    private void clearErrors() {
        nameErrorLabel.setText("");
        codeErrorLabel.setText("");
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

