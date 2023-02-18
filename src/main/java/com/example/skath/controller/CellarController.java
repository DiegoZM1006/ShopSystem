package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.Alerts;
import com.example.skath.model.Credit;
import com.example.skath.model.Sale;
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
import java.util.ResourceBundle;

public class CellarController implements Initializable {

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnShow;

    @FXML
    private TableColumn<Credit, String> clientName;

    @FXML
    private TableColumn<Credit, Integer> id;

    @FXML
    private TableColumn<Credit, Date> dateCredit;

    @FXML
    private TextField idTF;

    @FXML
    private TextField nameClientTF;

    @FXML
    private TableView<Credit> table;

    @FXML
    private TableColumn<Credit, String> description;

    @FXML
    private TableColumn<Credit, Integer> amount;

    @FXML
    private DatePicker date;

    private Connection cn;

    private ObservableList<Credit> credits = FXCollections.observableArrayList();

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

        String queryProductToReturn = "SELECT CD.ID_CREDIT, P.ID, P.AMOUNT, P.DESCRIPTION FROM credit_date as CD inner join credit as C inner join product as P " +
                "on CD.ID_CREDIT = C.ID and C.ID_PRODUCT = P.ID WHERE CD.ID_CREDIT = ?";
        PreparedStatement pstmtProductToReturn = cn.prepareStatement(queryProductToReturn);
        pstmtProductToReturn.setInt(1, table.getSelectionModel().getSelectedItem().getId());
        ResultSet resultProductToReturn = pstmtProductToReturn.executeQuery();
        resultProductToReturn.next();

        String queryDeleteSalesDetail = "DELETE FROM credit_date WHERE ID_CREDIT = ?";
        PreparedStatement pstmtDeleteSalesDetail = cn.prepareStatement(queryDeleteSalesDetail);
        pstmtDeleteSalesDetail.setInt(1, resultProductToReturn.getInt("CD.ID_CREDIT"));
        int resultDeleteSalesDetail = pstmtDeleteSalesDetail.executeUpdate();

        String queryToUpdateProduct = "UPDATE product SET amount = ? WHERE ID = ?";
        PreparedStatement pstmtToUpdateProduct = cn.prepareStatement(queryToUpdateProduct);
        pstmtToUpdateProduct.setInt(1, resultProductToReturn.getInt("P.AMOUNT") + 1);
        pstmtToUpdateProduct.setInt(2, resultProductToReturn.getInt("P.ID"));
        int resultUpdateProduct = pstmtToUpdateProduct.executeUpdate();

        if(resultDeleteSalesDetail == 0) Alerts.error("Error", "Los abonos no fueron eliminados");
        if(resultUpdateProduct == 0) Alerts.error("Error", "El producto no fue regresado al inventario");

        String queryToDeleteSales = "DELETE FROM credit WHERE ID = ?";
        PreparedStatement pstmtToDeleteSales = cn.prepareStatement(queryToDeleteSales);
        pstmtToDeleteSales.setInt(1, resultProductToReturn.getInt("CD.ID_CREDIT"));
        int resultToDeleteSales = pstmtToDeleteSales.executeUpdate();

        credits.clear();
        btnSearch(new ActionEvent());

        if(resultToDeleteSales == 0) {
            Alerts.error("Error", "El credito no se ha podido eliminar");
        } else {
            Alerts.info("Credito eliminado", "El credito fue EXITOSAMENTE eliminada");
        }

        Singleton.getInstance().closeCn();

    }

    @FXML
    void btnNew(ActionEvent event) {

        credits.clear();
        date.setValue(null);
        idTF.setText("");
        nameClientTF.setText("");
        btnDelete.setDisable(true);
        btnShow.setDisable(true);

    }

    @FXML
    void btnShow(ActionEvent event) throws SQLException {

        Singleton.getInstance().setIdCreditsDetail(table.getSelectionModel().getSelectedItem().getId());

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource("creditsDetail.fxml")
            );
            Parent node = fxmlLoader.load();
            Scene scene = new Scene(node);
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Detalle de credito #" + table.getSelectionModel().getSelectedItem().getId());
            window.setResizable(false);
            window.setMaximized(false);
            window.setScene(scene);
            window.showAndWait();

        } catch (IOException ex){
            ex.printStackTrace();
        }

        credits.clear();
        btnSearch(new ActionEvent());
        Singleton.getInstance().setIdCreditsDetail(null);

    }

    @FXML
    void btnSearch(ActionEvent event) throws SQLException {

        credits.clear();

        cn = Singleton.getInstance().getCn();

        String query = "SELECT CR.ID, CL.NAME, P.DESCRIPTION, CR.AMOUNT, CR.DATE FROM credit as CR inner join client as CL inner join product as P " +
                "on CR.ID_CLIENT = CL.ID and CR.ID_PRODUCT = P.ID WHERE 1 = 1";

        if (date.getValue() != null) {
            query += " AND CAST(CR.DATE as date) = CAST('" + date.getValue() + "' as date)";
        }
        if (!idTF.getText().equals("")) {
            query += " AND CR.ID = " + idTF.getText();
        }
        if (!nameClientTF.getText().equals("")) {
            query += " AND CL.NAME REGEXP CONCAT('^','" + nameClientTF.getText() + "')";
        }

        PreparedStatement pstmt = cn.prepareStatement(query);
        ResultSet result = pstmt.executeQuery();

        while (result.next()) {

            credits.add(new Credit(
                    result.getInt("CR.ID"),
                    result.getString("CL.NAME"),
                    result.getString("P.DESCRIPTION"),
                    result.getInt("CR.AMOUNT"),
                    result.getDate("CR.DATE")
                    ));

        }

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        clientName.setCellValueFactory(new PropertyValueFactory<>("clientName"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateCredit.setCellValueFactory(new PropertyValueFactory<>("date"));

        table.setItems(credits);

        Singleton.getInstance().closeCn();

    }

}
