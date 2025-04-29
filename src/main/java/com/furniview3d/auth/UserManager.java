package main.java.com.furniview3d.auth;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages user authentication for the FurniView3D application
 */
public class UserManager {
    private static final String USER_FILE = "users.dat";
    private Map<String, String> users; // username -> hashed password
    private String currentUser;
    private static UserManager instance;

    // Private constructor for singleton pattern
    private UserManager() {
        users = new HashMap<>();
        currentUser = null;
        loadUsers();
    }

    // Singleton instance getter
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    /**
     * Registers a new user
     * @param username Username
     * @param password Password
     * @return true if registration successful, false if username already exists
     */
    public boolean registerUser(String username, String password) {
        if (users.containsKey(username)) {
            return false; // Username already exists
        }

        String hashedPassword = hashPassword(password);
        users.put(username, hashedPassword);
        saveUsers();
        return true;
    }

    /**
     * Authenticates a user
     * @param username Username
     * @param password Password
     * @return true if authentication successful, false otherwise
     */
    public boolean login(String username, String password) {
        if (!users.containsKey(username)) {
            return false; // User doesn't exist
        }

        String hashedPassword = hashPassword(password);
        if (users.get(username).equals(hashedPassword)) {
            currentUser = username;
            return true;
        }
        return false;
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Gets the current logged-in user
     * @return Username of current user, or null if no user is logged in
     */
    public String getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Saves users to file
     */
    private void saveUsers() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            out.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    /**
     * Loads users from file
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        File file = new File(USER_FILE);
        if (!file.exists()) {
            // Create default admin user if file doesn't exist
            registerUser("admin", "admin123");
            return;
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            users = (Map<String, String>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading users: " + e.getMessage());
            // Create default admin user if there was an error loading the file
            registerUser("admin", "admin123");
        }
    }

    /**
     * Hashes a password using SHA-256
     * @param password The password to hash
     * @return The hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Fall back to plain text if hashing fails
            System.err.println("Error hashing password: " + e.getMessage());
            return password;
        }
    }
}