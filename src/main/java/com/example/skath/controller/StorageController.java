package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.Alerts;
import com.example.skath.model.Family;
import com.example.skath.model.Singleton;
import com.example.skath.model.Storage;
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

public class StorageController implements Initializable {

    @FXML
    private TableView<Storage> table;

    @FXML
    private TableColumn<Storage, Integer> ID;

    @FXML
    private TableColumn<Storage, String> name;

    // Components form

    @FXML
    private TextField idTF;

    @FXML
    private TextField nameTF;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    // Connection var

    private Connection cn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idTF.setDisable(true);

        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);

        try {
            callData();
        } catch (SQLException e) {
            Alerts.error("Upps, error de conexion", "Comprueba que tienes conexion a la base de datos");
            throw new RuntimeException(e);
        }
    }

    void callData() throws SQLException {

        // Establecemos la conexion
        cn = Singleton.getInstance().getCn();

        ArrayList<Storage> stores = new ArrayList<>();

        String query = "SELECT * FROM storage";
        PreparedStatement pstmt = cn.prepareStatement(query);
        ResultSet result = pstmt.executeQuery();

        while(result.next()) {
            stores.add(new Storage(result.getInt("ID"), result.getString("NAME")));
        }

        ObservableList<Storage> obsStores = FXCollections.observableArrayList();
        obsStores.addAll(stores);

        ID.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));

        table.setItems(obsStores);

        manageEvents();

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    void manageEvents(){
        table.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, prevValue, selectedValue) -> {
                    if (selectedValue != null){
                        idTF.setText(String.valueOf(selectedValue.getId()));
                        nameTF.setText(selectedValue.getName());

                        btnSave.setDisable(true);
                        btnUpdate.setDisable(false);
                        btnDelete.setDisable(false);
                    }
                }
        );
    }

    // Buttons actions

    @FXML
    void btnDelete(ActionEvent event) throws SQLException {

        // Establecemos la conexion
        cn = Singleton.getInstance().getCn();

        String query = "DELETE FROM storage WHERE ID = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, Integer.parseInt(idTF.getText()));
        int result = pstmt.executeUpdate();

        cn.close();

        if(result == 1) {

            Alerts.info("Registro eliminado", "El registro fue EXITOSAMENTE eliminado");


            MainApplication.showWindow("storage.fxml", "Almacenes", false,false);
            Stage currentStage = (Stage) table.getScene().getWindow();
            currentStage.hide();

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
    }

    @FXML
    void btnSave(ActionEvent event) throws SQLException {

        if(!nameTF.getText().equals("")) {

            // Establecemos la conexion
            cn = Singleton.getInstance().getCn();

            String query = "INSERT INTO storage (NAME) values (?)";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setString(1, nameTF.getText());
            int result = pstmt.executeUpdate();

            if(result == 1) {
                Alerts.info("Registro agregado", "El registro fue EXITOSAMENTE agregado");

                MainApplication.showWindow("storage.fxml", "Almacenes", false,false);
                Stage currentStage = (Stage) table.getScene().getWindow();
                currentStage.hide();

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

            String query = "UPDATE storage SET NAME = ? WHERE ID = ?";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setString(1, nameTF.getText());
            pstmt.setInt(2, Integer.parseInt(idTF.getText()));
            int result = pstmt.executeUpdate();

            if(result == 1) {

                Alerts.info("Registro actualizado", "El registro fue EXITOSAMENTE actualizado");

                MainApplication.showWindow("storage.fxml", "Almacenes", false,false);
                Stage currentStage = (Stage) table.getScene().getWindow();
                currentStage.hide();

            } else {
                Alerts.error("Error", "Ha ocurrido un error inesperado en el sistema, el registro NO se actualizo");
            }

            // Cerramos la conexion
            Singleton.getInstance().closeCn();

        } else {
            Alerts.warning("Error", "Rellena todos los campos");
        }

    }

}
