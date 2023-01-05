package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.MD5Utils;
import com.example.skath.model.Singleton;
import com.example.skath.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private PasswordField passwordLbl;
    @FXML
    private TextField userLbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Singleton.getInstance();
    }

    @FXML
    void btnContinue(ActionEvent event) throws SQLException {

        if(!userLbl.getText().equals("") && !passwordLbl.getText().equals("")) {

            // Establecemos la conexion
            Connection cn = Singleton.getInstance().getCn();

            String query = "SELECT * FROM user WHERE USERNAME  = ? COLLATE utf8mb4_bin AND PASSWORD = ?";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setString(1, userLbl.getText());
            pstmt.setString(2, MD5Utils.md5(passwordLbl.getText()));
            ResultSet result = pstmt.executeQuery();

            if(result.next()) {

                Singleton.getInstance().setUser(new User(
                        result.getInt("ID"),
                        result.getString("USERNAME"),
                        result.getString("NAME"),
                        result.getString("LASTNAME")
                ));
                MainApplication.showWindow("dashboard.fxml", "Panel de Administracion", true,true);
                Stage currentStage = (Stage) userLbl.getScene().getWindow();
                currentStage.hide();
            } else {
                methAlert("Error, usuario no encontrado", "Contraseña o usuario incorrecto");
            }

            // Cerramos la conexion
            Singleton.getInstance().closeCn();

        } else {
            methAlert("Error", "Completa todos los campos");
        }
    }

    public void methAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

}