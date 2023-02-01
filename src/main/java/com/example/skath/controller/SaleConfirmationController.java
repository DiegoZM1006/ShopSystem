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
                long millis = System.currentTimeMillis();
                java.sql.Date date = new java.sql.Date(millis);

                String queryToSales = "INSERT INTO sales (ID_CLIENT, ID_USER, TOTAL, DATE) values(?, ?, ?, ?)";
                PreparedStatement pstmtToSales = cn.prepareStatement(queryToSales);
                pstmtToSales.setInt(1, Singleton.getInstance().getClientToFastClient().getID());
                pstmtToSales.setInt(2, Singleton.getInstance().getUser().getID());
                pstmtToSales.setDouble(3, Double.parseDouble(total.getText()));
                pstmtToSales.setDate(4, date);
                int resultToSales = pstmtToSales.executeUpdate();

                if(resultToSales == 1) {

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
                        } else {

                            String queryKnowAmount = "SELECT amount FROM product WHERE ID = ?";
                            PreparedStatement pstmtKnowAmount = cn.prepareStatement(queryKnowAmount);
                            pstmtKnowAmount.setInt(1, p.getID());
                            ResultSet resultKnowAmount = pstmtKnowAmount.executeQuery();
                            resultKnowAmount.next();

                            String queryUpdateInventory = "UPDATE product SET amount = ? WHERE ID = ?";
                            PreparedStatement pstmtUpdateInventory = cn.prepareStatement(queryUpdateInventory);
                            pstmtUpdateInventory.setInt(1, resultKnowAmount.getInt("amount") - p.getAmount());
                            pstmtUpdateInventory.setInt(2, p.getID());
                            int resultUpdateInventory = pstmtUpdateInventory.executeUpdate();

                        }

                    }

                    Alerts.info("Venta realizada", "La venta se realizo");
                    Singleton.getInstance().setTheSaleWasMade(true);

                    Stage currentStage = (Stage) table.getScene().getWindow();
                    currentStage.hide();

                } else {

                    Alerts.error("Error en venta", "Ha ocurrido un error en el sistema, la venta no se realizo");

                }


            } else if(paymentMth.getValue().equalsIgnoreCase("Credito")) {

                if(obsProducts.size() == 1 && obsProducts.get(0).getAmount() == 1) {

                    cn = Singleton.getInstance().getCn();
                    Product p = obsProducts.get(0);

                    String queryToCredit = "INSERT INTO credit (ID_CLIENT, ID_PRODUCT, AMOUNT) values (?, ?, ?)";
                    PreparedStatement pstmtToCredit = cn.prepareStatement(queryToCredit);
                    pstmtToCredit.setInt(1, Singleton.getInstance().getClientToFastClient().getID());
                    pstmtToCredit.setInt(2, p.getID());
                    pstmtToCredit.setInt(3, 1);
                    int resultToCredit = pstmtToCredit.executeUpdate();

                    if(resultToCredit == 1) {

                        long millis = System.currentTimeMillis();
                        java.sql.Date date = new java.sql.Date(millis);

                        String queryToKnowLastCredit = "SELECT count(*) as TOTAL FROM credit";
                        PreparedStatement pstmtToKnowLastCredit = cn.prepareStatement(queryToKnowLastCredit);
                        ResultSet resultToKnowLastCredit = pstmtToKnowLastCredit.executeQuery();
                        resultToKnowLastCredit.next();

                        String queryToCreditDate = "INSERT INTO credit_date (ID_USER, ID_CREDIT, PAY, PAYABLE, TOTAL, DATE) values (?, ?, ?, ?, ?, ?)";
                        PreparedStatement pstmtToCreditDate = cn.prepareStatement(queryToCreditDate);
                        pstmtToCreditDate.setInt(1, Singleton.getInstance().getUser().getID());
                        pstmtToCreditDate.setInt(2, resultToKnowLastCredit.getInt("TOTAL"));
                        pstmtToCreditDate.setDouble(3, Double.parseDouble(moneyReceived.getText()));
                        pstmtToCreditDate.setDouble(4, p.getPrice() - Double.parseDouble(moneyReceived.getText()));
                        pstmtToCreditDate.setDouble(5, p.getPrice());
                        pstmtToCreditDate.setDate(6, date);
                        int resultToCreditDate = pstmtToCreditDate.executeUpdate();

                        if(resultToCreditDate == 1) {
                            Alerts.info("Credito realizado", "El credito fue realizado");
                            Singleton.getInstance().setTheSaleWasMade(true);
                            Stage currentStage = (Stage) table.getScene().getWindow();
                            currentStage.hide();
                        } else {
                            Alerts.error("Error", "El credito NO se realizo");
                        }


                    } else {
                        Alerts.error("Error", "El credito no se pudo realizar");
                    }

                } else {
                    Alerts.error(
                            "Error",
                            "Para hacer un credito ten en cuenta lo siguiente: \n 1. Solo puede ser un producto \n 2. La cantidad de ese producto debe ser uno");

                }

            }

        } else {
            Alerts.error("Error", "Rellena la forma de pago");
        }

    }

}
