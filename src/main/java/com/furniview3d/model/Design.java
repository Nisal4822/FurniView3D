package main.java.com.furniview3d.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Design implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private Room room;
    private List<Furniture> furnitureList;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String designerId; // ID of the designer who created this design

    // Constructors
    public Design() {
        this.id = UUID.randomUUID().toString();
        this.name = "New Design";
        this.description = "";
        this.room = new Room();
        this.furnitureList = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.designerId = "";
    }

    public Design(String name, String description, Room room, String designerId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.description = description;
        this.room = room;
        this.furnitureList = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.designerId = designerId;
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
        this.lastModified = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModified = LocalDateTime.now();
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
        this.lastModified = LocalDateTime.now();
    }

    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }

    public void setFurnitureList(List<Furniture> furnitureList) {
        this.furnitureList = furnitureList;
        this.lastModified = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public String getDesignerId() {
        return designerId;
    }

    public void setDesignerId(String designerId) {
        this.designerId = designerId;
    }

    // Utility methods
    public void addFurniture(Furniture furniture) {
        furnitureList.add(furniture);
        this.lastModified = LocalDateTime.now();
    }

    public void removeFurniture(Furniture furniture) {
        furnitureList.remove(furniture);
        this.lastModified = LocalDateTime.now();
    }

    public void removeFurniture(String furnitureId) {
        furnitureList.removeIf(f -> f.getId().equals(furnitureId));
        this.lastModified = LocalDateTime.now();
    }

    public Furniture getFurnitureById(String furnitureId) {
        return furnitureList.stream()
                .filter(f -> f.getId().equals(furnitureId))
                .findFirst()
                .orElse(null);
    }

    public int getFurnitureCount() {
        return furnitureList.size();
    }

    @Override
    public String toString() {
        return "Design{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", room=" + room +
                ", furnitureCount=" + furnitureList.size() +
                ", createdAt=" + createdAt +
                ", lastModified=" + lastModified +
                ", designerId='" + designerId + '\'' +
                '}';
    }
}