package com.portfolio.defstuf.controllers.area;

import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.services.area.AreaService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for Manage Areas view
 * RF-02.1: Displays list of areas in cards
 */
public class ManageAreasController {
    
    @FXML
    private FlowPane areasFlowPane;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Button createNewAreaButton;
    
    private Stage primaryStage;
    private AreaService areaService;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    
    @FXML
    private void initialize() {
        areaService = new AreaService();
        loadAreas();
    }
    
    /**
     * Sets the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Loads all areas and displays them in cards
     */
    private void loadAreas() {
        try {
            List<Area> areas = areaService.getAllAreas();
            areasFlowPane.getChildren().clear();
            
            if (areas.isEmpty()) {
                statusLabel.setText("No areas created yet. Click 'Create New Area' to get started.");
            } else {
                statusLabel.setText("Total areas: " + areas.size());
                
                for (Area area : areas) {
                    VBox card = createAreaCard(area);
                    areasFlowPane.getChildren().add(card);
                }
            }
        } catch (AreaService.AreaException e) {
            showError("Error loading areas: " + e.getMessage());
            statusLabel.setText("Error loading areas");
        }
    }
    
    /**
     * Creates a card component for an area
     */
    private VBox createAreaCard(Area area) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        card.setPrefWidth(280);
        card.setPrefHeight(180);
        
        // Area name
        Label nameLabel = new Label(area.getName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Area code
        Label codeLabel = new Label("Code: " + area.getCode());
        codeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        
        // Created date
        String createdDate = area.getCreatedAt() != null 
            ? "Created: " + area.getCreatedAt().format(DATE_FORMATTER)
            : "Created: Unknown";
        Label dateLabel = new Label(createdDate);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #999;");
        
        // Buttons container
        VBox buttonsContainer = new VBox(8);
        
        // View button
        Button viewButton = new Button("View");
        viewButton.setPrefWidth(Double.MAX_VALUE);
        viewButton.setStyle(
            "-fx-background-color: #0078d4; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand;"
        );
        viewButton.setOnAction(e -> handleViewArea(area));
        
        // Review button (disabled for future implementation)
        Button reviewButton = new Button("Review");
        reviewButton.setPrefWidth(Double.MAX_VALUE);
        reviewButton.setDisable(true);
        reviewButton.setStyle(
            "-fx-background-color: #ccc; " +
            "-fx-text-fill: #666; " +
            "-fx-padding: 8; " +
            "-fx-font-size: 14px;"
        );
        reviewButton.setTooltip(new javafx.scene.control.Tooltip("Coming soon"));
        
        buttonsContainer.getChildren().addAll(viewButton, reviewButton);
        
        card.getChildren().addAll(nameLabel, codeLabel, dateLabel, buttonsContainer);
        VBox.setMargin(buttonsContainer, new Insets(10, 0, 0, 0));
        
        return card;
    }
    
    /**
     * Handles the View button click - navigates to Area Detail view
     */
    private void handleViewArea(Area area) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/area/AreaDetailView.fxml")
            );
            Parent root = loader.load();
            
            AreaDetailController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            controller.setArea(area);
            
            Scene scene = new Scene(root, 900, 750);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            preserveWindowState(primaryStage, scene, 900, 750);
            primaryStage.setTitle("DefStuf - Area Detail");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening area detail: " + e.getMessage());
        }
    }
    
    /**
     * Handles the Back button - returns to main view
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/MainView.fxml")
            );
            Parent root = loader.load();
            
            com.portfolio.defstuf.controllers.MainViewController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root, 900, 750);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            preserveWindowState(primaryStage, scene, 900, 750);
            primaryStage.setTitle("DefStuf");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error returning to main view: " + e.getMessage());
        }
    }
    
    /**
     * Handles the Create New Area button - navigates to Create Area view
     */
    @FXML
    private void handleCreateNewArea() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/area/CreateAreaView.fxml")
            );
            Parent root = loader.load();
            
            CreateAreaController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root, 600, 550);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            preserveWindowState(primaryStage, scene, 600, 550);
            primaryStage.setTitle("DefStuf - Create Area");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening create area view: " + e.getMessage());
        }
    }
    
    /**
     * Refreshes the areas list (called after creating a new area)
     */
    public void refreshAreas() {
        loadAreas();
    }
    
    /**
     * Preserves window state (size, position, maximized) when changing scenes
     */
    private void preserveWindowState(Stage stage, Scene newScene, double defaultWidth, double defaultHeight) {
        boolean wasMaximized = stage.isMaximized();
        boolean wasIconified = stage.isIconified();
        double currentWidth = stage.getWidth();
        double currentHeight = stage.getHeight();
        double currentX = stage.getX();
        double currentY = stage.getY();
        
        // If maximized, set scene and restore maximized state immediately
        if (wasMaximized) {
            stage.setScene(newScene);
            // Restore maximized state immediately and ensure it stays maximized
            javafx.application.Platform.runLater(() -> {
                stage.setMaximized(true);
                // Double-check to ensure it stays maximized
                javafx.application.Platform.runLater(() -> {
                    if (!stage.isMaximized()) {
                        stage.setMaximized(true);
                    }
                });
                if (wasIconified) {
                    stage.setIconified(true);
                }
            });
        } else {
            // For non-maximized windows, set scene and restore size/position
            stage.setScene(newScene);
            
            // Use Platform.runLater to restore state after scene is fully set
            javafx.application.Platform.runLater(() -> {
                // If window had a reasonable size, preserve it, otherwise use default
                if (currentWidth > 100 && currentHeight > 100) {
                    stage.setWidth(currentWidth);
                    stage.setHeight(currentHeight);
                } else {
                    stage.setWidth(defaultWidth);
                    stage.setHeight(defaultHeight);
                }
                // Preserve position
                if (currentX >= 0 && currentY >= 0) {
                    stage.setX(currentX);
                    stage.setY(currentY);
                }
                
                // Restore iconified state if it was
                if (wasIconified) {
                    stage.setIconified(true);
                }
            });
        }
        
        // Only show if not already showing
        if (!stage.isShowing()) {
            stage.show();
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

