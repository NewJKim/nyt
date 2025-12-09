package com.tiles.model;

/**
 * Difficulty Level Enumeration
 * Demonstrates: Abstraction through enumeration, Encapsulation of related data
 *
 * Location: src/main/java/com/tiles/model/DifficultyLevel.java
 */
public enum DifficultyLevel {
    EASY(1, 100, "Straightforward"),
    MEDIUM(2, 150, "Requires thought"),
    HARD(3, 200, "Tricky connections"),
    TRICKY(4, 250, "Very challenging");

    private final int level;
    private final int points;
    private final String description;

    DifficultyLevel(int level, int points, String description) {
        this.level = level;
        this.points = points;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public int getPoints() {
        return points;
    }

    public String getDescription() {
        return description;
    }
}