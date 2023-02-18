package com.example.skath.controller;

import com.example.skath.MainApplication;
import com.example.skath.model.Alerts;
import com.example.skath.model.Client;
import com.example.skath.model.Product;
import com.example.skath.model.Singleton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

public class SellController implements Initializable {

    @FXML
    private VBox sideBar;

    private Connection cn;

    // Form fields

    @FXML
    private TextField nameClientTF;

    @FXML
    private TextField productCodeTF;

    @FXML
    private TextField amountTF;

    @FXML
    private TextField priceTF;

    // Form buttons

    @FXML
    private Button btnSearchCellar;

    @FXML
    private Button btnSearchSales;

    @FXML
    private Button btnAddProduct;

    @FXML
    private Button btnDeleteProduct;

    @FXML
    private Button btnUpdateProduct;

    // General buttons

    @FXML
    private Button btnSell;

    // Sale information

    @FXML
    private Label clientSelected;

    @FXML
    private Label currentDate;

    @FXML
    private Label vendorSelected;

    @FXML
    private Label total;

    // tableView

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

    // Obs list

    private ObservableList<Product> obsProducts;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnMenu(new ActionEvent());

        nameClientTF.setText(null);
        productCodeTF.setText(null);

        // setDisable some things
        nameClientTF.setDisable(true);
        productCodeTF.setDisable(true);
        priceTF.setDisable(true);
        amountTF.setDisable(true);

        btnDeleteProduct.setDisable(true);
        btnSell.setDisable(true);
        btnUpdateProduct.setDisable(true);

        obsProducts = FXCollections.observableArrayList();

        // Sale information
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        currentDate.setText(formatter.format(date));
        vendorSelected.setText(Singleton.getInstance().getUser().getName() + " " + Singleton.getInstance().getUser().getLastName());

        manageEvents();
        textFieldNumerics();

        ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

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

