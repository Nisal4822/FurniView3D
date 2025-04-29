package main.java.com.furniview3d.controller;

import main.java.com.furniview3d.FurniView3DApp;
import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.model.Room;
import main.java.com.furniview3d.render.Renderer3D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Design3DController {
    private Design currentDesign;
    private Renderer3D renderer;
    private Furniture selectedFurniture;
    private List<Design3DListener> listeners = new ArrayList<>();
    private FurniView3DApp app; // Add reference to the app
    private boolean autoRotate = false;
    private double currentRotationX = -20;
    private double currentRotationY = -20;
    private Map<String, Boolean> furnitureVisibility = new HashMap<>();

    public Design3DController(Renderer3D renderer, FurniView3DApp app) {
        this.renderer = renderer;
        this.app = app;
    }

    public void setCurrentDesign(Design design) {
        try {
            this.currentDesign = design;
            this.selectedFurniture = null;

            // Reset furniture visibility
            furnitureVisibility.clear();

            // Set all furniture visible by default
            if (design != null && design.getFurnitureList() != null) {
                for (Furniture furniture : design.getFurnitureList()) {
                    if (furniture != null) {
                        furnitureVisibility.put(furniture.getId(), true);
                    }
                }
            }

            // Update the renderer
            if (renderer != null) {
                renderer.setDesign(design);
            }

            notifyListeners();
        } catch (Exception e) {
            System.err.println("Error in setCurrentDesign: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Design getCurrentDesign() {
        return currentDesign;
    }

    public void selectFurniture(String furnitureId) {
        try {
            if (currentDesign == null) {
                selectedFurniture = null;
                notifyListeners();
                return;
            }

            // Find furniture by ID
            selectedFurniture = null;
            for (Furniture furniture : currentDesign.getFurnitureList()) {
                if (furniture.getId().equals(furnitureId)) {
                    selectedFurniture = furniture;
                    break;
                }
            }

            notifyListeners();

            // Update app that selection changed
            if (app != null) {
                app.setCurrentDesign(currentDesign);
            }
        } catch (Exception e) {
            System.err.println("Error in selectFurniture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Furniture getSelectedFurniture() {
        return selectedFurniture;
    }

    public void resetView() {
        if (renderer != null) {
            currentRotationX = -20;
            currentRotationY = -20;
            renderer.resetView();
        }
    }

    public void rotateView(double xAngle, double yAngle) {
        if (renderer != null) {
            currentRotationX = xAngle;
            currentRotationY = yAngle;
            renderer.rotateView(xAngle, yAngle);
        }
    }

    public double getCurrentRotationX() {
        return currentRotationX;
    }

    public double getCurrentRotationY() {
        return currentRotationY;
    }

    public void toggleAutoRotate() {
        autoRotate = !autoRotate;
        // TODO: Implement auto-rotation in the Renderer3D class
        if (autoRotate) {
            // Start auto-rotation
            System.out.println("Auto-rotation started");
        } else {
            // Stop auto-rotation
            System.out.println("Auto-rotation stopped");
        }
    }

    public boolean isAutoRotateEnabled() {
        return autoRotate;
    }

    public void applyShading(String furnitureId, double intensity) {
        if (renderer != null) {
            renderer.applyShading(furnitureId, intensity);
        }
    }

    public void changeFurnitureColor(String furnitureId, Color color) {
        try {
            if (currentDesign == null) {
                return;
            }

            Furniture furniture = findFurnitureById(furnitureId);
            if (furniture != null && color != null) {
                furniture.setColor(color);

                // Update the 3D view by refreshing the design
                if (renderer != null) {
                    renderer.setDesign(currentDesign);
                }

                // Notify app that design changed
                if (app != null) {
                    app.setCurrentDesign(currentDesign);
                }

                notifyListeners();
            }
        } catch (Exception e) {
            System.err.println("Error in changeFurnitureColor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void scaleFurniture(String furnitureId, double scaleX, double scaleY, double scaleZ) {
        try {
            if (currentDesign == null) {
                return;
            }

            Furniture furniture = findFurnitureById(furnitureId);
            if (furniture != null) {
                // Store original dimensions for scaling
                double originalWidth = furniture.getWidth();
                double originalLength = furniture.getLength();
                double originalHeight = furniture.getHeight();

                // Apply scaling with minimum size check
                furniture.setWidth(Math.max(0.1, originalWidth * scaleX));
                furniture.setLength(Math.max(0.1, originalLength * scaleY));
                furniture.setHeight(Math.max(0.1, originalHeight * scaleZ));

                // Update the 3D view
                if (renderer != null) {
                    renderer.setDesign(currentDesign);
                }

                // Notify app that design changed
                if (app != null) {
                    app.setCurrentDesign(currentDesign);
                }

                notifyListeners();
            }
        } catch (Exception e) {
            System.err.println("Error in scaleFurniture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void scaleFurnitureUniform(String furnitureId, double scale) {
        scaleFurniture(furnitureId, scale, scale, scale);
    }

    public void moveFurniture(String furnitureId, double newX, double newY) {
        try {
            if (currentDesign == null) {
                return;
            }

            Furniture furniture = findFurnitureById(furnitureId);
            if (furniture != null) {
                // Apply constraints to keep furniture within room
                Room room = currentDesign.getRoom();
                if (room != null) {
                    // Limit X position
                    newX = Math.max(0, Math.min(newX, room.getWidth() - furniture.getWidth()));
                    // Limit Y position
                    newY = Math.max(0, Math.min(newY, room.getLength() - furniture.getLength()));
                }

                furniture.setPosX(newX);
                furniture.setPosY(newY);

                // Update the 3D view
                if (renderer != null) {
                    renderer.setDesign(currentDesign);
                }

                // Notify app that design changed
                if (app != null) {
                    app.setCurrentDesign(currentDesign);
                }

                notifyListeners();
            }
        } catch (Exception e) {
            System.err.println("Error in moveFurniture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void rotateFurniture(String furnitureId, double angle) {
        try {
            if (currentDesign == null) {
                return;
            }

            Furniture furniture = findFurnitureById(furnitureId);
            if (furniture != null) {
                furniture.setRotation(angle);

                // Update the 3D view
                if (renderer != null) {
                    renderer.setDesign(currentDesign);
                }

                // Notify app that design changed
                if (app != null) {
                    app.setCurrentDesign(currentDesign);
                }

                notifyListeners();
            }
        } catch (Exception e) {
            System.err.println("Error in rotateFurniture: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setFurnitureVisibility(String furnitureId, boolean visible) {
        furnitureVisibility.put(furnitureId, visible);

        // Update renderer to respect visibility settings
        if (renderer != null) {
            renderer.setFurnitureVisibility(furnitureId, visible);
        }
    }

    public boolean isFurnitureVisible(String furnitureId) {
        Boolean visible = furnitureVisibility.get(furnitureId);
        return visible == null || visible; // Default to visible if not set
    }

    /**
     * Helper method to find furniture by ID
     */
    private Furniture findFurnitureById(String furnitureId) {
        if (currentDesign == null || currentDesign.getFurnitureList() == null) {
            return null;
        }

        for (Furniture furniture : currentDesign.getFurnitureList()) {
            if (furniture != null && furniture.getId().equals(furnitureId)) {
                return furniture;
            }
        }

        return null;
    }

    public void addListener(Design3DListener listener) {
        listeners.add(listener);
    }

    public void removeListener(Design3DListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (Design3DListener listener : listeners) {
            if (listener != null) {
                listener.onDesign3DChanged(currentDesign, selectedFurniture);
            }
        }
    }

    public interface Design3DListener {
        void onDesign3DChanged(Design design, Furniture selectedFurniture);
    }
}