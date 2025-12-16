package com.portfolio.defstuf.controllers.study;

import com.portfolio.defstuf.controllers.MainViewController;
import com.portfolio.defstuf.models.area.Area;
import com.portfolio.defstuf.models.note.Note;
import com.portfolio.defstuf.models.note.NoteImage;
import com.portfolio.defstuf.models.study.*;
import com.portfolio.defstuf.repository.note.ImageRepository;
import com.portfolio.defstuf.repository.study.AnswerRepository;
import com.portfolio.defstuf.repository.study.QuestionRepository;
import com.portfolio.defstuf.services.area.AreaService;
import com.portfolio.defstuf.services.content.NoteContentRenderer;
import com.portfolio.defstuf.services.study.NoteGroupingService;
import com.portfolio.defstuf.services.study.SpacedRepetitionService;
import com.portfolio.defstuf.services.study.StudySessionService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Study Session view
 * FR-05.3, FR-05.4, FR-05.5, FR-05.6, FR-05.7: Handles complete study session flow
 */
public class StudySessionController {
    
    // FXML Components - Progress Panel (FR-05.6)
    @FXML private VBox progressPanel;
    @FXML private Label progressLabel;
    @FXML private Label elapsedTimeLabel;
    @FXML private Label avgTimeLabel;
    @FXML private Label forgotCountLabel;
    @FXML private Label hardCountLabel;
    @FXML private Label goodCountLabel;
    @FXML private Label easyCountLabel;
    @FXML private Label breaksTakenLabel;
    
    // FXML Components - Study Mode
    @FXML private VBox studyModeContainer;
    @FXML private Label areaBadgeLabel;
    @FXML private Label noteTitleLabel;
    @FXML private VBox answerContainer;
    @FXML private VBox descriptionsContainer;
    @FXML private Button showAnswerButton;
    @FXML private HBox ratingButtonsContainer;
    @FXML private Button forgotButton;
    @FXML private Button hardButton;
    @FXML private Button goodButton;
    @FXML private Button easyButton;
    
    // FXML Components - Break Mode (FR-05.7)
    @FXML private VBox breakModeContainer;
    @FXML private Label breakTimeLabel;
    @FXML private Label breakRemainingLabel;
    @FXML private Button endBreakButton;
    
    // FXML Components - Controls
    @FXML private Button breakButton;
    @FXML private Button finishButton;
    
    // Services and Repositories
    private StudySessionService studySessionService;
    private NoteGroupingService noteGroupingService;
    private SpacedRepetitionService spacedRepetitionService;
    private AnswerRepository answerRepository;
    private QuestionRepository questionRepository;
    private ImageRepository imageRepository;
    private AreaService areaService;
    private NoteContentRenderer contentRenderer;
    
    // Session Data
    private StudySession currentSession;
    private List<NoteGroup> noteGroups;
    private int currentNoteGroupIndex;
    private NoteGroup currentNoteGroup;
    private boolean answerRevealed;
    
    // Time Tracking (FR-05.6)
    private LocalDateTime sessionStartTime;
    private LocalDateTime currentNoteStartTime;
    private long totalElapsedSeconds;
    private boolean isOnBreak;
    private Timeline timeUpdateTimeline;
    private Timeline breakTimer;
    
    // Rating Counts (FR-05.6)
    private Map<String, Integer> ratingCounts;
    
    // Break Management (FR-05.7)
    private int breakDurationSeconds;
    private int breakElapsedSeconds;
    
    /**
     * Initializes the controller
     * Called by JavaFX when FXML is loaded
     */
    @FXML
    public void initialize() {
        // Initialize services
        studySessionService = new StudySessionService();
        noteGroupingService = new NoteGroupingService();
        spacedRepetitionService = new SpacedRepetitionService();
        answerRepository = new AnswerRepository();
        questionRepository = new QuestionRepository();
        imageRepository = new ImageRepository();
        areaService = new AreaService();
        contentRenderer = new NoteContentRenderer();
        
        // Initialize rating counts (using HashMap for mutability)
        ratingCounts = new HashMap<>();
        ratingCounts.put("forgot", 0);
        ratingCounts.put("hard", 0);
        ratingCounts.put("good", 0);
        ratingCounts.put("easy", 0);
        
        // Initialize UI state
        answerRevealed = false;
        isOnBreak = false;
        currentNoteGroupIndex = 0;
        
        // Setup keyboard shortcuts (FR-05.4)
        setupKeyboardShortcuts();
    }
    
