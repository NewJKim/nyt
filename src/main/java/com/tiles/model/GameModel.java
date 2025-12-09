package com.tiles.model;

import java.io.Serializable;
import java.util.*;

/**
 * Game Model - Core game state and business logic
 * Demonstrates: Encapsulation, Single Responsibility Principle
 *
 * Location: src/main/java/com/tiles/model/GameModel.java
 */
public class GameModel implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final int TILES_PER_GROUP = 4;
    public static final int GROUPS_COUNT = 4;
    public static final int TOTAL_TILES = TILES_PER_GROUP * GROUPS_COUNT;
    public static final int MAX_MISTAKES = 4;

    private List<Tile> tiles;
    private List<Category> categories;
    private int score;
    private int mistakes;
    private boolean gameOver;
    private boolean gameWon;
    private List<Category> solvedCategories;
    private int hintsUsed;

    /**
     * Default constructor - initializes with default game
     */
    public GameModel() {
        this.tiles = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.solvedCategories = new ArrayList<>();
        this.score = 0;
        this.mistakes = 0;
        this.gameOver = false;
        this.gameWon = false;
        this.hintsUsed = 0;
        initializeDefaultGame();
    }

    /**
     * Constructor with custom tiles and categories
     */
    public GameModel(List<Tile> tiles, List<Category> categories) {
        this();
        if (validateGameSetup(tiles, categories)) {
            this.tiles = new ArrayList<>(tiles);
            this.categories = new ArrayList<>(categories);
        } else {
            initializeDefaultGame();
        }
    }

    /**
     * Validate game setup
     */
    private boolean validateGameSetup(List<Tile> tiles, List<Category> categories) {
        if (tiles == null || categories == null) return false;
        if (tiles.size() != TOTAL_TILES) return false;
        if (categories.size() != GROUPS_COUNT) return false;

        // Check each category has exactly TILES_PER_GROUP tiles
        Map<Category, Integer> categoryCount = new HashMap<>();
        for (Tile tile : tiles) {
            categoryCount.put(tile.getCategory(),
                    categoryCount.getOrDefault(tile.getCategory(), 0) + 1);
        }

        for (Category cat : categories) {
            if (categoryCount.getOrDefault(cat, 0) != TILES_PER_GROUP) {
                return false;
            }
        }

        return true;
    }

    /**
     * Initialize default game with sample data
     */
    private void initializeDefaultGame() {
        tiles.clear();
        categories.clear();
        solvedCategories.clear();

        // Create categories with colors
        Category cat1 = new Category("Fish", "Types of fish",
                DifficultyLevel.EASY, new java.awt.Color(249, 223, 109));
        Category cat2 = new Category("Gems", "Precious stones",
                DifficultyLevel.MEDIUM, new java.awt.Color(160, 195, 90));
        Category cat3 = new Category("___BOOK", "Words before BOOK",
                DifficultyLevel.HARD, new java.awt.Color(176, 196, 239));
        Category cat4 = new Category("Slang Money", "Slang terms for money",
                DifficultyLevel.TRICKY, new java.awt.Color(186, 129, 197));

        categories.add(cat1);
        categories.add(cat2);
        categories.add(cat3);
        categories.add(cat4);

        // Create tiles for each category
        tiles.add(new Tile("BASS", cat1));
        tiles.add(new Tile("FLOUNDER", cat1));
        tiles.add(new Tile("SALMON", cat1));
        tiles.add(new Tile("TROUT", cat1));

        tiles.add(new Tile("RUBY", cat2));
        tiles.add(new Tile("PEARL", cat2));
        tiles.add(new Tile("JADE", cat2));
        tiles.add(new Tile("OPAL", cat2));

        tiles.add(new Tile("FACE", cat3));
        tiles.add(new Tile("COOK", cat3));
        tiles.add(new Tile("MATCH", cat3));
        tiles.add(new Tile("POCKET", cat3));

        tiles.add(new Tile("BREAD", cat4));
        tiles.add(new Tile("DOUGH", cat4));
        tiles.add(new Tile("CHEDDAR", cat4));
        tiles.add(new Tile("CLAMS", cat4));

        // Shuffle tiles
        shuffleTiles();
    }

    /**
     * Shuffle tiles for new game
     */
    public void shuffleTiles() {
        Collections.shuffle(tiles);
    }

    /**
     * Get currently selected tiles
     */
    public List<Tile> getSelectedTiles() {
        List<Tile> selected = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.isSelected()) {
                selected.add(tile);
            }
        }
        return selected;
    }

    /**
     * Check if selected tiles form a valid group
     * @return ValidationResult with outcome
     */
    public ValidationResult validateSelection() {
        List<Tile> selected = getSelectedTiles();

        if (selected.size() != TILES_PER_GROUP) {
            return new ValidationResult(false, "Select exactly " + TILES_PER_GROUP + " tiles", null);
        }

        // Check if all tiles belong to same category
        Category firstCategory = selected.get(0).getCategory();
        for (Tile tile : selected) {
            if (!tile.getCategory().equals(firstCategory)) {
                return new ValidationResult(false, "Not all tiles match!", null);
            }
        }

        return new ValidationResult(true, "Correct!", firstCategory);
    }

    /**
     * Process a valid match
     */
    public void processMatch(Category category) {
        for (Tile tile : tiles) {
            if (tile.getCategory().equals(category)) {
                tile.setMatched(true);
            }
        }

        solvedCategories.add(category);
        score += category.getPoints();

        // Check win condition
        if (solvedCategories.size() == GROUPS_COUNT) {
            gameWon = true;
            gameOver = true;
        }
    }

    /**
     * Process an incorrect guess
     */
    public void processMistake() {
        mistakes++;
        score = Math.max(0, score - 25); // Penalty

        // Deselect all tiles
        for (Tile tile : tiles) {
            tile.setSelected(false);
        }

        if (mistakes >= MAX_MISTAKES) {
            gameOver = true;
            gameWon = false;
        }
    }

    /**
     * Get unmatched tiles
     */
    public List<Tile> getUnmatchedTiles() {
        List<Tile> unmatched = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!tile.isMatched()) {
                unmatched.add(tile);
            }
        }
        return unmatched;
    }

    /**
     * Use hint - reveal category of one selected tile
     */
    public String useHint() {
        List<Tile> selected = getSelectedTiles();
        if (selected.isEmpty()) {
            return "Select at least one tile to get a hint!";
        }

        hintsUsed++;
        score = Math.max(0, score - 50); // Hint penalty
        Tile hintTile = selected.get(0);
        return "\"" + hintTile.getWord() + "\" belongs to: " +
                hintTile.getCategory().getName();
    }

    /**
     * Reset game state
     */
    public void resetGame() {
        score = 0;
        mistakes = 0;
        gameOver = false;
        gameWon = false;
        hintsUsed = 0;
        solvedCategories.clear();

        for (Tile tile : tiles) {
            tile.reset();
        }

        shuffleTiles();
    }

    /**
     * Deselect all tiles
     */
    public void deselectAll() {
        for (Tile tile : tiles) {
            tile.setSelected(false);
        }
    }

    // Getters
    public List<Tile> getTiles() {
        return new ArrayList<>(tiles);
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public int getScore() {
        return score;
    }

    public int getMistakes() {
        return mistakes;
    }

    public int getRemainingLives() {
        return MAX_MISTAKES - mistakes;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public List<Category> getSolvedCategories() {
        return new ArrayList<>(solvedCategories);
    }

    public int getHintsUsed() {
        return hintsUsed;
    }
}