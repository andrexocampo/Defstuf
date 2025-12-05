package com.portfolio.defstuf.controllers.study;

import com.portfolio.defstuf.controllers.MainViewController;
import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.models.study.StudySession;
import com.portfolio.defstuf.repository.note.NoteRepository;
import com.portfolio.defstuf.services.area.AreaService;
import com.portfolio.defstuf.services.study.StudySessionService;
import com.portfolio.defstuf.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller for Study Session Configuration view
 * RF-05.1, RF-05.2: Handles study session configuration and creation
 */
public class ConfigStudySessionController {
    
    @FXML
    private VBox areasContainer;
    
    @FXML
    private Label areasStatusLabel;
    
    @FXML
    private ComboBox<Integer> timeLimitCombo;
    
    @FXML
    private ComboBox<String> cardsLimitCombo;
    
    @FXML
    private ComboBox<StudySession.ReviewOrder> reviewOrderCombo;
    
    @FXML
    private Button startSessionButton;
    
    @FXML
    private Label statusLabel;
    
    private Stage primaryStage;
    private AreaService areaService;
    private StudySessionService studySessionService;
    private NoteRepository noteRepository;
    private ToggleGroup areaToggleGroup;
    
    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        areaService = new AreaService();
        studySessionService = new StudySessionService();
        noteRepository = new NoteRepository();
        areaToggleGroup = new ToggleGroup();
        
        // Load areas
        loadAreas();
        