    /**
     * Sets the study session and starts it
     * Called from ConfigStudySessionController
     * 
     * @param session The study session
     * @param areaId The area ID
     * @param sourceIds List of source IDs
     * @param onlyNewAndPending If true, only load new and pending notes; if false, load all notes
     */
    public void setStudySession(StudySession session, Long areaId, List<Long> sourceIds, boolean onlyNewAndPending) {
        this.currentSession = session;
        this.sessionStartTime = LocalDateTime.now();
        this.totalElapsedSeconds = 0;
        
        try {
            // Load area
            Area area = areaService.getAreaById(areaId);
            
            // Load note groups
            noteGroups = noteGroupingService.loadNotesForSession(
                areaId,
                sourceIds,
                session.getCardsLimit(),
                session.getReviewOrder(),
                area,
                onlyNewAndPending
            );
            
            if (noteGroups.isEmpty()) {
                showError("No notes found for the selected criteria.");
                handleFinish();
                return;
            }
            
            // Initialize break duration
            if (session.getBreakDurationMin() != null) {
                breakDurationSeconds = session.getBreakDurationMin() * 60;
            }
            
            // Update UI
            updateProgressPanel();
            showNextNote();
            
            // Start time tracking
            startTimeTracking();
            
            // Update session started time
            session.setStartedAt(sessionStartTime);
            studySessionService.updateSession(session);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error starting study session: " + e.getMessage());
        }
    }
    
    /**
     * Sets up keyboard shortcuts for ratings (FR-05.4)
     */
    private void setupKeyboardShortcuts() {
        // Keyboard shortcuts will be handled by Scene-level event handlers
        // This will be set up in showNextNote() method
    }
    
    /**
     * Shows the next note group
     */
    private void showNextNote() {
        // Check if there are more notes in review queue
        if (noteGroupingService.hasMoreInReviewQueue()) {
            currentNoteGroup = noteGroupingService.getNextFromReviewQueue();
        } else if (currentNoteGroupIndex < noteGroups.size()) {
            currentNoteGroup = noteGroups.get(currentNoteGroupIndex);
            currentNoteGroupIndex++;
        } else {
            // Session completed
            handleSessionComplete();
            return;
        }
        
        // Reset UI state
        answerRevealed = false;
        answerContainer.setVisible(false);
        answerContainer.setManaged(false);
        ratingButtonsContainer.setVisible(false);
        ratingButtonsContainer.setManaged(false);
        showAnswerButton.setVisible(true);
        showAnswerButton.setManaged(true);
        descriptionsContainer.getChildren().clear();
        
        // Display note title and area
        if (currentNoteGroup != null) {
            areaBadgeLabel.setText(currentNoteGroup.getArea().getName());
            noteTitleLabel.setText(contentRenderer.renderContent(currentNoteGroup.getSharedTitle()));
        }
        
        // Start timing for this note
        currentNoteStartTime = LocalDateTime.now();
        
        // Update progress panel after showing the note
        updateProgressPanel();
        
        // Setup keyboard shortcuts for this note
        setupNoteKeyboardShortcuts();
    }
    
    /**
     * Sets up keyboard shortcuts for current note
     */
    private void setupNoteKeyboardShortcuts() {
        // Keyboard shortcuts will be set up at Scene level
        // This is a placeholder - actual implementation will use Scene.setOnKeyPressed
    }
    
    /**
     * Handles "Show Answer" button click (FR-05.3)
     */
    @FXML
    private void handleShowAnswer() {
        if (currentNoteGroup == null) {
            return;
        }
        
        answerRevealed = true;
        showAnswerButton.setVisible(false);
        showAnswerButton.setManaged(false);
        answerContainer.setVisible(true);
        answerContainer.setManaged(true);
        ratingButtonsContainer.setVisible(true);
        ratingButtonsContainer.setManaged(true);
        
        // Display all answers (descriptions and images) for the group
        displayAnswers();
    }
    
