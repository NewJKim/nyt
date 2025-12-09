package com.tiles.controller;

import com.tiles.model.*;
import com.tiles.view.GameView;
import com.tiles.persistence.GamePersistence;

import javax.swing.Timer;
import javax.swing.JOptionPane;

/**
 * Game Controller - Mediates between Model and View
 * Demonstrates: MVC Pattern, Separation of Concerns
 *
 * Location: src/main/java/com/tiles/controller/GameController.java
 */
public class GameController {
    private GameModel model;
    private GameView view;

    /**
     * Constructor
     * @param model Game model
     */
    public GameController(GameModel model) {
        if (model == null) {
            throw new IllegalArgumentException("Model cannot be null");
        }
        this.model = model;
    }

    /**
     * Set the view (called after view is constructed)
     */
    public void setView(GameView view) {
        this.view = view;
    }

    /**
     * Handle tile click
     * @param tile The clicked tile
     */
    public void handleTileClick(Tile tile) {
        if (model.isGameOver()) {
            return;
        }

        if (tile.isMatched()) {
            return;
        }

        // Check selection limit
        int selectedCount = model.getSelectedTiles().size();
        if (!tile.isSelected() && selectedCount >= GameModel.TILES_PER_GROUP) {
            view.showMessage("Maximum " + GameModel.TILES_PER_GROUP +
                    " tiles can be selected!");
            return;
        }

        // Toggle selection
        tile.toggleSelection();
        view.updateBoard();

        // Auto-check if 4 tiles selected
        if (model.getSelectedTiles().size() == GameModel.TILES_PER_GROUP) {
            // Small delay for better UX
            Timer timer = new Timer(300, e -> checkSelection());
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Check current selection
     */
    public void checkSelection() {
        if (model.isGameOver()) {
            return;
        }

        ValidationResult result = model.validateSelection();

        if (result.isValid()) {
            // Correct match
            model.processMatch(result.getCategory());
            view.showMessage("✓ Correct! " + result.getCategory().getName());
            view.updateBoard();
            view.updateStats();

            if (model.isGameWon()) {
                view.showGameOver(true);
            }

            // Auto-save after each correct match
            saveGame();

        } else {
            // Incorrect match
            if (model.getSelectedTiles().size() == GameModel.TILES_PER_GROUP) {
                model.processMistake();
                view.showMessage("✗ " + result.getMessage());
                view.updateBoard();
                view.updateStats();

                if (model.isGameOver()) {
                    view.showGameOver(false);
                }

                saveGame();
            } else {
                view.showMessage(result.getMessage());
            }
        }
    }

    /**
     * Deselect all tiles
     */
    public void deselectAll() {
        model.deselectAll();
        view.updateBoard();
    }

    /**
     * Shuffle unmatched tiles
     */
    public void shuffleTiles() {
        if (model.isGameOver()) {
            return;
        }

        model.shuffleTiles();
        view.updateBoard();
        view.showMessage("Tiles shuffled!");
    }

    /**
     * Use hint
     */
    public void useHint() {
        if (model.isGameOver()) {
            return;
        }

        String hintMessage = model.useHint();
        view.showMessage(hintMessage);
        view.updateStats();
        saveGame();
    }

    /**
     * Start new game
     */
    public void newGame() {
        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Start a new game? Current progress will be lost.",
                "New Game",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            model.resetGame();
            view.updateBoard();
            view.updateStats();
            view.showMessage("New game started! Good luck!");
            GamePersistence.deleteSave();
        }
    }

    /**
     * Save current game
     */
    public void saveGame() {
        if (GamePersistence.saveGame(model)) {
            // Silent save (auto-save)
        } else {
            view.showMessage("Failed to save game");
        }
    }

    /**
     * Load saved game
     */
    public void loadGame() {
        GameModel loadedModel = GamePersistence.loadGame();
        if (loadedModel != null) {
            this.model = loadedModel;
            view.setModel(model);
            view.updateBoard();
            view.updateStats();
            view.showMessage("Game loaded successfully!");
        }
    }

    /**
     * Show rules
     */
    public void showRules() {
        String rules = "HOW TO PLAY:\n\n" +
                "1. Find groups of 4 tiles that share something in common\n" +
                "2. Click tiles to select them (max 4)\n" +
                "3. Selected tiles are checked automatically\n" +
                "4. Categories have different difficulty levels:\n" +
                "   • Yellow = Straightforward\n" +
                "   • Green = Requires thought\n" +
                "   • Blue = Tricky connections\n" +
                "   • Purple = Very challenging\n\n" +
                "5. You have " + GameModel.MAX_MISTAKES + " mistakes before game over\n" +
                "6. Higher difficulty = More points!\n\n" +
                "TIPS:\n" +
                "• Use Deselect to clear your selection\n" +
                "• Use Shuffle to rearrange tiles\n" +
                "• Use Hint (costs points) for help\n" +
                "• Game auto-saves after each move";

        JOptionPane.showMessageDialog(
                null,
                rules,
                "Game Rules",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Get current model
     */
    public GameModel getModel() {
        return model;
    }
}