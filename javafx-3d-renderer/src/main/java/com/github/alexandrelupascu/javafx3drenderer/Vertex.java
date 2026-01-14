package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;
import org.ejml.data.DMatrix4;
import org.ejml.data.DMatrix4x4;

import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;
import static org.ejml.dense.fixed.CommonOps_DDF4.add;
import static org.ejml.dense.fixed.CommonOps_DDF4.mult;

public class Vertex implements Drawable {
    private DMatrix4 coords; // homogenous coordinates
    private DMatrix4 originOffset;

    public Vertex(double x, double y, double z) {
        x = sanitizeCoordinate(x);
        y = sanitizeCoordinate(y);
        z = sanitizeCoordinate(z);

        coords = new DMatrix4(x, y, z, 1);
    }

    private double sanitizeCoordinate(double coord) {
        return (coord == 0) ? 0.0000000001 : coord;
    }

    public Vertex(Vertex origin, DMatrix4 originOffset) {
        if (origin != null && originOffset != null) {
            this.originOffset = originOffset;
            add(origin.getCoords(), originOffset, coords);
        } else {
            System.out.println("Vertex: Couldn't create vertex, argument(s) cannot be null.");
        }
    }

    public Vertex(DMatrix4 coords) {
        this.coords = coords;
    }

    // method called from the application on a given vertex
    public void draw(GraphicsContext ctx) {
        ctx.setFill(VERTEX_COLOR);
        ctx.fillRect(screen(Axis.X) - VERTEX_SIZE / 2, screen(Axis.Y) - VERTEX_SIZE / 2, VERTEX_SIZE, VERTEX_SIZE);
    }

    // projects a 3D point to 2D, arguments must be vertex's x or y
    private double project(Axis axis) {

        switch (axis) {
            case X -> {
                return coords.a1 / coords.a3;
            }
            case Y -> {
                return coords.a2 / coords.a3;
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

    public void rotate(DMatrix4x4 R, DMatrix4 origin) {
        // temps
        DMatrix4 v1 = new DMatrix4();
        DMatrix4x4 m1 = new DMatrix4x4();
        DMatrix4x4 m2 = new DMatrix4x4();

        // T(o) x R x T(-o) x coords = newCoords
        mult(Matrix.getTranslateOrigin(origin), R, m1);
        mult(m1, Matrix.getTranslateOrigin(Matrix.getInverse(origin)), m2);
        mult(m2,coords,v1);
        coords = v1;
    }

    public void moveBy(double x, double y, double z) {
        coords.a1 += x;
        coords.a2 += y;
        coords.a3 += z;
    }

    public DMatrix4 getCoords() {
        return coords;
    }
}
