package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;
import org.ejml.*;
import org.ejml.data.DMatrix3;

import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;

public class Vertex implements Drawable{
    private DMatrix3 coords;

    public Vertex(double x, double y, double z) {
        coords = new DMatrix3(x,y, (z == 0) ? 0.00001 : z);
    }

    // method called from the application on a given vertex
    public void draw(GraphicsContext ctx) {
        ctx.setFill(VERTEX_COLOR);
        ctx.fillRect(screen(Axis.X) - VERTEX_SIZE/2, screen(Axis.Y)- VERTEX_SIZE/2, VERTEX_SIZE, VERTEX_SIZE);
    }

    // projects a 3D point to 2D, arguments must be vertex's x or y
    private double project(Axis axis) {

        switch (axis) {
            case X -> {
                return coords.a1/coords.a3;
            }
            case Y -> {
                return coords.a2/coords.a3;
            }
            default -> {
                System.out.println("Vertex.screen: invalid argument");
                return 0.0;
            }
        }
    }

    // converts and returns the right screen coordinates, -1..1 -> 0..w and 0..h
    public double screen(Axis axis) {

        switch (axis) {
            case X -> {
                return ((project(Axis.X) + 1) / 2 * CANVAS_WIDTH);
            }
            case Y -> {
                return ((1 - (project(Axis.Y) + 1) / 2) * CANVAS_HEIGH);
            }
            default -> {
                System.out.println("Vertex.screen: invalid argument");
                return 0.0;
            }
        }
    }
}
