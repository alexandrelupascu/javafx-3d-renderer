package com.github.alexandrelupascu.javafx3drenderer;

import javafx.scene.paint.Color;
import org.ejml.data.DMatrix3;
import org.ejml.data.DMatrix4;
import org.ejml.data.DMatrix4x4;

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
        public static DMatrix4x4 getRotation(Axis axis, double angle) {

            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            switch (axis) {
                case X -> {
                    return new DMatrix4x4(
                            1, 0, 0, 0,
                            0, cos, -sin, 0,
                            0, sin, cos, 0,
                            0, 0, 0, 1
                    );
                }
                case Y -> {
                    return new DMatrix4x4(
                            cos, 0, sin, 0,
                            0, 1, 0, 0,
                            -sin, 0, cos, 0,
                            0, 0, 0, 1
                    );
                }
                case Z -> {
                    return new DMatrix4x4(
                            cos, -sin, 0, 0,
                            sin, cos, 0, 0,
                            0, 0, 1, 0,
                            0, 0, 0, 1
                    );
                }
                default -> {
                    return null;
                }
            }
        }

        public static DMatrix4x4 getTranslateOrigin(DMatrix4 origin) {
            return new DMatrix4x4(
                    1, 0, 0, origin.a1,
                    0, 1, 0, origin.a2,
                    0, 0, 1, origin.a3,
                    0, 0, 0, 1
            );
        }

        public static DMatrix4 getInverse(DMatrix4 vec) {
            return new DMatrix4(-vec.a1,-vec.a2,-vec.a3,1);
        }
    }
}
