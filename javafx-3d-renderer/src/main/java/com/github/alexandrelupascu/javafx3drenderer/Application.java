package com.github.alexandrelupascu.javafx3drenderer;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;

public class Application extends javafx.application.Application {

    // Define Objects here :
    // ---- define ----



    Mesh center = new Mesh("sphere.fbx", Color.PURPLE);
    Mesh small1 = new Mesh("sphere.fbx", Color.BLUEVIOLET);
    Mesh small2 = new Mesh("sphere.fbx", Color.BLUEVIOLET);
    Mesh small3 = new Mesh("sphere.fbx", Color.BLUEVIOLET);



    // ---- define ----


    // called once
    private void initialize() {
        center.moveBy(0,0,-4);

        small1.moveBy(0,0,-4);
        small2.moveBy(0,0,-4);
        small3.moveBy(0,0,-4);

        small1.scaleBy(-1);
        small2.scaleBy(0.9);
        small3.scaleBy(0.9);

        small2.rotateBy(0,120,0);
        small3.rotateBy(0,240,0);

    }

    // called every frame
    private void update(double t, double dt) {
        double rotationSpeed = 50 * dt;
        small1.rotateBy(rotationSpeed, rotationSpeed, 0);
        small2.rotateBy(0, rotationSpeed, rotationSpeed);
        small3.rotateBy(rotationSpeed, 0, rotationSpeed);

    }

    // called a set amount of time each frame
    private void fixedUpdate(double t) {

    }

    // renders onto the given graphics context
    private void draw(GraphicsContext ctx) {
        center.draw(ctx, ORIGIN_COLOR);
        small1.draw(ctx, ORIGIN_COLOR);
        small2.draw(ctx, ORIGIN_COLOR);
        small3.draw(ctx, ORIGIN_COLOR);

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

    static void main(String[] args) {
        launch();
    }
}