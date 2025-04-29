package main.java.com.furniview3d.auth;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.util.SwingUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginForm extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel statusLabel;

    public LoginForm() {
        // Basic frame setup
        setTitle("FurniView3D - Login");
        setSize(500, 380);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SwingUtils.centerWindow(this);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        mainPanel.setBackground(SwingUtils.BACKGROUND_COLOR);

        // Logo and title
        JLabel titleLabel = SwingUtils.createTitleLabel("FurniView3D");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = SwingUtils.createSubtitleLabel("Furniture Design Tool");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        formPanel.setBackground(SwingUtils.BACKGROUND_COLOR);
        formPanel.setBorder(new EmptyBorder(30, 0, 30, 0));

        // Username row
        JPanel usernameRow = new JPanel(new GridLayout(1, 2, 10, 5));
        usernameRow.setBackground(SwingUtils.BACKGROUND_COLOR);

        JLabel usernameLabel = SwingUtils.createLabel("User Name");
        usernameField = SwingUtils.createTextField();

        usernameRow.add(usernameLabel);
        usernameRow.add(usernameField);

        // Password row
        JPanel passwordRow = new JPanel(new GridLayout(1, 2, 10, 0));
        passwordRow.setBackground(SwingUtils.BACKGROUND_COLOR);

        JLabel passwordLabel = SwingUtils.createLabel("Password");
        passwordField = SwingUtils.createPasswordField();

        // Add key listener for Enter key
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        });

        passwordRow.add(passwordLabel);
        passwordRow.add(passwordField);

        // Status label
        statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(SwingUtils.SMALL_FONT);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 30, 5));
        buttonPanel.setBackground(SwingUtils.BACKGROUND_COLOR);

        JButton loginButton = SwingUtils.createPrimaryButton("Login", e -> attemptLogin());
        JButton registerButton = SwingUtils.createSecondaryButton("Register", e -> showRegistrationDialog());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        // Add to form panel
        formPanel.add(usernameRow);
        formPanel.add(passwordRow);
        formPanel.add(statusLabel);
        formPanel.add(buttonPanel);

        // Add all components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(formPanel);

        setContentPane(mainPanel);
    }

    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            return;
        }

        if (UserManager.getInstance().login(username, password)) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                FurniView3DApp app = FurniView3DApp.getInstance(username);
                app.setVisible(true);
            });
        } else {
            statusLabel.setText("Invalid username or password");
            passwordField.setText("");
        }
    }

    private void showRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register New User", true);
        dialog.setSize(400, 250);
        SwingUtils.centerWindow(dialog);

        JPanel panel = SwingUtils.createPanel();
        panel.setLayout(new GridLayout(4, 2, 10, 15));
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel usernameLabel = SwingUtils.createLabel("Username:");
        JTextField usernameField = SwingUtils.createTextField();

        JLabel passwordLabel = SwingUtils.createLabel("Password:");
        JPasswordField passwordField = SwingUtils.createPasswordField();

        JLabel confirmLabel = SwingUtils.createLabel("Confirm Password:");
        JPasswordField confirmField = SwingUtils.createPasswordField();

        JLabel statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setFont(SwingUtils.SMALL_FONT);

        JButton registerButton = SwingUtils.createPrimaryButton("Register", e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String confirm = new String(confirmField.getPassword());

            if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                statusLabel.setText("All fields are required");
                return;
            }

            if (!password.equals(confirm)) {
                statusLabel.setText("Passwords do not match");
                return;
            }

            if (UserManager.getInstance().registerUser(username, password)) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Registration successful! You can now log in.",
                        "Registration Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );
                dialog.dispose();
            } else {
                statusLabel.setText("Username already exists");
            }
        });

        panel.add(usernameLabel);
        panel.add(usernameField);
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(confirmLabel);
        panel.add(confirmField);
        panel.add(statusLabel);
        panel.add(registerButton);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }
}