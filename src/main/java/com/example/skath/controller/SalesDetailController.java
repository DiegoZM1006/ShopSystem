package com.example.skath.controller;

import com.example.skath.model.Alerts;
import com.example.skath.model.SalesDetail;
import com.example.skath.model.Singleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class SalesDetailController implements Initializable {

    @FXML
    private TableColumn<SalesDetail, Integer> amount;

    @FXML
    private Button btnDelete;

    @FXML
    private TableColumn<SalesDetail, Double> priceT;

    @FXML
    private TableColumn<SalesDetail, Double> priceU;

    @FXML
    private TableColumn<SalesDetail, String> product;

    @FXML
    private TableView<SalesDetail> table;

    private ObservableList<SalesDetail> salesDetail = FXCollections.observableArrayList();

    private Connection cn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnDelete.setDisable(true);
        try {
            callData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        manageEvents();

    }

    void callData() throws SQLException {

        cn = Singleton.getInstance().getCn();

        String query = "SELECT SD.ID, SD.ID_PRODUCT ,P.DESCRIPTION, SD.ID_SALES, SD.AMOUNT, SD.PRICE ,SD.TOTAL_PRICE " +
                "FROM sales_detail as SD inner join product as P on SD.ID_PRODUCT = P.ID " +
                "WHERE SD.ID_SALES = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, Singleton.getInstance().getIdSalesDetail());
        ResultSet result = pstmt.executeQuery();

        while (result.next()) {

            salesDetail.add(new SalesDetail(
                    result.getInt("SD.ID"),
                    result.getInt("SD.ID_PRODUCT"),
                    result.getString("P.DESCRIPTION"),
                    result.getInt("SD.ID_SALES"),
                    result.getInt("SD.AMOUNT"),
                    result.getDouble("SD.PRICE"),
                    result.getDouble("SD.TOTAL_PRICE")
            ));

        }

        product.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        priceU.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceT.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        table.setItems(salesDetail);

        Singleton.getInstance().closeCn();

    }

    void manageEvents() {

        table.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, prevValue, selectedValue) -> {
                    if (selectedValue != null){
                        btnDelete.setDisable(false);
                    }
                }
        );

    }

    @FXML
    void btnDelete(ActionEvent event) throws SQLException {

        cn = Singleton.getInstance().getCn();

        SalesDetail productToDelete = table.getSelectionModel().getSelectedItem();

        String queryToDelete = "DELETE FROM sales_detail WHERE ID = ?";
        PreparedStatement pstmtToDelete = cn.prepareStatement(queryToDelete);
        pstmtToDelete.setInt(1, productToDelete.getID());
        int resultToDelete = pstmtToDelete.executeUpdate();

        if(resultToDelete == 1) {

            String queryKnowAmount = "SELECT amount FROM product WHERE ID = ?";
            PreparedStatement pstmtKnowAmount = cn.prepareStatement(queryKnowAmount);
            pstmtKnowAmount.setInt(1, productToDelete.getIdProduct());
            ResultSet resultKnowAmount = pstmtKnowAmount.executeQuery();
            resultKnowAmount.next();

            String queryToUpdateProduct = "UPDATE product SET amount = ? WHERE ID = ?";
            PreparedStatement pstmtToUpdateProduct = cn.prepareStatement(queryToUpdateProduct);
            pstmtToUpdateProduct.setInt(1, resultKnowAmount.getInt("amount") + productToDelete.getAmount());
            pstmtToUpdateProduct.setInt(2, productToDelete.getIdProduct());
            int resultUpdateProduct = pstmtToUpdateProduct.executeUpdate();

            if(resultUpdateProduct == 1) {

                double total = 0;

                for(SalesDetail sd : salesDetail) {
                    if(sd.getID() != productToDelete.getID()) total += sd.getTotalPrice();
                }

                String queryUpdateSales = "UPDATE sales SET TOTAL = ? WHERE ID = ?";
                PreparedStatement pstmtUpdateSales = cn.prepareStatement(queryUpdateSales);
                pstmtUpdateSales.setDouble(1, total);
                pstmtUpdateSales.setInt(2, Singleton.getInstance().getIdSalesDetail());
                int resultUpdateSales = pstmtUpdateSales.executeUpdate();

                if(resultUpdateSales == 1) {
                    Alerts.info("Producto eliminado", "El producto fue EXITOSAMENTE eliminado");
                } else {
                    Alerts.error("Error", "EL producto fue eliminado pero la venta no pudo actualizar el valor");
                }

            } else {
                Alerts.error("Error", "Ha ocurrido un error en el sistema la cantidad del producto vendido no fue regresado al inventario");
            }

            salesDetail.clear();
            callData();

            cn = Singleton.getInstance().getCn();

            if(salesDetail.size() == 0) {

                String queryToDeleteSales =  "DELETE FROM sales WHERE ID = ?";
                PreparedStatement pstmtToDeleteSales = cn.prepareStatement(queryToDeleteSales);
                pstmtToDeleteSales.setInt(1, Singleton.getInstance().getIdSalesDetail());
                int resultToDeleteSales = pstmtToDeleteSales.executeUpdate();

                if(resultToDeleteSales == 0) {
                    Alerts.error("Error", "Ha ocurrido un error en el sistema y la informacion de la venta general no se ha borrado");
                }

            }

        } else {
            Alerts.error("Error", "Ha ocurrido un error el producto no se pudo eliminar");
        }

        Singleton.getInstance().closeCn();

    }

}
