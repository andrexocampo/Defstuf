module com.portfolio.defstuf {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    
    exports com.portfolio.defstuf;
    exports com.portfolio.defstuf.controllers;
    //exports com.portfolio.defstuf.services;
    exports com.portfolio.defstuf.repository;
    exports com.portfolio.defstuf.config;
    
    // Abrir el paquete de recursos para JavaFX
    opens com.portfolio.defstuf.views to javafx.fxml;
    opens com.portfolio.defstuf.controllers to javafx.fxml;
}
