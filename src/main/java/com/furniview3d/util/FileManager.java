package main.java.com.furniview3d.util;

import main.java.com.furniview3d.model.Design;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for file operations in the FurniView3D application
 */
public class FileManager {
    private static final String DESIGNS_DIRECTORY = "designs";
    private static final String DESIGN_EXTENSION = ".fvd"; // FurniView Design

    /**
     * Saves a design to a file
     * @param design The design to save
     * @param filename The filename (without extension)
     * @throws IOException If an I/O error occurs
     */
    public static void saveDesign(Design design, String filename) throws IOException {
        // Create the designs directory if it doesn't exist
        Path dirPath = Paths.get(DESIGNS_DIRECTORY);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        String fullPath = DESIGNS_DIRECTORY + File.separator + filename + DESIGN_EXTENSION;

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fullPath))) {
            out.writeObject(design);
        }
    }

    /**
     * Loads a design from a file
     * @param filename The filename (without extension)
     * @return The loaded design, or null if loading fails
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of the serialized object cannot be found
     */
    public static Design loadDesign(String filename) throws IOException, ClassNotFoundException {
        String fullPath = DESIGNS_DIRECTORY + File.separator + filename + DESIGN_EXTENSION;

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fullPath))) {
            return (Design) in.readObject();
        }
    }

    /**
     * Gets a list of all saved designs
     * @return List of design filenames (without extension)
     */
    public static List<String> getDesignList() {
        List<String> designList = new ArrayList<>();

        Path dirPath = Paths.get(DESIGNS_DIRECTORY);
        if (!Files.exists(dirPath)) {
            return designList; // Return empty list if directory doesn't exist
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*" + DESIGN_EXTENSION)) {
            for (Path path : stream) {
                String filename = path.getFileName().toString();
                // Remove extension
                designList.add(filename.substring(0, filename.lastIndexOf(DESIGN_EXTENSION)));
            }
        } catch (IOException e) {
            System.err.println("Error listing designs: " + e.getMessage());
        }

        return designList;
    }

    /**
     * Deletes a design file
     * @param filename The filename (without extension)
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteDesign(String filename) {
        String fullPath = DESIGNS_DIRECTORY + File.separator + filename + DESIGN_EXTENSION;

        try {
            return Files.deleteIfExists(Paths.get(fullPath));
        } catch (IOException e) {
            System.err.println("Error deleting design: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exports a design to a specified path
     * @param design The design to export
     * @param path The full path to export to
     * @throws IOException If an I/O error occurs
     */
    public static void exportDesign(Design design, String path) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
            out.writeObject(design);
        }
    }

    /**
     * Imports a design from a specified path
     * @param path The full path to import from
     * @return The imported design
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of the serialized object cannot be found
     */
    public static Design importDesign(String path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
            return (Design) in.readObject();
        }
    }

    /**
     * Checks if a design file exists
     * @param filename The filename (without extension)
     * @return true if the file exists, false otherwise
     */
    public static boolean designExists(String filename) {
        String fullPath = DESIGNS_DIRECTORY + File.separator + filename + DESIGN_EXTENSION;
        return Files.exists(Paths.get(fullPath));
    }

    /**
     * Creates a backup of all designs
     * @param backupPath The path to save the backup to
     * @throws IOException If an I/O error occurs
     */
    public static void backupDesigns(String backupPath) throws IOException {
        Path designsDir = Paths.get(DESIGNS_DIRECTORY);
        if (!Files.exists(designsDir)) {
            return; // Nothing to backup
        }

        Path backupDir = Paths.get(backupPath);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(designsDir, "*" + DESIGN_EXTENSION)) {
            for (Path path : stream) {
                Path target = Paths.get(backupDir.toString(), path.getFileName().toString());
                Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }
}