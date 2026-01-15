package com.github.alexandrelupascu.javafx3drenderer;

import com.lukaseichberg.fbxloader.*;
import javafx.scene.canvas.GraphicsContext;
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

    public Mesh(String fileName) {
        generateMesh(fileName);
        generateOrigin();
    }

    @Override
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

    public void generateMesh(String fileName) {
        Path filePath = resolveFilePath(fileName);

        if (!validateFilePath(filePath, fileName)) {
            return;
        }

        if (!isValidFbxFile(filePath, fileName)) {
            return;
        }

        FBXFile fbxFile = loadFbxFile(filePath);
        if (fbxFile == null) {
            return;
        }

        double[] vertexData = extractVertexData(fbxFile);
        int[] polygonData = extractPolygonData(fbxFile);

        generateMesh(vertexData, polygonData);
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
        vertices.forEach(vertex -> vertex.moveBy(x, y, z));
    }

    // rotate around a given point
    public void rotateBy(double yaw, double pitch, double roll, Vertex rotationPoint) {
        internalRotate(yaw, pitch, roll, rotationPoint.getCoords());
    }

    // rotate around the defined origin
    public void rotateBy(double yaw, double pitch, double roll) {
        internalRotate(yaw, pitch, roll, origin.getCoords());
    }

    public void resizeBy(double scaleFactor) {
        for (Vertex vertex : vertices) {
            DMatrix4 scaleDirection = findScaleDirection(vertex, origin, scaleFactor);
            vertex.moveBy(scaleDirection.a1, scaleDirection.a2, scaleDirection.a3);
        }
    }

    private void internalRotate(double yaw, double pitch, double roll, DMatrix4 rotationPoint) {
        DMatrix4x4 rotationMatrix = createRotationMatrix(yaw, pitch, roll);
        vertices.forEach(vertex -> vertex.rotate(rotationMatrix, rotationPoint));
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

        for (int polygonIndex : polygonData) {
            if (polygonIndex < 0) {
                verticesIndexes.add(Math.abs(polygonIndex) - 1);
                ArrayList<Vertex> verticesToAdd = new ArrayList<>();
                for (int i : verticesIndexes) {
                    verticesToAdd.add(this.vertices.get(i));
                }
                this.polygons.add(new Polygon(verticesToAdd));
                verticesIndexes.clear();
            } else {
                verticesIndexes.add(polygonIndex);
            }
        }
    }
}

