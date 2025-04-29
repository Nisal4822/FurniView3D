package main.java.com.furniview3d;

import main.java.com.furniview3d.auth.LoginForm;
import main.java.com.furniview3d.auth.UserManager;
import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.ui.catalog.CatalogPanel;
import main.java.com.furniview3d.ui.dashboard.DashboardPanel;
import main.java.com.furniview3d.ui.design2d.Design2DPanel;
import main.java.com.furniview3d.ui.design3d.Design3DPanel;
import main.java.com.furniview3d.ui.management.ManagementPanel;
import main.java.com.furniview3d.ui.room.RoomSetupPanel;
import main.java.com.furniview3d.util.FileManager;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurniView3DApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final String APP_TITLE = "FurniView3D - Furniture Design Tool";
    private static final int DEFAULT_WIDTH = 1280;
    private static final int DEFAULT_HEIGHT = 800;

    // Singleton instance
    private static FurniView3DApp instance;

    // Application state
    private String currentUserId;
    private Design currentDesign;
    private Map<String, JPanel> panels;
    private JPanel contentPanel;
    private JMenuBar menuBar;

    // List of design change listeners
    private List<DesignChangeListener> designChangeListeners = new ArrayList<>();

    private FurniView3DApp(String username) {
        this.currentUserId = username;
        this.panels = new HashMap<>();
        initializeFrame();
        createPanels();
        createMenuBar();
        showPanel("dashboard");
    }

    public static FurniView3DApp getInstance(String username) {
        if (instance == null) {
            instance = new FurniView3DApp(username);
        }
        return instance;
    }

    public static FurniView3DApp getInstance() {
        return instance;
    }

    public static void shutdown() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    private void initializeFrame() {
        setTitle(APP_TITLE + " - " + currentUserId);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        SwingUtils.centerWindow(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (SwingUtils.showConfirmDialog(FurniView3DApp.this,
                        "Are you sure you want to exit FurniView3D?")) {
                    shutdown();
                    System.exit(0);
                }
            }
        });

        contentPanel = new JPanel(new BorderLayout());
        setContentPane(contentPanel);
    }

    private void createPanels() {
        DashboardPanel dashboardPanel = new DashboardPanel(this);
        panels.put("dashboard", dashboardPanel);

        RoomSetupPanel roomSetupPanel = new RoomSetupPanel(this);
        panels.put("roomSetup", roomSetupPanel);

        Design2DPanel design2DPanel = new Design2DPanel(this);
        panels.put("design2D", design2DPanel);

        Design3DPanel design3DPanel = new Design3DPanel(this);
        panels.put("design3D", design3DPanel);

        CatalogPanel catalogPanel = new CatalogPanel(this);
        panels.put("catalog", catalogPanel);

        ManagementPanel managementPanel = new ManagementPanel(this);
        panels.put("management", managementPanel);
    }

    private void createMenuBar() {
        menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newDesignItem = new JMenuItem("New Design");
        JMenuItem openDesignItem = new JMenuItem("Open Design...");
        JMenuItem saveDesignItem = new JMenuItem("Save Design");
        JMenuItem saveAsDesignItem = new JMenuItem("Save Design As...");
        JMenuItem exitItem = new JMenuItem("Exit");

        newDesignItem.addActionListener(e -> createNewDesign());
        openDesignItem.addActionListener(e -> openDesign());
        saveDesignItem.addActionListener(e -> saveDesign());
        saveAsDesignItem.addActionListener(e -> saveDesignAs());
        exitItem.addActionListener(e -> {
            if (SwingUtils.showConfirmDialog(this, "Are you sure you want to exit?")) {
                shutdown();
                System.exit(0);
            }
        });

        fileMenu.add(newDesignItem);
        fileMenu.add(openDesignItem);
        fileMenu.addSeparator();
        fileMenu.add(saveDesignItem);
        fileMenu.add(saveAsDesignItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        JMenuItem preferencesItem = new JMenuItem("Preferences...");

        undoItem.setEnabled(false);
        redoItem.setEnabled(false);
        preferencesItem.addActionListener(e -> showPreferences());

        editMenu.add(undoItem);
        editMenu.add(redoItem);
        editMenu.addSeparator();
        editMenu.add(preferencesItem);

        // View menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem dashboardItem = new JMenuItem("Dashboard");
        JMenuItem catalogItem = new JMenuItem("Furniture Catalog");
        JMenuItem roomSetupItem = new JMenuItem("Room Setup");
        JMenuItem design2DItem = new JMenuItem("2D Design View");
        JMenuItem design3DItem = new JMenuItem("3D Design View");
        JMenuItem managementItem = new JMenuItem("Design Management");

        dashboardItem.addActionListener(e -> showPanel("dashboard"));
        catalogItem.addActionListener(e -> showPanel("catalog"));
        roomSetupItem.addActionListener(e -> showPanel("roomSetup"));
        design2DItem.addActionListener(e -> showPanel("design2D"));
        design3DItem.addActionListener(e -> showPanel("design3D"));
        managementItem.addActionListener(e -> showPanel("management"));

        viewMenu.add(dashboardItem);
        viewMenu.add(catalogItem);
        viewMenu.add(roomSetupItem);
        viewMenu.add(design2DItem);
        viewMenu.add(design3DItem);
        viewMenu.add(managementItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem helpContentsItem = new JMenuItem("Help Contents");
        JMenuItem aboutItem = new JMenuItem("About FurniView3D");

        helpContentsItem.addActionListener(e -> showHelp());
        aboutItem.addActionListener(e -> showAbout());

        helpMenu.add(helpContentsItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(helpMenu);

        // User menu (right-aligned)
        JMenu userMenu = new JMenu("Hi, "+currentUserId.toUpperCase());
        JMenuItem logoutItem = new JMenuItem("Log Out");
        logoutItem.addActionListener(e -> logout());
        userMenu.add(logoutItem);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userMenu);

        setJMenuBar(menuBar);
    }

    public void showPanel(String panelName) {
        contentPanel.removeAll();
        JPanel panel = panels.get(panelName);
        if (panel != null) {
            // If this is the design3D panel, update it with the current design
            if (panelName.equals("design3D") && panel instanceof Design3DPanel) {
                Design3DPanel design3DPanel = (Design3DPanel) panel;
                design3DPanel.updateDesignInfo();
            }

            contentPanel.add(panel, BorderLayout.CENTER);
            contentPanel.revalidate();
            contentPanel.repaint();
        } else {
            System.err.println("Panel not found: " + panelName);
        }
    }

    public Design getCurrentDesign() {
        return currentDesign;
    }

    public void setCurrentDesign(Design design) {
        this.currentDesign = design;

        // Update the title
        if (design != null) {
            setTitle(APP_TITLE + " - " + design.getName() + " - " + currentUserId);
        } else {
            setTitle(APP_TITLE + " - " + currentUserId);
        }

        // Notify all panels that implement DesignChangeListener
        notifyDesignChangeListeners();
    }

    public void addDesignChangeListener(DesignChangeListener listener) {
        if (!designChangeListeners.contains(listener)) {
            designChangeListeners.add(listener);
        }
    }

    public void removeDesignChangeListener(DesignChangeListener listener) {
        designChangeListeners.remove(listener);
    }

    private void notifyDesignChangeListeners() {
        for (DesignChangeListener listener : designChangeListeners) {
            listener.onDesignChanged(currentDesign);
        }
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    private void createNewDesign() {
        if (currentDesign != null) {
            if (!SwingUtils.showConfirmDialog(this,
                    "You have an open design. Create a new one?")) {
                return;
            }
        }

        Design newDesign = new Design();
        newDesign.setName("Untitled Design");
        newDesign.setDescription("New design created by " + currentUserId);
        newDesign.setDesignerId(currentUserId);
        newDesign.setRoom(new Room());

        setCurrentDesign(newDesign);
        this.showPanel("roomSetup");
    }

    private void openDesign() {
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

                // Create a new Room object if it's null
                if (design.getRoom() == null) {
                    design.setRoom(new Room());
                }

                // Ensure furniture list isn't null
                if (design.getFurnitureList() == null) {
                    design.setFurnitureList(new ArrayList<>());
                }

                // Update app state with loaded design
                setCurrentDesign(design);
                this.showPanel("design2D");
            } catch (IOException | ClassNotFoundException e) {
                SwingUtils.showErrorDialog(this, "Error loading design: " + e.getMessage());
            }
        }
    }

    private void saveDesign() {
        if (currentDesign == null) {
            SwingUtils.showErrorDialog(this, "No design is currently open.");
            return;
        }

        // If design doesn't have a filename yet, call saveDesignAs
        if (currentDesign.getName().equals("Untitled Design")) {
            saveDesignAs();
            return;
        }

        try {
            String fileName = currentDesign.getName().replaceAll("\\s+", "_").toLowerCase();
            FileManager.saveDesign(currentDesign, fileName);

            // Make sure the design is properly updated in the app state
            // This ensures the current design reference stays valid
            Design savedDesign = FileManager.loadDesign(fileName);
            if (savedDesign != null) {
                this.currentDesign = savedDesign;
                setTitle(APP_TITLE + " - " + currentDesign.getName() + " - " + currentUserId);
            }

            SwingUtils.showInfoDialog(this, "Design saved successfully.");
        } catch (IOException | ClassNotFoundException e) {
            SwingUtils.showErrorDialog(this, "Error saving design: " + e.getMessage());
        }
    }

    private void saveDesignAs() {
        if (currentDesign == null) {
            SwingUtils.showErrorDialog(this, "No design is currently open.");
            return;
        }

        String newName = JOptionPane.showInputDialog(this,
                "Enter a name for your design:",
                currentDesign.getName());

        if (newName != null && !newName.trim().isEmpty()) {
            currentDesign.setName(newName);
            try {
                String fileName = newName.replaceAll("\\s+", "_").toLowerCase();
                FileManager.saveDesign(currentDesign, fileName);

                // Reload the design to ensure it's fresh
                Design savedDesign = FileManager.loadDesign(fileName);
                if (savedDesign != null) {
                    this.currentDesign = savedDesign;
                    setTitle(APP_TITLE + " - " + currentDesign.getName() + " - " + currentUserId);
                    notifyDesignChangeListeners();
                }

                SwingUtils.showInfoDialog(this, "Design saved successfully.");
            } catch (IOException | ClassNotFoundException e) {
                SwingUtils.showErrorDialog(this, "Error saving design: " + e.getMessage());
            }
        }
    }

    private void showPreferences() {
        SwingUtils.showInfoDialog(this, "Preferences feature is under development.");
    }

    private void showHelp() {
        SwingUtils.showInfoDialog(this, "Help contents feature is under development.");
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(
                this,
                "FurniView3D - Furniture Design Tool\n\n" +
                        "Version: 1.0.0\n" +
                        "© 2025 FurniView3D Team\n\n" +
                        "An interactive furniture design application " +
                        "developed for PUSL3122 HCI, Computer Graphics, and Visualization " +
                        "coursework.",
                "About FurniView3D",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void logout() {
        if (SwingUtils.showConfirmDialog(this, "Are you sure you want to log out?")) {
            dispose();
            instance = null;
            UserManager.getInstance().logout();

            SwingUtilities.invokeLater(() -> {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            });
        }
    }

    public JPanel getPanel(String panelName) {
        return panels.get(panelName);
    }

    public void addPanel(String panelName, JPanel panel) {
        panels.put(panelName, panel);
    }

    // Interface for design change notifications
    public interface DesignChangeListener {
        void onDesignChanged(Design design);
    }
}