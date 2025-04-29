package main.java.com.furniview3d.ui.management;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.controller.DesignController;
import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.util.FileManager;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ManagementPanel extends JPanel implements DesignController.DesignChangeListener {
    private static final long serialVersionUID = 1L;

    private FurniView3DApp app;
    private DesignController designController;

    private JTable designsTable;
    private DefaultTableModel tableModel;
    private JPanel designInfoPanel;

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JLabel createdDateLabel;
    private JLabel modifiedDateLabel;
    private JLabel designerLabel;

    public ManagementPanel(FurniView3DApp app) {
        this.app = app;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        // Initialize controller with current design
        if (app.getCurrentDesign() != null) {
            designController = new DesignController(app.getCurrentDesign());
        } else {
            // Create a new design if none exists
            app.setCurrentDesign(new Design());
            designController = new DesignController(app.getCurrentDesign());
        }

        designController.addDesignChangeListener(this);

        // Create UI components
        createDesignsTable();
        createInfoPanel();
        createButtonPanel();

        // Load designs
        loadDesigns();
    }

    private void createDesignsTable() {
        // Create table model with columns
        String[] columnNames = {"Name", "Last Modified", "Designer"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        designsTable = new JTable(tableModel);
        designsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        designsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = designsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String designName = (String) tableModel.getValueAt(selectedRow, 0);
                    loadDesignDetails(designName);
                }
            }
        });

        // Set column widths
        designsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        designsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        designsTable.getColumnModel().getColumn(2).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(designsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Saved Designs"));
        scrollPane.setPreferredSize(new Dimension(500, 300));

        add(scrollPane, BorderLayout.WEST);
    }

    private void createInfoPanel() {
        designInfoPanel = new JPanel();
        designInfoPanel.setLayout(new BorderLayout());
        designInfoPanel.setBorder(BorderFactory.createTitledBorder("Design Details"));
        designInfoPanel.setBackground(Color.WHITE);

        JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        formPanel.setBackground(Color.WHITE);

        // Name field
        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBackground(Color.WHITE);
        namePanel.add(new JLabel("Name:"), BorderLayout.NORTH);
        nameField = new JTextField();
        namePanel.add(nameField, BorderLayout.CENTER);
        formPanel.add(namePanel);

        // Description area
        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setBackground(Color.WHITE);
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descPanel.add(descScrollPane, BorderLayout.CENTER);
        formPanel.add(descPanel);

        // Info labels
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Additional Information"));

        createdDateLabel = new JLabel("Created: ");
        modifiedDateLabel = new JLabel("Modified: ");
        designerLabel = new JLabel("Designer: ");

        infoPanel.add(createdDateLabel);
        infoPanel.add(modifiedDateLabel);
        infoPanel.add(designerLabel);

        // Add to main panel
        designInfoPanel.add(formPanel, BorderLayout.CENTER);
        designInfoPanel.add(infoPanel, BorderLayout.SOUTH);

        add(designInfoPanel, BorderLayout.CENTER);
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton newButton = SwingUtils.createPrimaryButton("New Design", e -> createNewDesign());
        JButton openButton = SwingUtils.createPrimaryButton("Open Design", e -> openSelectedDesign());
        JButton updateButton = SwingUtils.createSecondaryButton("Update Details", e -> updateDesignDetails());
        JButton deleteButton = SwingUtils.createSecondaryButton("Delete Design", e -> deleteSelectedDesign());

        buttonPanel.add(newButton);
        buttonPanel.add(openButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadDesigns() {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Get all saved designs
        List<String> designNames = FileManager.getDesignList();

        // Add to table
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
        for (String designName : designNames) {
            try {
                Design design = FileManager.loadDesign(designName);
                String formattedDate = design.getLastModified().format(formatter);
                tableModel.addRow(new Object[]{
                        design.getName(),
                        formattedDate,
                        design.getDesignerId()
                });
            } catch (Exception e) {
                // Skip files that can't be loaded
                System.err.println("Error loading design: " + designName);
            }
        }
    }

    private void loadDesignDetails(String designName) {
        try {
            // Find the design file name from the display name
            List<String> designFiles = FileManager.getDesignList();
            String fileName = null;

            for (String file : designFiles) {
                try {
                    Design design = FileManager.loadDesign(file);
                    if (design.getName().equals(designName)) {
                        fileName = file;
                        break;
                    }
                } catch (Exception e) {
                    // Skip files that can't be loaded
                }
            }

            if (fileName == null) {
                SwingUtils.showErrorDialog(this, "Could not find design file for " + designName);
                return;
            }

            Design design = FileManager.loadDesign(fileName);

            // Update UI
            nameField.setText(design.getName());
            descriptionArea.setText(design.getDescription());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
            createdDateLabel.setText("Created: " + design.getCreatedAt().format(formatter));
            modifiedDateLabel.setText("Modified: " + design.getLastModified().format(formatter));
            designerLabel.setText("Designer: " + design.getDesignerId());

            // Update current design controller
            designController.setCurrentDesign(design);

        } catch (Exception e) {
            SwingUtils.showErrorDialog(this, "Error loading design details: " + e.getMessage());
        }
    }

    private void createNewDesign() {
        // Create new design
        Design newDesign = designController.createNewDesign(app.getCurrentUserId());
        app.setCurrentDesign(newDesign);

        // Navigate to room setup
        app.showPanel("roomSetup");
    }

    private void openSelectedDesign() {
        int selectedRow = designsTable.getSelectedRow();
        if (selectedRow < 0) {
            SwingUtils.showErrorDialog(this, "Please select a design to open.");
            return;
        }

        String designName = (String) tableModel.getValueAt(selectedRow, 0);

        try {
            // Find the design file name from the display name
            List<String> designFiles = FileManager.getDesignList();
            String fileName = null;

            for (String file : designFiles) {
                try {
                    Design design = FileManager.loadDesign(file);
                    if (design.getName().equals(designName)) {
                        fileName = file;
                        break;
                    }
                } catch (Exception e) {
                    // Skip files that can't be loaded
                }
            }

            if (fileName == null) {
                SwingUtils.showErrorDialog(this, "Could not find design file for " + designName);
                return;
            }

            Design design = FileManager.loadDesign(fileName);
            app.setCurrentDesign(design);

            // Navigate to design2D panel
            app.showPanel("design2D");

        } catch (Exception e) {
            SwingUtils.showErrorDialog(this, "Error opening design: " + e.getMessage());
        }
    }

    private void updateDesignDetails() {
        if (designController.getCurrentDesign() == null) {
            SwingUtils.showErrorDialog(this, "No design is currently selected.");
            return;
        }

        // Update design properties
        String name = nameField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty()) {
            SwingUtils.showErrorDialog(this, "Design name cannot be empty.");
            return;
        }

        designController.updateDesignName(name);
        designController.updateDesignDescription(description);

        try {
            // Save the updated design
            String fileName = designController.getCurrentDesign().getName().replaceAll("\\s+", "_").toLowerCase();
            FileManager.saveDesign(designController.getCurrentDesign(), fileName);

            SwingUtils.showInfoDialog(this, "Design details updated successfully.");

            // Refresh the designs list
            loadDesigns();

        } catch (IOException e) {
            SwingUtils.showErrorDialog(this, "Error saving design changes: " + e.getMessage());
        }
    }

    private void deleteSelectedDesign() {
        int selectedRow = designsTable.getSelectedRow();
        if (selectedRow < 0) {
            SwingUtils.showErrorDialog(this, "Please select a design to delete.");
            return;
        }

        String designName = (String) tableModel.getValueAt(selectedRow, 0);

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the design '" + designName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                // Find the design file name from the display name
                List<String> designFiles = FileManager.getDesignList();
                String fileName = null;

                for (String file : designFiles) {
                    try {
                        Design design = FileManager.loadDesign(file);
                        if (design.getName().equals(designName)) {
                            fileName = file;
                            break;
                        }
                    } catch (Exception e) {
                        // Skip files that can't be loaded
                    }
                }

                if (fileName == null) {
                    SwingUtils.showErrorDialog(this, "Could not find design file for " + designName);
                    return;
                }

                // Delete the design file
                boolean deleted = FileManager.deleteDesign(fileName);

                if (deleted) {
                    SwingUtils.showInfoDialog(this, "Design deleted successfully.");

                    // Clear design details panel
                    nameField.setText("");
                    descriptionArea.setText("");
                    createdDateLabel.setText("Created: ");
                    modifiedDateLabel.setText("Modified: ");
                    designerLabel.setText("Designer: ");

                    // Refresh the designs list
                    loadDesigns();
                } else {
                    SwingUtils.showErrorDialog(this, "Error deleting design.");
                }

            } catch (Exception e) {
                SwingUtils.showErrorDialog(this, "Error deleting design: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDesignChanged(Design design) {
        // Update UI with current design details
        if (design != null) {
            nameField.setText(design.getName());
            descriptionArea.setText(design.getDescription());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");
            createdDateLabel.setText("Created: " + design.getCreatedAt().format(formatter));
            modifiedDateLabel.setText("Modified: " + design.getLastModified().format(formatter));
            designerLabel.setText("Designer: " + design.getDesignerId());
        }
    }
}