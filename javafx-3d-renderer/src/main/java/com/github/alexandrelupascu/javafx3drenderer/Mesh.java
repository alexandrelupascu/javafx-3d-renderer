package com.github.alexandrelupascu.javafx3drenderer;

import com.lukaseichberg.fbxloader.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import org.ejml.data.DMatrix4;
import org.ejml.data.DMatrix4x4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static com.github.alexandrelupascu.javafx3drenderer.Utilities.*;
import static org.ejml.dense.fixed.CommonOps_DDF4.*;

public class Mesh implements Drawable {
    private final ArrayList<Vertex> vertices = new ArrayList<>();
    private final ArrayList<Polygon> polygons = new ArrayList<>();
    private Vertex origin;
    private String fileName;

    private Color vertexColor;
    private Color edgesColor;

    public Mesh(String fileName) {
        if (generateMesh(fileName)) this.fileName = fileName;
        generateOrigin();
        this.vertexColor = DEFAULT_COLOR;
        this.edgesColor = DEFAULT_COLOR;

    }

    public Mesh(String fileName, Color drawColor) {
        if (generateMesh(fileName)) this.fileName = fileName;
        generateOrigin();
        this.vertexColor = drawColor;
        this.edgesColor = drawColor;
        this.fileName = fileName;
    }

    public Mesh(String fileName, Color vertexColor, Color edgesColor) {
        if (generateMesh(fileName)) this.fileName = fileName;
        generateOrigin();
        this.vertexColor = vertexColor;
        this.edgesColor = edgesColor;
        this.fileName = fileName;
    }

    @Override
    public void draw(GraphicsContext ctx, Color originColor) {
        for (Polygon p : polygons) {
            p.draw(ctx, edgesColor);
        }
        for (Vertex v : vertices) {
            v.draw(ctx, vertexColor);
        }

        origin.draw(ctx, originColor);
        origin.drawCoords(ctx);
    }

    public boolean generateMesh(String fileName) {
        Path filePath = resolveFilePath(fileName);

        if (!validateFilePath(filePath, fileName)) {
            return false;
        }

        if (!isValidFbxFile(filePath, fileName)) {
            return false;
        }

        FBXFile fbxFile = loadFbxFile(filePath);
        if (fbxFile == null) {
            return false;
        }

        double[] vertexData = extractVertexData(fbxFile);
        int[] polygonData = extractPolygonData(fbxFile);

        generateMesh(vertexData, polygonData);

        return true;
    }

    public void generateMesh(double[] vertexData, int[] polygonData) {
        createVerticesFromData(vertexData);
        createPolygonsFromData(polygonData);
    }

    // finds an average point to all vertices
    public void generateOrigin() {
        DMatrix4 sum = new DMatrix4(0, 0, 0, 0);
        DMatrix4 temp = new DMatrix4(0, 0, 0, 0);

        for (Vertex vertex : vertices) {
            add(vertex.getCoords(), temp, sum);
            temp = sum;
        }

        divide(sum, vertices.size());
        this.origin = new Vertex(sum);
    }

    public void moveBy(double x, double y, double z) {
        origin.moveBy(x, y, z);
        for (Vertex v : vertices) {
            v.moveBy(x, y, z);
        }
    }

    // to be called only in Application.initialize()
    public void moveAt(double x, double y, double z) {
        origin.moveAt(x,y,z);
        for (Vertex v : vertices) {
            v.moveAt(x, y, z);
        }
    }

