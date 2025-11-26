package com.portfolio.defstuf.controllers.screenshot;

import com.portfolio.defstuf.models.screenshot.Screenshot;
import com.portfolio.defstuf.services.screenshot.ScreenshotCaptureService;
import com.portfolio.defstuf.util.ImageUtils;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Controller for Screenshot Tool functionality
 * Presentation layer - handles all user interface logic
 */
public class ScreenshotController {
    
    private Stage primaryStage;
    private Stage captureStage;
    private Canvas canvas;
    private GraphicsContext gc;
    private Screenshot screenshot;
    private double startX, startY;
    private double currentX, currentY;
    private boolean isSelecting = false;
    private Rectangle2D selectedArea;
    
    private final ScreenshotCaptureService captureService = new ScreenshotCaptureService();
    private static final double MIN_SELECTION_SIZE = 10.0;
    
    /**
     * Sets the primary stage for the controller
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Starts the screen capture process
     */
    public void startCapture() {
        try {
            // Hide primary window
            if (primaryStage != null) {
                primaryStage.hide();
            }
            
            // Wait a moment for the window to hide
            Platform.runLater(() -> {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                Platform.runLater(() -> {
                    try {
                        // Capture full screen using the service
                        screenshot = captureService.captureFullScreen();
                        // Show selection interface
                        showCaptureUI();
                    } catch (AWTException e) {
                        e.printStackTrace();
                        showError("Error capturing screen: " + e.getMessage());
                        if (primaryStage != null) {
                            primaryStage.show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        showError("Error starting capture: " + e.getMessage());
                        if (primaryStage != null) {
                            primaryStage.show();
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting capture: " + e.getMessage());
            if (primaryStage != null) {
                primaryStage.show();
            }
        }
    }
    
    private void showCaptureUI() {
        if (screenshot == null || screenshot.getFxScreenshot() == null) {
            if (primaryStage != null) {
                primaryStage.show();
            }
            return;
        }
        
        WritableImage fxScreenshot = screenshot.getFxScreenshot();
        
        captureStage = new Stage();
        captureStage.initStyle(StageStyle.UNDECORATED);
        captureStage.setFullScreen(true);
        captureStage.setFullScreenExitHint("");
        
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        
        canvas = new Canvas(fxScreenshot.getWidth(), fxScreenshot.getHeight());
        gc = canvas.getGraphicsContext2D();
        
        // Draw screenshot
        gc.drawImage(fxScreenshot, 0, 0);
        
        // Mouse events
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        canvas.setOnMouseReleased(this::onMouseReleased);
        
        root.setCenter(canvas);
        
        // Control buttons
        HBox controls = new HBox(10);
        controls.setPadding(new javafx.geometry.Insets(10));
        controls.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 5;");
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 8 16;");
        cancelButton.setOnAction(e -> cancelCapture());
        
        Button captureButton = new Button("Capture");
        captureButton.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white; -fx-padding: 8 16;");
        captureButton.setOnAction(e -> captureSelectedArea());
        
        controls.getChildren().addAll(cancelButton, captureButton);
        controls.setAlignment(javafx.geometry.Pos.CENTER);
        root.setBottom(controls);
        BorderPane.setAlignment(controls, javafx.geometry.Pos.CENTER);
        
        Scene scene = new Scene(root, fxScreenshot.getWidth(), fxScreenshot.getHeight());
        captureStage.setScene(scene);
        captureStage.show();
    }
    
    private void onMousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();
        isSelecting = true;
        currentX = startX;
        currentY = startY;
    }
    
    private void onMouseDragged(MouseEvent e) {
        if (isSelecting) {
            currentX = e.getX();
            currentY = e.getY();
            drawSelection();
        }
    }
    
    private void onMouseReleased(MouseEvent e) {
        if (isSelecting) {
            currentX = e.getX();
            currentY = e.getY();
            isSelecting = false;
            
            // Calculate selected area
            double minX = Math.min(startX, currentX);
            double minY = Math.min(startY, currentY);
            double maxX = Math.max(startX, currentX);
            double maxY = Math.max(startY, currentY);
            double width = maxX - minX;
            double height = maxY - minY;
            
            Rectangle2D area = new Rectangle2D(minX, minY, width, height);
            
            // Validate area using the service
            if (captureService.isValidSelectionArea(area, MIN_SELECTION_SIZE, MIN_SELECTION_SIZE)) {
                selectedArea = area;
                screenshot.setSelectedArea(area);
                // Automatically capture when mouse is released
                Platform.runLater(() -> captureSelectedArea());
            } else {
                // If area is too small, just redraw without capturing
                drawSelection();
            }
        }
    }
    
    private void drawSelection() {
        if (screenshot == null || screenshot.getFxScreenshot() == null) {
            return;
        }
        
        WritableImage fxScreenshot = screenshot.getFxScreenshot();
        
        // Redraw screenshot
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.drawImage(fxScreenshot, 0, 0);
        
        // Draw dark overlay
        gc.setFill(Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Calculate selected area
        double minX = Math.min(startX, currentX);
        double minY = Math.min(startY, currentY);
        double width = Math.abs(currentX - startX);
        double height = Math.abs(currentY - startY);
        
        // Show selected area (without overlay)
        gc.clearRect(minX, minY, width, height);
        gc.drawImage(fxScreenshot, minX, minY, width, height, minX, minY, width, height);
        
        // Draw selection border
        gc.setStroke(Color.rgb(0, 120, 215));
        gc.setLineWidth(2);
        gc.strokeRect(minX, minY, width, height);
        
        // Draw corners
        double cornerSize = 8;
        gc.setFill(Color.rgb(0, 120, 215));
        
        // Corners
        gc.fillRect(minX - 2, minY - 2, cornerSize, 3);
        gc.fillRect(minX - 2, minY - 2, 3, cornerSize);
        
        gc.fillRect(minX + width - cornerSize + 2, minY - 2, cornerSize, 3);
        gc.fillRect(minX + width - 2, minY - 2, 3, cornerSize);
        
        gc.fillRect(minX - 2, minY + height - 2, cornerSize, 3);
        gc.fillRect(minX - 2, minY + height - cornerSize + 2, 3, cornerSize);
        
        gc.fillRect(minX + width - cornerSize + 2, minY + height - 2, cornerSize, 3);
        gc.fillRect(minX + width - 2, minY + height - cornerSize + 2, 3, cornerSize);
    }
    
    private void captureSelectedArea() {
        if (selectedArea == null || !captureService.isValidSelectionArea(selectedArea, MIN_SELECTION_SIZE, MIN_SELECTION_SIZE)) {
            showError("Please select an area first");
            return;
        }
        
        try {
            // Crop image using the service
            BufferedImage croppedImage = captureService.cropImage(screenshot, selectedArea);
            WritableImage croppedFXImage = SwingFXUtils.toFXImage(croppedImage, null);
            
            // Close capture window
            captureStage.close();
            
            // Show preview and save window
            showPreviewAndSave(croppedFXImage, croppedImage);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error capturing area: " + e.getMessage());
            cancelCapture();
        }
    }
    
    private void showPreviewAndSave(WritableImage fxImage, BufferedImage bufferedImage) {
        Stage previewStage = new Stage();
        previewStage.setTitle("Screenshot Preview");
        
        BorderPane root = new BorderPane();
        root.setPadding(new javafx.geometry.Insets(10));
        
        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(fxImage);
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(Math.min(800, fxImage.getWidth()));
        imageView.setFitHeight(Math.min(600, fxImage.getHeight()));
        
        ScrollPane scrollPane = new ScrollPane(imageView);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        HBox buttons = new HBox(10);
        buttons.setPadding(new javafx.geometry.Insets(10));
        buttons.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button saveButton = new Button("Save");
        saveButton.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white; -fx-padding: 8 16;");
        saveButton.setOnAction(e -> saveImage(bufferedImage, previewStage));
        
        Button newSnipButton = new Button("New Screenshot");
        newSnipButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 8 16;");
        newSnipButton.setOnAction(e -> {
            previewStage.close();
            startCapture();
        });
        
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 8 16;");
        closeButton.setOnAction(e -> {
            previewStage.close();
            if (primaryStage != null) {
                primaryStage.show();
            }
        });
        
        buttons.getChildren().addAll(saveButton, newSnipButton, closeButton);
        root.setCenter(scrollPane);
        root.setBottom(buttons);
        
        Scene scene = new Scene(root, 900, 700);
        previewStage.setScene(scene);
        previewStage.setOnCloseRequest(e -> {
            if (primaryStage != null) {
                primaryStage.show();
            }
        });
        previewStage.show();
    }
    
    private void saveImage(BufferedImage image, Stage previewStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Screenshot");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PNG", "*.png"),
            new FileChooser.ExtensionFilter("JPEG", "*.jpg", "*.jpeg"),
            new FileChooser.ExtensionFilter("All files", "*.*")
        );
        fileChooser.setInitialFileName("screenshot.png");
        
        File file = fileChooser.showSaveDialog(previewStage);
        if (file != null) {
            try {
                ImageUtils.saveImage(image, file);
                showInfo("Image saved successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error saving image: " + e.getMessage());
            }
        }
    }
    
    private void cancelCapture() {
        if (captureStage != null) {
            captureStage.close();
        }
        if (primaryStage != null) {
            primaryStage.show();
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

