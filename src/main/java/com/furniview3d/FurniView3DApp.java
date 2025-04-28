package main.java.com.furniview3d;

import main.java.com.furniview3d.auth.LoginForm;
import main.java.com.furniview3d.auth.UserManager;
import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.ui.dashboard.DashboardPanel;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * The main application class for FurniView3D that manages the application lifecycle
 * and provides navigation between different UI components.
 */
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

    /**
     * Private constructor for singleton pattern
     * @param username The logged-in username
     */
    private FurniView3DApp(String username) {
        this.currentUserId = username;
        this.panels = new HashMap<>();

        // Set up the main frame
        initializeFrame();

        // Create the UI components
        createPanels();

        // Create the menu bar
        createMenuBar();

        // Show the dashboard initially
        showPanel("dashboard");
    }

    /**
     * Gets the singleton instance or creates it if it doesn't exist
     * @param username The logged-in username
     * @return The application instance
     */
    public static FurniView3DApp getInstance(String username) {
        if (instance == null) {
            instance = new FurniView3DApp(username);
        }
        return instance;
    }

    /**
     * Gets the singleton instance
     * @return The application instance, or null if not initialized
     */
    public static FurniView3DApp getInstance() {
        return instance;
    }

    /**
     * Shuts down the application
     */
    public static void shutdown() {
        if (instance != null) {
            instance.dispose();
            instance = null;
        }
    }

    /**
     * Initializes the main application frame
     */
    private void initializeFrame() {
        setTitle(APP_TITLE + " - " + currentUserId);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // Center on screen
        SwingUtils.centerWindow(this);

        // Handle window closing
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

        // Set the content panel with BorderLayout
        contentPanel = new JPanel(new BorderLayout());
        setContentPane(contentPanel);
    }

    /**
     * Creates all the panels for the application
     */
    private void createPanels() {
        // Add the dashboard panel
        DashboardPanel dashboardPanel = new DashboardPanel(this);
        panels.put("dashboard", dashboardPanel);

        // Other panels will be added as they are needed
        // This is a lazy initialization approach to improve startup time
    }

    /**
     * Creates the menu bar for the application
     */
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

        // Placeholder actions
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
        JMenuItem designViewItem = new JMenuItem("Design View");
        JMenuItem toggle2D3DItem = new JMenuItem("Toggle 2D/3D View");

        dashboardItem.addActionListener(e -> showPanel("dashboard"));
        designViewItem.setEnabled(false);
        toggle2D3DItem.setEnabled(false);

        viewMenu.add(dashboardItem);
        viewMenu.add(designViewItem);
        viewMenu.addSeparator();
        viewMenu.add(toggle2D3DItem);

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
        JMenu userMenu = new JMenu(currentUserId);
        JMenuItem logoutItem = new JMenuItem("Log Out");

        logoutItem.addActionListener(e -> logout());

        userMenu.add(logoutItem);

        // Add spring to push user menu to the right
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(userMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Shows a specific panel
     * @param panelName The name of the panel to show
     */
    public void showPanel(String panelName) {
        // First remove all components
        contentPanel.removeAll();

        // Get the requested panel
        JPanel panel = panels.get(panelName);

        if (panel != null) {
            // Add the panel to the content panel
            contentPanel.add(panel, BorderLayout.CENTER);

            // Refresh the display
            contentPanel.revalidate();
            contentPanel.repaint();
        } else {
            System.err.println("Panel not found: " + panelName);
        }
    }

    /**
     * Gets the current design
     * @return The current design
     */
    public Design getCurrentDesign() {
        return currentDesign;
    }

    /**
     * Sets the current design
     * @param design The design to set as current
     */
    public void setCurrentDesign(Design design) {
        this.currentDesign = design;

        // Update window title to include design name
        if (design != null) {
            setTitle(APP_TITLE + " - " + design.getName() + " - " + currentUserId);
        } else {
            setTitle(APP_TITLE + " - " + currentUserId);
        }
    }

    /**
     * Gets the current user ID
     * @return The current user ID
     */
    public String getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Creates a new design
     */
    private void createNewDesign() {
        // Check if there are unsaved changes
        if (currentDesign != null) {
            if (!SwingUtils.showConfirmDialog(this,
                    "You have an open design. Create a new one?")) {
                return;
            }
        }

        // Create a new design with default settings
        Design newDesign = new Design();
        newDesign.setName("Untitled Design");
        newDesign.setDescription("New design created by " + currentUserId);
        newDesign.setDesignerId(currentUserId);
        newDesign.setRoom(new Room());

        // Set as current design
        setCurrentDesign(newDesign);

        // TODO: Navigate to the design view
        SwingUtils.showInfoDialog(this, "New design created. This feature is under development.");
    }

    /**
     * Opens an existing design
     */
    private void openDesign() {
        // TODO: Implement opening a design
        SwingUtils.showInfoDialog(this, "Open design feature is under development.");
    }

    /**
     * Saves the current design
     */
    private void saveDesign() {
        if (currentDesign == null) {
            SwingUtils.showErrorDialog(this, "No design is currently open.");
            return;
        }

        // TODO: Implement saving a design
        SwingUtils.showInfoDialog(this, "Save design feature is under development.");
    }

    /**
     * Saves the current design with a new name
     */
    private void saveDesignAs() {
        if (currentDesign == null) {
            SwingUtils.showErrorDialog(this, "No design is currently open.");
            return;
        }

        // TODO: Implement saving a design with a new name
        SwingUtils.showInfoDialog(this, "Save design as feature is under development.");
    }

    /**
     * Shows the preferences dialog
     */
    private void showPreferences() {
        // TODO: Implement preferences dialog
        SwingUtils.showInfoDialog(this, "Preferences feature is under development.");
    }

    /**
     * Shows the help dialog
     */
    private void showHelp() {
        // TODO: Implement help dialog
        SwingUtils.showInfoDialog(this, "Help contents feature is under development.");
    }

    /**
     * Shows the about dialog
     */
    private void showAbout() {
        JOptionPane.showMessageDialog(
                this,
                "FurniView3D - Furniture Design Tool\n\n" +
                        "Version: 1.0.0\n" +
                        "© 2025 FurniView3D Team\n\n" +
                        "An interactive furniture design application developed for PUSL3122 HCI, Computer Graphics, and Visualization coursework.",
                "About FurniView3D",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Logs out the current user
     */
    private void logout() {
        if (SwingUtils.showConfirmDialog(this, "Are you sure you want to log out?")) {
            // Close the application window
            dispose();

            // Reset the singleton instance
            instance = null;

            // Log out the user
            UserManager.getInstance().logout();

            // Show the login form again
            SwingUtilities.invokeLater(() -> {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            });
        }
    }

    /**
     * Gets a specific panel by name
     * @param panelName The name of the panel to get
     * @return The panel, or null if not found
     */
    public JPanel getPanel(String panelName) {
        return panels.get(panelName);
    }

    /**
     * Adds a panel to the application
     * @param panelName The name of the panel
     * @param panel The panel to add
     */
    public void addPanel(String panelName, JPanel panel) {
        panels.put(panelName, panel);
    }
}