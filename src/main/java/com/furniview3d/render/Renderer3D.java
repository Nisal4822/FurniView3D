package main.java.com.furniview3d.render;

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
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
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
    private boolean sceneNeedsUpdate = false;

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

    // Track furniture visibility
    private Map<String, Boolean> furnitureVisibility = new HashMap<>();

    public Renderer3D() {
        setLayout(new BorderLayout());
        jfxPanel = new JFXPanel();
        add(jfxPanel, BorderLayout.CENTER);

        // Initialize JavaFX components on the JavaFX thread
        Platform.runLater(() -> {
            try {
                initFX();
                isInitialized = true;

                // If design was set before initialization
                if (design != null && sceneNeedsUpdate) {
                    updateScene();
                    sceneNeedsUpdate = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error initializing JavaFX: " + e.getMessage());
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();

        // When added to hierarchy, request focus to receive keyboard events
        SwingUtilities.invokeLater(() -> requestFocusInWindow());

        // Force a scene update if we have design data
        if (design != null && isInitialized) {
            SwingUtilities.invokeLater(() -> {
                Platform.runLater(this::updateScene);
            });
        }
    }

    public void setDesign(Design design) {
        this.design = design;
        if (isInitialized) {
            Platform.runLater(this::updateScene);
        } else {
            sceneNeedsUpdate = true;
        }
    }

    public void setFurnitureVisibility(String furnitureId, boolean visible) {
        furnitureVisibility.put(furnitureId, visible);
        if (isInitialized && furnitureMap.containsKey(furnitureId)) {
            Platform.runLater(() -> {
                Group furniture = furnitureMap.get(furnitureId);
                furniture.setVisible(visible);
            });
        }
    }

    private void initFX() {
        try {
            // Create main container
            root = new Group();

            // Setup camera with reasonable values
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

            // Create empty scene with appropriate size
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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error in initFX: " + e.getMessage());
        }
    }

    private void updateScene() {
        if (!isInitialized) {
            sceneNeedsUpdate = true;
            return;
        }

        try {
            if (design == null) {
                renderEmptyScene();
                return;
            }

            // Clear previous content
            roomGroup.getChildren().clear();
            furnitureGroup.getChildren().clear();
            furnitureMap.clear();

            // Render room
            if (design.getRoom() != null) {
                renderRoom(design.getRoom());
            } else {
                System.err.println("Room is null in the design");
            }

            // Render furniture
            if (design.getFurnitureList() != null && !design.getFurnitureList().isEmpty()) {
                for (Furniture furniture : design.getFurnitureList()) {
                    if (furniture != null) {
                        renderFurniture(furniture);

                        // Apply visibility
                        Boolean visible = furnitureVisibility.get(furniture.getId());
                        if (visible != null && !visible && furnitureMap.containsKey(furniture.getId())) {
                            furnitureMap.get(furniture.getId()).setVisible(false);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error updating scene: " + e.getMessage());
            renderErrorScene(e.getMessage());
        }
    }

    private void renderErrorScene(String errorMessage) {
        try {
            roomGroup.getChildren().clear();
            furnitureGroup.getChildren().clear();

            Label label = new Label("Error rendering scene: " + errorMessage);
            label.setTextFill(Color.RED);
            label.setTranslateX(100);
            label.setTranslateY(100);

            roomGroup.getChildren().add(label);
        } catch (Exception e) {
            System.err.println("Error even in renderErrorScene: " + e.getMessage());
        }
    }

    private void renderEmptyScene() {
        try {
            roomGroup.getChildren().clear();
            furnitureGroup.getChildren().clear();

            // Add a placeholder message
            Label label = new Label("No design loaded. Create or open a design first.");
            label.setTextFill(Color.WHITE);
            label.setTranslateX(200);
            label.setTranslateY(300);

            roomGroup.getChildren().add(label);
        } catch (Exception e) {
            System.err.println("Error in renderEmptyScene: " + e.getMessage());
        }
    }

    private void renderRoom(Room room) {
        try {
            if (room == null) {
                System.err.println("Room is null, cannot render");
                return;
            }

            // Convert room dimensions from meters to JavaFX units (100 units = 1 meter)
            double width = room.getWidth() * 100;
            double length = room.getLength() * 100;
            double height = room.getHeight() * 100;

            // Check for invalid dimensions
            if (width <= 0 || length <= 0 || height <= 0) {
                System.err.println("Invalid room dimensions: " + width + "x" + length + "x" + height);
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

            // Add to room group
            roomGroup.getChildren().addAll(floor, wallLeft, wallRight, wallBack, wallFront, ceiling);

            // Center the room
            roomGroup.setTranslateX(0);
            roomGroup.setTranslateY(0);
            roomGroup.setTranslateZ(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error rendering room: " + e.getMessage());
        }
    }

    private void renderFurniture(Furniture furniture) {
        try {
            if (furniture == null) {
                System.err.println("Furniture is null, cannot render");
                return;
            }

            // Convert furniture dimensions from meters to JavaFX units
            double width = Math.max(0.1, furniture.getWidth()) * 100;
            double height = Math.max(0.1, furniture.getHeight()) * 100;
            double length = Math.max(0.1, furniture.getLength()) * 100;

            // Create furniture group
            Group furnitureObj = new Group();

            // Create basic shape based on furniture type
            Box shape = new Box(width, height, length);

            // Create material with furniture color
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(jfxColor(furniture.getColor()));
            material.setSpecularColor(Color.WHITE);
            material.setSpecularPower(32.0);
            shape.setMaterial(material);

            // Add to furniture group
            furnitureObj.getChildren().add(shape);

            // Position furniture in room
            Room room = design.getRoom();
            if (room == null) {
                System.err.println("Room is null when positioning furniture");
                return;
            }

            double roomWidth = room.getWidth() * 100;
            double roomLength = room.getLength() * 100;
            double roomHeight = room.getHeight() * 100;

            // Calculate position
            double posX = furniture.getPosX() * 100;
            double posZ = furniture.getPosY() * 100;

            // Center coordinates within the room
            posX = posX - (roomWidth / 2) + (width / 2);
            posZ = posZ - (roomLength / 2) + (length / 2);

            // Y position (up/down)
            double posY = (roomHeight / 2) - (height / 2);

            furnitureObj.setTranslateX(posX);
            furnitureObj.setTranslateY(posY);
            furnitureObj.setTranslateZ(posZ);

            // Apply rotation around Y axis (vertical)
            Rotate rotation = new Rotate(furniture.getRotation(), Rotate.Y_AXIS);
            furnitureObj.getTransforms().add(rotation);

            // Save reference and add to scene
            furnitureMap.put(furniture.getId(), furnitureObj);
            furnitureGroup.getChildren().add(furnitureObj);

            // Apply visibility setting if exists
            Boolean visible = furnitureVisibility.get(furniture.getId());
            if (visible != null) {
                furnitureObj.setVisible(visible);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error rendering furniture: " + e.getMessage());
        }
    }

    // Helper method to convert AWT Color to JavaFX Color
    private Color jfxColor(java.awt.Color awtColor) {
        if (awtColor == null) {
            return Color.GRAY; // Default color if null
        }

        return Color.rgb(
                awtColor.getRed(),
                awtColor.getGreen(),
                awtColor.getBlue(),
                awtColor.getAlpha() / 255.0
        );
    }

    // Method to apply shading to a specific furniture
    public void applyShading(String furnitureId, double intensity) {
        if (!isInitialized || !furnitureMap.containsKey(furnitureId)) return;

        Platform.runLater(() -> {
            try {
                Group furniture = furnitureMap.get(furnitureId);
                if (furniture != null && furniture.getChildren().size() > 0 &&
                        furniture.getChildren().get(0) instanceof Box) {
                    Box shape = (Box) furniture.getChildren().get(0);
                    PhongMaterial material = (PhongMaterial) shape.getMaterial();

                    // Adjust specular power based on intensity
                    material.setSpecularPower(intensity * 100);
                }
            } catch (Exception e) {
                System.err.println("Error applying shading: " + e.getMessage());
            }
        });
    }

    // Method to rotate the view
    public void rotateView(double xAngle, double yAngle) {
        if (!isInitialized) return;

        Platform.runLater(() -> {
            try {
                rotateX.setAngle(xAngle);
                rotateY.setAngle(yAngle);
            } catch (Exception e) {
                System.err.println("Error rotating view: " + e.getMessage());
            }
        });
    }

    // Method to reset the view
    public void resetView() {
        if (!isInitialized) return;

        Platform.runLater(() -> {
            try {
                rotateX.setAngle(-20);
                rotateY.setAngle(-20);
            } catch (Exception e) {
                System.err.println("Error resetting view: " + e.getMessage());
            }
        });
    }
}