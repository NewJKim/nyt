package com.tiles;

import com.tiles.model.GameModel;
import com.tiles.controller.GameController;
import com.tiles.view.GameView;
import com.tiles.persistence.GamePersistence;

import javax.swing.*;

/**
 * Main Application Entry Point
 * Demonstrates: MVC Pattern initialization, Application structure
 *
 * This NYT Tiles Game (Connections) demonstrates:
 * - Encapsulation: Private fields with controlled access
 * - Inheritance: Component hierarchy (JButton, JPanel, etc.)
 * - Polymorphism: Event listeners, Serializable interface
 * - Abstraction: MVC separation, interfaces
 *
 * Location: src/main/java/com/tiles/TilesGameApp.java
 */
public class TilesGameApp {

    public static void main(String[] args) {
        // Use Swing's event dispatch thread for thread safety
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Fall back to default look and feel
                System.err.println("Could not set system look and feel: " + e.getMessage());
            }

            // Initialize MVC components
            GameModel model = new GameModel();
            GameController controller = new GameController(model);
            GameView view = new GameView(controller, model);

            // Connect controller to view
            controller.setView(view);

            // Load saved game if exists
            if (GamePersistence.saveExists()) {
                int option = JOptionPane.showConfirmDialog(
                        null,
                        "A saved game was found. Would you like to continue?",
                        "Load Game",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    controller.loadGame();
                }
            }

            // Show rules on first launch
            if (!GamePersistence.saveExists()) {
                controller.showRules();
            }
        });
    }
}