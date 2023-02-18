package com.example.skath.controller;

import com.example.skath.model.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;

public class CreditsDetailController implements Initializable {

    @FXML
    private Label balance;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnBalance;

    @FXML
    private TableColumn<CreditDetail, Integer> id;

    @FXML
    private TableColumn<CreditDetail, Double> pay;

    @FXML
    private TextField payTF;

    @FXML
    private TableColumn<CreditDetail, Double> payable;

    @FXML
    private TableColumn<CreditDetail, Date> creditDate;

    @FXML
    private TableView<CreditDetail> table;

    @FXML
    private TableColumn<CreditDetail, Double> total;

    @FXML
    private Label totalBill;

    @FXML
    private Label totalCharged;

    @FXML
    private TableColumn<CreditDetail, String> userName;

    private Connection cn;

    private double varTotalBill = 0;

    private double varBalance = 0;

    private double varTotalCharged = 0;

    private ObservableList<CreditDetail> creditsDetail = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        btnDelete.setDisable(true);
        payTF.setText("");

        try {
            callData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        manageEvents();
        textFieldNumerics();

    }

    void textFieldNumerics() {

        payTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    if (!newValue.matches("\\d*(\\.\\d*)?")) {
                        payTF.setText(oldValue);
                    }
                }
            }
        });

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

    void callData() throws SQLException {

        varTotalBill = 0;
        varTotalCharged = 0;
        varBalance = 0;

        int cont = 0;

        cn = Singleton.getInstance().getCn();

        String query = "SELECT CD.ID, U.NAME, CD.PAY, CD.PAYABLE, CD.TOTAL, CD.DATE FROM credit_date as CD inner join user as U " +
                "on CD.ID_USER = U.ID WHERE CD.ID_CREDIT = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, Singleton.getInstance().getIdCreditsDetail());
        ResultSet result = pstmt.executeQuery();

        int contIdTemp = 1;

        while (result.next()) {

            creditsDetail.add(new CreditDetail(
                    contIdTemp,
                    result.getInt("CD.ID"),
                    result.getString("U.NAME"),
                    result.getDouble("CD.PAY"),
                    result.getDouble("CD.PAYABLE"),
                    result.getDouble("CD.TOTAL"),
                    result.getDate("CD.DATE")
            ));

            contIdTemp++;

            if(cont == 0) {
                varTotalBill = result.getDouble("CD.TOTAL");
                cont++;
            }
            varTotalCharged += result.getDouble("CD.PAY");

        }

        varBalance = varTotalBill - varTotalCharged;

        if(varBalance == 0) {
            btnBalance.setDisable(true);
        } else {
            btnBalance.setDisable(false);
        }

        totalBill.setText(String.valueOf(varTotalBill));
        totalCharged.setText(String.valueOf(varTotalCharged));
        balance.setText(String.valueOf(varBalance));

        id.setCellValueFactory(new PropertyValueFactory<>("idTemp"));
        userName.setCellValueFactory(new PropertyValueFactory<>("userName"));
        pay.setCellValueFactory(new PropertyValueFactory<>("pay"));
        payable.setCellValueFactory(new PropertyValueFactory<>("payable"));
        total.setCellValueFactory(new PropertyValueFactory<>("total"));
        creditDate.setCellValueFactory(new PropertyValueFactory<>("date"));


        table.setItems(creditsDetail);

        Singleton.getInstance().closeCn();

    }

    @FXML
    void btnDelete(ActionEvent event) throws SQLException {

        if(creditsDetail.size() > 1) {
            cn = Singleton.getInstance().getCn();

            String query = "DELETE FROM credit_date WHERE ID = ?";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setInt(1, table.getSelectionModel().getSelectedItem().getId());
            int result = pstmt.executeUpdate();

            if(result == 1) {
                creditsDetail.clear();
                callData();
                Alerts.info("Abono eliminado", "El abono fue EXITOSAMENTE eliminado");
            } else {
                Alerts.error("Error", "Ha ocurrido un error, el abono no fue eliminado");
            }

            Singleton.getInstance().closeCn();
        } else {
            Alerts.error("Error", "No puedes eliminar todos los abonos");
        }

    }

    @FXML
    void btnBalance(ActionEvent event) throws SQLException {

        if(!payTF.getText().equals("")) {

            boolean thePayIsMinus = Double.parseDouble(payTF.getText()) + varTotalCharged <= varTotalBill;

            if(thePayIsMinus) {

                cn = Singleton.getInstance().getCn();
                long millis = System.currentTimeMillis();
                java.sql.Date date = new java.sql.Date(millis);

                String query = "INSERT INTO credit_date(ID_USER, ID_CREDIT, PAY, PAYABLE, TOTAL, DATE) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = cn.prepareStatement(query);
                pstmt.setInt(1, Singleton.getInstance().getUser().getID());
                pstmt.setInt(2, Singleton.getInstance().getIdCreditsDetail());
                pstmt.setDouble(3, Double.parseDouble(payTF.getText()));
                pstmt.setDouble(4, varBalance - Double.parseDouble(payTF.getText()));
                pstmt.setDouble(5, varTotalBill);
                pstmt.setDate(6, date);
                int result = pstmt.executeUpdate();

                if(result == 1) {

                    creditsDetail.clear();
                    payTF.setText("");
                    callData();
                    Alerts.info("Abono realizado", "El abono fue CORRECTAMENTE realizado");

                } else {
                    Alerts.error("Error", "Ha ocurrido un error en el sistema, el abono NO se realizo");
                }

                Singleton.getInstance().closeCn();

            } else {
                Alerts.error("Error", "El pago no puede exceder el valor total de la factura");
            }

        } else {
            Alerts.warning("Advertencia", "Rellena el campo abonar");
        }

    }

}
