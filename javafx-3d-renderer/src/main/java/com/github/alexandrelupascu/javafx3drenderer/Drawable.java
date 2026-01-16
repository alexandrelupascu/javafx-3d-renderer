package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public interface Drawable {
    void draw(GraphicsContext ctx, Color drawColor);
}