        // Load configuration defaults
        loadConfigurationDefaults();
    }
    
    /**
     * Sets the primary stage
     */
    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }
    
    /**
     * Loads all areas and displays them with radio buttons and note counts
     */
    private void loadAreas() {
        try {
            List<Area> areas = areaService.getAllAreas();
            areasContainer.getChildren().clear();
            
            if (areas.isEmpty()) {
                areasStatusLabel.setText("No areas available. Please create an area first.");
                areasStatusLabel.setStyle("-fx-text-fill: #d32f2f;");
                startSessionButton.setDisable(true);
                return;
            }
            
            areasStatusLabel.setText("Select an area to review from the list below:");
            areasStatusLabel.setStyle("-fx-text-fill: #666;");
            
            Long userId = SessionManager.getInstance().getCurrentUserId();
            
            for (Area area : areas) {
                HBox areaRow = createAreaRadioButton(area, userId);
                areasContainer.getChildren().add(areaRow);
            }
            
        } catch (AreaService.AreaException e) {
            showError("Error loading areas: " + e.getMessage());
            areasStatusLabel.setText("Error loading areas");
            areasStatusLabel.setStyle("-fx-text-fill: #d32f2f;");
        }
    }
    
    /**
     * Creates a radio button row for an area with note count
     */
    private HBox createAreaRadioButton(Area area, Long userId) {
        HBox row = new HBox(15);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setPadding(new Insets(8, 10, 8, 10));
        row.setStyle("-fx-background-color: #fafafa; -fx-background-radius: 5;");
        
        // Radio button
        RadioButton radioButton = new RadioButton();
        radioButton.setToggleGroup(areaToggleGroup);
        radioButton.setUserData(area);
        
        // Area name label
        Label nameLabel = new Label(area.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        nameLabel.setPrefWidth(200);
        
        // Note count label
        Label countLabel = new Label();
        countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        
        // Get note count for this area
        try {
            int noteCount = noteRepository.countByAreaIdAndUserId(area.getId(), userId);
            String countText = noteCount == 1 ? "1 definition" : noteCount + " definitions";
            countLabel.setText("(" + countText + " pending)");
            
            if (noteCount == 0) {
                countLabel.setText("(0 definitions - cannot start session)");
                countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #d32f2f;");
                radioButton.setDisable(true);
            }
        } catch (SQLException e) {
            countLabel.setText("(Error loading count)");
            countLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #d32f2f;");
        }
        
        row.getChildren().addAll(radioButton, nameLabel, countLabel);
        
        return row;
    }
    
    /**
     * Loads default configuration values into combo boxes
     */
    private void loadConfigurationDefaults() {
        // Time limit options: 15, 25, 30, 60 minutes
        timeLimitCombo.getItems().addAll(15, 25, 30, 60);
        timeLimitCombo.setValue(25); // Default 25 minutes
        
        // Cards limit options: 20, 50, 100, All
        cardsLimitCombo.getItems().addAll("20", "50", "100", "All");
        cardsLimitCombo.setValue("20"); // Default to 20
        
        // Review order options
        reviewOrderCombo.getItems().addAll(
            StudySession.ReviewOrder.RANDOM,
            StudySession.ReviewOrder.OLDEST_FIRST,
            StudySession.ReviewOrder.HARDEST_FIRST
        );
        reviewOrderCombo.setValue(StudySession.ReviewOrder.RANDOM);
        
        // Set cell factories for better display
        reviewOrderCombo.setCellFactory(param -> new ListCell<StudySession.ReviewOrder>() {
            @Override
            protected void updateItem(StudySession.ReviewOrder item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    switch (item) {
                        case RANDOM:
                            setText("Random");
                            break;
                        case OLDEST_FIRST:
                            setText("Oldest First");
                            break;
                        case HARDEST_FIRST:
                            setText("Hardest First");
                            break;
                    }
                }
            }
        });
        
        reviewOrderCombo.setButtonCell(new ListCell<StudySession.ReviewOrder>() {
            @Override
            protected void updateItem(StudySession.ReviewOrder item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    switch (item) {
                        case RANDOM:
                            setText("Random");
                            break;
                        case OLDEST_FIRST:
                            setText("Oldest First");
                            break;
                        case HARDEST_FIRST:
                            setText("Hardest First");
                            break;
                    }
                }
            }
        });
    }
    
    /**
     * Handles the start session button click
     * RF-05.2: Creates study session with selected configuration
     */
    @FXML
    private void handleStartSession() {
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        try {
            // Get selected area
            RadioButton selectedRadio = (RadioButton) areaToggleGroup.getSelectedToggle();
            if (selectedRadio == null) {
                showError("Please select an area to review");
                return;
            }
            
            Area selectedArea = (Area) selectedRadio.getUserData();
            
            // Get configuration values
            Integer timeLimit = timeLimitCombo.getValue();
            if (timeLimit == null) {
                timeLimit = 25; // Default
            }
            
            String cardsLimitStr = cardsLimitCombo.getValue();
            Integer cardsLimit = null;
            if (cardsLimitStr != null && !cardsLimitStr.equals("All")) {
                try {
                    cardsLimit = Integer.parseInt(cardsLimitStr);
                } catch (NumberFormatException e) {
                    cardsLimit = null; // All cards
                }
            }
            
            StudySession.ReviewOrder reviewOrder = reviewOrderCombo.getValue();
            if (reviewOrder == null) {
                reviewOrder = StudySession.ReviewOrder.RANDOM;
            }
            
            // Generate session name
            String sessionName = "Study Session - " + selectedArea.getName() + " - " 
                               + java.time.LocalDateTime.now().format(
                                   java.time.format.DateTimeFormatter.ofPattern("MMM dd, HH:mm"));
            
            // Create study session
            StudySession session = studySessionService.createStudySession(
                sessionName,
                selectedArea.getId(),
                timeLimit,
                cardsLimit,
                reviewOrder
            );
            
            // Show success message
            statusLabel.setText("Study session created successfully! Session ID: " + session.getId());
            statusLabel.setStyle("-fx-text-fill: #28a745;");
            
            // TODO: Navigate to study session view (placeholder for future implementation)
            // For now, just show a message
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Study Session Created");
            infoAlert.setHeaderText("Study session created successfully!");
            infoAlert.setContentText("Session ID: " + session.getId() + "\n\n" +
                                   "The study session view will be implemented in the next iteration.");
            infoAlert.showAndWait();
            
            // Navigate back to main view for now
            handleBack();
            
        } catch (StudySessionService.StudySessionException e) {
            showError("Error creating study session: " + e.getMessage());
        }
    }
    
    /**
     * Validates the form
     */
    private boolean validateForm() {
        // Check if area is selected
        if (areaToggleGroup.getSelectedToggle() == null) {
            showError("Please select an area to review");
            return false;
        }
        
        // Check if selected area has notes
        RadioButton selectedRadio = (RadioButton) areaToggleGroup.getSelectedToggle();
        Area selectedArea = (Area) selectedRadio.getUserData();
        
        try {
            Long userId = SessionManager.getInstance().getCurrentUserId();
            int noteCount = noteRepository.countByAreaIdAndUserId(selectedArea.getId(), userId);
            
            if (noteCount == 0) {
                showError("Selected area has no definitions. Please select an area with definitions.");
                return false;
            }
        } catch (SQLException e) {
            showError("Error validating area: " + e.getMessage());
            return false;
        }
        
        return true;
    }
    
    /**
     * Handles the back button click
     */
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/MainView.fxml")
            );
            Parent root = loader.load();
            
            MainViewController controller = loader.getController();
            controller.setPrimaryStage(primaryStage);
            
            Scene scene = new Scene(root, 900, 700);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            primaryStage.setTitle("DefStuf - Main");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error returning to main view: " + e.getMessage());
        }
    }
    
    /**
     * Shows an error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        
        statusLabel.setText("Error: " + message);
        statusLabel.setStyle("-fx-text-fill: #d32f2f;");
    }
}

