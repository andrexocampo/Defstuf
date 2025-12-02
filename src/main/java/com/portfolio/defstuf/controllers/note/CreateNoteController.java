package com.portfolio.defstuf.controllers.note;

import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.models.note.NoteType;
import com.portfolio.defstuf.models.note.Source;
import com.portfolio.defstuf.models.screenshot.Screenshot;
import com.portfolio.defstuf.services.area.AreaService;
import com.portfolio.defstuf.services.note.NoteService;
import com.portfolio.defstuf.services.screenshot.ScreenshotCaptureService;
import com.portfolio.defstuf.session.SessionManager;
import com.portfolio.defstuf.util.ImageFileManager;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Create Note view
 * Handles note creation with title, description, source, area, note type, and multiple screenshots
 */
public class CreateNoteController {
    
    @FXML
    private TextField titleField;
    
    @FXML
    private ComboBox<Source> sourceComboBox;
    
    @FXML
    private TextArea descriptionArea;
    
    @FXML
    private ComboBox<Area> areaComboBox;
    
    @FXML
    private ComboBox<NoteType> noteTypeComboBox;
    
    @FXML
    private VBox screenshotsContainer;
    
    @FXML
    private Label screenshotsPlaceholderLabel;
    
    @FXML
    private Button captureButton;
    
    @FXML
    private Button saveButton;
    
    @FXML
    private Button cancelButton;
    
    private Stage primaryStage;
    private Stage captureStage;
    private ScreenshotCaptureService captureService;
    private AreaService areaService;
    private NoteService noteService;
    
    // List to store multiple screenshots
    private List<WritableImage> screenshotsFX = new ArrayList<>();
    private List<BufferedImage> screenshotsBuffered = new ArrayList<>();
    
    // Variables for capture UI
    private Canvas canvas;
    private GraphicsContext gc;
    private Screenshot screenshot;
    private double startX, startY;
    private double currentX, currentY;
    private boolean isSelecting = false;
    private Rectangle2D selectedArea;
    private static final double MIN_SELECTION_SIZE = 10.0;
    
    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        captureService = new ScreenshotCaptureService();
        areaService = new AreaService();
        noteService = new NoteService();
        
        // Load areas
        loadAreas();
        
        // Load sources and set default to "personal"
        loadSources();
        
        // Load note types and set default to "Definition"
        loadNoteTypes();
        
