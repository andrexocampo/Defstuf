package com.portfolio.defstuf;

import com.portfolio.defstuf.controllers.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/com/portfolio/defstuf/views/MainView.fxml")
        );
        Parent root = loader.load();
        
        // Get the controller and set the primary stage
        MainViewController controller = loader.getController();
        controller.setPrimaryStage(stage);
        
        // Create the scene
        Scene scene = new Scene(root, 640, 480);
        
        // Apply CSS styles
        scene.getStylesheets().add(
            App.class.getResource("/com/portfolio/defstuf/styles/main.css")
                .toExternalForm()
        );
        
        // Configure and show the window
        stage.setTitle("DefStuf");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
