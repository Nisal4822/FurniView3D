package main.java.com.furniview3d.controller;

import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.render.Renderer3D;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Design3DController {

    private Design currentDesign;
    private Renderer3D renderer;
    private Furniture selectedFurniture;
    private List<Design3DListener> listeners = new ArrayList<>();
    private boolean autoRotate = false;

    public Design3DController(Renderer3D renderer) {
        this.renderer = renderer;
    }

    public void setCurrentDesign(Design design) {
        this.currentDesign = design;
        this.selectedFurniture = null;

        if (renderer != null) {
            renderer.setDesign(design);
        }

        notifyListeners();
    }

    public Design getCurrentDesign() {
        return currentDesign;
    }

    public void selectFurniture(String furnitureId) {
        if (currentDesign == null) {
            selectedFurniture = null;
            notifyListeners();
            return;
        }

        selectedFurniture = currentDesign.getFurnitureById(furnitureId);
        notifyListeners();
    }

    public Furniture getSelectedFurniture() {
        return selectedFurniture;
    }

    public void resetView() {
        if (renderer != null) {
            renderer.resetView();
        }
    }

    public void rotateView(double xAngle, double yAngle) {
        if (renderer != null) {
            renderer.rotateView(xAngle, yAngle);
        }
    }

    public void toggleAutoRotate() {
        autoRotate = !autoRotate;
        // Auto-rotation would be implemented in the renderer
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
        if (currentDesign == null) {
            return;
        }

        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setColor(color);

            // Update the 3D view by refreshing the design
            if (renderer != null) {
                renderer.setDesign(currentDesign);
            }

            notifyListeners();
        }
    }

    public void scaleFurniture(String furnitureId, double scaleX, double scaleY, double scaleZ) {
        if (currentDesign == null) {
            return;
        }

        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setWidth(furniture.getWidth() * scaleX);
            furniture.setLength(furniture.getLength() * scaleY);
            furniture.setHeight(furniture.getHeight() * scaleZ);

            // Update the 3D view
            if (renderer != null) {
                renderer.setDesign(currentDesign);
            }

            notifyListeners();
        }
    }

    public void scaleFurnitureUniform(String furnitureId, double scale) {
        scaleFurniture(furnitureId, scale, scale, scale);
    }

    public void moveFurniture(String furnitureId, double newX, double newY) {
        if (currentDesign == null) {
            return;
        }

        Furniture furniture = currentDesign.getFurnitureById(furnitureId);
        if (furniture != null) {
            furniture.setPosX(newX);
            furniture.setPosY(newY);

            // Update the 3D view
            if (renderer != null) {
                renderer.setDesign(currentDesign);
            }

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

            // Update the 3D view
            if (renderer != null) {
                renderer.setDesign(currentDesign);
            }

            notifyListeners();
        }
    }

    public void takeScreenshot() {
        // In a full implementation, this would capture the current 3D view
        // and save it as an image file
        System.out.println("Screenshot feature not implemented yet");
    }

    public void exportTo3DModel(String filePath, String format) {
        // In a full implementation, this would export the current design
        // to a 3D model format like OBJ, FBX, etc.
        System.out.println("Export to 3D model not implemented yet");
    }

    public void toggleWireframe() {
        // In a full implementation, this would toggle between solid and wireframe view
        System.out.println("Wireframe toggle not implemented yet");
    }

    public void setLightingIntensity(double intensity) {
        // In a full implementation, this would adjust the lighting in the 3D scene
        System.out.println("Lighting intensity adjustment not implemented yet");
    }

    public void addListener(Design3DListener listener) {
        listeners.add(listener);
    }

    public void removeListener(Design3DListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (Design3DListener listener : listeners) {
            listener.onDesign3DChanged(currentDesign, selectedFurniture);
        }
    }

    public interface Design3DListener {
        void onDesign3DChanged(Design design, Furniture selectedFurniture);
    }
}