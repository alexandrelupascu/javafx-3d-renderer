package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static com.github.alexandrelupascu.javafx3drenderer.Utilities.Axis;
import static com.github.alexandrelupascu.javafx3drenderer.Utilities.EDGE_COLOR;

public class Polygon implements Drawable {
    private final ArrayList<Vertex> vertices;

    public Polygon(ArrayList<Vertex> vertices) {
        this.vertices = vertices;
    }


    public void draw(GraphicsContext ctx, Color drawColor) {
        ctx.setStroke(drawColor);
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
