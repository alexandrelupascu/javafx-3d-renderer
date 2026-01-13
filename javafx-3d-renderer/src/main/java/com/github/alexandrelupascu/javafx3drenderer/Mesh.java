package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;
import org.ejml.data.DMatrix4;
import org.ejml.data.DMatrix4x4;

import java.util.ArrayList;
import java.util.Arrays;

import static com.github.alexandrelupascu.javafx3drenderer.Utilities.Axis;
import static com.github.alexandrelupascu.javafx3drenderer.Utilities.Matrix;
import static org.ejml.dense.fixed.CommonOps_DDF4.*;


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
        DMatrix4 m1 = new DMatrix4(0, 0, 0, 0);
        DMatrix4 m2 = new DMatrix4(0, 0, 0, 0);

        for (Vertex vertex : vertices) {
            add(vertex.getCoords(), m2, m1);
            m2 = m1;
        }
        divide(m1, vertices.size());

        this.origin = new Vertex(m1);

        m1.print();
    }

    // rotate around a given point
    public void rotate(double yaw, double pitch, double roll, Vertex rotationPoint) {
        internalRotate(yaw, pitch, roll, rotationPoint.getCoords());
    }

    // rotate around the defined origin
    public void rotate(double yaw, double pitch, double roll) {
        //DMatrix4 rotationPoint = new DMatrix4(origin.getCoords());

        internalRotate(yaw, pitch, roll, origin.getCoords());
    }

    private void internalRotate(double yaw, double pitch, double roll, DMatrix4 rotationPoint) {

        // create Rotation Matrix
        DMatrix4x4 Rz = Matrix.getRotation(Axis.Z, yaw);
        DMatrix4x4 Ry = Matrix.getRotation(Axis.Y, pitch);
        DMatrix4x4 Rx = Matrix.getRotation(Axis.X, roll);
        DMatrix4x4 R = new DMatrix4x4();
        DMatrix4x4 temp = new DMatrix4x4();
        mult(Rz, Ry, temp);
        mult(temp, Rx, R);

        for (Vertex vertex : vertices) {
            vertex.rotate(R, rotationPoint);
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
