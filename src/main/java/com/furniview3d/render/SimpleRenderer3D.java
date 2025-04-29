package main.java.com.furniview3d.render;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.model.Room;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * A simplified 3D renderer that focuses on just displaying the room and furniture
 */
public class SimpleRenderer3D extends JPanel {
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

    public SimpleRenderer3D() {
        setLayout(new BorderLayout());

        // Create JavaFX panel
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        // Initialize JavaFX on its own thread
        Platform.runLater(() -> {
            try {
                initFX();
                isInitialized = true;

                // If design was already set, render it now
                if (design != null) {
                    updateScene();
                }
            } catch (Exception e) {
                System.err.println("Error initializing JavaFX: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Sets the design to display
     */
    public void setDesign(Design design) {
        this.design = design;

        if (isInitialized) {
            Platform.runLater(this::updateScene);
        }
    }

    /**
     * Reset the camera view to default
     */
    public void resetView() {
        if (isInitialized) {
            Platform.runLater(() -> {
                rotateX.setAngle(-20);
                rotateY.setAngle(-20);
            });
        }
    }

    /**
     * Initialize the JavaFX components
     */
    private void initFX() {
        // Create root group
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
        ambientLight.setOpacity(0.6);

        PointLight pointLight1 = new PointLight(Color.WHITE);
        pointLight1.setTranslateX(300);
        pointLight1.setTranslateY(-300);
        pointLight1.setTranslateZ(-500);

        PointLight pointLight2 = new PointLight(Color.WHITE);
        pointLight2.setTranslateX(-300);
        pointLight2.setTranslateY(-200);
        pointLight2.setTranslateZ(500);

        root.getChildren().addAll(ambientLight, pointLight1, pointLight2);

        // Create scene
        scene = new Scene(root, 800, 600, true);
        scene.setFill(Color.LIGHTGRAY);
        scene.setCamera(camera);

        // Add mouse drag to rotate view
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

        // Set the scene in the JFXPanel
        jfxPanel.setScene(scene);
    }

    /**
     * Update the scene with current design
     */
    private void updateScene() {
        if (!isInitialized) return;

        // Clear previous content
        roomGroup.getChildren().clear();
        furnitureGroup.getChildren().clear();

        if (design == null) {
            // Nothing to render
            return;
        }

        // Render room
        Room room = design.getRoom();
        if (room != null) {
            renderRoom(room);
        }

        // Render furniture
        if (design.getFurnitureList() != null) {
            for (Furniture furniture : design.getFurnitureList()) {
                if (furniture != null) {
                    renderFurniture(furniture, room);
                }
            }
        }
    }

    /**
     * Render the room
     */
    private void renderRoom(Room room) {
        try {
            // Convert dimensions to JavaFX units (1 meter = 100 units)
            double width = room.getWidth() * 100;
            double length = room.getLength() * 100;
            double height = room.getHeight() * 100;

            // Verify dimensions
            if (width <= 0 || length <= 0 || height <= 0) {
                System.err.println("Invalid room dimensions");
                return;
            }

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

            // Add all parts to room group
            roomGroup.getChildren().addAll(floor, wallLeft, wallRight, wallBack, wallFront, ceiling);
        } catch (Exception e) {
            System.err.println("Error rendering room: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Render a piece of furniture
     */
    private void renderFurniture(Furniture furniture, Room room) {
        try {
            // Skip if null
            if (furniture == null || room == null) return;

            // Convert to JavaFX units (1 meter = 100 units)
            double width = furniture.getWidth() * 100;
            double length = furniture.getLength() * 100;
            double height = furniture.getHeight() * 100;

            // Create furniture shape (simple box for now)
            Box shape = new Box(width, height, length);

            // Set material with furniture color
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(jfxColor(furniture.getColor()));
            material.setSpecularColor(Color.WHITE);
            material.setSpecularPower(32.0);
            shape.setMaterial(material);

            // Calculate position in the room
            double roomWidth = room.getWidth() * 100;
            double roomLength = room.getLength() * 100;
            double roomHeight = room.getHeight() * 100;

            // Calculate position - center coordinates
            double posX = furniture.getPosX() * 100 - (roomWidth / 2) + (width / 2);
            double posZ = furniture.getPosY() * 100 - (roomLength / 2) + (length / 2);
            double posY = (roomHeight / 2) - (height / 2);

            // Create group for this furniture piece
            Group furnitureGroup = new Group(shape);
            furnitureGroup.setTranslateX(posX);
            furnitureGroup.setTranslateY(posY);
            furnitureGroup.setTranslateZ(posZ);

            // Apply rotation
            Rotate rotation = new Rotate(furniture.getRotation(), Rotate.Y_AXIS);
            furnitureGroup.getTransforms().add(rotation);

            // Add to scene
            this.furnitureGroup.getChildren().add(furnitureGroup);
        } catch (Exception e) {
            System.err.println("Error rendering furniture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Convert AWT Color to JavaFX Color
     */
    private Color jfxColor(java.awt.Color awtColor) {
        if (awtColor == null) {
            return Color.GRAY; // Default if null
        }

        return Color.rgb(
                awtColor.getRed(),
                awtColor.getGreen(),
                awtColor.getBlue(),
                awtColor.getAlpha() / 255.0
        );
    }
}