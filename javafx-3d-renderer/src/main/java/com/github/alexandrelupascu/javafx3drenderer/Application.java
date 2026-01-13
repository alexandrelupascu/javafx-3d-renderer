package com.github.alexandrelupascu.javafx3drenderer;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {

        VBox root = new VBox();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGH);
        GraphicsContext context = canvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = -1;


            // ---- define ----
            Mesh mesh = new Mesh();

            // ---- define ----


            @Override
            public void handle(long now) {
                if (lastTime < 0) lastTime = now;
                double dt = (now - lastTime) * 1e-9;
                context.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGH);


                // ---- update ----
                mesh.rotate(0,dt,0);

                // ---- update ----




                // ---- draw ----

                mesh.draw(context);

                // ---- draw ----

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