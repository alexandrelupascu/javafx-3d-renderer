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

    public Mesh(String filePath) {
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

        origin.draw(ctx);
        origin.drawCoords(ctx);
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
    }

    public void moveBy(double x, double y, double z) {

        origin.moveBy(x, y, z);
        for (Vertex vertex : vertices) {
            vertex.moveBy(x, y, z);
        }
    }


    // rotate around a given point
    public void rotateBy(double yaw, double pitch, double roll, Vertex rotationPoint) {
        internalRotate(yaw, pitch, roll, rotationPoint.getCoords());
    }

    // rotate around the defined origin
    public void rotateBy(double yaw, double pitch, double roll) {
        internalRotate(yaw, pitch, roll, origin.getCoords());
    }

    private void internalRotate(double yaw, double pitch, double roll, DMatrix4 rotationPoint) {

        // create Rotation Matrix
        // R = Rz x Ry x Rx
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

    public void resizeBy(double scaleFactor) {
        for (Vertex v : vertices) {
            DMatrix4 scaleDirection = findScaleDirection(v, origin, scaleFactor);
            v.moveBy(scaleDirection.a1, scaleDirection.a2, scaleDirection.a3);
        }
    }

    // returns a vector from v1 to v2
    private DMatrix4 findScaleDirection(Vertex v1, Vertex v2, double factor) {
        DMatrix4 result = new DMatrix4(0,0,0,1);
        result.a1 = (v2.getCoords().a1 - v1.getCoords().a1) * factor;
        result.a2 = (v2.getCoords().a2 - v1.getCoords().a2) * factor;
        result.a3 = (v2.getCoords().a3 - v1.getCoords().a3) * factor;

        return result;
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
