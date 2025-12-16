package com.portfolio.defstuf.controllers;

import com.portfolio.defstuf.SystemInfo;
import com.portfolio.defstuf.controllers.auth.LoginController;
import com.portfolio.defstuf.controllers.note.CreateNoteController;
import com.portfolio.defstuf.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * Controller for MainView.fxml
 */
public class MainViewController {
    
    @FXML
    private Label infoLabel;
    
    @FXML
    private Label userLabel;
    
    @FXML
    private Button createNoteButton;
    
    @FXML
    private Button startReviewButton;
    
    @FXML
    private Button manageAreasButton;
    
    @FXML
    private Button logoutButton;
    
    private Stage primaryStage;
    
    /**
     * This method is automatically executed after loading the FXML
     * when all components are initialized
     */
    @FXML
    private void initialize() {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        infoLabel.setText("JavaFX " + javafxVersion + ", running on Java " + javaVersion);
        
        // Load and display current user information
        loadUserInfo();
    }
    
    /**
     * Sets the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Loads and displays the current user's information
     */
    private void loadUserInfo() {
        var currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("Welcome, " + currentUser.getName());
        } else {
            userLabel.setText("Welcome, Guest");
        }
    }
    
    /**
     * Opens the Create Note view
     */
    @FXML
    private void openCreateNoteView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/note/CreateNoteView.fxml")
            );
            Parent root = loader.load();
            
            CreateNoteController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            // Preserve window state
            preserveWindowState(primaryStage, scene, 900, 750);
            primaryStage.setTitle("DefStuf - Create Note");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening Create Note view: " + e.getMessage());
        }
    }
    
    /**
     * Opens the Study Session Configuration view
     * RF-05.1: Navigation from main view to Study Session Configuration
     */
    @FXML
    private void openStudySessionConfigView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/study/ConfigStudySessionView.fxml")
            );
            Parent root = loader.load();
            
            com.portfolio.defstuf.controllers.study.ConfigStudySessionController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            // Preserve window state
            preserveWindowState(primaryStage, scene, 900, 750);
            primaryStage.setTitle("DefStuf - Configure Study Session");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening Study Session Configuration view: " + e.getMessage());
        }
    }
    
    /**
     * Opens the Manage Areas view
     * RF-02.5: Navigation from main view to Manage Areas
     */
    @FXML
    private void openManageAreasView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/area/ManageAreasView.fxml")
            );
            Parent root = loader.load();
            
            com.portfolio.defstuf.controllers.area.ManageAreasController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            // Preserve window state
            preserveWindowState(primaryStage, scene, 900, 750);
            primaryStage.setTitle("DefStuf - Manage Areas");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening Manage Areas view: " + e.getMessage());
        }
    }
    
    /**
     * Handles the logout action
     */
    @FXML
    private void handleLogout() {
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Logout Confirmation");
        confirmAlert.setHeaderText("Are you sure you want to logout?");
        confirmAlert.setContentText("You will need to login again to access your account.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // Clear the current session
                SessionManager.getInstance().logout();
                
                // Navigate back to login view
                navigateToLogin();
            }
        });
    }
    
    /**
     * Navigates to the login view
     */
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/auth/LoginView.fxml")
            );
            Parent root = loader.load();
            
            LoginController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            // Preserve window state
            preserveWindowState(primaryStage, scene, 600, 550);
            primaryStage.setTitle("DefStuf - Login");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading login view: " + e.getMessage());
        }
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
        
        stage.setScene(newScene);
        
        // If window was maximized, restore that state
        if (wasMaximized) {
            stage.setMaximized(true);
        } else {
            // If window had a reasonable size, preserve it, otherwise use default
            if (currentWidth > 100 && currentHeight > 100) {
                stage.setWidth(currentWidth);
                stage.setHeight(currentHeight);
            } else {
                stage.setWidth(defaultWidth);
                stage.setHeight(defaultHeight);
            }
            // Preserve position if window wasn't maximized
            if (currentX >= 0 && currentY >= 0) {
                stage.setX(currentX);
                stage.setY(currentY);
            }
        }
        
        // Restore iconified state if it was
        if (wasIconified) {
            stage.setIconified(true);
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
