package main.java.com.furniview3d.model;
import java.awt.Color;
import java.io.Serializable;
import java.util.UUID;

public class Furniture implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String type; // "chair", "table", "sofa", etc.
    private double width; // in meters
    private double length; // in meters
    private double height; // in meters
    private Color color;
    private String material;
    private String imagePath; // path to 2D image
    private String modelPath; // path to 3D model
    private double posX; // X position in the room
    private double posY; // Y position in the room
    private double rotation; // rotation in degrees

    // Constructors
    public Furniture() {
        this.id = UUID.randomUUID().toString();
        this.name = "New Furniture";
        this.type = "chair";
        this.width = 0.6;
        this.length = 0.6;
        this.height = 0.8;
        this.color = Color.GRAY;
        this.material = "wood";
        this.imagePath = "resources/images/furniture/default.png";
        this.modelPath = "resources/models/default.obj";
        this.posX = 0.0;
        this.posY = 0.0;
        this.rotation = 0.0;
    }

    public Furniture(String name, String type, double width, double length, double height,
                     Color color, String material, String imagePath, String modelPath) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.type = type;
        this.width = width;
        this.length = length;
        this.height = height;
        this.color = color;
        this.material = material;
        this.imagePath = imagePath;
        this.modelPath = modelPath;
        this.posX = 0.0;
        this.posY = 0.0;
        this.rotation = 0.0;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getModelPath() {
        return modelPath;
    }

    public void setModelPath(String modelPath) {
        this.modelPath = modelPath;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    // Utility methods
    public double getArea() {
        return width * length;
    }

    @Override
    public String toString() {
        return "Furniture{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", width=" + width +
                ", length=" + length +
                ", height=" + height +
                ", color=" + color +
                ", material='" + material + '\'' +
                ", posX=" + posX +
                ", posY=" + posY +
                ", rotation=" + rotation +
                '}';
    }
}