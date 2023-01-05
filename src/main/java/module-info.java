module com.example.skath {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.skath to javafx.fxml;
    exports com.example.skath;
    exports com.example.skath.controller;
    opens com.example.skath.controller to javafx.fxml;
    opens com.example.skath.model to javafx.base;
    exports com.example.skath.model;
}