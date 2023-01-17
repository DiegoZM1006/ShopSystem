package com.example.skath.controller;

import com.example.skath.MainApplication;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class FastInventoryController implements Initializable {

    private Connection cn;

    // TableView

    @FXML
    private TableView<Product> table;

    @FXML
    private TableColumn<Product, Integer> ID;

    @FXML
    private TableColumn<Product, Storage> storage;

    @FXML
    private TableColumn<Product, Family> family;

    @FXML
    private TableColumn<Product, String> description;

    @FXML
    private TableColumn<Product, Double> price;

    @FXML
    private TableColumn<Product, Integer> amount;

    // Fields form

    @FXML
    private TextField amountTF;

    @FXML
    private TextField idTF;

    @FXML
    private TextField nameTF;

    @FXML
    private TextField priceTF;

    @FXML
    private ComboBox<Storage> storageCB;

    @FXML
    private ComboBox<Family> familyCB;

    // Buttons form

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnSelProduct;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        idTF.setDisable(true);
        btnSelProduct.setDisable(true);
        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);

        try {
            callData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        textFieldNumerics();
        manageEvents();

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

    void callData() throws SQLException {

        // Establecemos la conexion
        cn = Singleton.getInstance().getCn();

        ObservableList<Family> obsFamily = FXCollections.observableArrayList();
        ObservableList<Storage> obsStorage = FXCollections.observableArrayList();
        ObservableList<Product> obsProducts = FXCollections.observableArrayList();

        String queryStorage = "SELECT * FROM storage";
        PreparedStatement pstmtStorage = cn.prepareStatement(queryStorage);
        ResultSet resultStorage = pstmtStorage.executeQuery();

        while(resultStorage.next()) {
            obsStorage.add(
                    new Storage(
                            resultStorage.getInt("ID"), resultStorage.getString("NAME")
                    )
            );
        }

        String queryFamily = "SELECT * FROM family";
        PreparedStatement pstmtFamily = cn.prepareStatement(queryFamily);
        ResultSet resultFamily = pstmtFamily.executeQuery();

        while(resultFamily.next()) {
            obsFamily.add(
                    new Family(
                            resultFamily.getInt("ID"), resultFamily.getString("NAME")
                    )
            );
        }

        String queryProduct = "" +
                "SELECT I.ID, F.ID as ID_FAMILY, F.NAME as FAMILY_NAME, S.ID as ID_STORE, S.NAME as STORE_NAME, I.DESCRIPTION, I.PRICE, I.AMOUNT " +
                "FROM product as I INNER JOIN family as F INNER JOIN storage as S " +
                "ON F.ID = I.ID_FAMILY and S.ID = I.ID_STORE " +
                "ORDER BY I.ID";
        PreparedStatement pstmtProduct = cn.prepareStatement(queryProduct);
        ResultSet resultProduct = pstmtProduct.executeQuery();

        while(resultProduct.next()) {
            obsProducts.add(
                    new Product(
                            resultProduct.getInt("ID"),
                            new Storage(resultProduct.getInt("ID_STORE"), resultProduct.getString("STORE_NAME")),
                            new Family(resultProduct.getInt("ID_FAMILY"), resultProduct.getString("FAMILY_NAME")),
                            resultProduct.getString("DESCRIPTION"), resultProduct.getDouble("PRICE"),
                            resultProduct.getInt("AMOUNT")
                    )
            );
        }

        storageCB.setItems(obsStorage);
        familyCB.setItems(obsFamily);
        table.setItems(obsProducts);

        ID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        storage.setCellValueFactory(new PropertyValueFactory<>("Storage"));
        family.setCellValueFactory(new PropertyValueFactory<>("Family"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        amount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    void manageEvents() {

        table.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, prevValue, selectedValue) -> {
                    if (selectedValue != null){
                        idTF.setText(String.valueOf(selectedValue.getID()));
                        storageCB.setValue(selectedValue.getStorage());
                        familyCB.setValue(selectedValue.getFamily());
                        nameTF.setText(selectedValue.getDescription());
                        priceTF.setText(selectedValue.getPrice() + "");
                        amountTF.setText(selectedValue.getAmount() + "");

                        btnSave.setDisable(true);
                        btnUpdate.setDisable(false);
                        btnDelete.setDisable(false);
                        btnSelProduct.setDisable(false);
                    }
                }
        );

    }

    // Buttons form methods

    @FXML
    void btnSave(ActionEvent event) throws SQLException {

        if(
                !nameTF.getText().equals("") && storageCB.getValue() != null &&
                        familyCB.getValue() != null && !priceTF.getText().equals("") &&
                        !amountTF.getText().equals("")
        ) {

            // Establecemos la conexion
            cn = Singleton.getInstance().getCn();

            String query = "INSERT INTO product (ID_STORE, ID_FAMILY, DESCRIPTION, PRICE, AMOUNT) values (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setInt(1, storageCB.getValue().getId());
            pstmt.setInt(2, familyCB.getValue().getId());
            pstmt.setString(3, nameTF.getText());
            pstmt.setDouble(4, Double.parseDouble(priceTF.getText()));
            pstmt.setInt(5, Integer.parseInt(amountTF.getText()));
            int result = pstmt.executeUpdate();

            if(result == 1) {
                Alerts.info("Registro agregado", "El registro fue EXITOSAMENTE agregado");

                callData();

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
                !nameTF.getText().equals("") && storageCB.getValue() != null &&
                        familyCB.getValue() != null && !priceTF.getText().equals("") &&
                        !amountTF.getText().equals("")
        ) {

            // Establecemos la conexion
            cn = Singleton.getInstance().getCn();

            String query = "UPDATE product SET ID_STORE = ?, ID_FAMILY = ?, DESCRIPTION = ?, PRICE = ?, AMOUNT = ? WHERE ID = ?";
            PreparedStatement pstmt = cn.prepareStatement(query);
            pstmt.setInt(1, storageCB.getValue().getId());
            pstmt.setInt(2, familyCB.getValue().getId());
            pstmt.setString(3, nameTF.getText());
            pstmt.setDouble(4, Double.parseDouble(priceTF.getText()));
            pstmt.setInt(5, Integer.parseInt(amountTF.getText()));
            pstmt.setInt(6, Integer.parseInt(idTF.getText()));
            int result = pstmt.executeUpdate();

            if(result == 1) {

                Alerts.info("Registro actualizado", "El registro fue EXITOSAMENTE actualizado");

                callData();

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

        String query = "DELETE FROM product WHERE ID = ?";
        PreparedStatement pstmt = cn.prepareStatement(query);
        pstmt.setInt(1, Integer.parseInt(idTF.getText()));
        int result = pstmt.executeUpdate();

        if(result == 1) {

            Alerts.info("Registro eliminado", "El registro fue EXITOSAMENTE eliminado");

            callData();

        } else {
            Alerts.error("Error", "Ha ocurrido un error inesperado en el sistema, el registro NO se elimino");
        }

        // Cerramos la conexion
        Singleton.getInstance().closeCn();

    }

    @FXML
    void btnNew(ActionEvent event) {

        idTF.setText(null);
        nameTF.setText(null);
        priceTF.setText(null);
        amountTF.setText(null);
        storageCB.setValue(null);
        familyCB.setValue(null);

        btnSelProduct.setDisable(true);
        btnSave.setDisable(false);
        btnUpdate.setDisable(true);
        btnDelete.setDisable(true);

    }

    @FXML
    void btnSelProduct(ActionEvent event) {

        Singleton.getInstance().setProductToFastProduct(new Product(
                Integer.parseInt(idTF.getText()), storageCB.getValue(),
                familyCB.getValue(), nameTF.getText(), Double.parseDouble(priceTF.getText()), 1
        ));

        Stage currentStage = (Stage) table.getScene().getWindow();
        currentStage.hide();

    }

}
