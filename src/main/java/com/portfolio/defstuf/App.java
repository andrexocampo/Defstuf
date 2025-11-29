package com.portfolio.defstuf;

import com.portfolio.defstuf.controllers.MainViewController;
import com.portfolio.defstuf.controllers.auth.LoginController;
import com.portfolio.defstuf.repository.user.DatabaseInitializer;
import com.portfolio.defstuf.session.SessionManager;
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
        // Initialize database tables
        DatabaseInitializer.initializeDatabase();
        
        // Check if user is already logged in
        if (SessionManager.getInstance().isLoggedIn()) {
            showMainView(stage);
        } else {
            showLoginView(stage);
        }
    }
    
    /**
     * Shows the login view
     */
    private void showLoginView(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/com/portfolio/defstuf/views/auth/LoginView.fxml")
        );
        Parent root = loader.load();
        
        LoginController controller = loader.getController();
        controller.setPrimaryStage(stage);
        
        Scene scene = new Scene(root, 500, 400);
        scene.getStylesheets().add(
            App.class.getResource("/com/portfolio/defstuf/styles/main.css")
                .toExternalForm()
        );
        
        stage.setTitle("DefStuf - Login");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Shows the main application view
     */
    private void showMainView(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/com/portfolio/defstuf/views/MainView.fxml")
        );
        Parent root = loader.load();
        
        MainViewController controller = loader.getController();
        controller.setPrimaryStage(stage);
        
        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(
            App.class.getResource("/com/portfolio/defstuf/styles/main.css")
                .toExternalForm()
        );
        
        stage.setTitle("DefStuf");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
