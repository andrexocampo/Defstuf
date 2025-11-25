package com.portfolio.defstuf.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.portfolio.defstuf.SystemInfo;

/**
 * Controlador para MainView.fxml
 */
public class MainViewController {
    
    @FXML
    private Label infoLabel;
    
    /**
     * Este método se ejecuta automáticamente después de cargar el FXML
     * cuando todos los componentes están inicializados
     */
    @FXML
    private void initialize() {
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();
        infoLabel.setText("JavaFX " + javafxVersion + ", ejecutándose en Java " + javaVersion);
    }
}

