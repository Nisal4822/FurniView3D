package main.java.com.furniview3d.controller;

import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.util.FileManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for managing designs in the FurniView3D application
 */
public class DesignController {

    private Design currentDesign;
    private List<DesignChangeListener> listeners = new ArrayList<>();
    private String lastSavedFileName;

    /**
     * Constructor with current design
     * @param design The current design to control
     */
    public DesignController(Design design) {
        this.currentDesign = design;
        this.lastSavedFileName = null;
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
     * @param design The design to set
     */
    public void setCurrentDesign(Design design) {
        this.currentDesign = design;
        this.lastSavedFileName = null; // Reset last saved filename
        notifyListeners();
    }

    /**
     * Creates a new empty design
     * @param designerId The ID of the designer creating the design
     * @return The new design
     */
    public Design createNewDesign(String designerId) {
        Design design = new Design();
        design.setName("Untitled Design");
        design.setDescription("New design created by " + designerId);
        design.setDesignerId(designerId);
        design.setRoom(new Room());

        setCurrentDesign(design);
        return design;
    }

    /**
     * Updates the design name
     * @param name The new design name
     */
    public void updateDesignName(String name) {
        currentDesign.setName(name);
        notifyListeners();
    }

    /**
     * Updates the design description
     * @param description The new design description
     */
    public void updateDesignDescription(String description) {
        currentDesign.setDescription(description);
        notifyListeners();
    }

    /**
     * Adds furniture to the design
     * @param furniture The furniture to add
     */
    public void addFurniture(Furniture furniture) {
        currentDesign.addFurniture(furniture);
        notifyListeners();
    }

    /**
     * Removes furniture from the design
     * @param furnitureId The ID of the furniture to remove
     * @return true if furniture was removed, false if not found
     */
    public boolean removeFurniture(String furnitureId) {
        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            currentDesign.removeFurniture(furniture);
            notifyListeners();
            return true;
        }
        return false;
    }

    /**
     * Gets all furniture in the design
     * @return List of furniture items
     */
    public List<Furniture> getAllFurniture() {
        return currentDesign.getFurnitureList();
    }

    /**
     * Updates furniture position
     * @param furnitureId The ID of the furniture to update
     * @param posX The new X position
     * @param posY The new Y position
     * @return true if furniture was updated, false if not found
     */
    public boolean updateFurniturePosition(String furnitureId, double posX, double posY) {
        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setPosX(posX);
            furniture.setPosY(posY);
            notifyListeners();
            return true;
        }
        return false;
    }

    /**
     * Updates furniture rotation
     * @param furnitureId The ID of the furniture to update
     * @param rotation The new rotation angle in degrees
     * @return true if furniture was updated, false if not found
     */
    public boolean updateFurnitureRotation(String furnitureId, double rotation) {
        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setRotation(rotation);
            notifyListeners();
            return true;
        }
        return false;
    }

    /**
     * Saves the current design
     * @param fileName The filename to save as (without extension)
     * @throws IOException If an I/O error occurs
     */
    public void saveDesign(String fileName) throws IOException {
        FileManager.saveDesign(currentDesign, fileName);
        lastSavedFileName = fileName;
    }

    /**
     * Saves the current design with the last used filename
     * @throws IOException If an I/O error occurs
     * @throws IllegalStateException If no previous filename exists
     */
    public void saveDesign() throws IOException, IllegalStateException {
        if (lastSavedFileName == null) {
            throw new IllegalStateException("No previous filename exists. Use saveDesign(String) instead.");
        }
        saveDesign(lastSavedFileName);
    }

    /**
     * Loads a design
     * @param fileName The filename to load (without extension)
     * @return The loaded design
     * @throws IOException If an I/O error occurs
     * @throws ClassNotFoundException If the class of the serialized object cannot be found
     */
    public Design loadDesign(String fileName) throws IOException, ClassNotFoundException {
        Design design = FileManager.loadDesign(fileName);
        setCurrentDesign(design);
        lastSavedFileName = fileName;
        return design;
    }

    /**
     * Gets a list of all saved designs
     * @return List of design filenames (without extension)
     */
    public List<String> getSavedDesigns() {
        return FileManager.getDesignList();
    }

    /**
     * Checks if a design with the given name exists
     * @param fileName The filename to check (without extension)
     * @return true if the design exists, false otherwise
     */
    public boolean designExists(String fileName) {
        return FileManager.designExists(fileName);
    }

    /**
     * Gets the last saved filename
     * @return The last saved filename, or null if not saved
     */
    public String getLastSavedFileName() {
        return lastSavedFileName;
    }

    /**
     * Gets the current design creation date
     * @return The creation date and time
     */
    public LocalDateTime getDesignCreationDate() {
        return currentDesign.getCreatedAt();
    }

    /**
     * Gets the current design last modified date
     * @return The last modified date and time
     */
    public LocalDateTime getDesignLastModifiedDate() {
        return currentDesign.getLastModified();
    }

    /**
     * Gets the designer ID for the current design
     * @return The designer ID
     */
    public String getDesignerId() {
        return currentDesign.getDesignerId();
    }

    /**
     * Checks if the design has been modified since last save
     * @return true if the design has been modified, false otherwise
     */
    public boolean isDesignModified() {
        if (lastSavedFileName == null) {
            return true; // Not saved yet
        }

        try {
            Design savedDesign = FileManager.loadDesign(lastSavedFileName);
            return !savedDesign.getLastModified().equals(currentDesign.getLastModified());
        } catch (Exception e) {
            return true; // Error loading saved design, assume modified
        }
    }

    /**
     * Adds a design change listener
     * @param listener The listener to add
     */
    public void addDesignChangeListener(DesignChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a design change listener
     * @param listener The listener to remove
     */
    public void removeDesignChangeListener(DesignChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners of a design change
     */
    private void notifyListeners() {
        for (DesignChangeListener listener : listeners) {
            listener.onDesignChanged(currentDesign);
        }
    }

    /**
     * Interface for design change listeners
     */
    public interface DesignChangeListener {
        void onDesignChanged(Design design);
    }
}