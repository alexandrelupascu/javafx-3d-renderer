package com.github.alexandrelupascu.javafx3drenderer;

import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;
import javafx.scene.canvas.GraphicsContext;

public class Edge implements Drawable{
    private Vertex[] tips;
    private Vertex v0;
    private Vertex v1;

    public Edge(Vertex v0, Vertex v1) {
        tips = new Vertex[]{v0, v1};
        this.v0 = v0;
        this.v1 = v1;
    }

    public void draw(GraphicsContext ctx) {
        ctx.setStroke(EDGE_COLOR);
        ctx.setLineWidth(EDGE_SIZE);
        ctx.strokeLine(v0.screen(Axis.X), v0.screen(Axis.Y), v1.screen(Axis.X), v1.screen(Axis.Y));
    }
}
