module com.portfolio.defstuf {
    requires javafx.controls;
    requires javafx.fxml;
    
    exports com.portfolio.defstuf;
    exports com.portfolio.defstuf.controllers;
    
    // Abrir el paquete de recursos para JavaFX
    opens com.portfolio.defstuf.views to javafx.fxml;
    opens com.portfolio.defstuf.controllers to javafx.fxml;
}
