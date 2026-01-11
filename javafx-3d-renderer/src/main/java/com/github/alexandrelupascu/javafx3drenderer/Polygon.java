package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;
import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;
import java.util.ArrayList;

public class Polygon implements Drawable {
    private ArrayList<Vertex> vertices;

    public Polygon() {
        vertices = new ArrayList<>();
    }

    public Polygon(Vertex v0, Vertex v1, Vertex v2) {
        vertices = new ArrayList<>();
        vertices.add(v0);
        vertices.add(v1);
        vertices.add(v2);
    }

    public Polygon(Vertex v0, Vertex v1, Vertex v2, Vertex v3) {
        vertices = new ArrayList<>();
        vertices.add(v0);
        vertices.add(v1);
        vertices.add(v2);
        vertices.add(v3);
    }

    public void draw(GraphicsContext ctx) {
        ctx.setStroke(EDGE_COLOR);
        ctx.beginPath();

        // start at first vertex
        ctx.moveTo(vertices.get(0).screen(Axis.X), vertices.get(0).screen(Axis.Y));

        // draw lines to all other vertices
        for (int i = 1; i < vertices.size(); i++) {
            ctx.lineTo(vertices.get(i).screen(Axis.X), vertices.get(i).screen(Axis.Y));
        }

        ctx.closePath();  // connects last to first
        ctx.stroke();     // draw it
    }


    public void add(Vertex v) {
        vertices.add(v);
    }
}