    /**
     * Displays all answers for the current note group (FR-05.3)
     */
    private void displayAnswers() {
        descriptionsContainer.getChildren().clear();
        
        try {
            for (Note note : currentNoteGroup.getNotes()) {
                // Create container for each answer
                VBox answerBox = new VBox(15);
                answerBox.getStyleClass().add("answer-card");
                
                // Description
                Label descriptionLabel = new Label(contentRenderer.renderContent(note.getDescription()));
                descriptionLabel.setWrapText(true);
                descriptionLabel.setMaxWidth(800);
                descriptionLabel.getStyleClass().add("answer-description");
                answerBox.getChildren().add(descriptionLabel);
                
                // Load and display images (FR-05.3: Images ordered by created_at)
                List<NoteImage> images = imageRepository.findByNoteId(note.getId());
                if (!images.isEmpty()) {
                    HBox imagesBox = new HBox(10);
                    imagesBox.getStyleClass().add("images-container");
                    
                    for (NoteImage image : images) {
                        try {
                            File imageFile = new File(image.getImagePath());
                            if (imageFile.exists()) {
                                Image fxImage = new Image(imageFile.toURI().toString());
                                ImageView imageView = new ImageView(fxImage);
                                imageView.setFitWidth(300);
                                imageView.setFitHeight(200);
                                imageView.setPreserveRatio(true);
                                imageView.setSmooth(true);
                                imagesBox.getChildren().add(imageView);
                            }
                        } catch (Exception e) {
                            System.err.println("Error loading image: " + e.getMessage());
                        }
                    }
                    
                    if (!imagesBox.getChildren().isEmpty()) {
                        answerBox.getChildren().add(imagesBox);
                    }
                }
                
                // Add separator if multiple answers
                if (currentNoteGroup.getNotes().size() > 1 && 
                    currentNoteGroup.getNotes().indexOf(note) < currentNoteGroup.getNotes().size() - 1) {
                    Separator separator = new Separator();
                    answerBox.getChildren().add(separator);
                }
                
                descriptionsContainer.getChildren().add(answerBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error loading note content: " + e.getMessage());
        }
    }
    
    /**
     * Handles rating: Forgot (FR-05.4)
     */
    @FXML
    private void handleRatingForgot() {
        handleRating("forgot");
    }
    
    /**
     * Handles rating: Hard (FR-05.4)
     */
    @FXML
    private void handleRatingHard() {
        handleRating("hard");
    }
    
    /**
     * Handles rating: Good (FR-05.4)
     */
    @FXML
    private void handleRatingGood() {
        handleRating("good");
    }
    
    /**
     * Handles rating: Easy (FR-05.4)
     */
    @FXML
    private void handleRatingEasy() {
        handleRating("easy");
    }
    
    /**
     * Handles rating submission (FR-05.4, FR-05.5)
     * Automatically advances to next question after rating
     */
    private void handleRating(String rating) {
        if (currentNoteGroup == null || !answerRevealed) {
            return;
        }
        
        try {
            // Calculate time spent on this note
            long timeSpentSeconds = java.time.Duration.between(
                currentNoteStartTime, LocalDateTime.now()
            ).getSeconds();
            totalElapsedSeconds += timeSpentSeconds;
            
            // Get or create Answer
            Answer answer = answerRepository.findOrCreateByContent(rating);
            
            // Create Question for each note in the group (same answer for all)
            for (Note note : currentNoteGroup.getNotes()) {
                Question question = new Question(
                    currentSession.getId(),
                    answer.getId(),
                    note.getId()
                );
                questionRepository.save(question);
                
                // Update ScheduledReview (FR-05.5)
                spacedRepetitionService.updateScheduledReview(note.getId(), rating);
            }
            
            // Update rating counts (FR-05.6)
            ratingCounts.put(rating, ratingCounts.get(rating) + 1);
            
            // If negative answer, add to review queue (FR-05.3: Quality over quantity)
            if ("forgot".equals(rating) || "hard".equals(rating)) {
                noteGroupingService.addToReviewQueue(currentNoteGroup);
            }
            
            // Update session counters
            currentSession.setNotesStudiedCount(
                (currentSession.getNotesStudiedCount() != null ? currentSession.getNotesStudiedCount() : 0) + 1
            );
            
            // Update session in database
            try {
                studySessionService.updateSession(currentSession);
            } catch (Exception e) {
                System.err.println("Error updating session: " + e.getMessage());
            }
            
            // Automatically advance to next note (this will also update progress)
            showNextNote();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error saving rating: " + e.getMessage());
        }
    }
    
    /**
     * Handles break button click (FR-05.7)
     */
    @FXML
    private void handleBreak() {
        if (isOnBreak) {
            return;
        }
        
        // Check if breaks are allowed
        if (currentSession.getMaxBreaksAllowed() != null) {
            if (currentSession.getMaxBreaksAllowed() == 0) {
                showInfo("Breaks are not allowed in this session.");
                return;
            }
            if (currentSession.getBreaksTaken() != null && 
                currentSession.getBreaksTaken() >= currentSession.getMaxBreaksAllowed()) {
                showInfo("Maximum number of breaks reached for this session.");
                breakButton.setDisable(true);
                return;
            }
        }
        
        // Start break
        startBreak();
    }
    
    /**
     * Starts a break (FR-05.7)
     */
    private void startBreak() {
        isOnBreak = true;
        breakElapsedSeconds = 0;
        
        // Switch to break mode UI
        studyModeContainer.setVisible(false);
        studyModeContainer.setManaged(false);
        breakModeContainer.setVisible(true);
        breakModeContainer.setManaged(true);
        
        // Disable finish button during break
        finishButton.setDisable(true);
        
        // Start break timer
        startBreakTimer();
    }
    
    /**
     * Starts the break timer (FR-05.7: Break time counts as study time)
     */
    private void startBreakTimer() {
        // Stop any existing break timer
        if (breakTimer != null) {
            breakTimer.stop();
        }
        
        breakTimer = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                breakElapsedSeconds++;
                totalElapsedSeconds++; // Break time counts as study time
                
                // Update break time display
                long minutes = breakElapsedSeconds / 60;
                long seconds = breakElapsedSeconds % 60;
                breakTimeLabel.setText(String.format("%02d:%02d", minutes, seconds));
                
                // Update remaining time
                if (breakDurationSeconds > 0) {
                    int remaining = breakDurationSeconds - breakElapsedSeconds;
                    if (remaining > 0) {
                        long remMinutes = remaining / 60;
                        long remSeconds = remaining % 60;
                        breakRemainingLabel.setText(String.format("Remaining: %02d:%02d", remMinutes, remSeconds));
                    } else {
                        // Break time ended automatically
                        breakRemainingLabel.setText("Break time ended!");
                        Platform.runLater(() -> handleEndBreak());
                    }
                }
            })
        );
        breakTimer.setCycleCount(Timeline.INDEFINITE);
        breakTimer.play();
    }
    
    /**
     * Handles end break button click (FR-05.7)
     */
    @FXML
    private void handleEndBreak() {
        if (!isOnBreak) {
            return;
        }
        
        endBreak();
    }
    
    /**
     * Ends the break (FR-05.7)
     */
    private void endBreak() {
        if (!isOnBreak) {
            return;
        }
        
        isOnBreak = false;
        
        // Stop break timer
        if (breakTimer != null) {
            breakTimer.stop();
        }
        
        // Update session
        currentSession.setBreaksTaken(
            (currentSession.getBreaksTaken() != null ? currentSession.getBreaksTaken() : 0) + 1
        );
        
        try {
            studySessionService.updateSession(currentSession);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Switch back to study mode
        studyModeContainer.setVisible(true);
        studyModeContainer.setManaged(true);
        breakModeContainer.setVisible(false);
        breakModeContainer.setManaged(false);
        
        // Re-enable finish button
        finishButton.setDisable(false);
        
        // Update progress
        updateProgressPanel();
    }
    
    /**
     * Handles finish button click (FR-05.7)
     */
    @FXML
    private void handleFinish() {
        handleSessionComplete();
    }
    
    /**
     * Handles session completion (FR-05.7)
     */
    private void handleSessionComplete() {
        // Stop time tracking
        if (timeUpdateTimeline != null) {
            timeUpdateTimeline.stop();
        }
        
        // Update session end time and status
        currentSession.setCompletedAt(LocalDateTime.now());
        currentSession.setStatus(StudySession.Status.COMPLETED);
        
        // Update actual study time (including breaks)
        currentSession.setActualStudyTimeSec((int) totalElapsedSeconds);
        
        try {
            studySessionService.updateSession(currentSession);
            
            // Calculate statistics (prepared but not displayed yet)
            calculateSessionStatistics();
            
            // Show completion message and return to main view
            showInfo("Study session completed!");
            returnToMainView();
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error completing session: " + e.getMessage());
        }
    }
    
    /**
     * Calculates session statistics (prepared for future display)
     */
    private void calculateSessionStatistics() {
        // TODO: Calculate and prepare statistics for future display
        // This will be used when statistics view is implemented
        // Statistics are calculated but not displayed yet
        // Example: totalNotes, totalTime, ratingDistribution, etc.
    }
    
    /**
     * Updates the progress panel (FR-05.6)
     */
    private void updateProgressPanel() {
        int currentProgress = currentNoteGroupIndex;
        int totalNotes = noteGroups.size();
        
        progressLabel.setText(String.format("Progress: %d/%d", currentProgress, totalNotes));
        
        // Update elapsed time
        long minutes = totalElapsedSeconds / 60;
        long seconds = totalElapsedSeconds % 60;
        elapsedTimeLabel.setText(String.format("Time: %02d:%02d", minutes, seconds));
        
        // Update average time per definition
        if (currentSession.getNotesStudiedCount() != null && currentSession.getNotesStudiedCount() > 0) {
            long avgSeconds = totalElapsedSeconds / currentSession.getNotesStudiedCount();
            long avgMinutes = avgSeconds / 60;
            long avgSecs = avgSeconds % 60;
            avgTimeLabel.setText(String.format("Avg: %02d:%02d", avgMinutes, avgSecs));
        } else {
            avgTimeLabel.setText("Avg: --");
        }
        
        // Update rating distribution
        forgotCountLabel.setText("❌" + ratingCounts.get("forgot"));
        hardCountLabel.setText("😰" + ratingCounts.get("hard"));
        goodCountLabel.setText("✅" + ratingCounts.get("good"));
        easyCountLabel.setText("🚀" + ratingCounts.get("easy"));
        
        // Update breaks taken
        int breaks = currentSession.getBreaksTaken() != null ? currentSession.getBreaksTaken() : 0;
        int maxBreaks = currentSession.getMaxBreaksAllowed() != null ? currentSession.getMaxBreaksAllowed() : -1;
        if (maxBreaks >= 0) {
            breaksTakenLabel.setText(String.format("Breaks: %d/%d", breaks, maxBreaks));
        } else {
            breaksTakenLabel.setText(String.format("Breaks: %d", breaks));
        }
    }
    
    /**
     * Starts time tracking (FR-05.6)
     */
    private void startTimeTracking() {
        timeUpdateTimeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> {
                if (!isOnBreak) {
                    totalElapsedSeconds++;
                }
                updateProgressPanel();
            })
        );
        timeUpdateTimeline.setCycleCount(Timeline.INDEFINITE);
        timeUpdateTimeline.play();
    }
    
    /**
     * Returns to main view
     */
    private void returnToMainView() {
        try {
            Stage stage = (Stage) finishButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/portfolio/defstuf/views/MainView.fxml")
            );
            Parent root = loader.load();
            
            MainViewController controller = loader.getController();
            controller.setPrimaryStage(stage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                getClass().getResource("/com/portfolio/defstuf/styles/main.css").toExternalForm()
            );
            
            // Preserve window state
            preserveWindowState(stage, scene, 900, 750);
            stage.setTitle("DefStuf - Main");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error returning to main view: " + e.getMessage());
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
    
    /**
     * Shows an error alert
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Shows an info alert
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

