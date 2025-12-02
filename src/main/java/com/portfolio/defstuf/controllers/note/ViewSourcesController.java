package com.portfolio.defstuf.controllers.note;

import com.portfolio.defstuf.models.note.Source;
import com.portfolio.defstuf.services.note.SourceService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.List;

/**
 * Controller for View Sources window
 * Displays a list of all sources with edit and delete options
 */
public class ViewSourcesController {
    
    @FXML
    private VBox sourcesContainer;
    
    @FXML
    private Label statusLabel;
    
    @FXML
    private Button addSourceButton;
    
    private SourceService sourceService;
    private Runnable onSourceUpdated; // Callback to refresh sources in CreateNoteController
    
    @FXML
    private void initialize() {
        sourceService = new SourceService();
        loadSources();
    }
    
    /**
     * Sets a callback to be executed when a source is updated or deleted
     * This allows the parent controller to refresh its source list
     */
    public void setOnSourceUpdated(Runnable callback) {
        this.onSourceUpdated = callback;
    }
    
    /**
     * Loads all sources and displays them in cards
     */
    private void loadSources() {
        try {
            List<Source> sources = sourceService.getAllSources();
            sourcesContainer.getChildren().clear();
            
            if (sources.isEmpty()) {
                statusLabel.setText("No sources created yet.");
                Label emptyLabel = new Label("No sources available. Click 'Add Source' to create your first source.");
                emptyLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-padding: 20;");
                sourcesContainer.getChildren().add(emptyLabel);
            } else {
                statusLabel.setText("Total sources: " + sources.size());
                
                for (Source source : sources) {
                    VBox card = createSourceCard(source);
                    sourcesContainer.getChildren().add(card);
                }
            }
        } catch (SourceService.SourceException e) {
            showError("Error loading sources: " + e.getMessage());
            statusLabel.setText("Error loading sources");
        }
    }
    
    /**
     * Creates a card component for a source
     */
    private VBox createSourceCard(Source source) {
        VBox card = new VBox(10);
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 15; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
        card.setPrefWidth(Double.MAX_VALUE);
        
        // Source name
        Label nameLabel = new Label(source.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        
        // Source code
        Label codeLabel = new Label("Code: " + (source.getCode() != null ? source.getCode() : "N/A"));
        codeLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");
        
        // Buttons container
        HBox buttonsContainer = new HBox(10);
        
        // Edit button
        Button editButton = new Button("Edit");
        editButton.setStyle(
            "-fx-background-color: #0078d4; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 16; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand;"
        );
        editButton.setOnAction(e -> handleEditSource(source));
        
        // Delete button
        Button deleteButton = new Button("Delete");
        deleteButton.setStyle(
            "-fx-background-color: #d13438; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 16; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand;"
        );
        deleteButton.setOnAction(e -> handleDeleteSource(source));
        
        buttonsContainer.getChildren().addAll(editButton, deleteButton);
        VBox.setMargin(buttonsContainer, new Insets(5, 0, 0, 0));
        
        card.getChildren().addAll(nameLabel, codeLabel, buttonsContainer);
        
        return card;
    }
    
    /**
     * Handles the Edit button click - opens edit dialog
     */
    private void handleEditSource(Source source) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit Source");
        dialog.setHeaderText("Edit source information");
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        
        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        // Create the input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField(source.getName());
        nameField.setPromptText("e.g., Book: Clean Code, Page: Stack Overflow");
        TextField codeField = new TextField(source.getCode() != null ? source.getCode() : "");
        codeField.setPromptText("Auto-generated if empty");
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Code (optional):"), 0, 1);
        grid.add(codeField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on name field
        Platform.runLater(() -> nameField.requestFocus());
        
        // Enable/Disable save button depending on whether a name was entered
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        // Validate that name is not empty
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveButton.setDisable(newValue.trim().isEmpty());
        });
        
        // Convert the result to a pair of strings when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return new Pair<>(nameField.getText().trim(), codeField.getText().trim());
            }
            return null;
        });
        
        // Show dialog and process result
        java.util.Optional<Pair<String, String>> result = dialog.showAndWait();
        
        result.ifPresent(pair -> {
            String name = pair.getKey();
            String code = pair.getValue();
            
            if (name.isEmpty()) {
                showError("Source name cannot be empty");
                return;
            }
            
            try {
                // Update the source
                sourceService.updateSource(source.getId(), name, code.isEmpty() ? null : code);
                
                // Refresh sources list
                loadSources();
                
                // Notify parent controller to refresh its source list
                if (onSourceUpdated != null) {
                    onSourceUpdated.run();
                }
                
                showInfo("Source '" + name + "' updated successfully!");
            } catch (SourceService.SourceException e) {
                showError("Error updating source: " + e.getMessage());
            }
        });
    }
    
    /**
     * Handles the Delete button click - deletes the source with confirmation
     */
    private void handleDeleteSource(Source source) {
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete Source");
        confirmAlert.setHeaderText("Are you sure you want to delete this source?");
        confirmAlert.setContentText(
            "Source: " + source.getName() + 
            (source.getCode() != null ? " (" + source.getCode() + ")" : "") + "\n\n" +
            "This action cannot be undone. Any notes associated with this source will have their source set to null."
        );
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    sourceService.deleteSource(source.getId());
                    
                    // Refresh sources list
                    loadSources();
                    
                    // Notify parent controller to refresh its source list
                    if (onSourceUpdated != null) {
                        onSourceUpdated.run();
                    }
                    
                    showInfo("Source deleted successfully!");
                    
                } catch (SourceService.SourceException e) {
                    showError("Error deleting source: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Refreshes the sources list (public method for external calls)
     */
    public void refreshSources() {
        loadSources();
    }
    
    /**
     * Handles the "Add Source" button click
     * Opens a dialog to create a new source
     */
    @FXML
    private void handleAddSource() {
        // Create a custom dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add New Source");
        dialog.setHeaderText("Enter source information");
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        
        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        // Create the input fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField nameField = new TextField();
        nameField.setPromptText("e.g., Book: Clean Code, Page: Stack Overflow");
        TextField codeField = new TextField();
        codeField.setPromptText("Auto-generated if empty");
        
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Code (optional):"), 0, 1);
        grid.add(codeField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on name field
        Platform.runLater(() -> nameField.requestFocus());
        
        // Enable/Disable create button depending on whether a name was entered
        Button createButton = (Button) dialog.getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        
        // Validate that name is not empty
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            createButton.setDisable(newValue.trim().isEmpty());
        });
        
        // Convert the result to a pair of strings when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return new Pair<>(nameField.getText().trim(), codeField.getText().trim());
            }
            return null;
        });
        
        // Show dialog and process result
        java.util.Optional<Pair<String, String>> result = dialog.showAndWait();
        
        result.ifPresent(pair -> {
            String name = pair.getKey();
            String code = pair.getValue();
            
            if (name.isEmpty()) {
                showError("Source name cannot be empty");
                return;
            }
            
            try {
                // Create the source
                sourceService.createSource(name, code.isEmpty() ? null : code);
                
                // Refresh sources list
                loadSources();
                
                // Notify parent controller to refresh its source list
                if (onSourceUpdated != null) {
                    onSourceUpdated.run();
                }
                
                showInfo("Source '" + name + "' created successfully!");
            } catch (SourceService.SourceException e) {
                showError("Error creating source: " + e.getMessage());
            }
        });
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
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

