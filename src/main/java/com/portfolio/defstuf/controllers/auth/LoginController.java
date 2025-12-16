package com.portfolio.defstuf.controllers.auth;

import com.portfolio.defstuf.controllers.MainViewController;
import com.portfolio.defstuf.services.auth.AuthenticationService;
import com.portfolio.defstuf.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for Login view
 */
public class LoginController {
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Hyperlink registerLink;
    
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
     * Handles the login action
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        // Clear previous errors
        clearErrors();
        
        // Validate input
        if (email.isEmpty()) {
            showFieldError("Email is required");
            emailField.requestFocus();
            return;
        }
        
        if (password.isEmpty()) {
            showFieldError("Password is required");
            passwordField.requestFocus();
            return;
        }
        
        // Validate email format
        if (!authService.isValidEmail(email)) {
            showError("Invalid email format");
            emailField.requestFocus();
            return;
        }
        
        // Attempt login
        try {
            var user = authService.login(email, password);
            
            // Set current user in session
            SessionManager.getInstance().setCurrentUser(user);
            
            // Navigate to main view
            navigateToMainView();
            
        } catch (AuthenticationService.AuthenticationException e) {
            showError(e.getMessage());
            passwordField.clear();
            emailField.requestFocus();
        } catch (Exception e) {
            showError("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Navigates to the register view
     */
    @FXML
    private void navigateToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/auth/RegisterView.fxml")
            );
            Parent root = loader.load();
            
            RegisterController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            // Preserve window state
            preserveWindowState(primaryStage, scene, 600, 650);
            primaryStage.setTitle("DefStuf - Register");
        } catch (Exception e) {
            showError("Error loading register view: " + e.getMessage());
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
            
            Scene scene = new Scene(root);
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
        emailField.setStyle("");
        passwordField.setStyle("");
    }
    
    private void showFieldError(String message) {
        showError(message);
        emailField.setStyle("-fx-border-color: #d32f2f;");
        passwordField.setStyle("-fx-border-color: #d32f2f;");
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
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
}

