package com.portfolio.defstuf.controllers;

import com.portfolio.defstuf.SystemInfo;
import com.portfolio.defstuf.controllers.note.CreateNoteController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    private Button screenshotButton;
    
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
    }
    
    /**
     * Sets the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Opens the Create Note view
     */
    @FXML
    private void openScreenshotTool() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/note/CreateNoteView.fxml")
            );
            Parent root = loader.load();
            
            // Get the controller and set the primary stage
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
    
    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
