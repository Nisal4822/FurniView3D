package main.java.com.furniview3d.ui.design3d;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

/**
 * Improved 3D panel with mouse control and better interior view
 */
public class Design3DPanel extends JPanel implements FurniView3DApp.DesignChangeListener {
    private static final long serialVersionUID = 1L;

    // App reference
    private FurniView3DApp app;

    // JavaFX components
    private JFXPanel jfxPanel;
    private boolean isInitialized = false;

    // 3D scene components
    private Scene scene;
    private Group root;
    private Group roomGroup;
    private Group furnitureGroup;
    private Group contentGroup;
    private PerspectiveCamera camera;

    // Transformations
    private Rotate rotateX;
    private Rotate rotateY;
    private Scale scaleTransform;
    private double zoomFactor = 1.0;

    // Mouse handling
    private double mouseOldX, mouseOldY;
    private double mousePosX, mousePosY;

    // UI components
    private JLabel nameLabel;
    private JLabel dimensionsLabel;
    private JLabel shapeLabel;
    private JLabel furnitureCountLabel;

    // Special options
    private JCheckBox showAllWallsCheckbox;
    private JCheckBox showCeilingCheckbox;
    private JCheckBox showFrontWallCheckbox;
    private JCheckBox showBackWallCheckbox;
    private JCheckBox showLeftWallCheckbox;
    private JCheckBox showRightWallCheckbox;

    // Furniture mapping for quick updates
    private Map<String, Group> furnitureNodes = new HashMap<>();

    /**
     * Constructor
     */
    public Design3DPanel(FurniView3DApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Register as design change listener
        app.addDesignChangeListener(this);

        // Create navigation buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton view2DButton = SwingUtils.createPrimaryButton("Switch to 2D View", e -> app.showPanel("design2D"));
        JButton backButton = SwingUtils.createSecondaryButton("Back to Room Setup", e -> app.showPanel("roomSetup"));
        buttonPanel.add(view2DButton);
        buttonPanel.add(backButton);

        // Create 3D rendering panel
        jfxPanel = new JFXPanel();

        // Create information panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Current Design"));
        infoPanel.setBackground(java.awt.Color.WHITE);

        nameLabel = new JLabel("Name: ");
        dimensionsLabel = new JLabel("Dimensions: ");
        shapeLabel = new JLabel("Shape: ");
        furnitureCountLabel = new JLabel("Furniture: ");

        infoPanel.add(nameLabel);
        infoPanel.add(dimensionsLabel);
        infoPanel.add(shapeLabel);
        infoPanel.add(furnitureCountLabel);

        // Create view options panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBorder(BorderFactory.createTitledBorder("View Options"));
        optionsPanel.setBackground(java.awt.Color.WHITE);

        JLabel instructionsLabel = new JLabel("<html>Mouse Controls:<br>• Drag to rotate<br>• Scroll to zoom</html>");

        // Create wall visibility options
        JPanel wallPanel = new JPanel(new GridLayout(0, 1));
        wallPanel.setBorder(BorderFactory.createTitledBorder("Wall Visibility"));
        wallPanel.setBackground(java.awt.Color.WHITE);

        showAllWallsCheckbox = new JCheckBox("Show All Walls", true);
        showAllWallsCheckbox.setBackground(java.awt.Color.WHITE);
        showAllWallsCheckbox.addActionListener(e -> {
            boolean selected = showAllWallsCheckbox.isSelected();
            showFrontWallCheckbox.setSelected(selected);
            showBackWallCheckbox.setSelected(selected);
            showLeftWallCheckbox.setSelected(selected);
            showRightWallCheckbox.setSelected(selected);
            updateWallVisibility();
        });

        showCeilingCheckbox = new JCheckBox("Show Ceiling", false);
        showCeilingCheckbox.setBackground(java.awt.Color.WHITE);
        showCeilingCheckbox.addActionListener(e -> updateWallVisibility());

        showFrontWallCheckbox = new JCheckBox("Show Front Wall", false);
        showFrontWallCheckbox.setBackground(java.awt.Color.WHITE);
        showFrontWallCheckbox.addActionListener(e -> updateWallVisibility());

        showBackWallCheckbox = new JCheckBox("Show Back Wall", true);
        showBackWallCheckbox.setBackground(java.awt.Color.WHITE);
        showBackWallCheckbox.addActionListener(e -> updateWallVisibility());

        showLeftWallCheckbox = new JCheckBox("Show Left Wall", true);
        showLeftWallCheckbox.setBackground(java.awt.Color.WHITE);
        showLeftWallCheckbox.addActionListener(e -> updateWallVisibility());

        showRightWallCheckbox = new JCheckBox("Show Right Wall", false);
        showRightWallCheckbox.setBackground(java.awt.Color.WHITE);
        showRightWallCheckbox.addActionListener(e -> updateWallVisibility());

        wallPanel.add(showAllWallsCheckbox);
        wallPanel.add(showCeilingCheckbox);
        wallPanel.add(showFrontWallCheckbox);
        wallPanel.add(showBackWallCheckbox);
        wallPanel.add(showLeftWallCheckbox);
        wallPanel.add(showRightWallCheckbox);

        JButton resetButton = SwingUtils.createPrimaryButton("Reset View", e -> resetView());

        optionsPanel.add(instructionsLabel);
        optionsPanel.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(wallPanel);
        optionsPanel.add(javax.swing.Box.createRigidArea(new Dimension(0, 10)));
        optionsPanel.add(resetButton);

        // Create the side panel to hold info and controls
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        sidePanel.setPreferredSize(new Dimension(220, 0));

        JPanel sidePanel1 = new JPanel();
        sidePanel1.setLayout(new BoxLayout(sidePanel1, BoxLayout.X_AXIS));
        sidePanel1.setBorder(new EmptyBorder(0, 0, 0, 0));
        sidePanel1.setPreferredSize(new Dimension(220, 10));


        sidePanel.add(infoPanel);
        sidePanel.add(javax.swing.Box.createRigidArea(new Dimension(0, 20)));
        sidePanel.add(optionsPanel);
        sidePanel.add(javax.swing.Box.createVerticalGlue());

        // Add components to main panel
        add(buttonPanel, BorderLayout.NORTH);
        add(jfxPanel, BorderLayout.CENTER);
        add(sidePanel, BorderLayout.EAST);
        add(sidePanel1, BorderLayout.SOUTH);

        // Initialize JavaFX on its own thread
        Platform.runLater(this::initJavaFX);
    }

