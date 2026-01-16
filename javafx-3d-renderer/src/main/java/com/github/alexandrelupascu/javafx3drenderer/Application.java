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



    Mesh center = new Mesh("sphere.fbx", Color.BLACK);
    Mesh small1 = new Mesh("sphere.fbx", Color.GREEN);
    Mesh small2 = new Mesh("sphere.fbx", Color.ORANGE);
    Mesh small3 = new Mesh("sphere.fbx", Color.PURPLE);



    // ---- define ----


    // called once
    private void initialize() {
        center.moveAt(0,0,-6);
        small1.moveAt(0,1,-6);
        small2.moveAt(1,-1,-6);
        small3.moveAt(-1,-1,-6);

        center.scaleBy(0.6);
        small1.scaleBy(0.3);
        small2.scaleBy(0.3);
        small3.scaleBy(0.3);

    }

    // called every frame
    private void update(double t, double dt) {
        double s = 50 * dt;

        center.rotateBy(s,s,s, center.getOrigin());

        small1.rotateBy(s,0,0,center.getOrigin());
        small2.rotateBy(s,s,0,center.getOrigin());
        small3.rotateBy(s,0,s,center.getOrigin());

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