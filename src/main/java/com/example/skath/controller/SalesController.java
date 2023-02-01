package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.Alerts;
import com.example.skath.model.Sales;
import com.example.skath.model.SalesDetail;
import com.example.skath.model.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SalesController implements Initializable {


    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnShow;

    @FXML
    private TableColumn<Sales, String> clientName;

    @FXML
    private TableColumn<Sales, Date> saleDate;

    @FXML
    private TableColumn<Sales, Integer> id;

    @FXML
    private TextField idTF;

    @FXML
    private TableView<Sales> table;

    @FXML
    private TableColumn<Sales, Double> total;

    @FXML
    private TableColumn<Sales, String> userName;

    @FXML
    private DatePicker date;

    private Connection cn;

    private ObservableList<Sales> sales = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnDelete.setDisable(true);
        btnShow.setDisable(true);
        manageEvents();

    }

    void manageEvents() {

        table.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, prevValue, selectedValue) -> {
                    if (selectedValue != null){
                        btnDelete.setDisable(false);
                        btnShow.setDisable(false);

                    }
                }
        );

    }

    @FXML
    void btnDelete(ActionEvent event) throws SQLException {

        cn = Singleton.getInstance().getCn();

        String queryProductToReturn = "SELECT SD.ID as ID_SALES_DETAIL, P.ID as ID_PRODUCT, P.AMOUNT as AMOUNT_INVENTORY, SD.AMOUNT as AMOUNT_RETURN " +
                "FROM sales_detail as SD inner join product as P on SD.ID_PRODUCT = P.ID WHERE SD.ID_SALES = ?";
        PreparedStatement pstmtProductToReturn = cn.prepareStatement(queryProductToReturn);
        pstmtProductToReturn.setInt(1, table.getSelectionModel().getSelectedItem().getID());
        ResultSet resultProductToReturn = pstmtProductToReturn.executeQuery();

        while (resultProductToReturn.next()) {

            String queryDeleteSalesDetail = "DELETE FROM sales_detail WHERE ID = ?";
            PreparedStatement pstmtDeleteSalesDetail = cn.prepareStatement(queryDeleteSalesDetail);
            pstmtDeleteSalesDetail.setInt(1, resultProductToReturn.getInt("ID_SALES_DETAIL"));
            int resultDeleteSalesDetail = pstmtDeleteSalesDetail.executeUpdate();

            String queryToUpdateProduct = "UPDATE product SET amount = ? WHERE ID = ?";
            PreparedStatement pstmtToUpdateProduct = cn.prepareStatement(queryToUpdateProduct);
            pstmtToUpdateProduct.setInt(1, resultProductToReturn.getInt("AMOUNT_INVENTORY") + resultProductToReturn.getInt("AMOUNT_RETURN"));
            pstmtToUpdateProduct.setInt(2, resultProductToReturn.getInt("ID_PRODUCT"));
            int resultUpdateProduct = pstmtToUpdateProduct.executeUpdate();

            if(resultDeleteSalesDetail == 0) Alerts.error("Error", "La venta con el producto " + resultProductToReturn.getString("P.NAME") + " no fue eliminado");
            if(resultUpdateProduct == 0) Alerts.error("Error", "EL producto " + resultProductToReturn.getString("P.NAME" + " no fue regresado a inventario"));

        }

        String queryToDeleteSales = "DELETE FROM sales WHERE ID = ?";
        PreparedStatement pstmtToDeleteSales = cn.prepareStatement(queryToDeleteSales);
        pstmtToDeleteSales.setInt(1, table.getSelectionModel().getSelectedItem().getID());
        int resultToDeleteSales = pstmtToDeleteSales.executeUpdate();

        sales.clear();
        btnSearch(new ActionEvent());

        if(resultToDeleteSales == 0) {
            Alerts.error("Error", "La venta no se ha podido eliminar");
        } else {
            Alerts.info("Venta eliminada", "La venta fue EXITOSAMENTE eliminada");
        }

        Singleton.getInstance().closeCn();

    }

    @FXML
    void btnNew(ActionEvent event) {

        sales.clear();
        date.setValue(null);
        idTF.setText("");
        btnDelete.setDisable(true);
        btnShow.setDisable(true);

    }

    @FXML
    void btnShow(ActionEvent event) throws SQLException {

        Singleton.getInstance().setIdSalesDetail(table.getSelectionModel().getSelectedItem().getID());

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource("salesDetail.fxml")
            );
            Parent node = fxmlLoader.load();
            Scene scene = new Scene(node);
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Detalle de venta #" + table.getSelectionModel().getSelectedItem().getID());
            window.setResizable(false);
            window.setMaximized(false);
            window.setScene(scene);
            window.showAndWait();

        } catch (IOException ex){
            ex.printStackTrace();
        }

        sales.clear();
        btnSearch(new ActionEvent());
        Singleton.getInstance().setIdSalesDetail(null);

    }

    @FXML
    void btnSearch(ActionEvent event) throws SQLException {

        sales.clear();

        cn = Singleton.getInstance().getCn();

        String query = "SELECT S.ID as FOLIO, C.NAME as CLIENTNAME, U.NAME as USERNAME, S.TOTAL as TOTAL, S.DATE as DATE " +
                "FROM sales as S inner join client as C inner join user as U " +
                "ON S.ID_CLIENT = C.ID AND S.ID_USER = U.ID " +
                "WHERE 1 = 1";
        if (date.getValue() != null) {
            query += " AND CAST(S.DATE as date) = CAST('" + date.getValue() + "' as date)";
        }
        if (!idTF.getText().equals("")) {
            query += " AND S.ID = " + idTF.getText();
        }
        PreparedStatement pstmt = cn.prepareStatement(query);
        ResultSet result = pstmt.executeQuery();

        while (result.next()) {

            sales.add(new Sales(
                    result.getInt("FOLIO"),
                    result.getString("CLIENTNAME"),
                    result.getString("USERNAME"),
                    result.getDouble("TOTAL"),
                    result.getDate("DATE")
            ));

        }

        id.setCellValueFactory(new PropertyValueFactory<>("ID"));
        clientName.setCellValueFactory(new PropertyValueFactory<>("client"));
        userName.setCellValueFactory(new PropertyValueFactory<>("user"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        saleDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.setItems(sales);

        Singleton.getInstance().closeCn();

    }

}
