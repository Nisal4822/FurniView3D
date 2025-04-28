package main.java.com.furniview3d.render;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.model.Room;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.HashMap;
import java.util.Map;

public class Renderer3D extends JPanel {
    private static final long serialVersionUID = 1L;

    private JFXPanel jfxPanel;
    private Design design;
    private boolean isInitialized = false;

    // JavaFX components
    private Group root;
    private PerspectiveCamera camera;
    private Group roomGroup;
    private Group furnitureGroup;
    private Scene scene;

    // Mouse control variables
    private double mouseOldX, mouseOldY;
    private double mousePosX, mousePosY;
    private final Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
    private final Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);

    // Mapping of furniture IDs to their 3D representations
    private Map<String, Group> furnitureMap = new HashMap<>();

    public Renderer3D() {
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        // Initialize JavaFX components
        Platform.runLater(() -> {
            initFX();
            isInitialized = true;

            // If design was set before initialization
            if (design != null) {
                updateScene();
            }
        });
    }

    public void setDesign(Design design) {
        this.design = design;

        if (isInitialized) {
            Platform.runLater(this::updateScene);
        }
    }

    private void initFX() {
        // Create main container
        root = new Group();

        // Setup camera
        camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(10000.0);
        camera.getTransforms().addAll(
                new Translate(0, 0, -1000),
                rotateX,
                rotateY
        );

        // Create groups for room and furniture
        roomGroup = new Group();
        furnitureGroup = new Group();
        root.getChildren().addAll(roomGroup, furnitureGroup);

        // Setup lighting
        AmbientLight ambientLight = new AmbientLight(Color.WHITE);
        //ambientLight.setIntensity(0.5);

        PointLight pointLight = new PointLight(Color.WHITE);
        pointLight.setTranslateX(300);
        pointLight.setTranslateY(-300);
        pointLight.setTranslateZ(-500);

        root.getChildren().addAll(ambientLight, pointLight);

        // Create empty scene
        scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);
        scene.setCamera(camera);

        // Add mouse control for rotation
        scene.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        scene.setOnMouseDragged(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                mousePosX = event.getSceneX();
                mousePosY = event.getSceneY();

                rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX) * 0.2);
                rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY) * 0.2);

                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
            }
        });

        // Set scene in the JFXPanel
        jfxPanel.setScene(scene);
    }

    private void updateScene() {
        if (design == null) {
            renderEmptyScene();
            return;
        }

        // Clear previous content
        roomGroup.getChildren().clear();
        furnitureGroup.getChildren().clear();
        furnitureMap.clear();

        // Render room
        renderRoom(design.getRoom());

        // Render furniture
        for (Furniture furniture : design.getFurnitureList()) {
            renderFurniture(furniture);
        }
    }

    private void renderEmptyScene() {
        roomGroup.getChildren().clear();
        furnitureGroup.getChildren().clear();

        // Add a placeholder message
        Label label = new Label("No design loaded");
        label.setTextFill(Color.WHITE);
        label.setTranslateX(350);
        label.setTranslateY(300);

        roomGroup.getChildren().add(label);
    }

    private void renderRoom(Room room) {
        // Convert room dimensions from meters to JavaFX units (100 units = 1 meter)
        double width = room.getWidth() * 100;
        double length = room.getLength() * 100;
        double height = room.getHeight() * 100;

        // Create floor
        Box floor = new Box(width, 5, length);
        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(jfxColor(room.getColorScheme().getFloorColor()));
        floor.setMaterial(floorMaterial);
        floor.setTranslateY(height / 2);

        // Create walls
        Box wallLeft = new Box(5, height, length);
        Box wallRight = new Box(5, height, length);
        Box wallBack = new Box(width, height, 5);
        Box wallFront = new Box(width, height, 5);

        PhongMaterial wallMaterial = new PhongMaterial();
        wallMaterial.setDiffuseColor(jfxColor(room.getColorScheme().getWallColor()));

        wallLeft.setMaterial(wallMaterial);
        wallRight.setMaterial(wallMaterial);
        wallBack.setMaterial(wallMaterial);
        wallFront.setMaterial(wallMaterial);

        wallLeft.setTranslateX(-width / 2);
        wallRight.setTranslateX(width / 2);
        wallBack.setTranslateZ(-length / 2);
        wallFront.setTranslateZ(length / 2);

        // Create ceiling
        Box ceiling = new Box(width, 5, length);
        PhongMaterial ceilingMaterial = new PhongMaterial();
        ceilingMaterial.setDiffuseColor(jfxColor(room.getColorScheme().getCeilingColor()));
        ceiling.setMaterial(ceilingMaterial);
        ceiling.setTranslateY(-height / 2);

        // Add to room group
        roomGroup.getChildren().addAll(floor, wallLeft, wallRight, wallBack, wallFront, ceiling);

        // Center the room
        roomGroup.setTranslateX(0);
        roomGroup.setTranslateY(0);
        roomGroup.setTranslateZ(0);
    }

    private void renderFurniture(Furniture furniture) {
        // Convert furniture dimensions from meters to JavaFX units
        double width = furniture.getWidth() * 100;
        double height = furniture.getHeight() * 100;
        double length = furniture.getLength() * 100;

        // Create furniture group
        Group furnitureObj = new Group();

        // Create basic shape based on furniture type
        Box shape = new Box(width, height, length);

        // Create material with furniture color
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(jfxColor(furniture.getColor()));
        material.setSpecularColor(Color.WHITE);
        shape.setMaterial(material);

        // Add to furniture group
        furnitureObj.getChildren().add(shape);

        // Position furniture in room
        Room room = design.getRoom();
        double roomWidth = room.getWidth() * 100;
        double roomLength = room.getLength() * 100;

        // Calculate position (centered in room)
        double posX = (furniture.getPosX() * 100) - (roomWidth / 2);
        double posZ = (furniture.getPosY() * 100) - (roomLength / 2);
        double posY = (room.getHeight() * 100 / 2) - (height / 2);

        furnitureObj.setTranslateX(posX);
        furnitureObj.setTranslateY(posY);
        furnitureObj.setTranslateZ(posZ);

        // Apply rotation
        Rotate rotation = new Rotate(furniture.getRotation(), Rotate.Y_AXIS);
        furnitureObj.getTransforms().add(rotation);

        // Save reference and add to scene
        furnitureMap.put(furniture.getId(), furnitureObj);
        furnitureGroup.getChildren().add(furnitureObj);
    }

    // Helper method to convert AWT Color to JavaFX Color
    private Color jfxColor(java.awt.Color awtColor) {
        return Color.rgb(
                awtColor.getRed(),
                awtColor.getGreen(),
                awtColor.getBlue(),
                awtColor.getAlpha() / 255.0
        );
    }

    // Method to apply shading to a specific furniture
    public void applyShading(String furnitureId, double intensity) {
        if (!furnitureMap.containsKey(furnitureId)) return;

        Platform.runLater(() -> {
            Group furniture = furnitureMap.get(furnitureId);
            if (furniture.getChildren().get(0) instanceof Box) {
                Box shape = (Box) furniture.getChildren().get(0);
                PhongMaterial material = (PhongMaterial) shape.getMaterial();

                // Adjust specular power based on intensity
                material.setSpecularPower(intensity * 100);
            }
        });
    }

    // Method to rotate the view
    public void rotateView(double xAngle, double yAngle) {
        Platform.runLater(() -> {
            rotateX.setAngle(xAngle);
            rotateY.setAngle(yAngle);
        });
    }

    // Method to reset the view
    public void resetView() {
        Platform.runLater(() -> {
            rotateX.setAngle(-20);
            rotateY.setAngle(-20);
        });
    }
}