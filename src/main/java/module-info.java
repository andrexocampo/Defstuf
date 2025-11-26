module com.portfolio.defstuf {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.j;
    
    exports com.portfolio.defstuf;
    exports com.portfolio.defstuf.controllers;
    exports com.portfolio.defstuf.controllers.screenshot;
    exports com.portfolio.defstuf.controllers.note;
    exports com.portfolio.defstuf.services.screenshot;
    exports com.portfolio.defstuf.models.screenshot;
    exports com.portfolio.defstuf.util;
    exports com.portfolio.defstuf.repository;
    exports com.portfolio.defstuf.config;
    
    // Open packages for JavaFX FXML
    opens com.portfolio.defstuf.views to javafx.fxml;
    opens com.portfolio.defstuf.controllers to javafx.fxml;
    opens com.portfolio.defstuf.controllers.screenshot to javafx.fxml;
    opens com.portfolio.defstuf.controllers.note to javafx.fxml;
}