        Singleton.getInstance().setClientToFastClient(null);
        Singleton.getInstance().setProductToFastProduct(null);

    }

    void manageEvents() {

        table.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, prevValue, selectedValue) -> {
                    if (selectedValue != null){
                        productCodeTF.setText(selectedValue.getID() + "");
                        priceTF.setText(selectedValue.getPrice() + "");
                        amountTF.setText(selectedValue.getAmount() + "");

                        priceTF.setDisable(false);
                        amountTF.setDisable(false);

                        btnUpdateProduct.setDisable(false);
                        btnAddProduct.setDisable(true);
                        btnDeleteProduct.setDisable(false);
                    }
                }
        );

    }

    void textFieldNumerics() {

        priceTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    if (!newValue.matches("\\d*(\\.\\d*)?")) {
                        priceTF.setText(oldValue);
                    }
                }
            }
        });

        amountTF.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue != null) {
                    if (!newValue.matches("\\d*")) {
                        amountTF.setText(newValue.replaceAll("[^\\d]", ""));
                    }
                }
            }
        });

    }

    void totalSale() {

        Double t = 0.00;

        for (Product p : obsProducts) {

            t += p.getPrice() * p.getAmount();

        }

        total.setText(String.valueOf(Math.round(t * 100.0) / 100.0));

    }

    void selectClient(Client client) throws SQLException {

        // Establecemos la conexion
        Connection cn = Singleton.getInstance().getCn();

        String query = "SELECT * FROM client WHERE ID = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, client.getID());
        ResultSet result = pstmt.executeQuery();

        if(result.next()) {

            nameClientTF.setText(result.getString("NAME"));
            clientSelected.setText(result.getString("NAME"));

        } else {
            Alerts.error("Cliente no encontrado", "Hubo un error, el id del cliente no se encuentra en el sistema");
        }

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    void selectProduct(Product product) throws SQLException {

        // Establecemos la conexion
        Connection cn = Singleton.getInstance().getCn();

        String query = "SELECT * FROM product WHERE ID = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, product.getID());
        ResultSet result = pstmt.executeQuery();

        if(result.next()) {

            productCodeTF.setText(result.getString("ID"));
            priceTF.setText(String.valueOf(result.getDouble("PRICE")));
            amountTF.setText(String.valueOf(Singleton.getInstance().getProductToFastProduct().getAmount()));

            priceTF.setDisable(false);
            amountTF.setDisable(false);

        } else {
            Alerts.error("Producto no encontrado", "Hubo un error, el id del producto no se encuentra en el sistema");
        }

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    // Buttons form actions

    @FXML
    void btnSelClient(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource("fastClient.fxml")
            );
            Parent node = fxmlLoader.load();
            Scene scene = new Scene(node);
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Clientes (Acceso rapido)");
            window.setResizable(false);
            window.setMaximized(false);
            window.setScene(scene);
            window.showAndWait();

            if(Singleton.getInstance().getClientToFastClient() != null) {
                selectClient(Singleton.getInstance().getClientToFastClient());
            }

        } catch (IOException ex){
            ex.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    void btnSelProduct(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource("fastInventory.fxml")
            );
            Parent node = fxmlLoader.load();
            Scene scene = new Scene(node);
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Inventario (Acceso rapido)");
            window.setResizable(false);
            window.setMaximized(false);
            window.setScene(scene);
            window.showAndWait();

            if(Singleton.getInstance().getProductToFastProduct() != null) {
                selectProduct(Singleton.getInstance().getProductToFastProduct());

                btnAddProduct.setDisable(false);
                btnDeleteProduct.setDisable(true);
                btnUpdateProduct.setDisable(true);
            }

        } catch (IOException | SQLException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    void btnAddProduct(ActionEvent event) {

        if(
                nameClientTF.getText() != null && productCodeTF.getText() != null
        ) {

            boolean existsProduct = false;
            Product productToAdd = Singleton.getInstance().getProductToFastProduct();

            productToAdd.setPrice(Double.parseDouble(priceTF.getText()));
            productToAdd.setAmount(Integer.parseInt(amountTF.getText()));

            for(int i = 0; i < obsProducts.size(); i++) {

                if(obsProducts.get(i).getID() == productToAdd.getID()) {

                    productToAdd.setAmount(obsProducts.get(i).getAmount() + productToAdd.getAmount());
                    obsProducts.set(i, productToAdd);
                    existsProduct = true;
                    break;

                }

            }

            if(!existsProduct) obsProducts.add(productToAdd);
            table.setItems(obsProducts);

            totalSale();

            Alerts.info("Producto añadido", "El producto fue añadido");

            // Formating product fields
            // Singleton.getInstance().setClientToFastClient(null);
            Singleton.getInstance().setProductToFastProduct(null);
            productCodeTF.setText(null);
            priceTF.setText(null);
            amountTF.setText(null);

            priceTF.setDisable(true);
            amountTF.setDisable(true);
            btnUpdateProduct.setDisable(true);
            btnSell.setDisable(false);

        } else {
            Alerts.warning("Rellena los campos", "Por favor rellena los campos del cliente y tambien del producto");
        }

    }

    @FXML
    void btnUpdateProduct(ActionEvent event) {

        Product product = table.getSelectionModel().getSelectedItem();

        product.setPrice(Double.parseDouble(priceTF.getText()));
        product.setAmount(Integer.parseInt(amountTF.getText()));

        obsProducts.set(table.getSelectionModel().getSelectedIndex(), product);

        totalSale();

        Alerts.info("Producto actualizado", "El producto fue actualizado");

    }

    @FXML
    void btnDeleteProduct(ActionEvent event) {

        obsProducts.remove(table.getSelectionModel().getSelectedIndex());

        productCodeTF.setText(null);
        priceTF.setText(null);
        amountTF.setText(null);

        priceTF.setDisable(true);
        amountTF.setDisable(true);

        btnAddProduct.setDisable(false);
        btnDeleteProduct.setDisable(true);
        btnUpdateProduct.setDisable(true);

        if(obsProducts.size() < 1) {
            btnSell.setDisable(true);
        }

        table.getSelectionModel().clearSelection();

        totalSale();

        Alerts.info("Producto eliminado", "El producto fue eliminado");

    }

    @FXML
    void btnSell(ActionEvent event) throws SQLException {

        StringBuilder invalidProduct = new StringBuilder();
        boolean theSaleIsValid = true;

        cn = Singleton.getInstance().getCn();

        String query = "SELECT * FROM product WHERE ID = ?";
        PreparedStatement pstmt;
        ResultSet result;


        for(Product p : obsProducts) {

             pstmt = cn.prepareStatement(query);
             pstmt.setInt(1, p.getID());
             result = pstmt.executeQuery();

             if(result.next()) {
                 if ((result.getInt("AMOUNT") - p.getAmount()) < 0) {
                     theSaleIsValid = false;
                     invalidProduct.append(p.getDescription()).append("\n");
                 }
             } else {
                 theSaleIsValid = false;
                 Alerts.error("Error", "Un producto no se encuentra registrado en el sistema");
             }

        }

        if(theSaleIsValid) {

            Singleton.getInstance().setObsProducts(obsProducts);
            Singleton.getInstance().setTotalPayment(Double.parseDouble(total.getText()));

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(
                        MainApplication.class.getResource("saleConfirmation.fxml")
                );
                Parent node = fxmlLoader.load();
                Scene scene = new Scene(node);
                Stage window = new Stage();
                window.initModality(Modality.APPLICATION_MODAL);
                window.setTitle("Confirmacion Venta");
                window.setResizable(false);
                window.setMaximized(false);
                window.setScene(scene);
                window.showAndWait();
            } catch (IOException ex){
                ex.printStackTrace();
            }

            Singleton.getInstance().closeCn();

            if (Singleton.getInstance().isTheSaleWasMade()) {
                btnDeleteSell(new ActionEvent());
                Singleton.getInstance().setTheSaleWasMade(false);
            }

        } else {

            Alerts.error(
                    "Error de existencias",
                    "Los siguientes productos no se pueden vender porque su existencia quedaria en negativo: \n\n" + invalidProduct
            );

        }

    }

    @FXML
    void btnDeleteSell(ActionEvent event) {

        nameClientTF.setText(null);
        clientSelected.setText("Sin seleccionar");

        priceTF.setDisable(true);
        amountTF.setDisable(true);

        Singleton.getInstance().setClientToFastClient(null);
        Singleton.getInstance().setProductToFastProduct(null);
        productCodeTF.setText(null);
        priceTF.setText(null);
        amountTF.setText(null);

        btnSell.setDisable(true);
        btnDeleteProduct.setDisable(true);
        btnUpdateProduct.setDisable(true);
        btnAddProduct.setDisable(false);
        obsProducts.clear();

        total.setText("0.0");

    }

    @FXML
    void btnSearchSales(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource("sales.fxml")
            );
            Parent node = fxmlLoader.load();
            Scene scene = new Scene(node);
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Ventas realizadas");
            window.setResizable(false);
            window.setMaximized(false);
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException ex){
            ex.printStackTrace();
        }

    }

    @FXML
    void btnSearchCellar(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource("cellar.fxml")
            );
            Parent node = fxmlLoader.load();
            Scene scene = new Scene(node);
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setTitle("Creditos realizados");
            window.setResizable(false);
            window.setMaximized(false);
            window.setScene(scene);
            window.showAndWait();
        } catch (IOException ex){
            ex.printStackTrace();
        }

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
    void btnDashboard(ActionEvent event) {
        btnShowWindow("dashboard.fxml", "Panel de Administracion");
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
