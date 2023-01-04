package com.example.skath.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Alerts {

    public static void error(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void info(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public static void warning(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

   public static Optional<ButtonType> confirmation(String title, String msg) {
       Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
       alert.setHeaderText(null);
       alert.setTitle(title);
       alert.setContentText(msg);
       return alert.showAndWait();
    }

}
