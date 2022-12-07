package com.example.skath;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        showWindow("login.fxml", "Login");
    }

    public static void showWindow(String fxml, String title){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource(fxml)
            );
            Parent node = fxmlLoader.load();
            Scene scene = new Scene(node);
            Stage window = new Stage();
            window.setTitle(title);
            window.setResizable(false);
            window.setScene(scene);
            window.show();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}