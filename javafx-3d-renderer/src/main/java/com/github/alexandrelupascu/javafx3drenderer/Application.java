package com.github.alexandrelupascu.javafx3drenderer;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        final int SCREEN_WIDTH = 800, SCREEN_HEIGHT = 600;
        final int CANVAS_WIDTH = 800, CANVAS_HEIGH = 600;

        VBox root = new VBox();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGH);
        GraphicsContext context = canvas.getGraphicsContext2D();


        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = -1;

            double x = 0, y = 40;

            @Override
            public void handle(long now) {
                if (lastTime < 0) lastTime = now;
                double dt = (now - lastTime) * 1e-9;

                x += 90 * dt;

                context.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGH);
                context.fillRect(x, y, 40, 40);

                lastTime = now;
            }
        };
        timer.start();


        root.getChildren().add(canvas);

        stage.setScene(scene);
        stage.setTitle("3D Renderer");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}