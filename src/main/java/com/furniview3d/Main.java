package main.java.com.furniview3d;

import main.java.com.furniview3d.auth.LoginForm;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;

/**
 * The main entry point for the FurniView3D application.
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting FurniView3D application...");

        // Set system look and feel
        SwingUtils.setSystemLookAndFeel();

        // Launch the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // First show the login form
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(
                        null,
                        "Error starting application: " + e.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}

