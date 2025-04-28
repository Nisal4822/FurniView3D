package main.java.com.furniview3d.model;

import java.awt.Color;
import java.io.Serializable;
import java.util.UUID;

/**
 * Represents a color scheme for a room in the FurniView3D application
 */
public class ColorScheme implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private Color wallColor;
    private Color floorColor;
    private Color ceilingColor;
    private Color accentColor;

    // Constructors
    public ColorScheme() {
        this.id = UUID.randomUUID().toString();
        this.name = "Default Color Scheme";
        this.wallColor = Color.WHITE;
        this.floorColor = new Color(210, 180, 140); // Light brown
        this.ceilingColor = Color.WHITE;
        this.accentColor = new Color(70, 130, 180); // Steel blue
    }

    public ColorScheme(String name, Color wallColor, Color floorColor, Color ceilingColor, Color accentColor) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.wallColor = wallColor;
        this.floorColor = floorColor;
        this.ceilingColor = ceilingColor;
        this.accentColor = accentColor;
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

    public Color getWallColor() {
        return wallColor;
    }

    public void setWallColor(Color wallColor) {
        this.wallColor = wallColor;
    }

    public Color getFloorColor() {
        return floorColor;
    }

    public void setFloorColor(Color floorColor) {
        this.floorColor = floorColor;
    }

    public Color getCeilingColor() {
        return ceilingColor;
    }

    public void setCeilingColor(Color ceilingColor) {
        this.ceilingColor = ceilingColor;
    }

    public Color getAccentColor() {
        return accentColor;
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
    }

    // Utility methods
    public static ColorScheme createModernScheme() {
        return new ColorScheme(
                "Modern",
                new Color(240, 240, 240), // Off-white walls
                new Color(60, 60, 60),    // Dark gray floor
                Color.WHITE,              // White ceiling
                new Color(0, 150, 136)    // Teal accent
        );
    }

    public static ColorScheme createWarmScheme() {
        return new ColorScheme(
                "Warm",
                new Color(255, 235, 205), // Blanched almond walls
                new Color(139, 69, 19),   // Saddle brown floor
                new Color(255, 248, 220), // Cornsilk ceiling
                new Color(178, 34, 34)    // Firebrick accent
        );
    }

    public static ColorScheme createCoolScheme() {
        return new ColorScheme(
                "Cool",
                new Color(240, 248, 255), // Alice blue walls
                new Color(47, 79, 79),    // Dark slate gray floor
                Color.WHITE,              // White ceiling
                new Color(70, 130, 180)   // Steel blue accent
        );
    }

    @Override
    public String toString() {
        return "ColorScheme{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", wallColor=" + wallColor +
                ", floorColor=" + floorColor +
                ", ceilingColor=" + ceilingColor +
                ", accentColor=" + accentColor +
                '}';
    }
}