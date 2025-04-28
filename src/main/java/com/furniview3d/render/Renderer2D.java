package main.java.com.furniview3d.render;

import main.java.com.furniview3d.model.Design;
import main.java.com.furniview3d.model.Furniture;
import main.java.com.furniview3d.model.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class Renderer2D extends JPanel {
    private static final long serialVersionUID = 1L;

    private Design design;
    private double scale = 1.0;
    private int panX = 0;
    private int panY = 0;
    private boolean showGrid = true;
    private boolean showLabels = true;
    private Map<String, Image> furnitureImages = new HashMap<>();

    public Renderer2D() {
        setBackground(Color.WHITE);
    }

    public void setDesign(Design design) {
        this.design = design;
        loadFurnitureImages();
        repaint();
    }

    public void setScale(double scale) {
        this.scale = Math.max(0.1, Math.min(scale, 5.0));
        repaint();
    }

    public double getScale() {
        return scale;
    }

    public void setPan(int x, int y) {
        this.panX = x;
        this.panY = y;
        repaint();
    }

    public void toggleGrid() {
        this.showGrid = !showGrid;
        repaint();
    }

    public void toggleLabels() {
        this.showLabels = !showLabels;
        repaint();
    }

    private void loadFurnitureImages() {
        if (design == null) return;

        furnitureImages.clear();
        for (Furniture furniture : design.getFurnitureList()) {
            try {
                // For now, we'll just create placeholder images
                Image image = createPlaceholderImage(furniture);
                furnitureImages.put(furniture.getId(), image);
            } catch (Exception e) {
                System.err.println("Error loading image for furniture: " + furniture.getName());
            }
        }
    }

    private Image createPlaceholderImage(Furniture furniture) {
        int width = (int)(furniture.getWidth() * 100);
        int height = (int)(furniture.getLength() * 100);

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        switch (furniture.getType().toLowerCase()) {
            case "chair":
                g2d.setColor(new Color(150, 80, 50));
                break;
            case "table":
                g2d.setColor(new Color(120, 100, 70));
                break;
            case "sofa":
                g2d.setColor(new Color(70, 90, 140));
                break;
            default:
                g2d.setColor(new Color(120, 120, 120));
        }

        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, width - 1, height - 1);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(furniture.getType(), 5, height/2);

        g2d.dispose();
        return img;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.translate(panX, panY);
        g2d.scale(scale, scale);

        if (design != null) {
            drawRoom(g2d);
            drawFurniture(g2d);
        } else {
            drawPlaceholder(g2d);
        }

        g2d.dispose();
    }

    private void drawRoom(Graphics2D g2d) {
        Room room = design.getRoom();

        int width = (int)(room.getWidth() * 100);
        int height = (int)(room.getLength() * 100);

        int x = (getWidth() / 2) - (width / 2);
        int y = (getHeight() / 2) - (height / 2);

        g2d.setColor(room.getColorScheme().getFloorColor());
        g2d.fillRect(x, y, width, height);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(x, y, width, height);

        if (showGrid) {
            drawGrid(g2d, x, y, width, height);
        }

        if (showLabels) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 14));
            g2d.drawString(room.getName(), x + 10, y + 20);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));
            g2d.drawString(String.format("%.1fm × %.1fm", room.getWidth(), room.getLength()),
                    x + 10, y + 40);
        }
    }

    private void drawGrid(Graphics2D g2d, int x, int y, int width, int height) {
        g2d.setColor(new Color(200, 200, 200, 100));
        g2d.setStroke(new BasicStroke(1));

        for (int i = 0; i <= width; i += 100) {
            g2d.drawLine(x + i, y, x + i, y + height);
        }

        for (int i = 0; i <= height; i += 100) {
            g2d.drawLine(x, y + i, x + width, y + i);
        }
    }

    private void drawFurniture(Graphics2D g2d) {
        if (design.getFurnitureList().isEmpty()) return;

        Room room = design.getRoom();

        int roomWidth = (int)(room.getWidth() * 100);
        int roomHeight = (int)(room.getLength() * 100);

        int roomX = (getWidth() / 2) - (roomWidth / 2);
        int roomY = (getHeight() / 2) - (roomHeight / 2);

        for (Furniture furniture : design.getFurnitureList()) {
            double posX = furniture.getPosX() * 100;
            double posY = furniture.getPosY() * 100;

            int screenX = roomX + (int)posX;
            int screenY = roomY + (int)posY;

            int width = (int)(furniture.getWidth() * 100);
            int height = (int)(furniture.getLength() * 100);

            AffineTransform oldTransform = g2d.getTransform();

            g2d.translate(screenX + width/2, screenY + height/2);
            g2d.rotate(Math.toRadians(furniture.getRotation()));

            Image image = furnitureImages.get(furniture.getId());
            if (image != null) {
                g2d.drawImage(image, -width/2, -height/2, width, height, null);
            } else {
                g2d.setColor(furniture.getColor());
                g2d.fillRect(-width/2, -height/2, width, height);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(-width/2, -height/2, width, height);
            }

            if (showLabels) {
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));

                Rectangle2D textBounds = g2d.getFontMetrics().getStringBounds(furniture.getName(), g2d);
                g2d.setColor(new Color(255, 255, 255, 200));
                g2d.fillRect(-width/2, -height/2 - 20, (int)textBounds.getWidth() + 6, 20);

                g2d.setColor(Color.BLACK);
                g2d.drawString(furniture.getName(), -width/2 + 3, -height/2 - 5);
            }

            g2d.setTransform(oldTransform);
        }
    }

    private void drawPlaceholder(Graphics2D g2d) {
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        String message = "No design loaded";

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);

        g2d.drawString(message,
                (getWidth() - textWidth) / 2,
                getHeight() / 2);
    }
}