    // rotate around a given point
    public void rotateBy(double pitch, double yaw, double roll, Vertex rotationPoint) {
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);
        internalRotate(yaw, pitch, roll, rotationPoint.getCoords());
    }

    // rotate around the defined origin
    public void rotateBy(double pitch, double yaw, double roll) {
        yaw = Math.toRadians(yaw);
        pitch = Math.toRadians(pitch);
        roll = Math.toRadians(roll);
        internalRotate(yaw, pitch, roll, origin.getCoords());
    }

    public void scaleBy(double scaleFactor) {
        scaleFactor = scaleFactor - 1;

        for (Vertex v : vertices) {
            DMatrix4 scaleDirection = findScaleDirection(origin, v, scaleFactor);
            v.moveBy(scaleDirection.a1, scaleDirection.a2, scaleDirection.a3);
        }
    }

    private void internalRotate(double pitch, double yaw, double roll, DMatrix4 rotationPoint) {
        DMatrix4x4 rotationMatrix = createRotationMatrix(roll, pitch, yaw);
        for (Vertex v : vertices) {
            v.rotate(rotationMatrix, rotationPoint);
        }
        origin.rotate(rotationMatrix, rotationPoint);
    }

    private DMatrix4x4 createRotationMatrix(double yaw, double pitch, double roll) {
        // create Rotation Matrix
        // R = Rz x Ry x Rx
        DMatrix4x4 Rz = Matrix.getRotation(Axis.Z, yaw);
        DMatrix4x4 Ry = Matrix.getRotation(Axis.Y, pitch);
        DMatrix4x4 Rx = Matrix.getRotation(Axis.X, roll);

        DMatrix4x4 result = new DMatrix4x4();
        DMatrix4x4 temp = new DMatrix4x4();

        mult(Rz, Ry, temp);
        mult(temp, Rx, result);

        return result;
    }

    // returns a vector from v1 to v2
    private DMatrix4 findScaleDirection(Vertex v1, Vertex v2, double factor) {
        DMatrix4 result = new DMatrix4(0, 0, 0, 1);
        result.a1 = (v2.getCoords().a1 - v1.getCoords().a1) * factor;
        result.a2 = (v2.getCoords().a2 - v1.getCoords().a2) * factor;
        result.a3 = (v2.getCoords().a3 - v1.getCoords().a3) * factor;
        return result;
    }

    private Path resolveFilePath(String fileName) {
        // build path relative to project root
        Path currentDir = Paths.get("").toAbsolutePath();
        return currentDir.resolve("src/main/resources").resolve(fileName);
    }

    private boolean validateFilePath(Path path, String fileName) {
        if (!Files.exists(path)) {
            System.out.println("The file " + path + " couldn't be found.");
            return false;
        }

        if (!Files.isRegularFile(path)) {
            System.out.println(fileName + " exists but is not a regular file (might be a directory).");
            return false;
        }

        return true;
    }

    private boolean isValidFbxFile(Path path, String fileName) {
        if (!getFileExtension(path).equals("fbx")) {
            System.out.println(fileName + " isn't a fbx file or doesn't have a .fbx suffix.");
            return false;
        }

        return true;
    }

    private FBXFile loadFbxFile(Path path) {
        try {
            return FBXLoader.loadFBXFile(path.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private double[] extractVertexData(FBXFile file) {
        FBXNode root = file.getRootNode();
        FBXNode verticesNode = root.getNodeFromPath("Objects/Geometry/Vertices");
        FBXProperty property = verticesNode.getProperty(0);

        if (property.getDataType() != FBXDataType.DOUBLE_ARRAY) {
            System.err.println("Mesh.generateMesh(fileName): Unexpected data type!");
            System.exit(-1);
        }

        return (double[]) property.getData();
    }

    private int[] extractPolygonData(FBXFile file) {
        FBXNode root = file.getRootNode();
        FBXNode polygonsNode = root.getNodeFromPath("Objects/Geometry/PolygonVertexIndex");
        FBXProperty property = polygonsNode.getProperty(0);

        if (property.getDataType() != FBXDataType.INT_ARRAY) {
            System.err.println("Mesh.generateMesh(fileName): Unexpected data type!");
            System.exit(-1);
        }

        return (int[]) property.getData();
    }

    private void createVerticesFromData(double[] vertexData) {
        for (int i = 0; i < vertexData.length; i += 3) {
            vertices.add(new Vertex(vertexData[i], vertexData[i + 1], vertexData[i + 2]));
        }
    }

    private void createPolygonsFromData(int[] polygonData) {
        ArrayList<Integer> verticesIndexes = new ArrayList<>();

        for (int vertexIndex : polygonData) {
            if (vertexIndex < 0) {
                verticesIndexes.add(Math.abs(vertexIndex) - 1);
                ArrayList<Vertex> verticesToAdd = new ArrayList<>();
                for (int i : verticesIndexes) {
                    verticesToAdd.add(this.vertices.get(i));
                }
                this.polygons.add(new Polygon(verticesToAdd));
                verticesIndexes.clear();
            } else {
                verticesIndexes.add(vertexIndex);
            }
        }
    }

    public Vertex getOrigin() {
        return origin;
    }
}

