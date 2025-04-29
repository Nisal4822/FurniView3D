package main.java.com.furniview3d.ui.design2d;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.controller.Design2DController;
import main.java.com.furniview3d.controller.FurnitureController;
import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.render.Renderer2D;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class Design2DPanel extends JPanel implements
        Design2DController.Design2DListener,
        FurniView3DApp.DesignChangeListener {

    private static final long serialVersionUID = 1L;
    private FurniView3DApp app;
    private Renderer2D renderer;
    private Design2DController controller;
    private FurnitureController furnitureController;
    private JPanel toolbarPanel;
    private JPanel propertiesPanel;
    private JPanel statusPanel;
    private JToggleButton selectButton;
    private JToggleButton moveButton;
    private JToggleButton rotateButton;
    private JButton zoomInButton;
    private JButton zoomOutButton;
    private JButton resetViewButton;
    private JToggleButton gridButton;
    private JButton view3DButton; // Button to switch to 3D view
    private JComboBox<String> furnitureTypeComboBox;
    private JList<String> furnitureList;
    private DefaultListModel<String> furnitureListModel;
    private boolean isDragging = false;
    private Point dragStartPoint;
    private Point lastDragPoint;
    private String currentTool = "select";
    private JLabel roomInfoLabel;

    public Design2DPanel(FurniView3DApp app) {
        this.app = app;
        this.furnitureController = new FurnitureController();
        setLayout(new BorderLayout());

        // Register as a design change listener
        app.addDesignChangeListener(this);

        // Create the renderer
        renderer = new Renderer2D();
        controller = new Design2DController(renderer);
        controller.addListener(this);

        // Set current design
        if (app.getCurrentDesign() != null) {
            controller.setCurrentDesign(app.getCurrentDesign());
        }

        // Create UI components
        createToolbar();
        createPropertiesPanel();
        createStatusPanel();

        // Add renderer to center
        add(renderer, BorderLayout.CENTER);

        // Add mouse listeners to renderer for interaction
        setupMouseListeners();
    }

    private void createToolbar() {
        toolbarPanel = new JPanel();
        toolbarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        toolbarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));

        // Tool selection
        ButtonGroup toolGroup = new ButtonGroup();
        selectButton = new JToggleButton("Select");
        selectButton.setSelected(true);
        selectButton.addActionListener(e -> currentTool = "select");
        toolGroup.add(selectButton);

        moveButton = new JToggleButton("Move");
        moveButton.addActionListener(e -> currentTool = "move");
        toolGroup.add(moveButton);

        rotateButton = new JToggleButton("Rotate");
        rotateButton.addActionListener(e -> currentTool = "rotate");
        toolGroup.add(rotateButton);

        // View controls
        zoomInButton = new JButton("Zoom +");
        zoomInButton.addActionListener(e -> controller.zoomIn());

        zoomOutButton = new JButton("Zoom -");
        zoomOutButton.addActionListener(e -> controller.zoomOut());

        resetViewButton = new JButton("Reset View");
        resetViewButton.addActionListener(e -> controller.resetView());

        gridButton = new JToggleButton("Grid");
        gridButton.setSelected(true);
        gridButton.addActionListener(e -> controller.toggleGrid());

        // 3D View Button
        view3DButton = SwingUtils.createPrimaryButton("Switch to 3D View", e -> switchTo3DView());

        // Room info label
        roomInfoLabel = new JLabel("Room: Not Available");

        // Add to toolbar
        toolbarPanel.add(selectButton);
        toolbarPanel.add(moveButton);
        toolbarPanel.add(rotateButton);
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(zoomInButton);
        toolbarPanel.add(zoomOutButton);
        toolbarPanel.add(resetViewButton);
        toolbarPanel.add(gridButton);
        toolbarPanel.add(new JSeparator(SwingConstants.VERTICAL));
        toolbarPanel.add(view3DButton);
        toolbarPanel.add(Box.createHorizontalStrut(20));
        toolbarPanel.add(roomInfoLabel);

        add(toolbarPanel, BorderLayout.NORTH);
    }

    private void createPropertiesPanel() {
        propertiesPanel = new JPanel();
        propertiesPanel.setLayout(new BorderLayout());
        propertiesPanel.setPreferredSize(new Dimension(250, 0));
        propertiesPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));

        // Furniture selection
        JPanel furniturePanel = new JPanel(new BorderLayout());
        furniturePanel.setBorder(BorderFactory.createTitledBorder("Add Furniture"));

        // Furniture type combo
        String[] types = furnitureController.getFurnitureTypes();
        furnitureTypeComboBox = new JComboBox<>(types);
        furnitureTypeComboBox.addActionListener(e -> updateFurnitureList());

        // Furniture list
        furnitureListModel = new DefaultListModel<>();
        furnitureList = new JList<>(furnitureListModel);
        furnitureList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        furnitureList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addSelectedFurniture();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(furnitureList);
        JButton addButton = SwingUtils.createPrimaryButton("Add to Design", e -> addSelectedFurniture());

        furniturePanel.add(furnitureTypeComboBox, BorderLayout.NORTH);
        furniturePanel.add(scrollPane, BorderLayout.CENTER);
        furniturePanel.add(addButton, BorderLayout.SOUTH);

        // Properties panel (initially empty)
        JPanel itemPropertiesPanel = new JPanel();
        itemPropertiesPanel.setLayout(new BoxLayout(itemPropertiesPanel, BoxLayout.Y_AXIS));
        itemPropertiesPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
        itemPropertiesPanel.add(new JLabel("Select an item to edit properties"));

        // Add to properties panel
        propertiesPanel.add(furniturePanel, BorderLayout.NORTH);
        propertiesPanel.add(itemPropertiesPanel, BorderLayout.CENTER);

        // Add navigation buttons
        JPanel navPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        navPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton backButton = SwingUtils.createSecondaryButton("Back to Room Setup", e -> app.showPanel("roomSetup"));
        JButton catalogButton = SwingUtils.createSecondaryButton("Furniture Catalog", e -> app.showPanel("catalog"));
        navPanel.add(backButton);
        navPanel.add(catalogButton);
        propertiesPanel.add(navPanel, BorderLayout.SOUTH);

        add(propertiesPanel, BorderLayout.EAST);

        // Initialize furniture list
        updateFurnitureList();
    }

    private void createStatusPanel() {
        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(new EmptyBorder(5, 10, 5, 10));
        JLabel statusLabel = new JLabel("Ready");
        statusPanel.add(statusLabel, BorderLayout.WEST);

        // Add save button to status panel
        JButton saveButton = SwingUtils.createPrimaryButton("Save Design", e -> saveDesign());
        statusPanel.add(saveButton, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    private void saveDesign() {
        if (app.getCurrentDesign() == null) {
            SwingUtils.showErrorDialog(this, "No design is currently open.");
            return;
        }

        // If it's an untitled design, prompt for a name
        if (app.getCurrentDesign().getName().equals("Untitled Design")) {
            String designName = JOptionPane.showInputDialog(
                    this,
                    "Enter a name for your design:",
                    "Save Design",
                    JOptionPane.QUESTION_MESSAGE
            );
            if (designName == null || designName.trim().isEmpty()) {
                return; // User cancelled or entered empty name
            }
            app.getCurrentDesign().setName(designName);
        }

        try {
            String fileName = app.getCurrentDesign().getName().replaceAll("\\s+", "_").toLowerCase();
            main.java.com.furniview3d.util.FileManager.saveDesign(app.getCurrentDesign(), fileName);
            SwingUtils.showInfoDialog(this, "Design saved successfully.");
        } catch (Exception e) {
            SwingUtils.showErrorDialog(this, "Error saving design: " + e.getMessage());
        }
    }

    private void setupMouseListeners() {
        renderer.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                handleMouseReleased(e);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (currentTool.equals("select")) {
                    controller.selectFurniture(e.getPoint());
                }
            }
        });

        renderer.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }
        });

        // Add key listener for delete key
        renderer.setFocusable(true);
        renderer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    Furniture selected = controller.getSelectedFurniture();
                    if (selected != null) {
                        controller.removeFurniture(selected.getId());
                    }
                }
            }
        });
    }

    private void handleMousePressed(MouseEvent e) {
        if (currentTool.equals("select")) {
            // Selection is handled in mouseClicked
            return;
        }

        // Start dragging
        isDragging = true;
        dragStartPoint = e.getPoint();
        lastDragPoint = e.getPoint();

        // If no furniture is selected, try to select one now
        if (controller.getSelectedFurniture() == null) {
            controller.selectFurniture(e.getPoint());
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (!isDragging || controller.getSelectedFurniture() == null) {
            return;
        }

        Furniture selected = controller.getSelectedFurniture();
        if (currentTool.equals("move")) {
            // Calculate movement in pixels
            int deltaX = e.getX() - lastDragPoint.x;
            int deltaY = e.getY() - lastDragPoint.y;

            // Convert to model coordinates (meters)
            double scale = controller.getScale();
            double moveDeltaX = deltaX / (100.0 * scale);
            double moveDeltaY = deltaY / (100.0 * scale);

            // Update position
            controller.moveFurnitureRelative(selected.getId(), moveDeltaX, moveDeltaY);
            lastDragPoint = e.getPoint();
        } else if (currentTool.equals("rotate")) {
            // Calculate center of furniture in screen coordinates
            Room room = controller.getCurrentDesign().getRoom();
            double roomWidth = room.getWidth() * 100;
            double roomLength = room.getLength() * 100;
            int screenCenterX = renderer.getWidth() / 2;
            int screenCenterY = renderer.getHeight() / 2;
            double furnitureX = selected.getPosX() * 100;
            double furnitureY = selected.getPosY() * 100;
            int furnitureCenterX = screenCenterX + (int)(furnitureX * controller.getScale());
            int furnitureCenterY = screenCenterY + (int)(furnitureY * controller.getScale());

            // Calculate angle
            double angle = Math.toDegrees(Math.atan2(
                    e.getY() - furnitureCenterY,
                    e.getX() - furnitureCenterX
            ));

            // Update rotation
            controller.rotateFurniture(selected.getId(), angle);
        }
    }

    private void handleMouseReleased(MouseEvent e) {
        isDragging = false;
    }

    private void updateFurnitureList() {
        // Clear list
        furnitureListModel.clear();

        // Get selected furniture type
        String selectedType = (String) furnitureTypeComboBox.getSelectedItem();
        if (selectedType == null) {
            return;
        }

        // Get furniture of selected type
        List<Furniture> typeList = furnitureController.getFurnitureByType(selectedType);

        // Add to list model
        for (Furniture furniture : typeList) {
            furnitureListModel.addElement(furniture.getName());
        }
    }

    private void addSelectedFurniture() {
        // Get selected furniture type and name
        String selectedType = (String) furnitureTypeComboBox.getSelectedItem();
        String selectedName = furnitureList.getSelectedValue();
        if (selectedType == null || selectedName == null) {
            return;
        }

        // Find matching furniture
        List<Furniture> typeList = furnitureController.getFurnitureByType(selectedType);
        Furniture templateFurniture = null;
        for (Furniture furniture : typeList) {
            if (furniture.getName().equals(selectedName)) {
                templateFurniture = furniture;
                break;
            }
        }

        if (templateFurniture == null) {
            return;
        }

        // Create a copy for the design
        Furniture newFurniture = furnitureController.createFurnitureCopy(templateFurniture);

        // Place in center of room
        Room room = controller.getCurrentDesign().getRoom();
        newFurniture.setPosX(room.getWidth() / 2 - newFurniture.getWidth() / 2);
        newFurniture.setPosY(room.getLength() / 2 - newFurniture.getLength() / 2);

        // Add to design
        controller.addFurniture(newFurniture);

        // Update the app's current design to notify other panels
        app.setCurrentDesign(app.getCurrentDesign());
    }

    private void updatePropertiesPanel(Furniture furniture) {
        // Create properties panel for selected furniture
        JPanel itemPropertiesPanel = new JPanel();
        itemPropertiesPanel.setLayout(new BoxLayout(itemPropertiesPanel, BoxLayout.Y_AXIS));
        itemPropertiesPanel.setBorder(BorderFactory.createTitledBorder("Properties"));

        if (furniture == null) {
            itemPropertiesPanel.add(new JLabel("Select an item to edit properties"));
        } else {
            // Name
            itemPropertiesPanel.add(new JLabel("Name: " + furniture.getName()));
            itemPropertiesPanel.add(Box.createVerticalStrut(5));

            // Type
            itemPropertiesPanel.add(new JLabel("Type: " + furniture.getType()));
            itemPropertiesPanel.add(Box.createVerticalStrut(5));

            // Dimensions
            itemPropertiesPanel.add(new JLabel(String.format("Dimensions: %.1fm × %.1fm × %.1fm",
                    furniture.getWidth(), furniture.getLength(), furniture.getHeight())));
            itemPropertiesPanel.add(Box.createVerticalStrut(5));

            // Position
            JPanel posPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            posPanel.add(new JLabel("Position:"));
            JFormattedTextField posXField = new JFormattedTextField(furniture.getPosX());
            posXField.setColumns(5);
            posXField.addPropertyChangeListener("value", e -> {
                double x = ((Number) posXField.getValue()).doubleValue();
                controller.moveFurniture(furniture.getId(), x, furniture.getPosY());

                // Notify app of design change
                app.setCurrentDesign(app.getCurrentDesign());
            });

            JFormattedTextField posYField = new JFormattedTextField(furniture.getPosY());
            posYField.setColumns(5);
            posYField.addPropertyChangeListener("value", e -> {
                double y = ((Number) posYField.getValue()).doubleValue();
                controller.moveFurniture(furniture.getId(), furniture.getPosX(), y);

                // Notify app of design change
                app.setCurrentDesign(app.getCurrentDesign());
            });
            posPanel.add(new JLabel("X:"));
            posPanel.add(posXField);
            posPanel.add(new JLabel("Y:"));
            posPanel.add(posYField);
            itemPropertiesPanel.add(posPanel);
            itemPropertiesPanel.add(Box.createVerticalStrut(5));

            // Rotation
            JPanel rotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rotPanel.add(new JLabel("Rotation:"));
            JFormattedTextField rotField = new JFormattedTextField(furniture.getRotation());
            rotField.setColumns(5);
            rotField.addPropertyChangeListener("value", e -> {
                double rotation = ((Number) rotField.getValue()).doubleValue();
                controller.rotateFurniture(furniture.getId(), rotation);

                // Notify app of design change
                app.setCurrentDesign(app.getCurrentDesign());
            });
            rotPanel.add(rotField);
            rotPanel.add(new JLabel("degrees"));
            itemPropertiesPanel.add(rotPanel);
            itemPropertiesPanel.add(Box.createVerticalStrut(5));

            // Color
            JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            colorPanel.add(new JLabel("Color:"));
            JButton colorButton = new JButton("");
            colorButton.setBackground(furniture.getColor());
            colorButton.setPreferredSize(new Dimension(30, 20));
            colorButton.addActionListener(e -> {
                Color newColor = JColorChooser.showDialog(
                        SwingUtils.getWindowAncestor(this),
                        "Choose Furniture Color",
                        furniture.getColor()
                );
                if (newColor != null) {
                    controller.changeFurnitureColor(furniture.getId(), newColor);
                    colorButton.setBackground(newColor);

                    // Notify app of design change
                    app.setCurrentDesign(app.getCurrentDesign());
                }
            });
            colorPanel.add(colorButton);
            itemPropertiesPanel.add(colorPanel);
            itemPropertiesPanel.add(Box.createVerticalStrut(10));

            // Delete button
            JButton deleteButton = SwingUtils.createSecondaryButton("Remove", e -> {
                controller.removeFurniture(furniture.getId());

                // Notify app of design change
                app.setCurrentDesign(app.getCurrentDesign());
            });
            itemPropertiesPanel.add(deleteButton);
        }

        // Update properties panel
        propertiesPanel.remove(propertiesPanel.getComponentCount() - 2); // Remove properties panel but keep nav panel
        propertiesPanel.add(itemPropertiesPanel, BorderLayout.CENTER);
        propertiesPanel.revalidate();
        propertiesPanel.repaint();
    }

    private void switchTo3DView() {
        // Make sure the current design is saved in the application
        if (app.getCurrentDesign() != null) {
            // Navigate to 3D view panel
            app.showPanel("design3D");
        } else {
            SwingUtils.showErrorDialog(this, "No design is currently open.");
        }
    }

    private void updateRoomInfoLabel() {
        Design design = app.getCurrentDesign();
        if (design != null && design.getRoom() != null) {
            Room room = design.getRoom();
            String roomInfo = String.format("Room: %s (%.1fm × %.1fm × %.1fm, %s)",
                    room.getName(),
                    room.getWidth(),
                    room.getLength(),
                    room.getHeight(),
                    room.getShape());
            roomInfoLabel.setText(roomInfo);
        } else {
            roomInfoLabel.setText("Room: Not Available");
        }
    }

    @Override
    public void onDesign2DChanged(Design design, Furniture selectedFurniture) {
        SwingUtilities.invokeLater(() -> {
            updatePropertiesPanel(selectedFurniture);
            updateRoomInfoLabel();
        });
    }

    @Override
    public void onDesignChanged(Design design) {
        // When design changes, update the controller and refresh the view
        SwingUtilities.invokeLater(() -> {
            controller.setCurrentDesign(design);
            updateRoomInfoLabel();
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();

        // When this panel becomes visible, make sure it has the latest design
        SwingUtilities.invokeLater(() -> {
            if (app.getCurrentDesign() != null) {
                controller.setCurrentDesign(app.getCurrentDesign());
                updateRoomInfoLabel();
            }
        });
    }
}