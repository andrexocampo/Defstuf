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
            
            Scene scene = new Scene(root, 800, 700);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            primaryStage.setTitle("DefStuf - Create Note");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening Create Note view: " + e.getMessage());
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
            
            Scene scene = new Scene(root, 500, 400);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            primaryStage.setTitle("DefStuf - Login");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading login view: " + e.getMessage());
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
