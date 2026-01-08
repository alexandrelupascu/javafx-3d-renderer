package com.github.alexandrelupascu.javafx3drenderer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        Scene scene = new Scene(root, 1500, 1000);



        stage.setScene(scene);
        stage.setTitle("3D Renderer");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}