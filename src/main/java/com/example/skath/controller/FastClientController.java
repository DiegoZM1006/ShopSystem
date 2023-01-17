package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.Alerts;
import com.example.skath.model.Client;
import com.example.skath.model.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FastClientController implements Initializable {

    private Connection cn;

    // TableView

    @FXML
    private TableColumn<Client, Integer> ID;

    @FXML
    private TableColumn<Client, String> name;

    @FXML
    private TableView<Client> table;

    // Form buttons

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnSelClient;

    @FXML
    private Button btnUpdate;

    // Form fields

    @FXML
    private TextField idTF;

    @FXML
    private TextField nameTF;

    // Obs List

    ObservableList<Client> obsClients = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idTF.setDisable(true);
        btnSelClient.setDisable(true);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

        try {
            callData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        manageEvents();

    }

    void callData() throws SQLException {

        // Establecemos la conexion
        cn = Singleton.getInstance().getCn();

        ArrayList<Client> clients = new ArrayList<>();

        String query = "SELECT * FROM client";
        PreparedStatement pstmt = cn.prepareStatement(query);
        ResultSet result = pstmt.executeQuery();

        while(result.next()) {
            clients.add(new Client(
                    result.getInt("ID"), result.getString("NAME")
            ));
        }

        obsClients = FXCollections.observableArrayList();
        obsClients.addAll(clients);

        ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.setItems(obsClients);

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    void manageEvents(){
        table.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, prevValue, selectedValue) -> {
                    if (selectedValue != null){
                        idTF.setText(String.valueOf(selectedValue.getID()));
                        nameTF.setText(selectedValue.getName());

                        btnSave.setDisable(true);
                        btnUpdate.setDisable(false);
                        btnDelete.setDisable(false);
                        btnSelClient.setDisable(false);
                    }
                }
        );
    }

    // Form buttons actions

    @FXML
    void btnSave(ActionEvent event) throws SQLException {

        if(!nameTF.getText().equals("")) {

            // Establecemos la conexion
            cn = Singleton.getInstance().getCn();

            String query = "INSERT INTO client (NAME) values (?)";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setString(1, nameTF.getText());
            int result = pstmt.executeUpdate();

            if(result == 1) {
                Alerts.info("Registro agregado", "El registro fue EXITOSAMENTE agregado");

                callData();

            } else {
                Alerts.error("Error", "Ha ocurrido un error inesperado en el sistema, el registro NO se guardo");
            }

            // Cerramos la conexion
            Singleton.getInstance().closeCn();

        } else {
            Alerts.warning("Error", "Rellena todos los campos");
        }

    }

    @FXML
    void btnUpdate(ActionEvent event) throws SQLException {

        if(!nameTF.getText().equals("")) {

            // Establecemos la conexion
            cn = Singleton.getInstance().getCn();

            String query = "UPDATE client SET NAME = ? WHERE ID = ?";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setString(1, nameTF.getText());
            pstmt.setInt(2, Integer.parseInt(idTF.getText()));
            int result = pstmt.executeUpdate();

            if(result == 1) {

                Alerts.info("Registro actualizado", "El registro fue EXITOSAMENTE actualizado");

                callData();

            } else {
                Alerts.error("Error", "Ha ocurrido un error inesperado en el sistema, el registro NO se actualizo");
            }

            // Cerramos la conexion
            Singleton.getInstance().closeCn();

        } else {
            Alerts.warning("Error", "Rellena todos los campos");
        }

    }

    @FXML
    void btnDelete(ActionEvent event) throws SQLException {

        // Establecemos la conexion
        cn = Singleton.getInstance().getCn();

        String query = "DELETE FROM client WHERE ID = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, Integer.parseInt(idTF.getText()));
        int result = pstmt.executeUpdate();

        cn.close();

        if(result == 1) {

            Alerts.info("Registro eliminado", "El registro fue EXITOSAMENTE eliminado");

            callData();

        } else {
            Alerts.error("Error", "Ha ocurrido un error inesperado en el sistema, el registro NO se elimino");
        }

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    @FXML
    void btnNew(ActionEvent event) {

        idTF.setText(null);
        nameTF.setText(null);

        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnSelClient.setDisable(true);

    }

    @FXML
    void btnSelClient(ActionEvent event) throws SQLException {

        Singleton.getInstance().setClientToFastClient(new Client(Integer.parseInt(idTF.getText()), nameTF.getText()));

        Stage currentStage = (Stage) table.getScene().getWindow();
        currentStage.hide();

    }

}
