package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.MD5Utils;
import com.example.skath.model.Singleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.transform.Result;
import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConfigurationController implements Initializable {

    @FXML
    private VBox sideBar;

    // Form profile components

    @FXML
    private TextField lastnameTF;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField usernameTF;

    // Form change password components

    @FXML
    private PasswordField actualPasswordTF;

    @FXML
    private PasswordField newPasswordTF;

    @FXML
    private PasswordField repeatNewPasswordTF;

    // Form store information components

    @FXML
    private TextField nameStoreTF;

    @FXML
    private TextField phoneNumberTF;

    @FXML
    private TextField streetAddressTF;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnMenu(new ActionEvent());

        // Calling the data of the user
        try {
            callData();
        } catch (SQLException e) {
            methAlert("Error", "Se presento un ERROR en el sistema", "Error");
            throw new RuntimeException(e);
        }

    }

    void callData() throws SQLException {

        String query = "SELECT * FROM user WHERE ID = ?";
        PreparedStatement pstmt = Singleton.getInstance().getCn().prepareStatement(query);
        pstmt.setInt(1, Singleton.getInstance().getUser().getID());
        ResultSet result = pstmt.executeQuery();

        result.next();

        nameTF.setText(result.getString("NAME"));
        lastnameTF.setText(result.getString("LASTNAME"));
        usernameTF.setText(result.getString("USERNAME"));

        query = "SELECT * FROM store";
        pstmt = Singleton.getInstance().getCn().prepareStatement(query);
        result = pstmt.executeQuery();

        result.next();

        nameStoreTF.setText(result.getString("NAME"));
        phoneNumberTF.setText(result.getString("PHONE_NUMBER"));
        streetAddressTF.setText(result.getString("STREET_ADDRESS"));

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

    public void methAlert(String alertType, String title, String msg) {

        Alert alert = new Alert(Alert.AlertType.NONE);

        if(alertType.equals("Error")) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else if(alertType.equals("Info")) {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }

        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    int updateDataFormProfile(String username, String name, String lastname, int ID) throws SQLException {
        String query = "UPDATE user SET USERNAME = ?, NAME = ?, LASTNAME = ? WHERE ID = ?";
        PreparedStatement pstmt = Singleton.getInstance().getCn().prepareStatement(query);
        pstmt.setString(1, username);
        pstmt.setString(2, name);
        pstmt.setString(3, lastname);
        pstmt.setInt(4, ID);
        return pstmt.executeUpdate();
    }

    // Buttons actions forms

    @FXML
    void btnProfile(ActionEvent event) throws SQLException {

        // Compruebo si relleno todos los campos del formulario
        if(!nameTF.getText().equals("") && !lastnameTF.getText().equals("") && !usernameTF.getText().equals("")) {

            // Hacemos un llamado a la base de datos con el ID del usuario que ingreso en el sistema
            String query = "SELECT count(*) as REGISTROS FROM user WHERE ID = ?";
            PreparedStatement pstmt = Singleton.getInstance().getCn().prepareStatement(query);
            pstmt.setInt(1, Singleton.getInstance().getUser().getID());
            ResultSet result = pstmt.executeQuery();
            result.next();

            // Comprobamos si existe un registro en la base de datos
            if(result.getInt("REGISTROS") == 1) {

                // Muestro una alerta de confirmacion, para validar si el usuario quiere realizar los cambios
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setTitle("Confirmacion de actualizacion de datos de usuario");
                alert.setContentText("¿Esta usted seguro de actualizar los datos de su perfil?");
                Optional<ButtonType> resultConfirmation = alert.showAndWait();

                // Si el boton pulsado fue el de "aceptar" actualizara la base de datos con los datos puestos en los campos del formulario
                if(resultConfirmation.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    int update = updateDataFormProfile(usernameTF.getText(), nameTF.getText(), lastnameTF.getText(), Singleton.getInstance().getUser().getID());

                    // Validamos si la base de datos si fue actualizada
                    if(update > 0) {
                        methAlert("Info", "Perfil actualizado", "Los datos fueron CORRECTAMENTE actualizados");
                    } else {
                        methAlert("Error", "Upps, hubo un error", "Ocurrio un error en el sistema, los datos no se actualizaron");
                    }

                } else {
                    methAlert("Error", "Proceso Cancelado", "Los datos de tu perfil no fueron actualizados");
                    btnShowWindow("configuration.fxml", "Configuracion");
                }

            } else {
                methAlert("Error", "Upps, hubo un error", "El usuario no se encontro en el sistema");
            }

        } else {
            methAlert("Error","Error", "Rellena todos los campos para guardar los cambios");
        }

    }

    @FXML
    void btnPassword(ActionEvent event) throws SQLException {

        // Compruebo si relleno todos los campos del formulario
        if(!actualPasswordTF.getText().equals("") && !newPasswordTF.getText().equals("") && !repeatNewPasswordTF.getText().equals("")) {

            // Compruebo si los campos de la nueva contrasñea coinciden
            if(newPasswordTF.getText().equals(repeatNewPasswordTF.getText())) {

                // Hago un llamado a la base de datos para traer los datos del usuario
                String query = "SELECT * FROM user WHERE ID = ? and PASSWORD = ?";
                PreparedStatement pstmt = Singleton.getInstance().getCn().prepareStatement(query);
                pstmt.setInt(1, Singleton.getInstance().getUser().getID());
                pstmt.setString(2, MD5Utils.md5(actualPasswordTF.getText()));
                ResultSet result = pstmt.executeQuery();

                // Si el usuario ingreso bien su contraseña encontrara un registro
                if(result.next()) {

                    // Muestro una alerta de confirmacion, para ver si el usuario quiere cambiar su contraseña
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText(null);
                    alert.setTitle("Confirmacion de cambio de contraseña");
                    alert.setContentText("¿Esta usted seguro de actualizar la contraseña?");
                    Optional<ButtonType> resultConfirmation = alert.showAndWait();

                    // Si el boton pulsado fue "aceptar" el sistema realizara el cambio de la contraseña
                    if(resultConfirmation.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        methAlert("Info", "Contraseña cambiada", "La contraseña fue CORRECTAMENTE cambiada");

                        query = "UPDATE user SET PASSWORD = ? WHERE ID = ?";
                        pstmt = Singleton.getInstance().getCn().prepareStatement(query);
                        pstmt.setString(1, MD5Utils.md5(newPasswordTF.getText()));
                        pstmt.setInt(2, Singleton.getInstance().getUser().getID());
                        int newResult = pstmt.executeUpdate();

                        actualPasswordTF.setText("");
                        newPasswordTF.setText("");
                        repeatNewPasswordTF.setText("");

                    } else {
                        methAlert("Error", "Proceso Cancelado", "La contraseña no fue actualizada");

                        actualPasswordTF.setText("");
                        newPasswordTF.setText("");
                        repeatNewPasswordTF.setText("");

                    }

                } else {
                    methAlert("Error", "Error en la contraseña", "La contraseña actual es incorrecta");
                }

            } else {
                methAlert("Error","Error en coincidencia", "El campo (nueva contraseña) no coincide con el campo (repetir nueva contraseña)");
            }

        } else {
            methAlert("Error","Error", "Rellena todos los campos para actualizar la contraseña");
        }

    }

    @FXML
    void btnInfoStore(ActionEvent event) throws SQLException {

        // Compruebo si relleno todos los campos del formulario
        if(!nameStoreTF.getText().equals("") && !phoneNumberTF.getText().equals("") && !streetAddressTF.getText().equals("")) {

            // Muestro una alerta de confirmacion
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setTitle("Confirmacion de actualizacion datos de la tienda");
            alert.setContentText("¿Esta usted seguro de actualizar los datos de la tienda?");
            Optional<ButtonType> resultConfirmation = alert.showAndWait();

            // Si el boton pulsado fue "aceptar" el sistema realizara el cambio de los datos de la tienda
            if(resultConfirmation.get().getButtonData() == ButtonBar.ButtonData.OK_DONE) {

                String query = "UPDATE store SET NAME = ?, PHONE_NUMBER = ?, STREET_ADDRESS = ? WHERE ID = 1";
                PreparedStatement pstmt = Singleton.getInstance().getCn().prepareStatement(query);
                pstmt.setString(1, nameStoreTF.getText());
                pstmt.setString(2, phoneNumberTF.getText());
                pstmt.setString(3, streetAddressTF.getText());
                int result = pstmt.executeUpdate();

                if(result > 0) {
                    methAlert("Info", "Datos actualizados", "Los datos de la tienda fueron CORRECTAMENTE actualizados");
                } else {
                    methAlert("Error", "Error inesperado", "Los datos de la tienda NO fueron actualizados");
                    btnShowWindow("configuration.fxml", "Configuracion");
                }

            } else {
                methAlert("Error", "Proceso Cancelado", "Los datos de la tienda no fueron actualizados");
                btnShowWindow("configuration.fxml", "Configuracion");
            }

        } else {
            methAlert("Error","Error", "Rellena todos los campos del formulario");
        }

    }

    // Buttons actions sideBar

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
    void btnDashboard(ActionEvent event) {
        btnShowWindow("dashboard.fxml", "Panel de Administracion");
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
    void btnUsers(ActionEvent event) {
        btnShowWindow("users.fxml", "Usuarios");
    }

    @FXML
    void btnExit(ActionEvent event) {

        Singleton.getInstance().setSideBarCollapsed(false);

        MainApplication.showWindow("login.fxml", "Iniciar Sesion", false,false);
        Stage currentStage = (Stage) sideBar.getScene().getWindow();
        currentStage.hide();
    }

}
