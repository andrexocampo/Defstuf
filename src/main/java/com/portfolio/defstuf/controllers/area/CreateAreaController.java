package com.portfolio.defstuf.controllers.area;

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
 * Controller for Create Area view
 * RF-02.2: Form to create new area with validations
 */
public class CreateAreaController {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField codeField;
    
    @FXML
    private Label nameErrorLabel;
    
    @FXML
    private Label codeErrorLabel;
    
    @FXML
    private Button createButton;
    
    @FXML
    private Button cancelButton;
    
    private Stage primaryStage;
    private AreaService areaService;
    
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
     * Handles the Create button click
     */
    @FXML
    private void handleCreate() {
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
        
        // Try to create the area
        try {
            areaService.createArea(name, code);
            
            // Show success message
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Area created successfully!");
            alert.showAndWait();
            
            // Navigate back to Manage Areas view
            navigateToManageAreas();
            
        } catch (AreaService.AreaException e) {
            // Show specific error message
            if (e.getMessage().contains("already exists")) {
                codeErrorLabel.setText("Code already exists. Please use a different code.");
            } else if (e.getMessage().contains("cannot be empty")) {
                if (e.getMessage().contains("name")) {
                    nameErrorLabel.setText("Name cannot be empty");
                } else if (e.getMessage().contains("code")) {
                    codeErrorLabel.setText("Code cannot be empty");
                }
            } else if (e.getMessage().contains("characters or less") || 
                      e.getMessage().contains("only contain")) {
                codeErrorLabel.setText(e.getMessage());
            } else {
                showError("Error creating area: " + e.getMessage());
            }
        }
    }
    
    /**
     * Handles the Cancel button click
     */
    @FXML
    private void handleCancel() {
        navigateToManageAreas();
    }
    
    /**
     * Navigates back to Manage Areas view
     */
    private void navigateToManageAreas() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/area/ManageAreasView.fxml")
            );
            Parent root = loader.load();
            
            ManageAreasController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            // Refresh the areas list
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

