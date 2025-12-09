package com.tiles.model;

import java.io.Serializable;
import java.awt.Color;

/**
 * Category Model - Represents a group of related tiles
 * Demonstrates: Encapsulation, Immutability (final fields)
 *
 * Location: src/main/java/com/tiles/model/Category.java
 */
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String description;
    private final DifficultyLevel difficulty;
    private final Color color;

    /**
     * Constructor for creating a category
     * @param name Category name
     * @param description Category description/hint
     * @param difficulty Difficulty level
     * @param color Color for visual representation
     */
    public Category(String name, String description, DifficultyLevel difficulty, Color color) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        this.name = name.trim();
        this.description = description != null ? description.trim() : "";
        this.difficulty = difficulty != null ? difficulty : DifficultyLevel.MEDIUM;
        this.color = color != null ? color : Color.GRAY;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public DifficultyLevel getDifficulty() {
        return difficulty;
    }

    public Color getColor() {
        return color;
    }

    public int getPoints() {
        return difficulty.getPoints();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Category)) return false;
        Category other = (Category) obj;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return name + ": " + description;
    }
}