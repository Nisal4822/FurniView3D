package main.java.com.furniview3d.ui.dashboard;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.util.FileManager;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final FurniView3DApp app;
    private JPanel recentDesignsPanel;
    private JPanel actionsPanel;

    public DashboardPanel(FurniView3DApp app) {
        this.app = app;
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Create the welcome panel
        createWelcomePanel();

        // Create the main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Color.WHITE);

        // Create the recent designs panel
        createRecentDesignsPanel();

        // Create the quick actions panel
        createActionsPanel();

        contentPanel.add(recentDesignsPanel);
        contentPanel.add(actionsPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Create the status panel
        createStatusPanel();
    }

    private void createWelcomePanel() {
        JPanel welcomePanel = new JPanel(new BorderLayout());
        welcomePanel.setBackground(Color.WHITE);
        welcomePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel welcomeLabel = SwingUtils.createTitleLabel("FurniView3D");
        welcomeLabel.setForeground(SwingUtils.PRIMARY_COLOR);

        JLabel subtitleLabel = new JLabel("Design and visualize furniture layouts for your spaces");
        subtitleLabel.setFont(SwingUtils.BODY_FONT);
        subtitleLabel.setForeground(Color.DARK_GRAY);

        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(subtitleLabel, BorderLayout.CENTER);

        add(welcomePanel, BorderLayout.NORTH);
    }

    private void createRecentDesignsPanel() {
        recentDesignsPanel = new JPanel();
        recentDesignsPanel.setLayout(new BorderLayout());
        recentDesignsPanel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SwingUtils.PRIMARY_COLOR),
                "Recent Designs"
        );
        titledBorder.setTitleFont(SwingUtils.HEADER_FONT);
        titledBorder.setTitleColor(SwingUtils.PRIMARY_COLOR);
        recentDesignsPanel.setBorder(titledBorder);

        // Get recent designs
        JPanel designsListPanel = new JPanel();
        designsListPanel.setLayout(new BoxLayout(designsListPanel, BoxLayout.Y_AXIS));
        designsListPanel.setBackground(Color.WHITE);

        List<String> designNames = FileManager.getDesignList();
        if (designNames.isEmpty()) {
            JLabel noDesignsLabel = new JLabel("No recent designs found. Create a new design to get started.");
            noDesignsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            noDesignsLabel.setForeground(Color.GRAY);
            designsListPanel.add(Box.createVerticalGlue());
            designsListPanel.add(noDesignsLabel);
            designsListPanel.add(Box.createVerticalGlue());
        } else {
            JScrollPane scrollPane = new JScrollPane(designsListPanel);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());

            // Add up to 5 most recent designs
            int count = Math.min(designNames.size(), 5);
            for (int i = 0; i < count; i++) {
                DesignListItem designItem = new DesignListItem(designNames.get(i));
                designsListPanel.add(designItem);
                // Add a separator except after the last item
                if (i < count - 1) {
                    designsListPanel.add(new JSeparator());
                }
            }
            recentDesignsPanel.add(scrollPane, BorderLayout.CENTER);
        }

        // "View All" button at the bottom
        JButton viewAllButton = SwingUtils.createSecondaryButton("View All Designs", e -> viewAllDesigns());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewAllButton);

        recentDesignsPanel.add(designsListPanel, BorderLayout.CENTER);
        recentDesignsPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void viewAllDesigns() {
        // Direct to the Design Management panel
        app.showPanel("management");
    }

    private void createActionsPanel() {
        actionsPanel = new JPanel();
        actionsPanel.setLayout(new BorderLayout());
        actionsPanel.setBackground(Color.WHITE);

        TitledBorder titledBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SwingUtils.PRIMARY_COLOR),
                "Quick Actions"
        );
        titledBorder.setTitleFont(SwingUtils.HEADER_FONT);
        titledBorder.setTitleColor(SwingUtils.PRIMARY_COLOR);
        actionsPanel.setBorder(titledBorder);

        // Create grid for action buttons
        JPanel actionsGrid = new JPanel(new GridLayout(2, 2, 15, 15));
        actionsGrid.setBackground(Color.WHITE);
        actionsGrid.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Action buttons
        actionsGrid.add(createActionButton("Create New Design", "Create a new room design from scratch",
                e -> createNewDesign()));
        actionsGrid.add(createActionButton("Open Design", "Open an existing room design",
                e -> openDesign()));
        actionsGrid.add(createActionButton("Import Design", "Import a design from an external file",
                e -> importDesign()));
        actionsGrid.add(createActionButton("Settings & Preferences", "Customize application settings",
                e -> openSettings()));

        actionsPanel.add(actionsGrid, BorderLayout.CENTER);
    }

    private JPanel createActionButton(String title, String description, java.awt.event.ActionListener action) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        panel.setBackground(Color.WHITE);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SwingUtils.HEADER_FONT);
        titleLabel.setForeground(SwingUtils.PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(5, 10, 0, 10));

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(SwingUtils.SMALL_FONT);
        descLabel.setForeground(Color.DARK_GRAY);
        descLabel.setBorder(new EmptyBorder(0, 10, 10, 10));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descLabel, BorderLayout.CENTER);

        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(245, 245, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return panel;
    }

    private void createNewDesign() {
        // Create a new design with default settings
        Design newDesign = new Design();
        newDesign.setName("Untitled Design");
        newDesign.setDescription("New design created by " + app.getCurrentUserId());
        newDesign.setDesignerId(app.getCurrentUserId());

        // Make sure room is initialized properly
        Room room = new Room();
        room.setName("New Room");
        room.setWidth(5.0);
        room.setLength(5.0);
        room.setHeight(2.5);
        room.setShape("Rectangular");
        newDesign.setRoom(room);

        // Set as current design
        app.setCurrentDesign(newDesign);

        // Navigate to the room setup panel
        app.showPanel("roomSetup");
    }

    private void openDesign() {
        // Display a list of designs to open
        List<String> designNames = FileManager.getDesignList();
        if (designNames.isEmpty()) {
            SwingUtils.showInfoDialog(this, "No saved designs found.");
            return;
        }

        String selectedDesign = (String) JOptionPane.showInputDialog(
                this,
                "Select a design to open:",
                "Open Design",
                JOptionPane.QUESTION_MESSAGE,
                null,
                designNames.toArray(),
                designNames.get(0)
        );

        if (selectedDesign != null) {
            try {
                Design design = FileManager.loadDesign(selectedDesign);

                // Ensure room is properly initialized
                if (design.getRoom() == null) {
                    Room room = new Room();
                    room.setName("Default Room");
                    room.setWidth(5.0);
                    room.setLength(5.0);
                    room.setHeight(2.5);
                    room.setShape("Rectangular");
                    design.setRoom(room);
                }

                // Ensure furniture list is initialized
                if (design.getFurnitureList() == null) {
                    design.setFurnitureList(new java.util.ArrayList<>());
                }

                app.setCurrentDesign(design);
                app.showPanel("design2D");
            } catch (IOException | ClassNotFoundException e) {
                SwingUtils.showErrorDialog(this, "Error loading design: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void importDesign() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Design");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "FurniView Design Files (*.fvd)", "fvd"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                Design design = FileManager.importDesign(filePath);

                // Validate design
                if (design == null) {
                    SwingUtils.showErrorDialog(this, "Invalid design file.");
                    return;
                }

                // Ensure room is properly initialized
                if (design.getRoom() == null) {
                    Room room = new Room();
                    room.setName("Imported Room");
                    room.setWidth(5.0);
                    room.setLength(5.0);
                    room.setHeight(2.5);
                    room.setShape("Rectangular");
                    design.setRoom(room);
                }

                // Ensure furniture list is initialized
                if (design.getFurnitureList() == null) {
                    design.setFurnitureList(new java.util.ArrayList<>());
                }

                app.setCurrentDesign(design);
                app.showPanel("design2D");

                SwingUtils.showInfoDialog(this, "Design imported successfully.");
            } catch (Exception e) {
                SwingUtils.showErrorDialog(this, "Error importing design: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void openSettings() {
        SwingUtils.showInfoDialog(this, "Settings feature is under development.");
    }

    private void createStatusPanel() {
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(245, 245, 245));
        statusPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setFont(SwingUtils.SMALL_FONT);
        statusLabel.setForeground(Color.DARK_GRAY);

        statusPanel.add(statusLabel, BorderLayout.WEST);

        add(statusPanel, BorderLayout.SOUTH);
    }

    // Inner class for design list items
    private class DesignListItem extends JPanel {
        private static final long serialVersionUID = 1L;

        public DesignListItem(String designName) {
            setLayout(new BorderLayout(10, 5));
            setBackground(Color.WHITE);
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            JLabel nameLabel = new JLabel(designName);
            nameLabel.setFont(SwingUtils.BODY_FONT);
            nameLabel.setForeground(SwingUtils.PRIMARY_COLOR);

            JLabel dateLabel = new JLabel("Last modified: Recently");
            try {
                Design design = FileManager.loadDesign(designName);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
                String formattedDate = design.getLastModified().format(formatter);
                dateLabel.setText("Last modified: " + formattedDate);
            } catch (Exception e) {
                // Do nothing, use default text
            }

            dateLabel.setFont(SwingUtils.SMALL_FONT);
            dateLabel.setForeground(Color.GRAY);

            JPanel infoPanel = new JPanel(new BorderLayout());
            infoPanel.setBackground(Color.WHITE);
            infoPanel.add(nameLabel, BorderLayout.NORTH);
            infoPanel.add(dateLabel, BorderLayout.SOUTH);

            JButton openButton = SwingUtils.createPrimaryButton("Open", e -> openDesignItem(designName));

            add(infoPanel, BorderLayout.CENTER);
            add(openButton, BorderLayout.EAST);

            // Add hover effect
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(new Color(245, 245, 250));
                    infoPanel.setBackground(new Color(245, 245, 250));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(Color.WHITE);
                    infoPanel.setBackground(Color.WHITE);
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    openDesignItem(designName);
                }
            });
        }

        private void openDesignItem(String designName) {
            try {
                // Load the design
                Design design = FileManager.loadDesign(designName);

                // Perform validations and ensure all properties are properly initialized
                if (design.getRoom() == null) {
                    Room room = new Room();
                    room.setName("Default Room");
                    room.setWidth(5.0);
                    room.setLength(5.0);
                    room.setHeight(2.5);
                    room.setShape("Rectangular");
                    design.setRoom(room);
                }

                if (design.getFurnitureList() == null) {
                    design.setFurnitureList(new java.util.ArrayList<>());
                }

                // Set the current design in the application
                app.setCurrentDesign(design);

                // Navigate to the 2D design view
                app.showPanel("design2D");
            } catch (IOException | ClassNotFoundException e) {
                SwingUtils.showErrorDialog(DashboardPanel.this, "Error loading design: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}