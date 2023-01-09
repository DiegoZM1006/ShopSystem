package com.example.skath.controller;

import com.example.skath.model.Alerts;
import com.example.skath.model.Singleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PermitsController implements Initializable {

    @FXML
    private Text title;

    // Checkbox

    @FXML
    private CheckBox clientsCB;

    @FXML
    private CheckBox configurationCB;

    @FXML
    private CheckBox creditsCB;

    @FXML
    private CheckBox dashboardCB;

    @FXML
    private CheckBox inventoryCB;

    @FXML
    private CheckBox packagesCB;

    @FXML
    private CheckBox reportsCB;
    
    @FXML
    private CheckBox usersCB;

    @FXML
    private CheckBox sellCB;

    // More var
    
    private Connection cn;

    private int idUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idUser = Singleton.getInstance().getIdToScreenPermits();

        try {
            callData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    void callData() throws SQLException {

        // Establecemos la conexion
        cn = Singleton.getInstance().getCn();

        String query = "SELECT * FROM user WHERE ID = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, idUser);
        ResultSet result = pstmt.executeQuery();

        if(result.next()) {

            title.setText("Permisos de: " + result.getString("USERNAME"));

            query = "SELECT P.NAME FROM user as U INNER JOIN user_permit as UP INNER JOIN permit as P ON U.ID = UP.ID_USER and UP.ID_PERMIT = P.ID WHERE U.ID = ?";
            pstmt = cn.prepareStatement(query);
            pstmt.setInt(1, idUser);
            result = pstmt.executeQuery();

            while(result.next()) {
                validateCheckBox(result.getString("NAME"));
            }

        } else {
            Alerts.error("Error", "Hubo un error en el sistema, el usuario no fue encontrado");

            Stage currentStage = (Stage) title.getScene().getWindow();
            currentStage.hide();

        }

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    void validateCheckBox(String permitName) {

        switch (permitName) {
            case "Dashboard" -> dashboardCB.setSelected(true);
            case "Reports" -> reportsCB.setSelected(true);
            case "Sell" -> sellCB.setSelected(true);
            case "Inventory" -> inventoryCB.setSelected(true);
            case "Packages" -> packagesCB.setSelected(true);
            case "Clients" -> clientsCB.setSelected(true);
            case "Credits" -> creditsCB.setSelected(true);
            case "Configuration" -> configurationCB.setSelected(true);
            case "Users" -> usersCB.setSelected(true);
            default -> {
            }
        }

    }

    @FXML
    void btnSave(ActionEvent event) throws SQLException {

        // Establecemos la conexion
        cn = Singleton.getInstance().getCn();

        String query = "DELETE FROM user_permit WHERE ID_USER = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, idUser);
        int result = pstmt.executeUpdate();

        ArrayList<Integer> permits = permitsSelected();

        if(permits != null) {

            String query2;
            PreparedStatement pstmt2;
            int result2;

            for (int p : permits) {

                query2 = "INSERT INTO user_permit(ID_USER, ID_PERMIT) VALUES (?,?)";
                pstmt2 = cn.prepareStatement(query2);
                pstmt2.setInt(1, idUser);
                pstmt2.setInt(2, p);
                result2 = pstmt2.executeUpdate();

                if(result2 == 0) {
                    Alerts.error("Error", "Ha ocurrido un error agregando un permiso");
                }

            }

            Alerts.info("Actualizacion", "Los permisos fueron actualizados");

        }

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    ArrayList<Integer> permitsSelected() {

        ArrayList<Integer> permits = new ArrayList<>();

        if(dashboardCB.isSelected()) {
            permits.add(1);
        }
        if(reportsCB.isSelected()) {
            permits.add(2);
        }
        if(sellCB.isSelected()) {
            permits.add(3);
        }
        if(inventoryCB.isSelected()) {
            permits.add(4);
        }
        if(packagesCB.isSelected()) {
            permits.add(5);
        }
        if(clientsCB.isSelected()) {
            permits.add(6);
        }
        if(creditsCB.isSelected()) {
            permits.add(7);
        }
        if(configurationCB.isSelected()) {
            permits.add(8);
        }
        if(usersCB.isSelected()) {
            permits.add(9);
        }

        return permits;

    }

}