    /**
     * Initialize JavaFX components
     */
    private void initJavaFX() {
        try {
            // Create root container
            root = new Group();

            // Setup camera with better angle for interior view
            camera = new PerspectiveCamera(true);
            camera.setFieldOfView(50);  // Wider field of view
            camera.setNearClip(0.1);
            camera.setFarClip(10000.0);
            camera.getTransforms().addAll(
                    new Translate(0, 0, -800)
            );

            // Create groups for room and furniture
            roomGroup = new Group();
            furnitureGroup = new Group();

            // Setup transformations with better initial angles
            rotateX = new Rotate(30, Rotate.X_AXIS);  // Less extreme X rotation
            rotateY = new Rotate(20, Rotate.Y_AXIS);  // Less extreme Y rotation
            scaleTransform = new Scale(1.0, 1.0, 1.0);

            // Center the content in the scene better
            contentGroup = new Group();
            contentGroup.getChildren().addAll(roomGroup, furnitureGroup);
            contentGroup.getTransforms().addAll(rotateX, rotateY, scaleTransform);

            // Add lighting for better visibility
            AmbientLight ambientLight = new AmbientLight(Color.WHITE);
            ambientLight.setOpacity(0.6);  // Softer ambient light

            // Add point lights from different angles
            PointLight pointLight1 = new PointLight(Color.WHITE);
            pointLight1.setTranslateX(400);
            pointLight1.setTranslateY(-400);
            pointLight1.setTranslateZ(-400);
            pointLight1.setOpacity(0.8);

            PointLight pointLight2 = new PointLight(Color.WHITE);
            pointLight2.setTranslateX(-400);
            pointLight2.setTranslateY(100);
            pointLight2.setTranslateZ(500);
            pointLight2.setOpacity(0.8);

            // Add interior light source
            PointLight interiorLight = new PointLight(Color.WHITE);
            interiorLight.setTranslateY(-100);
            interiorLight.setOpacity(0.7);

            // Add everything to root
            root.getChildren().addAll(contentGroup,ambientLight);

            // Create scene with proper depth buffer and size
            scene = new Scene(root, 300, 200, true);
            scene.setFill(Color.rgb(240, 240, 240));  // Lighter background
            scene.setCamera(camera);

            // Add mouse rotation control
            scene.setOnMousePressed(this::handleMousePressed);
            scene.setOnMouseDragged(this::handleMouseDragged);

            // Add mouse wheel zoom
            scene.setOnScroll(this::handleScroll);

            // Center content in the scene
            // Corrected: This is done properly in updateDesign3D now

            // Set the scene to the panel
            jfxPanel.setScene(scene);

            // Mark as initialized
            isInitialized = true;

            // Update with current design
            updateDesign3D();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing JavaFX: " + e.getMessage());
        }
    }

