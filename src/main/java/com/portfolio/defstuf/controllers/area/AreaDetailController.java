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
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

/**
 * Controller for Area Detail view
 * RF-02.4: Shows detailed information about a specific area
 */
public class AreaDetailController {
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Label nameLabel;
    
    @FXML
    private Label codeLabel;
    
    @FXML
    private Label createdLabel;
    
    @FXML
    private Label statisticsLabel;
    
    @FXML
    private Label notesLabel;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button deleteButton;
    
    private Stage primaryStage;
    private AreaService areaService;
    private Area currentArea;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm");
    
    @FXML
    private void initialize() {
        areaService = new AreaService();
    }
    
    /**
     * Sets the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Sets the area to display and loads its information
     */
    public void setArea(Area area) {
        this.currentArea = area;
        if (area != null) {
            loadAreaDetails();
        }
    }
    
    /**
     * Loads and displays area details
     */
    private void loadAreaDetails() {
        if (currentArea == null) {
            return;
        }
        
        try {
            // Reload area to ensure we have latest data
            Area area = areaService.getAreaById(currentArea.getId());
            if (area == null) {
                showError("Area not found");
                handleBack();
                return;
            }
            
            currentArea = area;
            
            // Update UI
            titleLabel.setText(area.getName() + " - Details");
            nameLabel.setText(area.getName());
            codeLabel.setText(area.getCode());
            
            if (area.getCreatedAt() != null) {
                createdLabel.setText(area.getCreatedAt().format(DATE_FORMATTER));
            } else {
                createdLabel.setText("Unknown");
            }
            
            // TODO: Load statistics when implemented
            statisticsLabel.setText("No statistics available yet. Future: total notes, review pending, etc.");
            
            // TODO: Load notes count when Note entity is implemented
            notesLabel.setText("No notes in this area yet. Future: list of notes, add note button, etc.");
            
        } catch (AreaService.AreaException e) {
            showError("Error loading area details: " + e.getMessage());
        }
    }
    
    /**
     * Handles the Edit button click - navigates to Edit Area view
     */
    @FXML
    private void handleEdit() {
        if (currentArea == null) {
            showError("No area selected");
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/area/EditAreaView.fxml")
            );
            Parent root = loader.load();
            
            EditAreaController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setArea(currentArea);
            
            Scene scene = new Scene(root, 500, 400);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            primaryStage.setTitle("DefStuf - Edit Area");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening edit area view: " + e.getMessage());
        }
    }
    
    /**
     * Handles the Delete button click - deletes the area with confirmation
     */
    @FXML
    private void handleDelete() {
        if (currentArea == null) {
            showError("No area selected");
            return;
        }
        
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Area");
        confirmAlert.setHeaderText("Are you sure you want to delete this area?");
        confirmAlert.setContentText(
            "Area: " + currentArea.getName() + " (" + currentArea.getCode() + ")\n\n" +
            "This action cannot be undone. Any notes associated with this area may be affected."
        );
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    areaService.deleteArea(currentArea.getId());
                    
                    // Show success message
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Success");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText("Area deleted successfully!");
                    successAlert.showAndWait();
                    
                    // Navigate back to Manage Areas
                    handleBack();
                    
                } catch (AreaService.AreaException e) {
                    showError("Error deleting area: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Handles the Back button - returns to Manage Areas view
     */
    @FXML
    private void handleBack() {
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
            showError("Error returning to manage areas: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

