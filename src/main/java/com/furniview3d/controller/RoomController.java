package main.java.com.furniview3d.controller;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.model.ColorScheme;
import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Room;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Controller class for handling room operations in the FurniView3D application
 */
public class RoomController {
    private Design currentDesign;
    private FurniView3DApp app; // Add reference to the app
    private List<RoomChangeListener> listeners = new ArrayList<>();

    // Available room shapes
    private static final List<String> ROOM_SHAPES = Arrays.asList(
            "Rectangular", "L-Shaped", "Square"
    );

    // Predefined color schemes
    private static final List<ColorScheme> PREDEFINED_COLOR_SCHEMES = Arrays.asList(
            ColorScheme.createModernScheme(),
            ColorScheme.createWarmScheme(),
            ColorScheme.createCoolScheme()
    );

    /**
     * Constructor with current design and app reference
     * @param design The current design containing the room to control
     * @param app The main application
     */
    public RoomController(Design design, FurniView3DApp app) {
        this.currentDesign = design;
        this.app = app;
    }

    /**
     * Gets the current room
     * @return The current room
     */
    public Room getCurrentRoom() {
        return currentDesign.getRoom();
    }

    /**
     * Sets the current design
     * @param design The design to set
     */
    public void setCurrentDesign(Design design) {
        this.currentDesign = design;
        notifyListeners();
    }

    /**
     * Updates the room dimensions
     * @param width Width in meters
     * @param length Length in meters
     * @param height Height in meters
     */
    public void updateRoomDimensions(double width, double length, double height) {
        Room room = currentDesign.getRoom();
        room.setWidth(width);
        room.setLength(length);
        room.setHeight(height);

        // Notify app that design has changed
        if (app != null) {
            app.setCurrentDesign(currentDesign);
        }

        notifyListeners();
    }

    /**
     * Updates the room shape
     * @param shape The new room shape
     */
    public void updateRoomShape(String shape) {
        if (!ROOM_SHAPES.contains(shape)) {
            throw new IllegalArgumentException("Invalid room shape: " + shape);
        }

        Room room = currentDesign.getRoom();
        room.setShape(shape);

        // Notify app that design has changed
        if (app != null) {
            app.setCurrentDesign(currentDesign);
        }

        notifyListeners();
    }

    /**
     * Updates the room name
     * @param name The new room name
     */
    public void updateRoomName(String name) {
        Room room = currentDesign.getRoom();
        room.setName(name);

        // Notify app that design has changed
        if (app != null) {
            app.setCurrentDesign(currentDesign);
        }

        notifyListeners();
    }

    /**
     * Sets a predefined color scheme for the room
     * @param schemeName The name of the predefined scheme
     * @return true if successful, false if scheme not found
     */
    public boolean setPredefinedColorScheme(String schemeName) {
        for (ColorScheme scheme : PREDEFINED_COLOR_SCHEMES) {
            if (scheme.getName().equalsIgnoreCase(schemeName)) {
                Room room = currentDesign.getRoom();
                room.setColorScheme(scheme);

                // Notify app that design has changed
                if (app != null) {
                    app.setCurrentDesign(currentDesign);
                }

                notifyListeners();
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the color scheme with custom colors
     * @param wallColor Wall color
     * @param floorColor Floor color
     * @param ceilingColor Ceiling color
     * @param accentColor Accent color
     */
    public void updateColorScheme(Color wallColor, Color floorColor, Color ceilingColor, Color accentColor) {
        Room room = currentDesign.getRoom();
        ColorScheme scheme = room.getColorScheme();
        scheme.setWallColor(wallColor);
        scheme.setFloorColor(floorColor);
        scheme.setCeilingColor(ceilingColor);
        scheme.setAccentColor(accentColor);

        // Notify app that design has changed
        if (app != null) {
            app.setCurrentDesign(currentDesign);
        }

        notifyListeners();
    }

    /**
     * Gets a list of available room shapes
     * @return List of room shape names
     */
    public List<String> getAvailableRoomShapes() {
        return new ArrayList<>(ROOM_SHAPES);
    }

    /**
     * Gets a list of predefined color schemes
     * @return List of predefined color schemes
     */
    public List<ColorScheme> getPredefinedColorSchemes() {
        return new ArrayList<>(PREDEFINED_COLOR_SCHEMES);
    }

    /**
     * Creates a new empty room with default values
     * @return The new room
     */
    public Room createNewRoom() {
        Room room = new Room();
        currentDesign.setRoom(room);

        // Notify app that design has changed
        if (app != null) {
            app.setCurrentDesign(currentDesign);
        }

        notifyListeners();
        return room;
    }

    /**
     * Validates room dimensions
     * @param width Width in meters
     * @param length Length in meters
     * @param height Height in meters
     * @return true if dimensions are valid, false otherwise
     */
    public boolean validateRoomDimensions(double width, double length, double height) {
        // Rooms must have positive dimensions
        if (width <= 0 || length <= 0 || height <= 0) {
            return false;
        }

        // Maximum dimensions for practical reasons (in meters)
        if (width > 50 || length > 50 || height > 10) {
            return false;
        }

        return true;
    }

    /**
     * Adds a room change listener
     * @param listener The listener to add
     */
    public void addRoomChangeListener(RoomChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a room change listener
     * @param listener The listener to remove
     */
    public void removeRoomChangeListener(RoomChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notifies all listeners of a room change
     */
    private void notifyListeners() {
        for (RoomChangeListener listener : listeners) {
            listener.onRoomChanged(currentDesign.getRoom());
        }
    }

    /**
     * Interface for room change listeners
     */
    public interface RoomChangeListener {
        void onRoomChanged(Room room);
    }
}