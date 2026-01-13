package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.paint.Color;
import org.ejml.data.*;

public class Utilities {
    final static int SCREEN_WIDTH = 800, SCREEN_HEIGHT = 600;
    final static int CANVAS_WIDTH = 800, CANVAS_HEIGH = 600;

    final static double VERTEX_SIZE = 10;
    final static Color VERTEX_COLOR = Color.BLUE;

    final static double EDGE_SIZE = 3;
    final static Color EDGE_COLOR = Color.BLUE;

    public enum Axis {
        X,
        Y,
        Z,
    }

    public static class Matrix {
        public static DMatrix3x3 getR(Axis axis, double angle) {

            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            switch (axis) {
                case X -> {
                    return new DMatrix3x3(1,0,0,0,cos,-sin,0,sin,cos);
                }
                case Y -> {
                    return new DMatrix3x3(cos,0,sin,0,1,0,-sin,0,cos);
                }
                case Z -> {
                    return new DMatrix3x3(cos,-sin,0,sin,cos,0,0,0,1);
                }
                default -> {
                    return null;
                }
            }
        }
    }
}
