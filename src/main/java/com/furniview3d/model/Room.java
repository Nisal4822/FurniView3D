package main.java.com.furniview3d.model;
import java.awt.Color;
import java.io.Serializable;
import java.util.UUID;

public class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private double width; // in meters
    private double length; // in meters
    private double height; // in meters
    private String shape; // "rectangular", "L-shaped", etc.
    private ColorScheme colorScheme;

    // Constructors
    public Room() {
        this.id = UUID.randomUUID().toString();
        this.name = "New Room";
        this.width = 5.0;
        this.length = 5.0;
        this.height = 2.5;
        this.shape = "rectangular";
        this.colorScheme = new ColorScheme();
    }

    public Room(String name, double width, double length, double height, String shape, ColorScheme colorScheme) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.width = width;
        this.length = length;
        this.height = height;
        this.shape = shape;
        this.colorScheme = colorScheme;
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

    public String getShape() {
        return shape;
    }

    public void setShape(String shape) {
        this.shape = shape;
    }

    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

    // Utility methods
    public double getArea() {
        return width * length;
    }

    public double getVolume() {
        return width * length * height;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", width=" + width +
                ", length=" + length +
                ", height=" + height +
                ", shape='" + shape + '\'' +
                ", colorScheme=" + colorScheme +
                '}';
    }
}


