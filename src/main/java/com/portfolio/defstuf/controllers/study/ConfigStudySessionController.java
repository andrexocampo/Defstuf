package com.portfolio.defstuf.controllers.study;

import com.portfolio.defstuf.controllers.MainViewController;
import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.models.note.Source;
import com.portfolio.defstuf.models.study.StudySession;
import com.portfolio.defstuf.repository.note.NoteRepository;
import com.portfolio.defstuf.services.area.AreaService;
import com.portfolio.defstuf.services.note.SourceService;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    
    @FXML
    private VBox sourcesContainer;
    
    @FXML
    private CheckBox selectAllSourcesCheckbox;
    
    @FXML
    private Label selectedSourcesCountLabel;
    
    @FXML
    private VBox sourcesCheckboxContainer;
    
    @FXML
    private Label sourcesStatusLabel;
    
    private Stage primaryStage;
    private AreaService areaService;
    private StudySessionService studySessionService;
    private NoteRepository noteRepository;
    private SourceService sourceService;
    private ToggleGroup areaToggleGroup;
    private List<Source> availableSources;
    private List<CheckBox> sourceCheckboxes;
    private Area selectedArea;
    
    /**
     * Initializes the controller
     */
    @FXML
    private void initialize() {
        areaService = new AreaService();
        studySessionService = new StudySessionService();
        noteRepository = new NoteRepository();
        sourceService = new SourceService();
        areaToggleGroup = new ToggleGroup();
        availableSources = new ArrayList<>();
        sourceCheckboxes = new ArrayList<>();
        
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
     * Creates a radio button row for an area with note counts (new, pending, total)
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
        
        // Add listener to radio button selection
        radioButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                handleAreaSelection();
            }
        });
        
        // Area name label
        Label nameLabel = new Label(area.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        nameLabel.setPrefWidth(200);
        
        // Counters container
        HBox countersBox = new HBox(10);
        countersBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        countersBox.setUserData("countersBox"); // Tag to identify this container later
        
        // Get note counts for this area
        try {
            List<Long> allSourceIds = null; // For counting without source filter
            LocalDate today = LocalDate.now();
            
            int newNotes = noteRepository.countNewNotesByAreaIdAndUserIdAndSourceIds(
                area.getId(), userId, allSourceIds);
            int pendingNotes = noteRepository.countPendingNotesByAreaIdAndUserIdAndSourceIds(
                area.getId(), userId, allSourceIds, today);
            int totalNotes = noteRepository.countByAreaIdAndUserIdAndSourceIds(
                area.getId(), userId, allSourceIds);
            
            // Create labels for each counter
            Label newLabel = new Label(String.format("New: %d", newNotes));
            Label pendingLabel = new Label(String.format("Pending: %d", pendingNotes));
            Label totalLabel = new Label(String.format("Total: %d", totalNotes));
            
            newLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 12px;");
            pendingLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 12px;");
            totalLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
            
            countersBox.getChildren().addAll(newLabel, pendingLabel, totalLabel);
            
            if (totalNotes == 0) {
                Label errorLabel = new Label("(0 definitions - cannot start session)");
                errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");
                countersBox.getChildren().clear();
                countersBox.getChildren().add(errorLabel);
                radioButton.setDisable(true);
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("(Error loading count)");
            errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");
            countersBox.getChildren().clear();
            countersBox.getChildren().add(errorLabel);
        }
        
        row.getChildren().addAll(radioButton, nameLabel, countersBox);
        
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
     * Handles area radio button selection and loads sources for that area
     */
    private void handleAreaSelection() {
        RadioButton selectedRadio = (RadioButton) areaToggleGroup.getSelectedToggle();
        if (selectedRadio == null) {
            sourcesContainer.setVisible(false);
            sourcesContainer.setManaged(false);
            selectedArea = null;
            return;
        }
        
        selectedArea = (Area) selectedRadio.getUserData();
        loadSourcesForArea(selectedArea);
        updateNoteCountForSelectedSources();
    }
    
    /**
     * Loads sources that have notes in the selected area
     */
    private void loadSourcesForArea(Area area) {
        try {
            Long userId = SessionManager.getInstance().getCurrentUserId();
            
            // Get distinct source IDs for this area and user
            List<Long> sourceIds = noteRepository.getDistinctSourceIdsByAreaIdAndUserId(area.getId(), userId);
            
            sourcesCheckboxContainer.getChildren().clear();
            sourceCheckboxes.clear();
            availableSources.clear();
            
            if (sourceIds.isEmpty()) {
                sourcesContainer.setVisible(false);
                sourcesContainer.setManaged(false);
                sourcesStatusLabel.setText("No sources found with notes in this area.");
                return;
            }
            
            // Load source objects
            for (Long sourceId : sourceIds) {
                try {
                    Source source = sourceService.getSourceById(sourceId);
                    availableSources.add(source);
                    
                    // Create checkbox for this source
                    CheckBox checkBox = new CheckBox();
                    checkBox.setUserData(source);
                    
                    // Get note count for this source
                    int noteCount = noteRepository.countByAreaIdAndUserIdAndSourceId(
                        area.getId(), userId, sourceId
                    );
                    String countText = noteCount == 1 ? "1 definition" : noteCount + " definitions";
                    checkBox.setText(source.getName() + " (" + countText + ")");
                    checkBox.setStyle("-fx-font-size: 13px;");
                    
                    // Add listener to update counts when selection changes
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        updateNoteCountForSelectedSources();
                        updateSelectAllCheckboxState();
                        updateSelectedSourcesCountLabel();
                    });
                    
                    sourceCheckboxes.add(checkBox);
                    sourcesCheckboxContainer.getChildren().add(checkBox);
                } catch (SourceService.SourceException e) {
                    System.err.println("Error loading source " + sourceId + ": " + e.getMessage());
                }
            }
            
            // Show sources container
            sourcesContainer.setVisible(true);
            sourcesContainer.setManaged(true);
            selectAllSourcesCheckbox.setSelected(false);
            updateSelectedSourcesCountLabel();
            
        } catch (SQLException e) {
            showError("Error loading sources: " + e.getMessage());
            sourcesContainer.setVisible(false);
            sourcesContainer.setManaged(false);
        }
    }
    
    /**
     * Handles "Select All Sources" checkbox
     */
    @FXML
    private void handleSelectAllSources() {
        boolean selectAll = selectAllSourcesCheckbox.isSelected();
        for (CheckBox checkBox : sourceCheckboxes) {
            checkBox.setSelected(selectAll);
        }
        updateNoteCountForSelectedSources();
        updateSelectedSourcesCountLabel();
    }
    
    /**
     * Updates the state of "Select All" checkbox based on current selections
     */
    private void updateSelectAllCheckboxState() {
        if (sourceCheckboxes.isEmpty()) {
            selectAllSourcesCheckbox.setSelected(false);
            return;
        }
        
        long selectedCount = sourceCheckboxes.stream()
            .filter(CheckBox::isSelected)
            .count();
        
        selectAllSourcesCheckbox.setSelected(selectedCount == sourceCheckboxes.size());
    }
    
    /**
     * Updates the selected sources count label
     */
    private void updateSelectedSourcesCountLabel() {
        long selectedCount = sourceCheckboxes.stream()
            .filter(CheckBox::isSelected)
            .count();
        
        if (selectedCount == 0) {
            selectedSourcesCountLabel.setText("(All sources will be included)");
        } else if (selectedCount == 1) {
            selectedSourcesCountLabel.setText("(1 source selected)");
        } else {
            selectedSourcesCountLabel.setText("(" + selectedCount + " sources selected)");
        }
    }
    
    /**
     * Updates the note count display based on selected sources
     */
    private void updateNoteCountForSelectedSources() {
        if (selectedArea == null) {
            return;
        }
        
        try {
            Long userId = SessionManager.getInstance().getCurrentUserId();
            LocalDate today = LocalDate.now();
            
            // Get selected source IDs
            List<Long> selectedSourceIds = getSelectedSourceIds();
            
            // Count notes for selected sources
            int newNotes = noteRepository.countNewNotesByAreaIdAndUserIdAndSourceIds(
                selectedArea.getId(), userId, selectedSourceIds.isEmpty() ? null : selectedSourceIds);
            int pendingNotes = noteRepository.countPendingNotesByAreaIdAndUserIdAndSourceIds(
                selectedArea.getId(), userId, selectedSourceIds.isEmpty() ? null : selectedSourceIds, today);
            int totalNotes = noteRepository.countByAreaIdAndUserIdAndSourceIds(
                selectedArea.getId(), userId, selectedSourceIds.isEmpty() ? null : selectedSourceIds);
            
            // Update the area's count labels
            RadioButton selectedRadio = (RadioButton) areaToggleGroup.getSelectedToggle();
            if (selectedRadio != null) {
                HBox areaRow = (HBox) selectedRadio.getParent();
                // Find the counters box in the area row (third child)
                if (areaRow.getChildren().size() >= 3 && areaRow.getChildren().get(2) instanceof HBox) {
                    HBox countersBox = (HBox) areaRow.getChildren().get(2);
                    countersBox.getChildren().clear();
                    
                    if (totalNotes == 0) {
                        Label errorLabel = new Label("(0 definitions - cannot start session)");
                        errorLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12px;");
                        countersBox.getChildren().add(errorLabel);
                    } else {
                        Label newLabel = new Label(String.format("New: %d", newNotes));
                        Label pendingLabel = new Label(String.format("Pending: %d", pendingNotes));
                        Label totalLabel = new Label(String.format("Total: %d", totalNotes));
                        
                        newLabel.setStyle("-fx-text-fill: #2196F3; -fx-font-size: 12px;");
                        pendingLabel.setStyle("-fx-text-fill: #FF9800; -fx-font-size: 12px;");
                        totalLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
                        
                        countersBox.getChildren().addAll(newLabel, pendingLabel, totalLabel);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating note count: " + e.getMessage());
        }
    }
    
    /**
     * Gets list of selected source IDs
     * 
     * @return List of selected source IDs (empty list means all sources)
     */
    private List<Long> getSelectedSourceIds() {
        List<Long> selectedIds = new ArrayList<>();
        for (CheckBox checkBox : sourceCheckboxes) {
            if (checkBox.isSelected()) {
                Source source = (Source) checkBox.getUserData();
                selectedIds.add(source.getId());
            }
        }
        return selectedIds;
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
            Long userId = SessionManager.getInstance().getCurrentUserId();
            List<Long> selectedSourceIds = getSelectedSourceIds();
            LocalDate today = LocalDate.now();
            
            // Check how many new and pending notes there are
            int newNotes = noteRepository.countNewNotesByAreaIdAndUserIdAndSourceIds(
                selectedArea.getId(), userId, selectedSourceIds.isEmpty() ? null : selectedSourceIds);
            int pendingNotes = noteRepository.countPendingNotesByAreaIdAndUserIdAndSourceIds(
                selectedArea.getId(), userId, selectedSourceIds.isEmpty() ? null : selectedSourceIds, today);
            
            boolean hasNewOrPending = (newNotes > 0 || pendingNotes > 0);
            boolean isAdvancedReview = !hasNewOrPending;
            
            // If no new or pending notes, show confirmation dialog for advanced review
            if (isAdvancedReview) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Advanced Review Session");
                alert.setHeaderText(null);
                alert.setContentText(
                    "There are no new or pending notes for today.\n" +
                    "Would you like to start an advanced review session with all available notes?"
                );
                
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) {
                    return; // User cancelled
                }
            }
            
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
            
            // Navigate to study session view with flag indicating if we should only show new/pending
            navigateToStudySessionView(session, selectedArea.getId(), selectedSourceIds, hasNewOrPending);
            
        } catch (StudySessionService.StudySessionException e) {
            showError("Error creating study session: " + e.getMessage());
        } catch (SQLException e) {
            showError("Error checking note status: " + e.getMessage());
        }
    }
    
    /**
     * Navigates to the study session view
     * 
     * @param session The study session
     * @param areaId The area ID
     * @param sourceIds List of source IDs
     * @param onlyNewAndPending If true, only load new and pending notes; if false, load all notes
     */
    private void navigateToStudySessionView(StudySession session, Long areaId, List<Long> sourceIds, boolean onlyNewAndPending) {
        try {
            Stage stage = (Stage) startSessionButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/study/StudySessionView.fxml")
            );
            Parent root = loader.load();
            
            StudySessionController controller = loader.getController();
            controller.setStudySession(session, areaId, sourceIds, onlyNewAndPending);
            
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading study session view: " + e.getMessage());
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
            
            // Check note count with selected sources
            List<Long> selectedSourceIds = getSelectedSourceIds();
            int noteCount = noteRepository.countByAreaIdAndUserIdAndSourceIds(
                selectedArea.getId(), userId, selectedSourceIds
            );
            
            if (noteCount == 0) {
                if (selectedSourceIds.isEmpty()) {
                    showError("Selected area has no definitions. Please select an area with definitions.");
                } else {
                    showError("Selected area and sources have no definitions. Please select different sources or area.");
                }
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