    /**
     * Mouse press handler
     */
    private void handleMousePressed(MouseEvent event) {
        mouseOldX = event.getSceneX();
        mouseOldY = event.getSceneY();
    }

    /**
     * Mouse drag handler for rotation
     */
    private void handleMouseDragged(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();

            rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX) * 0.2);
            rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY) * 0.2);

            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
        }
    }

    /**
     * Scroll handler for zoom
     */
    private void handleScroll(ScrollEvent event) {
        double delta = event.getDeltaY();
        double scaleFactor = 1.05;

        if (delta > 0) {
            // Zoom in
            zoomFactor *= scaleFactor;
        } else {
            // Zoom out
            zoomFactor /= scaleFactor;
        }

        // Limit zoom range
        zoomFactor = Math.max(0.5, Math.min(zoomFactor, 5.0));  // Allow more zoom

        scaleTransform.setX(zoomFactor);
        scaleTransform.setY(zoomFactor);
        scaleTransform.setZ(zoomFactor);
    }

    /**
     * Update the 3D visualization with current design
     */
    private void updateDesign3D() {
        if (!isInitialized) return;

        // Schedule on JavaFX thread if needed
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::updateDesign3D);
            return;
        }

        try {
            // Clear existing content
            roomGroup.getChildren().clear();
            furnitureGroup.getChildren().clear();
            furnitureNodes.clear();

            // Get current design
            Design design = app.getCurrentDesign();
            if (design == null) return;

            // Get room
            Room room = design.getRoom();
            if (room == null) return;

            // Create the 3D room
            createRoom(room);

            // Update wall visibility
            updateWallVisibility();

            // Create furniture if available
            if (design.getFurnitureList() != null) {
                for (Furniture furniture : design.getFurnitureList()) {
                    if (furniture != null) {
                        createFurniture(furniture, room);
                    }
                }
            }

            // FIXED: Properly center content in the viewport
            // This ensures the room appears centered on the page
            double centerX = scene.getWidth() / 2;
            double centerY = scene.getHeight() / 2;

            // Apply translation to center the content in the scene
            contentGroup.setTranslateX(0);
            contentGroup.setTranslateY(0);

            // Position the root to center the scene
            root.setTranslateX(centerX);
            root.setTranslateY(centerY);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error updating 3D scene: " + e.getMessage());
        }
    }

    /**
     * Toggle visibility of walls based on checkboxes
     */
    private void updateWallVisibility() {
        if (!isInitialized) return;

        Platform.runLater(() -> {
            // Find and set visibility for each part
            for (javafx.scene.Node node : roomGroup.getChildren()) {
                if (node.getId() == null) continue;

                switch (node.getId()) {
                    case "wall-front":
                        node.setVisible(showFrontWallCheckbox.isSelected());
                        break;
                    case "wall-back":
                        node.setVisible(showBackWallCheckbox.isSelected());
                        break;
                    case "wall-left":
                        node.setVisible(showLeftWallCheckbox.isSelected());
                        break;
                    case "wall-right":
                        node.setVisible(showRightWallCheckbox.isSelected());
                        break;
                    case "ceiling":
                        node.setVisible(showCeilingCheckbox.isSelected());
                        break;
                }
            }
        });
    }

    /**
     * Create a 3D room based on room parameters
     */
    private void createRoom(Room room) {
        // Convert dimensions to JavaFX units (1m = 100 units)
        double width = room.getWidth() * 100;
        double height = room.getHeight() * 100;
        double depth = room.getLength() * 100;

        // Create floor with proper color
        Box floor = new Box(width, 10, depth);
        floor.setId("floor");
        PhongMaterial floorMaterial = new PhongMaterial();
        floorMaterial.setDiffuseColor(convertColor(room.getColorScheme().getFloorColor()));
        floorMaterial.setSpecularColor(Color.WHITE);
        floorMaterial.setSpecularPower(10);
        floor.setMaterial(floorMaterial);
        floor.setTranslateY(height/2);

        // Create ceiling
        Box ceiling = new Box(width, 10, depth);
        ceiling.setId("ceiling");
        PhongMaterial ceilingMaterial = new PhongMaterial();
        ceilingMaterial.setDiffuseColor(convertColor(room.getColorScheme().getCeilingColor()));
        ceilingMaterial.setSpecularPower(5);
        ceiling.setMaterial(ceilingMaterial);
        ceiling.setTranslateY(-height/2);

        // Get wall color from the room's color scheme
        Color wallColor = convertColor(room.getColorScheme().getWallColor());

        // Create walls with proper materials
        // Left wall
        PhongMaterial wallLeftMaterial = new PhongMaterial();
        wallLeftMaterial.setDiffuseColor(wallColor);
        wallLeftMaterial.setSpecularColor(Color.WHITE);
        wallLeftMaterial.setSpecularPower(5);

        // Right wall
        PhongMaterial wallRightMaterial = new PhongMaterial();
        wallRightMaterial.setDiffuseColor(wallColor);
        wallRightMaterial.setSpecularColor(Color.WHITE);
        wallRightMaterial.setSpecularPower(5);

        // Front wall
        PhongMaterial wallFrontMaterial = new PhongMaterial();
        wallFrontMaterial.setDiffuseColor(wallColor);
        wallFrontMaterial.setSpecularColor(Color.WHITE);
        wallFrontMaterial.setSpecularPower(5);

        // Back wall
        PhongMaterial wallBackMaterial = new PhongMaterial();
        wallBackMaterial.setDiffuseColor(wallColor);
        wallBackMaterial.setSpecularColor(Color.WHITE);
        wallBackMaterial.setSpecularPower(5);

        // Create walls
        Box wallLeft = new Box(10, height, depth);
        wallLeft.setId("wall-left");
        wallLeft.setMaterial(wallLeftMaterial);
        wallLeft.setTranslateX(-width/2);

        Box wallRight = new Box(10, height, depth);
        wallRight.setId("wall-right");
        wallRight.setMaterial(wallRightMaterial);
        wallRight.setTranslateX(width/2);

        Box wallFront = new Box(width, height, 10);
        wallFront.setId("wall-front");
        wallFront.setMaterial(wallFrontMaterial);
        wallFront.setTranslateZ(-depth/2);

        Box wallBack = new Box(width, height, 10);
        wallBack.setId("wall-back");
        wallBack.setMaterial(wallBackMaterial);
        wallBack.setTranslateZ(depth/2);

        // Add all parts to room group
        roomGroup.getChildren().addAll(floor, ceiling, wallLeft, wallRight, wallFront, wallBack);
    }

    /**
     * Create a piece of furniture in the 3D scene
     */
    private void createFurniture(Furniture furniture, Room room) {
        try {
            // Convert dimensions to JavaFX units (1m = 100 units)
            double width = furniture.getWidth() * 100;
            double height = furniture.getHeight() * 100;
            double length = furniture.getLength() * 100;

            // Create furniture body
            Box shape = new Box(width, height, length);

            // Create material with furniture color
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(convertColor(furniture.getColor()));
            material.setSpecularColor(Color.WHITE);
            material.setSpecularPower(50);
            shape.setMaterial(material);

            // Calculate room dimensions
            double roomWidth = room.getWidth() * 100;
            double roomLength = room.getLength() * 100;
            double roomHeight = room.getHeight() * 100;

            // Calculate furniture position relative to room center
            // Bottom-left corner of room is (0,0) in the design
            // Center of room is (0,0,0) in 3D scene
            double posX = (furniture.getPosX() + furniture.getWidth()/2) * 100 - roomWidth/2;
            double posZ = (furniture.getPosY() + furniture.getLength()/2) * 100 - roomLength/2;

            // Height position (y-coordinate) - place on floor with correct height
            double posY = (roomHeight/2) - (height/2);

            // Create group for this furniture piece
            Group furnitureObj = new Group();

            // Add shape to furniture object
            furnitureObj.getChildren().add(shape);

            // Position within the room
            furnitureObj.setTranslateX(posX);
            furnitureObj.setTranslateY(posY);
            furnitureObj.setTranslateZ(posZ);

            // Apply furniture rotation
            Rotate furnitureRotation = new Rotate(furniture.getRotation(), Rotate.Y_AXIS);
            furnitureObj.getTransforms().add(furnitureRotation);

            // Store reference and add to scene
            furnitureNodes.put(furniture.getId(), furnitureObj);
            furnitureGroup.getChildren().add(furnitureObj);

        } catch (Exception e) {
            System.err.println("Error creating furniture: " + e.getMessage());
        }
    }

    /**
     * Convert AWT color to JavaFX color
     */
    private Color convertColor(java.awt.Color color) {
        if (color == null) return Color.GRAY;
        return Color.rgb(
                color.getRed(),
                color.getGreen(),
                color.getBlue(),
                color.getAlpha() / 255.0
        );
    }

    /**
     * Reset the view to default camera angles and zoom
     */
    private void resetView() {
        Platform.runLater(() -> {
            rotateX.setAngle(30);
            rotateY.setAngle(20);

            // Reset zoom
            zoomFactor = 1.0;
            scaleTransform.setX(1.0);
            scaleTransform.setY(1.0);
            scaleTransform.setZ(1.0);

            // Reset wall visibility
            showFrontWallCheckbox.setSelected(false);
            showBackWallCheckbox.setSelected(true);
            showLeftWallCheckbox.setSelected(true);
            showRightWallCheckbox.setSelected(false);
            showCeilingCheckbox.setSelected(false);
            showAllWallsCheckbox.setSelected(false);
            updateWallVisibility();

            // FIXED: Recenter the scene after reset
            updateDesign3D();
        });
    }

    /**
     * Update the info panel with current design details
     */
    public void updateDesignInfo() {
        Design design = app.getCurrentDesign();
        if (design != null) {
            // Update design name
            nameLabel.setText("Name: " + design.getName());

            // Update room information
            Room room = design.getRoom();
            if (room != null) {
                dimensionsLabel.setText(String.format("Dimensions: %.1fm × %.1fm × %.1fm",
                        room.getWidth(), room.getLength(), room.getHeight()));
                shapeLabel.setText("Shape: " + room.getShape());
            } else {
                dimensionsLabel.setText("Dimensions: Not available");
                shapeLabel.setText("Shape: Not available");
            }

            // Update furniture count
            int count = (design.getFurnitureList() != null) ? design.getFurnitureList().size() : 0;
            furnitureCountLabel.setText("Furniture Count: " + count);

            // Update 3D visualization
            updateDesign3D();

        } else {
            nameLabel.setText("Name: No design loaded");
            dimensionsLabel.setText("Dimensions: N/A");
            shapeLabel.setText("Shape: N/A");
            furnitureCountLabel.setText("Furniture Count: 0");
        }
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Update when panel becomes visible
        SwingUtilities.invokeLater(this::updateDesignInfo);
    }

    @Override
    public void onDesignChanged(Design design) {
        // Update when design changes
        updateDesignInfo();
    }
}