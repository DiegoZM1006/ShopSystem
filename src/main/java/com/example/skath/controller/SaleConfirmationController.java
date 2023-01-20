package com.example.skath.controller;

import com.example.skath.model.Alerts;
import com.example.skath.model.Product;
import com.example.skath.model.Singleton;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class SaleConfirmationController implements Initializable {


    private Connection cn;

    private ObservableList<Product> obsProducts;

    // TableView

    @FXML
    private TableView<Product> table;

    @FXML
    private TableColumn<Product, Integer> ID;

    @FXML
    private TableColumn<Product, String> description;

    @FXML
    private TableColumn<Product, Double> price;

    @FXML
    private TableColumn<Product, Integer> amount;

    // Button charge

    @FXML
    private Button btnCharge;

    // Form fields

    @FXML
    private Label exchange;

    @FXML
    private TextField moneyReceived;

    @FXML
    private ComboBox<String> paymentMth;

    @FXML
    private Label total;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        obsProducts = Singleton.getInstance().getObsProducts();
        ObservableList<String> obsPaymentsMth = FXCollections.observableArrayList();

        obsPaymentsMth.add("Contado");
        obsPaymentsMth.add("Credito");

        total.setText(String.valueOf(Singleton.getInstance().getTotalPayment()));
        moneyReceived.setText(String.valueOf(Singleton.getInstance().getTotalPayment()));
        exchange.setText(String.valueOf(Double.parseDouble(moneyReceived.getText()) - Singleton.getInstance().getTotalPayment()));

        ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        table.setItems(obsProducts);
        paymentMth.setItems(obsPaymentsMth);

        textFieldNumerics();


    }

    void textFieldNumerics() {

        moneyReceived.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    if (!newValue.matches("\\d*(\\.\\d*)?")) {
                        moneyReceived.setText(oldValue);
                    }
                }
            }
        });

    }

    @FXML
    void moneyRecivedMth(KeyEvent event) {

        double totalToCharge = Singleton.getInstance().getTotalPayment();
        double moneyReceivedTotal = moneyReceived.getText().equals("") ? 0 : Double.parseDouble(moneyReceived.getText());
        double exchangeTotal = moneyReceivedTotal - totalToCharge;

        exchange.setText(String.valueOf(Math.round((exchangeTotal * 100.0)) / 100.0));

    }

    @FXML
    void btnCharge(ActionEvent event) throws SQLException {

        if(paymentMth.getValue() != null) {

            if(
                    paymentMth.getValue().equalsIgnoreCase("Contado")
                    && Double.parseDouble(exchange.getText()) == 0
            ) {

                // Establecemos la conexion
                cn = Singleton.getInstance().getCn();
                // LocalDate date = LocalDate.now();
                // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                // System.out.println(formatter.format(date));
                long millis = System.currentTimeMillis();
                java.sql.Date date = new java.sql.Date(millis);

                String queryToSales = "INSERT INTO sales (ID_CLIENT, ID_USER, TOTAL, DATE) values(?, ?, ?, ?)";
                PreparedStatement pstmtToSales = cn.prepareStatement(queryToSales);
                pstmtToSales.setInt(1, Singleton.getInstance().getClientToFastClient().getID());
                pstmtToSales.setInt(2, Singleton.getInstance().getUser().getID());
                pstmtToSales.setDouble(3, Double.parseDouble(total.getText()));
                pstmtToSales.setDate(4, date);
                int resultToSales = pstmtToSales.executeUpdate();

                if(resultToSales > 0) {

                    String queryKnowLastSale = "SELECT count(*) as TOTAL FROM SALES";
                    PreparedStatement pstmtKnowLastSale = cn.prepareStatement(queryKnowLastSale);
                    ResultSet resultKnowLastSale = pstmtKnowLastSale.executeQuery();

                    resultKnowLastSale.next();

                    String queryToSalesDetail = "INSERT INTO sales_detail (ID_PRODUCT, ID_SALES, AMOUNT, PRICE, TOTAL_PRICE) values(?, ?, ?, ?, ?)";
                    PreparedStatement pstmtToSalesDetail = cn.prepareStatement(queryToSalesDetail);
                    int resultToSalesDetail;

                    for (Product p : obsProducts) {

                        pstmtToSalesDetail.setInt(1, p.getID());
                        pstmtToSalesDetail.setInt(2, resultKnowLastSale.getInt("TOTAL"));
                        pstmtToSalesDetail.setInt(3, p.getAmount());
                        pstmtToSalesDetail.setDouble(4, p.getPrice());
                        pstmtToSalesDetail.setDouble(5, p.getPrice() * p.getAmount());
                        resultToSalesDetail = pstmtToSalesDetail.executeUpdate();

                        if(resultToSalesDetail < 0) {
                            Alerts.error("Error en venta", "El producto " + p.getDescription() + " no se pudo vender");
                        }

                    }

                    Alerts.info("Venta realizada", "La venta se realizo");

                    Stage currentStage = (Stage) table.getScene().getWindow();
                    currentStage.hide();

                } else {

                    Alerts.error("Error en venta", "Ha ocurrido un error en el sistema, la venta no se realizo");

                }


            } else if(paymentMth.getValue().equalsIgnoreCase("Credito")) {



            }

        } else {

            Alerts.error("Error", "Rellena la forma de pago");

        }

    }

}
