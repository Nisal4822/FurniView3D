package main.java.com.furniview3d.util;

import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.model.ColorScheme;

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

    // Create directory if it doesn't exist
    static {
        try {
            Path dirPath = Paths.get(DESIGNS_DIRECTORY);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
        } catch (IOException e) {
            System.err.println("Error creating designs directory: " + e.getMessage());
        }
    }

    /**
     * Saves a design to a file
     * @param design The design to save
     * @param filename The filename (without extension)
     * @throws IOException If an I/O error occurs
     */
    public static void saveDesign(Design design, String filename) throws IOException {
        if (design == null) {
            throw new IllegalArgumentException("Design cannot be null");
        }

        // Validate design contents before saving
        validateDesign(design);

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
            Design design = (Design) in.readObject();

            // Validate and fix any missing components
            if (design != null) {
                validateAndFixDesign(design);
            }

            return design;
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
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                System.err.println("Error creating designs directory: " + e.getMessage());
                return designList;
            }
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
        if (design == null) {
            throw new IllegalArgumentException("Design cannot be null");
        }

        // Validate design contents before exporting
        validateDesign(design);

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
            Design design = (Design) in.readObject();

            // Validate and fix any missing components
            if (design != null) {
                validateAndFixDesign(design);
            }

            return design;
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

    /**
     * Validates a design before saving
     * @param design The design to validate
     * @throws IllegalArgumentException If the design is invalid
     */
    private static void validateDesign(Design design) {
        if (design.getName() == null) {
            throw new IllegalArgumentException("Design name cannot be null");
        }

        if (design.getRoom() == null) {
            throw new IllegalArgumentException("Design must have a room");
        }

        if (design.getFurnitureList() == null) {
            throw new IllegalArgumentException("Design must have a furniture list (even if empty)");
        }
    }

    /**
     * Validates and fixes a design after loading
     * @param design The design to validate and fix
     */
    private static void validateAndFixDesign(Design design) {
        // Fix null room
        if (design.getRoom() == null) {
            Room room = new Room();
            room.setName("Default Room");
            design.setRoom(room);
        }

        // Fix null furniture list
        if (design.getFurnitureList() == null) {
            design.setFurnitureList(new ArrayList<>());
        }

        // Fix null room color scheme
        Room room = design.getRoom();
        if (room.getColorScheme() == null) {
            room.setColorScheme(new ColorScheme());
        }

        // Fix any furniture with null colors or materials
        for (Furniture furniture : design.getFurnitureList()) {
            if (furniture.getColor() == null) {
                furniture.setColor(java.awt.Color.GRAY);
            }

            if (furniture.getMaterial() == null) {
                furniture.setMaterial("Default");
            }
        }
    }
}