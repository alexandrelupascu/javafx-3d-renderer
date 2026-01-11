package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;
import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;

public class Vertex {
    private double x, y, z;

    public Vertex(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        if (z == 0) {
            this.z = 0.0001; // to avoid division by 0
        }
    }

    // method called from the application on a given vertex
    public void draw(GraphicsContext ctx) {
        ctx.setFill(VERTEX_COLOR);
        ctx.fillRect(screen(Axis.X), screen(Axis.Y), VERTEX_SIZE, VERTEX_SIZE);
    }

    // projects a 3D point to 2D, arguments must be vertex's x or y
    private double project(double value) {
        return value/z;
    }

    // converts and returns the right screen coordinates, -1..1 -> 0..w and 0..h
    public double screen(Axis axis) {

        double px = project(x);
        double py = project(y);

        switch (axis) {
            case X -> {
                return ((px + 1) / 2 * CANVAS_WIDTH) - VERTEX_SIZE/2;
            }
            case Y -> {
                return ((1 - (py + 1) / 2) * CANVAS_HEIGH) - VERTEX_SIZE/2;
            }
            default -> {
                System.out.println("Vertex.screen: invalid argument");
                return 0.0;
            }
        }
    }
}
