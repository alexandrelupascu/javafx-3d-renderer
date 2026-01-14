package com.github.alexandrelupascu.javafx3drenderer;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;

public class Application extends javafx.application.Application {

    // Define Objects here :
    // ---- define ----
    Mesh mesh = new Mesh("Placeholder");


    // ---- define ----

    public static void main(String[] args) {
        launch();
    }

    // called once
    private void initialize() {
        mesh.moveBy(0, -1, -5);
    }

    // called every frame
    private void update(double t, double dt) {
        mesh.rotateBy(dt, dt, 0);
        mesh.moveBy(Math.cos(t) * dt, Math.sin(t) * dt, 0);
        mesh.resizeBy(Math.cos(t) * dt);
    }

    // called a set amount of time each frame
    private void fixedUpdate(double t) {

    }

    // renders onto the given graphics context
    private void draw(GraphicsContext ctx) {
        mesh.draw(ctx);
    }

    @Override
    public void start(Stage stage) throws IOException {
        VBox root = new VBox();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        Canvas canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGH);
        GraphicsContext context = canvas.getGraphicsContext2D();
        AnimationTimer timer = new AnimationTimer() {
            private long lastTime = -1;
            private double time = 0.0;
            private double accumulator = 0.0;

            @Override
            public void handle(long now) {
                if (lastTime < 0) {
                    lastTime = now;
                    initialize();
                }
                double deltaTime = (now - lastTime) * 1e-9;
                time += deltaTime;
                context.clearRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGH);


                accumulator += deltaTime;
                if (accumulator >= FIXED_UPDATE_TARGET_RATE) {
                    fixedUpdate(time);
                    accumulator = 0;
                }

                update(time, deltaTime);
                draw(context);
                lastTime = now;
            }
        };
        timer.start();
        root.getChildren().add(canvas);
        stage.setScene(scene);
        stage.setTitle("3D Renderer");
        stage.show();
    }
}