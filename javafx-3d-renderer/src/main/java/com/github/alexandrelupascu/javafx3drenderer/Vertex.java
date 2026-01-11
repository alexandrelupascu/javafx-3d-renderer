package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;

public class Vertex {
    public double x, y, z;
    public double w = 50, h = 50;

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void draw(GraphicsContext ctx) {

    }

    // projects a 3D point to 2D
    private void project() {

    }


    // converts to the right screen coordinates
    private void screen() {

    }
}
