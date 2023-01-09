package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.*;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.security.cert.CertificateParsingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class UsersController implements Initializable {

    @FXML
    private VBox sideBar;

    private Connection cn;

    // TableView columns

    @FXML
    private TableView<User> table;

    @FXML
    private TableColumn<User, String> lastname;

    @FXML
    private TableColumn<User, Integer> ID;

    @FXML
    private TableColumn<User, String> name;

    @FXML
    private TableColumn<User, String> password;

    @FXML
    private TableColumn<User, String> username;

    // Buttons form

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnPermits;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    // Fields form

    @FXML
    private TextField usernameTF;

    @FXML
    private TextField idTF;

    @FXML
    private TextField lastnameTF;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField passwordTF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnMenu(new ActionEvent());

        idTF.setDisable(true);

        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        btnPermits.setDisable(true);

        try {
            callData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    void callData() throws SQLException {

        // Establecemos la conexion
        cn = Singleton.getInstance().getCn();

        ArrayList<User> users = new ArrayList<>();

        String query = "SELECT * FROM user";
        PreparedStatement pstmt = cn.prepareStatement(query);
        ResultSet result = pstmt.executeQuery();

        while(result.next()) {
            users.add(
                    new User(
                            result.getInt("ID"), result.getString("USERNAME"),
                            result.getString("NAME"), result.getString("LASTNAME"),
                            result.getString("PASSWORD")
                    )
            );
        }

        ObservableList<User> obsUsers = FXCollections.observableArrayList();
        obsUsers.addAll(users);

        ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastname.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        password.setCellValueFactory(new PropertyValueFactory<>("password"));

        table.setItems(obsUsers);

        manageEvents();

        // Cerramos la conexion
        Singleton.getInstance().closeCn();


    }

    void manageEvents(){
        table.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, prevValue, selectedValue) -> {
                    if (selectedValue != null){
                        idTF.setText(String.valueOf(selectedValue.getID()));
                        usernameTF.setText(selectedValue.getUsername());
                        nameTF.setText(selectedValue.getName());
                        lastnameTF.setText(selectedValue.getLastName());
                        passwordTF.setText(selectedValue.getPassword());

                        btnSave.setDisable(true);
                        btnUpdate.setDisable(false);
                        btnDelete.setDisable(false);
                        btnPermits.setDisable(false);
                    }
                }
        );
    }


    void btnShowWindow(String fxml, String title) {

        if(Singleton.getInstance().isSideBarCollapsed()) {
            Singleton.getInstance().setSideBarCollapsed(false);
        } else {
            Singleton.getInstance().setSideBarCollapsed(true);
        }

        MainApplication.showWindow(fxml, title, true,true);
        Stage currentStage = (Stage) sideBar.getScene().getWindow();
        currentStage.hide();

    }

    // Form buttons methods

    @FXML
    void btnSave(ActionEvent event) throws SQLException {

        if(
                !usernameTF.getText().equals("") && !nameTF.getText().equals("") &&
                !lastnameTF.getText().equals("") && !passwordTF.getText().equals("")
        ) {

            // Establecemos la conexion
            cn = Singleton.getInstance().getCn();

            String query = "INSERT INTO user (USERNAME, NAME, LASTNAME, PASSWORD) values (?, ?, ?, ?)";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setString(1, usernameTF.getText());
            pstmt.setString(2, nameTF.getText());
            pstmt.setString(3, lastnameTF.getText());
            pstmt.setString(4, MD5Utils.md5(passwordTF.getText()));
            int result = pstmt.executeUpdate();

            if(result == 1) {
                Alerts.info("Registro agregado", "El registro fue EXITOSAMENTE agregado");

                btnShowWindow("users.fxml", "Usuarios");

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

        if(
                !usernameTF.getText().equals("") && !nameTF.getText().equals("") &&
                !lastnameTF.getText().equals("") && !passwordTF.getText().equals("")
        ) {

            // Establecemos la conexion
            cn = Singleton.getInstance().getCn();

            String query = "UPDATE user SET USERNAME = ?, NAME = ?, LASTNAME = ?, PASSWORD = ? WHERE ID = ?";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setString(1, usernameTF.getText());
            pstmt.setString(2, nameTF.getText());
            pstmt.setString(3, lastnameTF.getText());
            pstmt.setString(4, MD5Utils.md5(passwordTF.getText()));
            pstmt.setInt(5, Integer.parseInt(idTF.getText()));
            int result = pstmt.executeUpdate();

            if(result == 1) {

                Alerts.info("Registro actualizado", "El registro fue EXITOSAMENTE actualizado");

                btnShowWindow("users.fxml", "Usuarios");

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

        String query = "DELETE FROM user WHERE ID = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, Integer.parseInt(idTF.getText()));
        int result = pstmt.executeUpdate();

        cn.close();

        if(result == 1) {

            Alerts.info("Registro eliminado", "El registro fue EXITOSAMENTE eliminado");

            btnShowWindow("users.fxml", "Usuarios");

        } else {
            Alerts.error("Error", "Ha ocurrido un error inesperado en el sistema, el registro NO se elimino");
        }

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    @FXML
    void btnNew(ActionEvent event) {

        idTF.setText(null);
        usernameTF.setText(null);
        nameTF.setText(null);
        lastnameTF.setText(null);
        passwordTF.setText(null);

        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);
        btnPermits.setDisable(true);

    }

    @FXML
    void btnPermits(ActionEvent event) {

        Singleton.getInstance().setIdToScreenPermits(Integer.parseInt(idTF.getText()));
        MainApplication.showWindow("permits.fxml", "Permisos de usuario", false,false);

    }

    // SideBar buttons methods

    @FXML
    void btnMenu(ActionEvent event) {
        if(Singleton.getInstance().isSideBarCollapsed()) {
            sideBar.setPrefWidth(0);
            Singleton.getInstance().setSideBarCollapsed(false);
        } else {
            sideBar.setPrefWidth(200);
            Singleton.getInstance().setSideBarCollapsed(true);
        }
    }

    @FXML
    void btnClients(ActionEvent event) {
        btnShowWindow("clients.fxml", "Clientes");
    }

    @FXML
    void btnConfiguration(ActionEvent event) {
        btnShowWindow("configuration.fxml", "Configuracion");
    }

    @FXML
    void btnCredits(ActionEvent event) {
        btnShowWindow("credits.fxml", "Cobranza");
    }

    @FXML
    void btnInventory(ActionEvent event) {
        btnShowWindow("inventory.fxml", "Inventario");
    }

    @FXML
    void btnPackages(ActionEvent event) {
        btnShowWindow("packages.fxml", "Paquetes");
    }

    @FXML
    void btnReports(ActionEvent event) {
        btnShowWindow("reports.fxml", "Reportes");
    }

    @FXML
    void btnSell(ActionEvent event) {
        btnShowWindow("sell.fxml", "Vender");
    }

    @FXML
    void btnDashboard(ActionEvent event) {
        btnShowWindow("dashboard.fxml", "Panel de Administracion");
    }

    @FXML
    void btnExit(ActionEvent event) {

        Singleton.getInstance().setSideBarCollapsed(false);

        MainApplication.showWindow("login.fxml", "Iniciar Sesion", false,false);
        Stage currentStage = (Stage) sideBar.getScene().getWindow();
        currentStage.hide();
    }

}
