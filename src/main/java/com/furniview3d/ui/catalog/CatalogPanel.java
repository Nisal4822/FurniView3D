package main.java.com.furniview3d.ui.catalog;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.controller.FurnitureController;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class CatalogPanel extends JPanel implements FurnitureController.FurnitureChangeListener {
    private static final long serialVersionUID = 1L;

    private FurniView3DApp app;
    private FurnitureController furnitureController;

    private JComboBox<String> categoryComboBox;
    private JPanel furnitureDisplayPanel;
    private JPanel detailsPanel;
    private JTextField searchField;
    private JButton addNewButton;

    private Furniture selectedFurniture;

    public CatalogPanel(FurniView3DApp app) {
        this.app = app;
        this.furnitureController = new FurnitureController();
        this.furnitureController.addFurnitureChangeListener(this);

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        createComponents();
        populateFurnitureItems("All");
    }

    private void createComponents() {
        // Top panel with search and filter
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(Color.WHITE);

        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(Color.WHITE);

        JLabel searchLabel = new JLabel("Search: ");
        searchField = new JTextField(20);
        searchField.addActionListener(e -> searchFurniture());

        JButton searchButton = SwingUtils.createPrimaryButton("Search", e -> searchFurniture());

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // Category filter
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);

        JLabel categoryLabel = new JLabel("Category: ");

        // Get furniture types and add "All" option
        String[] types = furnitureController.getFurnitureTypes();
        String[] categories = new String[types.length + 1];
        categories[0] = "All";
        System.arraycopy(types, 0, categories, 1, types.length);

        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.addActionListener(e -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            populateFurnitureItems(selectedCategory);
        });

        filterPanel.add(categoryLabel);
        filterPanel.add(categoryComboBox);

        // Add button
        addNewButton = SwingUtils.createSecondaryButton("Add New Furniture", e -> showAddFurnitureDialog());
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(addNewButton);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(filterPanel, BorderLayout.SOUTH);

        // Main content panel - split between catalog and details
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(Color.WHITE);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(5);

        // Furniture items display
        furnitureDisplayPanel = new JPanel();
        furnitureDisplayPanel.setLayout(new BoxLayout(furnitureDisplayPanel, BoxLayout.Y_AXIS));
        furnitureDisplayPanel.setBackground(Color.WHITE);
        furnitureDisplayPanel.setBorder(new TitledBorder("Furniture Catalog"));

        JScrollPane scrollPane = new JScrollPane(furnitureDisplayPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);

        // Details panel
        detailsPanel = new JPanel();
        detailsPanel.setLayout(new BorderLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new TitledBorder("Furniture Details"));

        // Add "Select an item" message to details panel
        JLabel selectLabel = new JLabel("Select a furniture item to view details", JLabel.CENTER);
        selectLabel.setForeground(Color.GRAY);
        detailsPanel.add(selectLabel, BorderLayout.CENTER);

        // Add components to split pane
        splitPane.setLeftComponent(scrollPane);
        splitPane.setRightComponent(detailsPanel);

        // Add all components to main panel
        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void populateFurnitureItems(String category) {
        furnitureDisplayPanel.removeAll();

        List<Furniture> furnitureList;
        if ("All".equals(category)) {
            furnitureList = furnitureController.getFurnitureCatalog();
        } else {
            furnitureList = furnitureController.getFurnitureByType(category);
        }

        if (furnitureList.isEmpty()) {
            JLabel emptyLabel = new JLabel("No furniture items found", JLabel.CENTER);
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            furnitureDisplayPanel.add(emptyLabel);
        } else {
            for (Furniture furniture : furnitureList) {
                FurnitureItemPanel itemPanel = new FurnitureItemPanel(furniture);
                furnitureDisplayPanel.add(itemPanel);
                furnitureDisplayPanel.add(Box.createVerticalStrut(5)); // Add spacing
            }
        }

        furnitureDisplayPanel.revalidate();
        furnitureDisplayPanel.repaint();
    }

    private void searchFurniture() {
        String searchText = searchField.getText().trim().toLowerCase();
        if (searchText.isEmpty()) {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            populateFurnitureItems(selectedCategory);
            return;
        }

        furnitureDisplayPanel.removeAll();

        // Get furniture based on category
        List<Furniture> furnitureList;
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        if ("All".equals(selectedCategory)) {
            furnitureList = furnitureController.getFurnitureCatalog();
        } else {
            furnitureList = furnitureController.getFurnitureByType(selectedCategory);
        }

        // Filter by search text
        boolean foundAny = false;
        for (Furniture furniture : furnitureList) {
            if (furniture.getName().toLowerCase().contains(searchText) ||
                    furniture.getType().toLowerCase().contains(searchText) ||
                    furniture.getMaterial().toLowerCase().contains(searchText)) {

                FurnitureItemPanel itemPanel = new FurnitureItemPanel(furniture);
                furnitureDisplayPanel.add(itemPanel);
                furnitureDisplayPanel.add(Box.createVerticalStrut(5)); // Add spacing
                foundAny = true;
            }
        }

        if (!foundAny) {
            JLabel emptyLabel = new JLabel("No furniture items found matching '" + searchText + "'", JLabel.CENTER);
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            furnitureDisplayPanel.add(emptyLabel);
        }

        furnitureDisplayPanel.revalidate();
        furnitureDisplayPanel.repaint();
    }

    private void showFurnitureDetails(Furniture furniture) {
        selectedFurniture = furniture;
        detailsPanel.removeAll();

        // Create details view
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        infoPanel.add(new JLabel("Name:"));
        infoPanel.add(new JLabel(furniture.getName()));

        infoPanel.add(new JLabel("Type:"));
        infoPanel.add(new JLabel(furniture.getType()));

        infoPanel.add(new JLabel("Dimensions:"));
        infoPanel.add(new JLabel(String.format("%.1fm × %.1fm × %.1fm",
                furniture.getWidth(), furniture.getLength(), furniture.getHeight())));

        infoPanel.add(new JLabel("Material:"));
        infoPanel.add(new JLabel(furniture.getMaterial()));

        infoPanel.add(new JLabel("Color:"));
        JPanel colorPanel = new JPanel();
        colorPanel.setBackground(furniture.getColor());
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        infoPanel.add(colorPanel);

        // Image preview (placeholder)
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setPreferredSize(new Dimension(200, 200));
        imagePanel.setBorder(BorderFactory.createTitledBorder("Preview"));

        // Create a simple graphic representation of the furniture
        JPanel previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                int width = (int)(furniture.getWidth() * 100);
                int length = (int)(furniture.getLength() * 100);

                // Scale to fit
                double scale = Math.min(
                        (double)(getWidth() - 20) / width,
                        (double)(getHeight() - 20) / length
                );

                int scaledWidth = (int)(width * scale);
                int scaledLength = (int)(length * scale);

                // Center in panel
                int x = (getWidth() - scaledWidth) / 2;
                int y = (getHeight() - scaledLength) / 2;

                // Draw furniture
                g.setColor(furniture.getColor());
                g.fillRect(x, y, scaledWidth, scaledLength);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, scaledWidth, scaledLength);

                // Draw text
                g.drawString(furniture.getType(), x + 5, y + 15);
            }
        };

        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(previewPanel, BorderLayout.CENTER);

        // Buttons for actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);

        //JButton addToDesignButton = SwingUtils.createPrimaryButton("Add to Design", e -> addToDesign());
        JButton editButton = SwingUtils.createSecondaryButton("Edit", e -> editFurniture());
        JButton deleteButton = SwingUtils.createSecondaryButton("Delete", e -> deleteFurniture());

        //buttonPanel.add(addToDesignButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        // Add all to details panel
        detailsPanel.add(infoPanel, BorderLayout.NORTH);
        detailsPanel.add(imagePanel, BorderLayout.CENTER);
        detailsPanel.add(buttonPanel, BorderLayout.SOUTH);

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private void showAddFurnitureDialog() {
        // Create a dialog for adding new furniture
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Furniture", true);
        dialog.setSize(400, 350);
        SwingUtils.centerWindow(dialog);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Form fields
        JTextField nameField = new JTextField(20);

        JComboBox<String> typeComboBox = new JComboBox<>(furnitureController.getFurnitureTypes());

        JFormattedTextField widthField = new JFormattedTextField(1.0);
        widthField.setColumns(10);

        JFormattedTextField lengthField = new JFormattedTextField(1.0);
        lengthField.setColumns(10);

        JFormattedTextField heightField = new JFormattedTextField(1.0);
        heightField.setColumns(10);

        JComboBox<String> materialComboBox = new JComboBox<>(new String[]{
                "Wood", "Metal", "Fabric", "Leather", "Glass", "Plastic"
        });

        JButton colorButton = new JButton();
        colorButton.setBackground(Color.GRAY);
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Select Furniture Color", colorButton.getBackground());
            if (newColor != null) {
                colorButton.setBackground(newColor);
            }
        });

        // Add fields to panel
        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Type:"));
        panel.add(typeComboBox);

        panel.add(new JLabel("Width (m):"));
        panel.add(widthField);

        panel.add(new JLabel("Length (m):"));
        panel.add(lengthField);

        panel.add(new JLabel("Height (m):"));
        panel.add(heightField);

        panel.add(new JLabel("Material:"));
        panel.add(materialComboBox);

        panel.add(new JLabel("Color:"));
        panel.add(colorButton);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = SwingUtils.createSecondaryButton("Cancel", e -> dialog.dispose());

        JButton saveButton = SwingUtils.createPrimaryButton("Save", e -> {
            // Validate inputs
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a name for the furniture");
                return;
            }

            try {
                double width = ((Number) widthField.getValue()).doubleValue();
                double length = ((Number) lengthField.getValue()).doubleValue();
                double height = ((Number) heightField.getValue()).doubleValue();

                if (width <= 0 || length <= 0 || height <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Dimensions must be positive values");
                    return;
                }

                // Create new furniture
                String type = (String) typeComboBox.getSelectedItem();
                String material = (String) materialComboBox.getSelectedItem();
                Color color = colorButton.getBackground();

                Furniture newFurniture = new Furniture(
                        name, type, width, length, height, color, material,
                        "resources/images/furniture/" + type.toLowerCase() + ".png",
                        "resources/models/" + type.toLowerCase() + ".obj"
                );

                furnitureController.addFurniture(newFurniture);
                furnitureController.saveCatalog();
                dialog.dispose();

                // Refresh catalog view
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                populateFurnitureItems(selectedCategory);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage());
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void addToDesign() {
        if (selectedFurniture == null) {
            return;
        }

        // Create a copy of the selected furniture for the design
        Furniture copy = furnitureController.createFurnitureCopy(selectedFurniture);

        // Add to current design
        if (app.getCurrentDesign() != null) {
            app.getCurrentDesign().addFurniture(copy);
            JOptionPane.showMessageDialog(this,
                    "Added " + copy.getName() + " to the current design",
                    "Furniture Added",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "No active design. Please create or open a design first.",
                    "No Design Active",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editFurniture() {
        if (selectedFurniture == null) {
            return;
        }

        // Similar to add dialog, but pre-populated with current values
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Furniture", true);
        dialog.setSize(400, 350);
        SwingUtils.centerWindow(dialog);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        // Form fields with current values
        JTextField nameField = new JTextField(selectedFurniture.getName(), 20);

        JComboBox<String> typeComboBox = new JComboBox<>(furnitureController.getFurnitureTypes());
        typeComboBox.setSelectedItem(selectedFurniture.getType());

        JFormattedTextField widthField = new JFormattedTextField(selectedFurniture.getWidth());
        widthField.setColumns(10);

        JFormattedTextField lengthField = new JFormattedTextField(selectedFurniture.getLength());
        lengthField.setColumns(10);

        JFormattedTextField heightField = new JFormattedTextField(selectedFurniture.getHeight());
        heightField.setColumns(10);

        JComboBox<String> materialComboBox = new JComboBox<>(new String[]{
                "Wood", "Metal", "Fabric", "Leather", "Glass", "Plastic"
        });
        materialComboBox.setSelectedItem(selectedFurniture.getMaterial());

        JButton colorButton = new JButton();
        colorButton.setBackground(selectedFurniture.getColor());
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(dialog, "Select Furniture Color", colorButton.getBackground());
            if (newColor != null) {
                colorButton.setBackground(newColor);
            }
        });

        // Add fields to panel
        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Type:"));
        panel.add(typeComboBox);

        panel.add(new JLabel("Width (m):"));
        panel.add(widthField);

        panel.add(new JLabel("Length (m):"));
        panel.add(lengthField);

        panel.add(new JLabel("Height (m):"));
        panel.add(heightField);

        panel.add(new JLabel("Material:"));
        panel.add(materialComboBox);

        panel.add(new JLabel("Color:"));
        panel.add(colorButton);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton cancelButton = SwingUtils.createSecondaryButton("Cancel", e -> dialog.dispose());

        JButton saveButton = SwingUtils.createPrimaryButton("Save", e -> {
            // Validate inputs
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please enter a name for the furniture");
                return;
            }

            try {
                double width = ((Number) widthField.getValue()).doubleValue();
                double length = ((Number) lengthField.getValue()).doubleValue();
                double height = ((Number) heightField.getValue()).doubleValue();

                if (width <= 0 || length <= 0 || height <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Dimensions must be positive values");
                    return;
                }

                // Update furniture
                String type = (String) typeComboBox.getSelectedItem();
                String material = (String) materialComboBox.getSelectedItem();
                Color color = colorButton.getBackground();




                furnitureController.updateFurniture(
                        selectedFurniture.getId(), name, type,
                        width, length, height, color, material
                );

                dialog.dispose();

                // Update the details view and refresh catalog
                showFurnitureDetails(selectedFurniture);
                String selectedCategory = (String) categoryComboBox.getSelectedItem();
                populateFurnitureItems(selectedCategory);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage());
            }
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Add to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteFurniture() {
        if (selectedFurniture == null) {
            return;
        }

        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete " + selectedFurniture.getName() + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            furnitureController.removeFurniture(selectedFurniture.getId());

            // Reset details panel
            detailsPanel.removeAll();
            JLabel selectLabel = new JLabel("Select a furniture item to view details", JLabel.CENTER);
            selectLabel.setForeground(Color.GRAY);
            detailsPanel.add(selectLabel, BorderLayout.CENTER);
            detailsPanel.revalidate();
            detailsPanel.repaint();

            // Refresh catalog
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            populateFurnitureItems(selectedCategory);

            selectedFurniture = null;
        }
    }

    @Override
    public void onFurnitureCatalogChanged() {
        SwingUtilities.invokeLater(() -> {
            String selectedCategory = (String) categoryComboBox.getSelectedItem();
            populateFurnitureItems(selectedCategory);
        });
    }

    // Inner class for furniture item display
    private class FurnitureItemPanel extends JPanel {
        private static final long serialVersionUID = 1L;

        private Furniture furniture;

        public FurnitureItemPanel(Furniture furniture) {
            this.furniture = furniture;

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            setBackground(Color.WHITE);

            // Color indicator
            JPanel colorIndicator = new JPanel();
            colorIndicator.setBackground(furniture.getColor());
            colorIndicator.setPreferredSize(new Dimension(40, 40));
            colorIndicator.setBorder(BorderFactory.createLineBorder(Color.BLACK));

            // Name and type
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(Color.WHITE);
            infoPanel.setBorder(new EmptyBorder(0, 10, 0, 0));

            JLabel nameLabel = new JLabel(furniture.getName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

            JLabel typeLabel = new JLabel(furniture.getType());
            typeLabel.setForeground(Color.GRAY);

            JLabel dimensionsLabel = new JLabel(String.format("%.1fm × %.1fm × %.1fm",
                    furniture.getWidth(), furniture.getLength(), furniture.getHeight()));
            dimensionsLabel.setForeground(Color.DARK_GRAY);
            dimensionsLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            infoPanel.add(nameLabel);
            infoPanel.add(Box.createVerticalStrut(5));
            infoPanel.add(typeLabel);
            infoPanel.add(Box.createVerticalStrut(3));
            infoPanel.add(dimensionsLabel);

            // Add button
            JButton viewButton = SwingUtils.createPrimaryButton("View", e -> showFurnitureDetails(furniture));
            viewButton.setPreferredSize(new Dimension(80, 30));

            // Add components to panel
            add(colorIndicator, BorderLayout.WEST);
            add(infoPanel, BorderLayout.CENTER);
            add(viewButton, BorderLayout.EAST);

            // Make the entire panel clickable
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showFurnitureDetails(furniture);
                }

                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(245, 245, 250));
                    infoPanel.setBackground(new Color(245, 245, 250));
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(Color.WHITE);
                    infoPanel.setBackground(Color.WHITE);
                }
            });
        }
    }
}