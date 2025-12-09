package com.tiles.model;

import java.io.Serializable;

/**
 * Tile Model - Encapsulates individual tile data
 * Demonstrates: Encapsulation (private fields with controlled access)
 */
public class Tile implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String word;
    private final Category category;
    private boolean selected;
    private boolean matched;

    /**
     * Constructor for creating a new tile
     * @param word The word displayed on the tile
     * @param category The category this tile belongs to
     */
    public Tile(String word, Category category) {
        if (word == null || word.trim().isEmpty()) {
            throw new IllegalArgumentException("Word cannot be null or empty");
        }
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.word = word.trim().toUpperCase();
        this.category = category;
        this.selected = false;
        this.matched = false;
    }

    // Getters - Encapsulation
    public String getWord() {
        return word;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean isMatched() {
        return matched;
    }

    /**
     * Set selection state with validation
     * Matched tiles cannot be selected
     */
    public void setSelected(boolean selected) {
        if (!matched) {
            this.selected = selected;
        }
    }

    /**
     * Mark tile as matched
     * Matched tiles are automatically deselected
     */
    public void setMatched(boolean matched) {
        this.matched = matched;
        if (matched) {
            this.selected = false;
        }
    }

    /**
     * Toggle selection state
     * @return new selection state
     */
    public boolean toggleSelection() {
        if (!matched) {
            selected = !selected;
        }
        return selected;
    }

    /**
     * Reset tile to initial state
     */
    public void reset() {
        this.selected = false;
        this.matched = false;
    }

    @Override
    public String toString() {
        return word;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Tile)) return false;
        Tile other = (Tile) obj;
        return word.equals(other.word) && category.equals(other.category);
    }

    @Override
    public int hashCode() {
        return word.hashCode() * 31 + category.hashCode();
    }
}
