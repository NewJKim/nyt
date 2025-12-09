package com.tiles.model;

import org.junit.*;
import static org.junit.Assert.*;
import java.awt.Color;
import java.util.*;

/**
 * JUnit Tests for NYT Tiles Game - Model Logic
 *
 * This test class contains 12 meaningful tests covering:
 * - Tile group validation
 * - Scoring calculation
 * - Category matching
 * - Edge cases and boundary conditions
 *
 * Location: src/test/java/com/tiles/model/GameModelTest.java
 */
public class GameModelTest {
    private GameModel model;
    private Category cat1, cat2, cat3, cat4;
    private List<Tile> testTiles;
    private List<Category> testCategories;

    @Before
    public void setUp() {
        // Create test categories
        cat1 = new Category("Fish", "Types of fish", DifficultyLevel.EASY, Color.YELLOW);
        cat2 = new Category("Gems", "Precious stones", DifficultyLevel.MEDIUM, Color.GREEN);
        cat3 = new Category("Books", "Book-related", DifficultyLevel.HARD, Color.BLUE);
        cat4 = new Category("Money", "Slang for money", DifficultyLevel.TRICKY, Color.MAGENTA);

        testCategories = Arrays.asList(cat1, cat2, cat3, cat4);

        // Create 16 test tiles (4 per category)
        testTiles = new ArrayList<>();
        testTiles.add(new Tile("BASS", cat1));
        testTiles.add(new Tile("SALMON", cat1));
        testTiles.add(new Tile("TROUT", cat1));
        testTiles.add(new Tile("TUNA", cat1));

        testTiles.add(new Tile("RUBY", cat2));
        testTiles.add(new Tile("PEARL", cat2));
        testTiles.add(new Tile("JADE", cat2));
        testTiles.add(new Tile("OPAL", cat2));

        testTiles.add(new Tile("FACE", cat3));
        testTiles.add(new Tile("COOK", cat3));
        testTiles.add(new Tile("NOTE", cat3));
        testTiles.add(new Tile("MARK", cat3));

        testTiles.add(new Tile("BREAD", cat4));
        testTiles.add(new Tile("DOUGH", cat4));
        testTiles.add(new Tile("CASH", cat4));
        testTiles.add(new Tile("BUCKS", cat4));

        model = new GameModel(testTiles, testCategories);
    }

    /**
     * TEST 1: Tile Group Validation - Correct Match
     * Tests that selecting 4 tiles from the same category validates correctly
     */
    @Test
    public void testValidateCorrectGroup() {
        // Select all 4 tiles from cat1 (Fish)
        List<Tile> tiles = model.getTiles();
        int selectedCount = 0;
        for (Tile tile : tiles) {
            if (tile.getCategory().equals(cat1)) {
                tile.setSelected(true);
                selectedCount++;
            }
        }

        assertEquals("Should have selected 4 tiles", 4, selectedCount);

        ValidationResult result = model.validateSelection();

        assertTrue("Validation should succeed for matching group", result.isValid());
        assertEquals("Should return correct category", cat1, result.getCategory());
        assertEquals("Should have success message", "Correct!", result.getMessage());
    }

    /**
     * TEST 2: Tile Group Validation - Incorrect Match (Mixed Categories)
     * Tests that selecting tiles from different categories fails validation
     */
    @Test
    public void testValidateIncorrectGroup() {
        List<Tile> tiles = model.getTiles();

        // Select tiles from different categories (mixed)
        tiles.get(0).setSelected(true);  // cat1
        tiles.get(1).setSelected(true);  // cat1
        tiles.get(4).setSelected(true);  // cat2
        tiles.get(8).setSelected(true);  // cat3

        ValidationResult result = model.validateSelection();

        assertFalse("Validation should fail for mixed categories", result.isValid());
        assertTrue("Should have error message", result.getMessage().contains("Not all"));
        assertNull("Category should be null for invalid result", result.getCategory());
    }

