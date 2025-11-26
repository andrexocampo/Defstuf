package com.portfolio.defstuf.controllers;

import com.portfolio.defstuf.SystemInfo;
import com.portfolio.defstuf.controllers.screenshot.ScreenshotController;
import javafx.fxml.FXML;
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
     * Opens the screenshot tool
     */
    @FXML
    private void openScreenshotTool() {
        ScreenshotController screenshotController = new ScreenshotController();
        screenshotController.setPrimaryStage(primaryStage);
        screenshotController.startCapture();
    }
}
