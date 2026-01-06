package com.portfolio.defstuf.controllers.auth;

import com.portfolio.defstuf.controllers.MainViewController;
import com.portfolio.defstuf.services.auth.AuthenticationService;
import com.portfolio.defstuf.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Controller for Register view
 */
public class RegisterController {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Hyperlink loginLink;
    
    private Stage primaryStage;
    private AuthenticationService authService;
    
    @FXML
    private void initialize() {
        authService = new AuthenticationService();
    }
    
    /**
     * Sets the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Handles the register action
     */
    @FXML
    private void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Clear previous errors
        clearErrors();
        
        // Validate input
        if (name.isEmpty()) {
            showFieldError("Name is required", nameField);
            return;
        }
        
        if (email.isEmpty()) {
            showFieldError("Email is required", emailField);
            return;
        }
        
        if (!authService.isValidEmail(email)) {
            showError("Invalid email format");
            emailField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showFieldError("Password is required", passwordField);
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters long");
            passwordField.requestFocus();
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            confirmPasswordField.clear();
            confirmPasswordField.requestFocus();
            return;
        }
        
        // Attempt registration
        try {
            var user = authService.register(name, email, password);
            
            // Set current user in session
            SessionManager.getInstance().setCurrentUser(user);
            
            // Show success message
            showSuccess("Registration successful! Welcome to DefStuf.");
            
            // Navigate to main view
            navigateToMainView();
            
        } catch (AuthenticationService.AuthenticationException e) {
            showError(e.getMessage());
            if (e.getMessage().contains("Email")) {
                emailField.requestFocus();
            } else {
                passwordField.requestFocus();
            }
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Navigates back to the login view
     */
    @FXML
    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/auth/LoginView.fxml")
            );
            Parent root = loader.load();
            
            LoginController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root, 600, 550);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            // Preserve window state
            preserveWindowState(primaryStage, scene, 600, 550);
            primaryStage.setTitle("DefStuf - Login");
        } catch (Exception e) {
            showError("Error loading login view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Navigates to the main application view
     */
    private void navigateToMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/MainView.fxml")
            );
            Parent root = loader.load();
            
            MainViewController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root, 900, 750);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            // Preserve window state
            preserveWindowState(primaryStage, scene, 900, 750);
            primaryStage.setTitle("DefStuf");
        } catch (Exception e) {
            showError("Error loading main view: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void clearErrors() {
        nameField.setStyle("");
        emailField.setStyle("");
        passwordField.setStyle("");
        confirmPasswordField.setStyle("");
    }
    
    private void showFieldError(String message, TextField field) {
        showError(message);
        field.setStyle("-fx-border-color: #d32f2f;");
        field.requestFocus();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Registration Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
}








