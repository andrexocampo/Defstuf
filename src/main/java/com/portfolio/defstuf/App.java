package com.portfolio.defstuf;

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
        // Cargar el archivo FXML
        FXMLLoader loader = new FXMLLoader(
            App.class.getResource("/com/portfolio/defstuf/views/MainView.fxml")
        );
        Parent root = loader.load();
        
        // Crear la escena
        Scene scene = new Scene(root, 640, 480);
        
        // Aplicar estilos CSS
        scene.getStylesheets().add(
            App.class.getResource("/com/portfolio/defstuf/styles/main.css")
                .toExternalForm()
        );
        
        // Configurar y mostrar la ventana
        stage.setTitle("DefStuf");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}