    /**
     * TEST 3: Scoring Calculation - Points Based on Difficulty
     * Tests that correct matches award points based on difficulty level
     */
    @Test
    public void testScoringCalculation() {
        int initialScore = model.getScore();
        assertEquals("Initial score should be 0", 0, initialScore);

        // Match EASY category (100 points)
        model.processMatch(cat1);
        assertEquals("Easy category should award 100 points", 100, model.getScore());

        // Match MEDIUM category (150 points)
        model.processMatch(cat2);
        assertEquals("Should have 250 total points", 250, model.getScore());

        // Match HARD category (200 points)
        model.processMatch(cat3);
        assertEquals("Should have 450 total points", 450, model.getScore());

        // Match TRICKY category (250 points)
        model.processMatch(cat4);
        assertEquals("Should have 700 total points", 700, model.getScore());
    }

    /**
     * TEST 4: Scoring Calculation - Mistake Penalty
     * Tests that incorrect guesses deduct points from score
     */
    @Test
    public void testMistakePenalty() {
        // Give the player some points first
        model.processMatch(cat1);
        int scoreAfterMatch = model.getScore();
        assertEquals("Should have 100 points", 100, scoreAfterMatch);

        // Make a mistake (penalty: -25 points)
        model.processMistake();

        assertEquals("Score should decrease by 25", 75, model.getScore());
        assertEquals("Mistake count should increase", 1, model.getMistakes());

        // Score should never go below 0
        for (int i = 0; i < 10; i++) {
            model.processMistake();
        }

        assertTrue("Score should never be negative", model.getScore() >= 0);
    }

    /**
     * TEST 5: Category Matching - All Tiles Marked as Matched
     * Tests that when a category is matched, all its tiles are marked correctly
     */
    @Test
    public void testCategoryMatchMarksAllTiles() {
        // Process match for cat1
        model.processMatch(cat1);

        // Check that all cat1 tiles are marked as matched
        int matchedCount = 0;
        for (Tile tile : model.getTiles()) {
            if (tile.getCategory().equals(cat1)) {
                assertTrue("Cat1 tiles should be marked as matched", tile.isMatched());
                matchedCount++;
            } else {
                assertFalse("Other tiles should not be matched", tile.isMatched());
            }
        }

        assertEquals("All 4 cat1 tiles should be matched", 4, matchedCount);
        assertEquals("Solved categories should contain cat1", 1, model.getSolvedCategories().size());
        assertTrue("Cat1 should be in solved list", model.getSolvedCategories().contains(cat1));
    }

    /**
     * TEST 6: Edge Case - Too Few Tiles Selected
     * Tests validation behavior when fewer than 4 tiles are selected
     */
    @Test
    public void testValidationWithTooFewTiles() {
        List<Tile> tiles = model.getTiles();

        // Select only 3 tiles
        tiles.get(0).setSelected(true);
        tiles.get(1).setSelected(true);
        tiles.get(2).setSelected(true);

        ValidationResult result = model.validateSelection();

        assertFalse("Should fail with too few tiles", result.isValid());
        assertTrue("Message should mention exact count needed",
                result.getMessage().contains("Select exactly 4"));
    }

    /**
     * TEST 7: Boundary Condition - Maximum Mistakes (Game Over)
     * Tests that game ends after reaching maximum mistake limit
     */
    @Test
    public void testMaxMistakesGameOver() {
        assertFalse("Game should not be over initially", model.isGameOver());
        assertEquals("Should have 4 lives remaining", 4, model.getRemainingLives());

        // Make 3 mistakes - should NOT be game over
        for (int i = 0; i < 3; i++) {
            model.processMistake();
            assertFalse("Game should continue", model.isGameOver());
        }

        assertEquals("Should have 1 life remaining", 1, model.getRemainingLives());

        // Make 4th mistake - should trigger game over
        model.processMistake();

        assertTrue("Game should be over after 4 mistakes", model.isGameOver());
        assertFalse("Should not be a win", model.isGameWon());
        assertEquals("Should have 0 lives remaining", 0, model.getRemainingLives());
    }

    /**
     * TEST 8: Boundary Condition - Win Condition (All Categories Solved)
     * Tests that game is won when all 4 categories are correctly matched
     */
    @Test
    public void testWinConditionAllCategoriesSolved() {
        assertFalse("Game should not be won initially", model.isGameWon());
        assertFalse("Game should not be over initially", model.isGameOver());

        // Solve first 3 categories - should NOT win yet
        model.processMatch(cat1);
        model.processMatch(cat2);
        model.processMatch(cat3);

        assertFalse("Game should not be won with 3/4 categories", model.isGameWon());
        assertFalse("Game should not be over yet", model.isGameOver());
        assertEquals("Should have 3 solved categories", 3, model.getSolvedCategories().size());

        // Solve 4th category - should trigger win
        model.processMatch(cat4);

        assertTrue("Game should be won with all categories", model.isGameWon());
        assertTrue("Game should be over", model.isGameOver());
        assertEquals("Should have all 4 categories solved", 4, model.getSolvedCategories().size());
    }

