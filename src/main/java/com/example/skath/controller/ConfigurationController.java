package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.Singleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigurationController implements Initializable {

    @FXML
    private VBox sideBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnMenu(new ActionEvent());
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

    // Buttons actions

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
