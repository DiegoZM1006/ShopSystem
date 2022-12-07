package com.example.skath.controller;

import com.example.skath.MainApplication;
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

    // Globals var
    private Connection cn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            String urlDb = "jdbc:mysql://localhost/db_test";
            String username = "root";
            String password = "";
            cn = DriverManager.getConnection(urlDb, username, password);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void btnContinue(ActionEvent event) throws SQLException {

        if(!userLbl.getText().equals("") && !passwordLbl.getText().equals("")) {

            String query = "SELECT * FROM user  WHERE UserName = ? AND Password = ?";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setString(1, userLbl.getText());
            pstmt.setString(2, passwordLbl.getText());
            ResultSet result = pstmt.executeQuery();

            if(result.next()) {
                cn.close();
                MainApplication.showWindow("index.fxml", "Index");
                Stage currentStage = (Stage) userLbl.getScene().getWindow();
                currentStage.hide();
            } else {
                methAlert("Error", "Wrong password or username");
            }

        } else {
            methAlert("Error", "Complete all the fields");
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