    /**
     * TEST 9: Edge Case - Matched Tiles Cannot Be Selected
     * Tests that tiles marked as matched cannot be selected again
     */
    @Test
    public void testMatchedTilesCannotBeSelected() {
        List<Tile> tiles = model.getTiles();
        Tile testTile = tiles.get(0);

        // Mark tile as matched
        testTile.setMatched(true);
        assertTrue("Tile should be matched", testTile.isMatched());

        // Try to select matched tile
        testTile.setSelected(true);

        assertFalse("Matched tile should not be selectable", testTile.isSelected());

        // Verify through toggle as well
        boolean result = testTile.toggleSelection();

        assertFalse("Toggle should return false for matched tile", result);
        assertFalse("Tile should still not be selected", testTile.isSelected());
    }

    /**
     * TEST 10: Tile Selection Management - Deselect All
     * Tests that deselecting all tiles clears all selections
     */
    @Test
    public void testDeselectAllTiles() {
        List<Tile> tiles = model.getTiles();

        // Select multiple tiles
        tiles.get(0).setSelected(true);
        tiles.get(1).setSelected(true);
        tiles.get(2).setSelected(true);

        assertEquals("Should have 3 selected tiles", 3, model.getSelectedTiles().size());

        // Deselect all
        model.deselectAll();

        assertEquals("Should have 0 selected tiles", 0, model.getSelectedTiles().size());

        // Verify no tiles are selected
        for (Tile tile : model.getTiles()) {
            assertFalse("No tiles should be selected", tile.isSelected());
        }
    }

    /**
     * TEST 11: Game State - Reset Functionality
     * Tests that reset properly clears all game state
     */
    @Test
    public void testResetGame() {
        // Modify game state
        model.processMatch(cat1);
        model.processMistake();
        List<Tile> tiles = model.getTiles();
        tiles.get(0).setSelected(true);

        // Verify state is modified
        assertTrue("Score should be greater than 0", model.getScore() > 0);
        assertTrue("Mistakes should be greater than 0", model.getMistakes() > 0);

        // Reset game
        model.resetGame();

        // Verify everything is reset
        assertEquals("Score should be 0", 0, model.getScore());
        assertEquals("Mistakes should be 0", 0, model.getMistakes());
        assertEquals("Hints should be 0", 0, model.getHintsUsed());
        assertFalse("Game should not be over", model.isGameOver());
        assertFalse("Game should not be won", model.isGameWon());
        assertEquals("Solved categories should be empty", 0, model.getSolvedCategories().size());

        // Verify all tiles are reset
        for (Tile tile : model.getTiles()) {
            assertFalse("Tiles should not be selected", tile.isSelected());
            assertFalse("Tiles should not be matched", tile.isMatched());
        }
    }

    /**
     * TEST 12: Hint System - Cost and Functionality
     * Tests that hint system works correctly and deducts points
     */
    @Test
    public void testHintSystemCostAndFunctionality() {
        // Give player some points
        model.processMatch(cat1);
        int scoreBeforeHint = model.getScore();
        assertEquals("Should have 100 points", 100, scoreBeforeHint);

        // Select a tile and use hint
        List<Tile> tiles = model.getTiles();
        tiles.get(4).setSelected(true);  // Select a tile from cat2

        String hintMessage = model.useHint();

        // Verify hint message contains useful information
        assertNotNull("Hint message should not be null", hintMessage);
        assertTrue("Hint should mention 'belongs to'", hintMessage.contains("belongs to"));

        // Verify point deduction
        assertEquals("Hint should cost 50 points", 50, model.getScore());
        assertEquals("Hints used should be 1", 1, model.getHintsUsed());

        // Test hint without selection
        model.deselectAll();
        String emptyHint = model.useHint();
        assertTrue("Should warn about no selection", emptyHint.contains("Select at least"));
    }
}