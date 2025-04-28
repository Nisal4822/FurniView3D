package main.java.com.furniview3d.controller;

import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.render.Renderer2D;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Design2DController {

    private Design currentDesign;
    private Renderer2D renderer;
    private Furniture selectedFurniture;
    private List<Design2DListener> listeners = new ArrayList<>();
    private double scaleFactor = 1.0;
    private int panX = 0;
    private int panY = 0;

    public Design2DController(Renderer2D renderer) {
        this.renderer = renderer;
    }

    public void setCurrentDesign(Design design) {
        this.currentDesign = design;
        this.selectedFurniture = null;

        if (renderer != null) {
            renderer.setDesign(design);
            renderer.setScale(scaleFactor);
            renderer.setPan(panX, panY);
            renderer.repaint();
        }

        notifyListeners();
    }

    public Design getCurrentDesign() {
        return currentDesign;
    }

    public void selectFurniture(Point point) {
        if (currentDesign == null || currentDesign.getFurnitureList().isEmpty()) {
            selectedFurniture = null;
            notifyListeners();
            return;
        }

        // Convert screen coordinates to model coordinates
        double roomWidth = currentDesign.getRoom().getWidth() * 100;
        double roomLength = currentDesign.getRoom().getLength() * 100;

        int screenCenterX = renderer.getWidth() / 2;
        int screenCenterY = renderer.getHeight() / 2;

        int roomTopLeftX = screenCenterX - (int)(roomWidth * scaleFactor / 2) + panX;
        int roomTopLeftY = screenCenterY - (int)(roomLength * scaleFactor / 2) + panY;

        // Check each furniture to see if point is within its bounds
        for (Furniture furniture : currentDesign.getFurnitureList()) {
            double furnitureX = furniture.getPosX() * 100;
            double furnitureY = furniture.getPosY() * 100;
            double furnitureWidth = furniture.getWidth() * 100;
            double furnitureLength = furniture.getLength() * 100;

            // Convert to screen coordinates
            int screenX = roomTopLeftX + (int)(furnitureX * scaleFactor);
            int screenY = roomTopLeftY + (int)(furnitureY * scaleFactor);
            int screenWidth = (int)(furnitureWidth * scaleFactor);
            int screenHeight = (int)(furnitureLength * scaleFactor);

            // Simple bounding box check
            if (point.x >= screenX && point.x <= screenX + screenWidth &&
                    point.y >= screenY && point.y <= screenY + screenHeight) {
                selectedFurniture = furniture;
                notifyListeners();
                return;
            }
        }

        // If no furniture was clicked, deselect
        selectedFurniture = null;
        notifyListeners();
    }

    public Furniture getSelectedFurniture() {
        return selectedFurniture;
    }

    public void addFurniture(Furniture furniture) {
        if (currentDesign == null) {
            return;
        }

        currentDesign.addFurniture(furniture);
        selectedFurniture = furniture;
        renderer.repaint();
        notifyListeners();
    }

    public void removeFurniture(String furnitureId) {
        if (currentDesign == null) {
            return;
        }

        if (selectedFurniture != null && selectedFurniture.getId().equals(furnitureId)) {
            selectedFurniture = null;
        }

        currentDesign.removeFurniture(furnitureId);
        renderer.repaint();
        notifyListeners();
    }

    public void moveFurniture(String furnitureId, double newX, double newY) {
        if (currentDesign == null) {
            return;
        }

        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setPosX(newX);
            furniture.setPosY(newY);
            renderer.repaint();
            notifyListeners();
        }
    }

    public void moveFurnitureRelative(String furnitureId, double deltaX, double deltaY) {
        if (currentDesign == null) {
            return;
        }

        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setPosX(furniture.getPosX() + deltaX);
            furniture.setPosY(furniture.getPosY() + deltaY);
            renderer.repaint();
            notifyListeners();
        }
    }

    public void rotateFurniture(String furnitureId, double angle) {
        if (currentDesign == null) {
            return;
        }

        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setRotation(angle);
            renderer.repaint();
            notifyListeners();
        }
    }

    public void changeFurnitureColor(String furnitureId, Color color) {
        if (currentDesign == null) {
            return;
        }

        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setColor(color);
            renderer.repaint();
            notifyListeners();
        }
    }

    public void zoomIn() {
        scaleFactor = Math.min(scaleFactor * 1.2, 5.0);
        renderer.setScale(scaleFactor);
        renderer.repaint();
    }

    public void zoomOut() {
        scaleFactor = Math.max(scaleFactor / 1.2, 0.2);
        renderer.setScale(scaleFactor);
        renderer.repaint();
    }

    public void pan(int deltaX, int deltaY) {
        panX += deltaX;
        panY += deltaY;
        renderer.setPan(panX, panY);
        renderer.repaint();
    }

    public void resetView() {
        scaleFactor = 1.0;
        panX = 0;
        panY = 0;
        renderer.setScale(scaleFactor);
        renderer.setPan(panX, panY);
        renderer.repaint();
    }

    public void toggleGrid() {
        renderer.toggleGrid();
    }

    public void toggleLabels() {
        renderer.toggleLabels();
    }

    public boolean isFurnitureWithinRoom(Furniture furniture) {
        if (currentDesign == null) {
            return false;
        }

        double roomWidth = currentDesign.getRoom().getWidth();
        double roomLength = currentDesign.getRoom().getLength();

        double furnitureX = furniture.getPosX();
        double furnitureY = furniture.getPosY();
        double furnitureWidth = furniture.getWidth();
        double furnitureLength = furniture.getLength();

        // Check if furniture is completely within room bounds
        return (furnitureX >= 0 &&
                furnitureY >= 0 &&
                furnitureX + furnitureWidth <= roomWidth &&
                furnitureY + furnitureLength <= roomLength);
    }

    public boolean checkFurnitureCollision(Furniture furniture) {
        if (currentDesign == null) {
            return false;
        }

        for (Furniture other : currentDesign.getFurnitureList()) {
            // Skip checking against itself
            if (other.getId().equals(furniture.getId())) {
                continue;
            }

            // Simplified collision check (bounding box)
            if (furniture.getPosX() < other.getPosX() + other.getWidth() &&
                    furniture.getPosX() + furniture.getWidth() > other.getPosX() &&
                    furniture.getPosY() < other.getPosY() + other.getLength() &&
                    furniture.getPosY() + furniture.getLength() > other.getPosY()) {
                return true;
            }
        }

        return false;
    }

    public void addListener(Design2DListener listener) {
        listeners.add(listener);
    }

    public void removeListener(Design2DListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (Design2DListener listener : listeners) {
            listener.onDesign2DChanged(currentDesign, selectedFurniture);
        }
    }

    public double getScale() {
        return scaleFactor;
    }

    public interface Design2DListener {
        void onDesign2DChanged(Design design, Furniture selectedFurniture);
    }
}