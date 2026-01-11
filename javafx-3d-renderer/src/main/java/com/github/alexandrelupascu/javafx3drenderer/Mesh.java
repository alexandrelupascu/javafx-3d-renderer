package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Arrays;

public class Mesh implements Drawable {
    ArrayList<Vertex> vertices = new ArrayList<>();
    ArrayList<Polygon> polygons = new ArrayList<>();

    public Mesh() {
        generateMesh();
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
