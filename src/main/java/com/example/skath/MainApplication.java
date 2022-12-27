package com.example.skath;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        showWindow("login.fxml", "Iniciar Sesion", false ,false);
    }

    public static void showWindow(String fxml, String title, boolean resizable ,boolean maximized) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    MainApplication.class.getResource(fxml)
            );
            Parent node = fxmlLoader.load();
            Scene scene = new Scene(node);
            Stage window = new Stage();
            window.setTitle(title);
            window.setResizable(resizable);
            if(maximized) {
                window.setWidth(Screen.getScreens().get(0).getBounds().getWidth());
                window.setHeight(Screen.getScreens().get(0).getBounds().getHeight());
            }
            window.setMaximized(maximized);
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