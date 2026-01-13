package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;
import org.ejml.data.*;

import java.util.ArrayList;
import java.util.Arrays;
import static com.github.alexandrelupascu.javafx3drenderer.Utilities.Matrix;
import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;
import static org.ejml.dense.fixed.CommonOps_DDF3.*;


public class Mesh implements Drawable {
    ArrayList<Vertex> vertices = new ArrayList<>();
    ArrayList<Polygon> polygons = new ArrayList<>();
    Vertex origin;

    public Mesh() {
        generateMesh();
        generateOrigin();
    }


    public void draw(GraphicsContext ctx) {

        for (Polygon p : polygons) {
            p.draw(ctx);
        }

        for (Vertex v : vertices) {
            v.draw(ctx);
        }
    }

    public void generateMesh() {
        generateVertices();
        generatePolygons();
    }

    // finds an average point to all vertices
    public void generateOrigin() {
        DMatrix3 origin = new DMatrix3(0,0,0);
        DMatrix3 temp = new DMatrix3(0,0,0);
        for (Vertex vertex : vertices) {
            add(vertex.getCoords(), temp, origin);
            temp = origin;
        }

        divide(origin, vertices.size());
    }

    public void rotate(double yaw, double pitch, double roll, Vertex rotationPoint) {
        internalRotate(yaw,pitch,roll, rotationPoint);
    }

    public void rotate(double yaw, double pitch, double roll) {
        internalRotate(yaw,pitch, roll,origin);
    }

    private void internalRotate(double yaw, double pitch, double roll, Vertex rotationPoint) {
        DMatrix3x3 Rz = Matrix.getR(Axis.Z, yaw);
        DMatrix3x3 Ry = Matrix.getR(Axis.Y, pitch);
        DMatrix3x3 Rx = Matrix.getR(Axis.X, roll);

        DMatrix3x3 R = new DMatrix3x3();
        DMatrix3x3 temp = new DMatrix3x3();
        mult(Rz,Ry,temp);
        mult(temp, Rx,R);

        for (Vertex vertex : vertices) {
            vertex.rotate(R);
        }
    }

    public void generateVertices() {
        Vertex[] verts = {
                new Vertex(-0.5, -0.5, 1),
                new Vertex(-0.5, -0.5, 2),
                new Vertex(0.5, -0.5, 1),
                new Vertex(0.5, -0.5, 2),
                new Vertex(0.5, 0.5, 1),
                new Vertex(0.5, 0.5, 2),
                new Vertex(-0.5, 0.5, 1),
                new Vertex(-0.5, 0.5, 2),
        };

        vertices.addAll(Arrays.asList(verts));
    }

    public void generatePolygons() {
        Polygon[] polys = {
                new Polygon(vertices.get(0), vertices.get(1), vertices.get(3), vertices.get(2)),
                new Polygon(vertices.get(6), vertices.get(7), vertices.get(5), vertices.get(4)),
                new Polygon(vertices.get(0), vertices.get(1), vertices.get(7), vertices.get(6)),
                new Polygon(vertices.get(2), vertices.get(3), vertices.get(5), vertices.get(4)),
                new Polygon(vertices.get(0), vertices.get(2), vertices.get(4), vertices.get(6)),
                new Polygon(vertices.get(1), vertices.get(3), vertices.get(5), vertices.get(7))
        };

        polygons.addAll(Arrays.asList(polys));
    }
}
