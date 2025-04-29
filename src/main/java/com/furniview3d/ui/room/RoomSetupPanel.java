package main.java.com.furniview3d.ui.room;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.controller.RoomController;
import main.java.com.furniview3d.model.ColorScheme;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RoomSetupPanel extends JPanel implements RoomController.RoomChangeListener {
    private static final long serialVersionUID = 1L;
    private FurniView3DApp app;
    private RoomController roomController;

    // Form components
    private JTextField nameField;
    private JComboBox<String> shapeComboBox;
    private JFormattedTextField widthField;
    private JFormattedTextField lengthField;
    private JFormattedTextField heightField;
    private JComboBox<String> colorSchemeComboBox;
    private JButton wallColorButton;
    private JButton floorColorButton;
    private JButton ceilingColorButton;
    private JButton accentColorButton;

    // Preview panel
    private RoomPreviewPanel previewPanel;

    public RoomSetupPanel(FurniView3DApp app) {
        this.app = app;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Initialize controller with the current design (if any)
        if (app.getCurrentDesign() != null) {
            // Pass app reference to controller
            roomController = new RoomController(app.getCurrentDesign(), app);
        } else {
            // Create a new design if none exists
            app.setCurrentDesign(new main.java.com.furniview3d.model.Design());
            roomController = new RoomController(app.getCurrentDesign(), app);
        }
        roomController.addRoomChangeListener(this);

        // Create UI components
        createFormPanel();
        createPreviewPanel();
        createButtonPanel();
    }

    private void createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Room Properties"));
        formPanel.setBackground(Color.WHITE);

        // Room name
        formPanel.add(new JLabel("Room Name:"));
        nameField = new JTextField(roomController.getCurrentRoom().getName());
        nameField.addActionListener(e -> updateRoomName());
        formPanel.add(nameField);

        // Room shape
        formPanel.add(new JLabel("Shape:"));
        List<String> shapes = roomController.getAvailableRoomShapes();
        shapeComboBox = new JComboBox<>(shapes.toArray(new String[0]));
        shapeComboBox.setSelectedItem(roomController.getCurrentRoom().getShape());
        shapeComboBox.addActionListener(e -> updateRoomShape());
        formPanel.add(shapeComboBox);

        // Room dimensions
        formPanel.add(new JLabel("Width (meters):"));
        widthField = new JFormattedTextField(roomController.getCurrentRoom().getWidth());
        widthField.setColumns(10);
        widthField.addPropertyChangeListener("value", e -> updateRoomDimensions());
        formPanel.add(widthField);

        formPanel.add(new JLabel("Length (meters):"));
        lengthField = new JFormattedTextField(roomController.getCurrentRoom().getLength());
        lengthField.setColumns(10);
        lengthField.addPropertyChangeListener("value", e -> updateRoomDimensions());
        formPanel.add(lengthField);

        formPanel.add(new JLabel("Height (meters):"));
        heightField = new JFormattedTextField(roomController.getCurrentRoom().getHeight());
        heightField.setColumns(10);
        heightField.addPropertyChangeListener("value", e -> updateRoomDimensions());
        formPanel.add(heightField);

        // Color scheme
        formPanel.add(new JLabel("Color Scheme:"));
        List<ColorScheme> schemes = roomController.getPredefinedColorSchemes();
        String[] schemeNames = new String[schemes.size()];
        for (int i = 0; i < schemes.size(); i++) {
            schemeNames[i] = schemes.get(i).getName();
        }
        colorSchemeComboBox = new JComboBox<>(schemeNames);
        colorSchemeComboBox.addActionListener(e -> updateColorScheme());
        formPanel.add(colorSchemeComboBox);

        // Custom colors
        formPanel.add(new JLabel("Wall Color:"));
        wallColorButton = new JButton();
        wallColorButton.setBackground(roomController.getCurrentRoom().getColorScheme().getWallColor());
        wallColorButton.addActionListener(e -> chooseColor(wallColorButton, "Wall"));
        formPanel.add(wallColorButton);

        formPanel.add(new JLabel("Floor Color:"));
        floorColorButton = new JButton();
        floorColorButton.setBackground(roomController.getCurrentRoom().getColorScheme().getFloorColor());
        floorColorButton.addActionListener(e -> chooseColor(floorColorButton, "Floor"));
        formPanel.add(floorColorButton);

        formPanel.add(new JLabel("Ceiling Color:"));
        ceilingColorButton = new JButton();
        ceilingColorButton.setBackground(roomController.getCurrentRoom().getColorScheme().getCeilingColor());
        ceilingColorButton.addActionListener(e -> chooseColor(ceilingColorButton, "Ceiling"));
        formPanel.add(ceilingColorButton);

        formPanel.add(new JLabel("Accent Color:"));
        accentColorButton = new JButton();
        accentColorButton.setBackground(roomController.getCurrentRoom().getColorScheme().getAccentColor());
        accentColorButton.addActionListener(e -> chooseColor(accentColorButton, "Accent"));
        formPanel.add(accentColorButton);

        // Add to main panel
        add(formPanel, BorderLayout.WEST);
    }

    private void createPreviewPanel() {
        JPanel previewContainer = new JPanel(new BorderLayout());
        previewContainer.setBorder(BorderFactory.createTitledBorder("Room Preview"));
        previewContainer.setBackground(Color.WHITE);
        previewPanel = new RoomPreviewPanel(roomController.getCurrentRoom());
        previewContainer.add(previewPanel, BorderLayout.CENTER);
        add(previewContainer, BorderLayout.CENTER);
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = SwingUtils.createSecondaryButton("Cancel", e -> app.showPanel("dashboard"));
        JButton nextButton = SwingUtils.createPrimaryButton("Next: Design Room", e -> proceedToDesign());
        buttonPanel.add(cancelButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void updateRoomName() {
        String name = nameField.getText().trim();
        if (!name.isEmpty()) {
            roomController.updateRoomName(name);
        }
    }

    private void updateRoomShape() {
        String shape = (String) shapeComboBox.getSelectedItem();
        roomController.updateRoomShape(shape);
    }

    private void updateRoomDimensions() {
        try {
            double width = ((Number) widthField.getValue()).doubleValue();
            double length = ((Number) lengthField.getValue()).doubleValue();
            double height = ((Number) heightField.getValue()).doubleValue();

            if (roomController.validateRoomDimensions(width, length, height)) {
                roomController.updateRoomDimensions(width, length, height);
            } else {
                SwingUtils.showErrorDialog(this, "Invalid room dimensions. Values must be positive and reasonable.");
            }
        } catch (Exception e) {
            SwingUtils.showErrorDialog(this, "Please enter valid numbers for room dimensions.");
        }
    }

    private void updateColorScheme() {
        String schemeName = (String) colorSchemeComboBox.getSelectedItem();
        if (schemeName != null) {
            roomController.setPredefinedColorScheme(schemeName);

            // Update color buttons
            Room room = roomController.getCurrentRoom();
            wallColorButton.setBackground(room.getColorScheme().getWallColor());
            floorColorButton.setBackground(room.getColorScheme().getFloorColor());
            ceilingColorButton.setBackground(room.getColorScheme().getCeilingColor());
            accentColorButton.setBackground(room.getColorScheme().getAccentColor());
        }
    }

    private void chooseColor(JButton button, String colorType) {
        Color currentColor = button.getBackground();
        Color newColor = JColorChooser.showDialog(this, "Choose " + colorType + " Color", currentColor);
        if (newColor != null) {
            button.setBackground(newColor);
            updateCustomColors();
        }
    }

    private void updateCustomColors() {
        roomController.updateColorScheme(
                wallColorButton.getBackground(),
                floorColorButton.getBackground(),
                ceilingColorButton.getBackground(),
                accentColorButton.getBackground()
        );
    }

    private void proceedToDesign() {
        // Save any pending changes
        updateRoomName();
        updateRoomDimensions();

        // Make sure the app has the updated design
        app.setCurrentDesign(app.getCurrentDesign());

        // Proceed to the design 2D panel
        app.showPanel("design2D");
    }

    @Override
    public void addNotify() {
        super.addNotify();
        // Make sure we have the latest design when panel becomes visible
        if (app.getCurrentDesign() != null && roomController != null) {
            roomController.setCurrentDesign(app.getCurrentDesign());
            Room room = roomController.getCurrentRoom();
            if (room != null) {
                nameField.setText(room.getName());
                widthField.setValue(room.getWidth());
                lengthField.setValue(room.getLength());
                heightField.setValue(room.getHeight());
                shapeComboBox.setSelectedItem(room.getShape());

                if (room.getColorScheme() != null) {
                    wallColorButton.setBackground(room.getColorScheme().getWallColor());
                    floorColorButton.setBackground(room.getColorScheme().getFloorColor());
                    ceilingColorButton.setBackground(room.getColorScheme().getCeilingColor());
                    accentColorButton.setBackground(room.getColorScheme().getAccentColor());
                }

                previewPanel.updateRoom(room);
            }
        }
    }

    @Override
    public void onRoomChanged(Room room) {
        // Update the preview panel
        previewPanel.updateRoom(room);

        // Update form fields if needed
        nameField.setText(room.getName());
        widthField.setValue(room.getWidth());
        lengthField.setValue(room.getLength());
        heightField.setValue(room.getHeight());
        wallColorButton.setBackground(room.getColorScheme().getWallColor());
        floorColorButton.setBackground(room.getColorScheme().getFloorColor());
        ceilingColorButton.setBackground(room.getColorScheme().getCeilingColor());
        accentColorButton.setBackground(room.getColorScheme().getAccentColor());

        revalidate();
        repaint();
    }

    // Inner class for room preview
    private class RoomPreviewPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private Room room;

        public RoomPreviewPanel(Room room) {
            this.room = room;
            setPreferredSize(new Dimension(400, 300));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        public void updateRoom(Room room) {
            this.room = room;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int padding = 30;
            int width = getWidth() - (padding * 2);
            int height = getHeight() - (padding * 2);

            // Calculate scale to fit the room in the preview
            double roomWidth = room.getWidth();
            double roomLength = room.getLength();
            double scale = Math.min(width / roomWidth, height / roomLength);
            int roomPixelWidth = (int) (roomWidth * scale);
            int roomPixelLength = (int) (roomLength * scale);

            // Center the room in the preview
            int x = (getWidth() - roomPixelWidth) / 2;
            int y = (getHeight() - roomPixelLength) / 2;

            // Draw floor
            g2d.setColor(room.getColorScheme().getFloorColor());
            g2d.fillRect(x, y, roomPixelWidth, roomPixelLength);

            // Draw walls (simple 2D representation)
            g2d.setColor(room.getColorScheme().getWallColor());
            g2d.setStroke(new BasicStroke(4));
            g2d.drawRect(x, y, roomPixelWidth, roomPixelLength);

            // Draw room name
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(room.getName(), x + 10, y + 20);

            // Draw dimensions
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            String dimensions = String.format("%.1fm × %.1fm × %.1fm", room.getWidth(), room.getLength(), room.getHeight());
            g2d.drawString(dimensions, x + 10, y + 40);

            // Draw shape information
            g2d.drawString("Shape: " + room.getShape(), x + 10, y + 60);

            // Draw a special visualization for L-shaped rooms if selected
            if (room.getShape().equalsIgnoreCase("L-Shaped")) {
                // Draw the L-shape by adding a rectangle to the main room
                int extensionWidth = roomPixelWidth / 3;
                int extensionLength = roomPixelLength / 3;

                // Fill extension with floor color
                g2d.setColor(room.getColorScheme().getFloorColor());
                g2d.fillRect(x - extensionWidth, y + roomPixelLength - extensionLength, extensionWidth, extensionLength);

                // Draw extension walls
                g2d.setColor(room.getColorScheme().getWallColor());
                g2d.drawRect(x - extensionWidth, y + roomPixelLength - extensionLength, extensionWidth, extensionLength);

                // Remove the inner line between main room and extension
                g2d.setColor(room.getColorScheme().getFloorColor());
                g2d.setStroke(new BasicStroke(5));
                g2d.drawLine(x, y + roomPixelLength - extensionLength, x, y + roomPixelLength);
            }

            // Add an accent color strip along the bottom wall
            g2d.setColor(room.getColorScheme().getAccentColor());
            g2d.setStroke(new BasicStroke(6));
            g2d.drawLine(x, y + roomPixelLength, x + roomPixelWidth, y + roomPixelLength);

            // For rectangular rooms, add some perspective cues
            if (room.getShape().equalsIgnoreCase("Rectangular") || room.getShape().equalsIgnoreCase("Square")) {
                // Draw a simple ceiling projection
                g2d.setColor(room.getColorScheme().getCeilingColor());
                g2d.setStroke(new BasicStroke(1));
                int ceilingOffset = 15; // perspective offset

                // Draw ceiling lines
                g2d.drawLine(x, y, x + ceilingOffset, y - ceilingOffset);
                g2d.drawLine(x + roomPixelWidth, y, x + roomPixelWidth + ceilingOffset, y - ceilingOffset);
                g2d.drawLine(x + ceilingOffset, y - ceilingOffset, x + roomPixelWidth + ceilingOffset, y - ceilingOffset);

                // Draw right wall projection
                g2d.drawLine(x + roomPixelWidth, y, x + roomPixelWidth, y + roomPixelLength);
                g2d.drawLine(x + roomPixelWidth, y + roomPixelLength, x + roomPixelWidth + ceilingOffset, y + roomPixelLength - ceilingOffset);
                g2d.drawLine(x + roomPixelWidth + ceilingOffset, y - ceilingOffset, x + roomPixelWidth + ceilingOffset, y + roomPixelLength - ceilingOffset);
            }
        }
    }
}