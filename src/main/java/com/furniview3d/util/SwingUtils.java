package main.java.com.furniview3d.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class SwingUtils {
    // Application theme colors
    public static final Color PRIMARY_COLOR = new Color(2, 2, 54); // Dark navy blue (#020236)
    public static final Color SECONDARY_COLOR = new Color(201, 94, 0); // Orange (#C95E00)
    public static final Color BACKGROUND_COLOR = Color.WHITE;
    public static final Color FIELD_BACKGROUND = new Color(225, 225, 225); // Light gray for input fields
    public static final Color TEXT_COLOR = new Color(33, 33, 33); // Dark gray
    public static final Color LABEL_COLOR = Color.BLACK;

    // Hover and pressed colors
    public static final Color PRIMARY_HOVER = new Color(4, 4, 80); // Slightly lighter navy
    public static final Color PRIMARY_PRESSED = new Color(1, 1, 40); // Darker navy
    public static final Color SECONDARY_HOVER = new Color(228, 108, 10); // Lighter orange
    public static final Color SECONDARY_PRESSED = new Color(170, 80, 0); // Darker orange

    // Common font styles
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 12);

    // Common borders
    public static final Border PANEL_BORDER = new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(5, 5, 5, 5)
    );

    public static final Border FIELD_BORDER = new EmptyBorder(8, 8, 8, 8);

    /**
     * Centers a window on the screen
     */
    public static void centerWindow(Window window) {
        window.setLocationRelativeTo(null);
    }

    /**
     * Creates a primary button (navy background)
     */
    public static JButton createPrimaryButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(128, 40));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(PRIMARY_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_COLOR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(PRIMARY_PRESSED);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(PRIMARY_HOVER);
            }
        });

        if (actionListener != null) {
            button.addActionListener(actionListener);
        }

        return button;
    }

    /**
     * Creates a secondary button (orange background)
     */
    public static JButton createSecondaryButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setBackground(SECONDARY_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(128, 40));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(SECONDARY_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(SECONDARY_COLOR);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(SECONDARY_PRESSED);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                button.setBackground(SECONDARY_HOVER);
            }
        });

        if (actionListener != null) {
            button.addActionListener(actionListener);
        }

        return button;
    }

    /**
     * Creates a standardized text field
     */
    public static JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(BODY_FONT);
        textField.setBackground(FIELD_BACKGROUND);
        textField.setBorder(FIELD_BORDER);
        textField.setPreferredSize(new Dimension(250, 40));
        return textField;
    }

    /**
     * Creates a standardized password field
     */
    public static JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(BODY_FONT);
        passwordField.setBackground(FIELD_BACKGROUND);
        passwordField.setBorder(FIELD_BORDER);
        passwordField.setPreferredSize(new Dimension(250, 40));
        return passwordField;
    }

    /**
     * Creates a standardized label
     */
    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(BODY_FONT);
        label.setForeground(LABEL_COLOR);
        return label;
    }

    /**
     * Creates a standardized title label
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    /**
     * Creates a standardized subtitle label
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    /**
     * Creates a standardized panel
     */
    public static JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_COLOR);
        return panel;
    }

    /**
     * Sets the application look and feel to the system look and feel
     */
    public static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set system look and feel: " + e.getMessage());
        }
    }

    /**
     * Shows an error dialog
     */
    public static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Shows an information dialog
     */
    public static void showInfoDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(
                parent,
                message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Shows a confirmation dialog
     */
    public static boolean showConfirmDialog(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(
                parent,
                message,
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }

    public static Window getWindowAncestor(Component component) {
        return SwingUtilities.getWindowAncestor(component);
    }
}