        // Update screenshots UI
        updateScreenshotsUI();
    }
    
    /**
     * Loads areas from database into the ComboBox
     */
    private void loadAreas() {
        try {
            List<Area> areas = areaService.getAllAreas();
            areaComboBox.getItems().clear();
            areaComboBox.getItems().addAll(areas);
            
            // Set cell factory to display area name
            areaComboBox.setCellFactory(param -> new ListCell<Area>() {
                @Override
                protected void updateItem(Area item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            
            // Set button cell to display area name
            areaComboBox.setButtonCell(new ListCell<Area>() {
                @Override
                protected void updateItem(Area item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error loading areas: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads sources from database and sets "Personal" as default
     */
    private void loadSources() {
        try {
            List<Source> sources = noteService.getAllSources();
            sourceComboBox.getItems().clear();
            sourceComboBox.getItems().addAll(sources);
            
            // Set cell factory to display source name
            sourceComboBox.setCellFactory(param -> new ListCell<Source>() {
                @Override
                protected void updateItem(Source item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            
            // Set button cell to display source name
            sourceComboBox.setButtonCell(new ListCell<Source>() {
                @Override
                protected void updateItem(Source item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Select Source");
                    } else {
                        setText(item.getName());
                    }
                }
            });
            
            // Set "Personal" as default selection
            for (Source source : sources) {
                if ("personal".equalsIgnoreCase(source.getCode())) {
                    sourceComboBox.getSelectionModel().select(source);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading sources: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Loads note types from database and sets "Definition" as default
     */
    private void loadNoteTypes() {
        try {
            // Ensure "Definition" exists
            noteService.getOrCreateNoteType("Definition");
            
            // Load all note types
            List<NoteType> noteTypes = noteService.getAllNoteTypes();
            noteTypeComboBox.getItems().clear();
            noteTypeComboBox.getItems().addAll(noteTypes);
            
            // Set cell factory to display note type name (NoteType.toString() already returns name)
            noteTypeComboBox.setCellFactory(param -> new ListCell<NoteType>() {
                @Override
                protected void updateItem(NoteType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            
            // Set button cell to display note type name
            noteTypeComboBox.setButtonCell(new ListCell<NoteType>() {
                @Override
                protected void updateItem(NoteType item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            
            // Set "Definition" as default selection
            for (NoteType noteType : noteTypes) {
                if ("Definition".equalsIgnoreCase(noteType.getName())) {
                    noteTypeComboBox.getSelectionModel().select(noteType);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading note types: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Opens the screenshot capture tool
     */
    @FXML
    private void captureScreenshot() {
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
    
    /**
     * Shows the capture UI overlay
     */
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
        controls.setPadding(new Insets(10));
        controls.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 5;");
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 8 16;");
        cancelButton.setOnAction(e -> cancelCapture());
        
        Button captureButton = new Button("Capture");
        captureButton.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white; -fx-padding: 8 16;");
        captureButton.setOnAction(e -> captureSelectedArea());
        
        controls.getChildren().addAll(cancelButton, captureButton);
        controls.setAlignment(Pos.CENTER);
        root.setBottom(controls);
        BorderPane.setAlignment(controls, Pos.CENTER);
        
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
            
            // Add to screenshots list
            screenshotsFX.add(croppedFXImage);
            screenshotsBuffered.add(croppedImage);
            
            // Update UI to show all screenshots
            updateScreenshotsUI();
            
            // Close capture window
            captureStage.close();
            
            // Show primary window again
            if (primaryStage != null) {
                primaryStage.show();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error capturing area: " + e.getMessage());
            cancelCapture();
        }
    }
    
    /**
     * Updates the screenshots UI to show all captured screenshots
     */
    private void updateScreenshotsUI() {
        screenshotsContainer.getChildren().clear();
        
        if (screenshotsFX.isEmpty()) {
            screenshotsPlaceholderLabel.setVisible(true);
            screenshotsContainer.getChildren().add(screenshotsPlaceholderLabel);
        } else {
            screenshotsPlaceholderLabel.setVisible(false);
            
            for (int i = 0; i < screenshotsFX.size(); i++) {
                final int index = i;
                WritableImage image = screenshotsFX.get(i);
                
                // Create container for each screenshot
                BorderPane imageContainer = new BorderPane();
                imageContainer.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 5;");
                
                // Image view
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(400);
                imageView.setFitHeight(250);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                
                imageContainer.setCenter(imageView);
                
                // Remove button
                HBox buttonContainer = new HBox();
                buttonContainer.setAlignment(Pos.CENTER_RIGHT);
                buttonContainer.setPadding(new Insets(5));
                
                Button removeButton = new Button("Remove");
                removeButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 5 10;");
                removeButton.setOnAction(e -> removeScreenshot(index));
                
                buttonContainer.getChildren().add(removeButton);
                imageContainer.setBottom(buttonContainer);
                
                screenshotsContainer.getChildren().add(imageContainer);
            }
        }
    }
    
    /**
     * Removes a screenshot at the specified index
     */
    private void removeScreenshot(int index) {
        if (index >= 0 && index < screenshotsFX.size()) {
            screenshotsFX.remove(index);
            screenshotsBuffered.remove(index);
            updateScreenshotsUI();
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
    
    /**
     * Saves the note to the database
     */
    @FXML
    private void saveNote() {
        // Get form values
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        
        // Validate title (REQUIRED)
        if (title.isEmpty()) {
            showError("Please enter a title for the note");
            titleField.requestFocus();
            return;
        }
        
        // Get selected source (or use "personal" as default)
        Source selectedSource = sourceComboBox.getSelectionModel().getSelectedItem();
        Long sourceId = null;
        
        if (selectedSource != null) {
            sourceId = selectedSource.getId();
        } else {
            // Fallback: try to get "personal" source
            try {
                java.util.Optional<Source> personalSource = noteService.getSourceByCode("personal");
                sourceId = personalSource.map(Source::getId).orElse(null);
            } catch (Exception e) {
                System.err.println("Error getting default source: " + e.getMessage());
            }
        }
        
        // Get selected area
        Area selectedArea = areaComboBox.getSelectionModel().getSelectedItem();
        Long areaId = selectedArea != null ? selectedArea.getId() : null;
        
        // Get selected note type (or use "Definition" as default)
        NoteType selectedNoteType = noteTypeComboBox.getSelectionModel().getSelectedItem();
        Long noteTypeId = null;
        
        if (selectedNoteType != null) {
            noteTypeId = selectedNoteType.getId();
        } else {
            // Fallback: ensure "Definition" exists and use it
            try {
                NoteType definitionType = noteService.getOrCreateNoteType("Definition");
                noteTypeId = definitionType.getId();
            } catch (Exception e) {
                System.err.println("Error getting default note type: " + e.getMessage());
            }
        }
        
        // Get current user ID
        Long userId = SessionManager.getInstance().getCurrentUserId();
        if (userId == null) {
            showError("You must be logged in to create a note");
            return;
        }
        
        // Save images to disk and prepare data for database
        List<String> imagePaths = new ArrayList<>();
        List<Long> fileSizes = new ArrayList<>();
        List<String> mimeTypes = new ArrayList<>();
        
        try {
            for (BufferedImage bufferedImage : screenshotsBuffered) {
                String imagePath = ImageFileManager.saveImage(bufferedImage, "image/png");
                long fileSize = ImageFileManager.getFileSize(imagePath);
                
                imagePaths.add(imagePath);
                fileSizes.add(fileSize);
                mimeTypes.add("image/png");
            }
        } catch (IOException e) {
            showError("Error saving images: " + e.getMessage());
            return;
        }
        
        // Create and save note
        try {
            noteService.createNote(
                userId,
                title,
                sourceId,  // Will default to "personal" if null in service
                description,  // Can be null/empty
                areaId,
                noteTypeId,
                imagePaths,
                fileSizes,
                mimeTypes
            );
            
            showInfo("Note saved successfully!");
            clearForm();
            
        } catch (NoteService.NoteException e) {
            showError("Error saving note: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clears the form after successful save
     */
    private void clearForm() {
        titleField.clear();
        descriptionArea.clear();
        areaComboBox.getSelectionModel().clearSelection();
        
        // Reset source to "Personal"
        for (Source source : sourceComboBox.getItems()) {
            if ("personal".equalsIgnoreCase(source.getCode())) {
                sourceComboBox.getSelectionModel().select(source);
                break;
            }
        }
        
        // Reset note type to "Definition"
        for (NoteType noteType : noteTypeComboBox.getItems()) {
            if ("Definition".equalsIgnoreCase(noteType.getName())) {
                noteTypeComboBox.getSelectionModel().select(noteType);
                break;
            }
        }
        
        // Clear screenshots
        screenshotsFX.clear();
        screenshotsBuffered.clear();
        updateScreenshotsUI();
    }
    
    /**
     * Cancels note creation and returns to main view
     */
    @FXML
    private void cancel() {
        // Navigate back to main view
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/MainView.fxml")
            );
            javafx.scene.Parent root = loader.load();
            
            // Get the controller and set the primary stage
            com.portfolio.defstuf.controllers.MainViewController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root, 640, 480);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            primaryStage.setTitle("DefStuf");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error returning to main view: " + e.getMessage());
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
