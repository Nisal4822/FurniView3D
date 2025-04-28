package main.java.com.furniview3d.controller;

import main.java.com.furniview3d.model.Furniture;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnitureController {

    private static final String CATALOG_FILE = "furniture_catalog.dat";
    private List<Furniture> furnitureCatalog;
    private Map<String, List<Furniture>> furnitureByType;
    private List<FurnitureChangeListener> listeners;

    // Default furniture types
    private static final String[] DEFAULT_TYPES = {
            "Chair", "Table", "Sofa", "Bed", "Cabinet", "Desk", "Bookshelf", "Lamp"
    };

    public FurnitureController() {
        furnitureCatalog = new ArrayList<>();
        furnitureByType = new HashMap<>();
        listeners = new ArrayList<>();

        // Initialize the type lists
        for (String type : DEFAULT_TYPES) {
            furnitureByType.put(type, new ArrayList<>());
        }

        // Load the furniture catalog from file
        loadCatalog();

        // If catalog is empty, initialize with default furniture
        if (furnitureCatalog.isEmpty()) {
            initializeDefaultFurniture();
        }
    }

    private void initializeDefaultFurniture() {
        // Add some default furniture to the catalog

        // Chairs
        addFurniture(createFurniture("Dining Chair", "Chair", 0.5, 0.5, 0.9,
                new Color(120, 81, 45), "wood"));
        addFurniture(createFurniture("Office Chair", "Chair", 0.6, 0.6, 1.0,
                new Color(40, 40, 40), "leather"));

        // Tables
        addFurniture(createFurniture("Dining Table", "Table", 1.6, 0.9, 0.75,
                new Color(110, 85, 60), "wood"));
        addFurniture(createFurniture("Coffee Table", "Table", 1.2, 0.6, 0.45,
                new Color(100, 70, 40), "wood"));

        // Sofas
        addFurniture(createFurniture("3-Seater Sofa", "Sofa", 2.0, 0.9, 0.8,
                new Color(70, 70, 140), "fabric"));
        addFurniture(createFurniture("Loveseat", "Sofa", 1.4, 0.9, 0.8,
                new Color(170, 80, 90), "fabric"));

        // Beds
        addFurniture(createFurniture("Queen Bed", "Bed", 1.6, 2.0, 0.5,
                new Color(160, 120, 80), "wood"));

        // Cabinets
        addFurniture(createFurniture("TV Cabinet", "Cabinet", 1.8, 0.5, 0.6,
                new Color(100, 90, 80), "wood"));

        saveCatalog();
    }

    private Furniture createFurniture(String name, String type, double width, double length,
                                      double height, Color color, String material) {
        Furniture furniture = new Furniture(name, type, width, length, height, color, material,
                "resources/images/furniture/" + type.toLowerCase() + ".png",
                "resources/models/" + type.toLowerCase() + ".obj");
        return furniture;
    }

    public List<Furniture> getFurnitureCatalog() {
        return new ArrayList<>(furnitureCatalog);
    }

    public List<Furniture> getFurnitureByType(String type) {
        List<Furniture> result = furnitureByType.get(type);
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(result);
    }

    public String[] getFurnitureTypes() {
        return furnitureByType.keySet().toArray(new String[0]);
    }

    public Furniture getFurnitureById(String id) {
        for (Furniture furniture : furnitureCatalog) {
            if (furniture.getId().equals(id)) {
                return furniture;
            }
        }
        return null;
    }

    public void addFurniture(Furniture furniture) {
        // Add to main catalog
        furnitureCatalog.add(furniture);

        // Add to type-specific list
        String type = furniture.getType();
        if (!furnitureByType.containsKey(type)) {
            furnitureByType.put(type, new ArrayList<>());
        }
        furnitureByType.get(type).add(furniture);

        // Notify listeners
        notifyListeners();
    }

    public boolean removeFurniture(String furnitureId) {
        Furniture furniture = getFurnitureById(furnitureId);
        if (furniture != null) {
            // Remove from main catalog
            furnitureCatalog.remove(furniture);

            // Remove from type-specific list
            List<Furniture> typeList = furnitureByType.get(furniture.getType());
            if (typeList != null) {
                typeList.remove(furniture);
            }

            // Notify listeners
            notifyListeners();
            return true;
        }
        return false;
    }

    public Furniture createFurnitureCopy(Furniture original) {
        // Create a copy with a new ID but same properties
        Furniture copy = new Furniture(
                original.getName(),
                original.getType(),
                original.getWidth(),
                original.getLength(),
                original.getHeight(),
                original.getColor(),
                original.getMaterial(),
                original.getImagePath(),
                original.getModelPath()
        );

        return copy;
    }

    public void updateFurniture(String furnitureId, String name, String type,
                                double width, double length, double height,
                                Color color, String material) {
        Furniture furniture = getFurnitureById(furnitureId);
        if (furniture != null) {
            // Store the old type for list updates
            String oldType = furniture.getType();

            // Update properties
            furniture.setName(name);
            furniture.setType(type);
            furniture.setWidth(width);
            furniture.setLength(length);
            furniture.setHeight(height);
            furniture.setColor(color);
            furniture.setMaterial(material);

            // If type changed, update the type-based lists
            if (!oldType.equals(type)) {
                // Remove from old type list
                List<Furniture> oldTypeList = furnitureByType.get(oldType);
                if (oldTypeList != null) {
                    oldTypeList.remove(furniture);
                }

                // Add to new type list
                if (!furnitureByType.containsKey(type)) {
                    furnitureByType.put(type, new ArrayList<>());
                }
                furnitureByType.get(type).add(furniture);
            }

            // Notify listeners
            notifyListeners();
        }
    }

    public void saveCatalog() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(CATALOG_FILE))) {
            out.writeObject(furnitureCatalog);
        } catch (IOException e) {
            System.err.println("Error saving furniture catalog: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCatalog() {
        File file = new File(CATALOG_FILE);
        if (!file.exists()) {
            return; // No catalog file yet
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(CATALOG_FILE))) {
            furnitureCatalog = (List<Furniture>) in.readObject();

            // Rebuild the type map
            furnitureByType.clear();
            for (String type : DEFAULT_TYPES) {
                furnitureByType.put(type, new ArrayList<>());
            }

            for (Furniture furniture : furnitureCatalog) {
                String type = furniture.getType();
                if (!furnitureByType.containsKey(type)) {
                    furnitureByType.put(type, new ArrayList<>());
                }
                furnitureByType.get(type).add(furniture);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading furniture catalog: " + e.getMessage());
            // Start with an empty catalog
            furnitureCatalog = new ArrayList<>();
        }
    }

    public void addFurnitureChangeListener(FurnitureChangeListener listener) {
        listeners.add(listener);
    }

    public void removeFurnitureChangeListener(FurnitureChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (FurnitureChangeListener listener : listeners) {
            listener.onFurnitureCatalogChanged();
        }
    }

    public interface FurnitureChangeListener {
        void onFurnitureCatalogChanged();
    }
}