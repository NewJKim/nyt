package com.tiles.view;

import com.tiles.model.*;
import com.tiles.controller.GameController;

import javax.swing.*;
import javax.swing.Timer;  // Explicitly import Swing Timer
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Game View - Swing UI Implementation
 * Demonstrates: MVC Pattern, Event-Driven Programming, UI Design
 *
 * Location: src/main/java/com/tiles/view/GameView.java
 */
public class GameView {
    private GameController controller;
    private GameModel model;
    private JFrame frame;
    private JPanel boardPanel;
    private JPanel solvedPanel;
    private JLabel scoreLabel;
    private JLabel livesLabel;
    private JLabel messageLabel;
    private Map<Tile, JButton> tileButtons;

    private static final int TILE_SIZE = 120;
    private static final int TILE_SPACING = 8;
    private static final Font TILE_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Color BG_COLOR = new Color(239, 239, 239);
    private static final Color TILE_DEFAULT = new Color(239, 239, 239);
    private static final Color TILE_SELECTED = new Color(95, 95, 95);
    private static final Color TILE_HOVER = new Color(220, 220, 220);

    /**
     * Constructor
     */
    public GameView(GameController controller, GameModel model) {
        this.controller = controller;
        this.model = model;
        this.tileButtons = new HashMap<>();
        initializeUI();
    }

    /**
     * Initialize UI components
     */
    private void initializeUI() {
        frame = new JFrame("NYT Tiles Game - Connections");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));
        frame.setBackground(BG_COLOR);

        // Create menu bar
        createMenuBar();

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        frame.add(headerPanel, BorderLayout.NORTH);

        // Create center panel with solved categories and board
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(BG_COLOR);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Solved categories panel
        solvedPanel = new JPanel();
        solvedPanel.setLayout(new BoxLayout(solvedPanel, BoxLayout.Y_AXIS));
        solvedPanel.setBackground(BG_COLOR);
        centerPanel.add(solvedPanel);

        // Board panel
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(4, 4, TILE_SPACING, TILE_SPACING));
        boardPanel.setBackground(BG_COLOR);
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        centerPanel.add(boardPanel);

        frame.add(centerPanel, BorderLayout.CENTER);

        // Create control panel
        JPanel controlPanel = createControlPanel();
        frame.add(controlPanel, BorderLayout.SOUTH);

        // Initialize board
        updateBoard();
        updateStats();

        // Frame settings
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Create menu bar
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");

        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(e -> controller.newGame());
        gameMenu.add(newGameItem);

        JMenuItem rulesItem = new JMenuItem("How to Play");
        rulesItem.addActionListener(e -> controller.showRules());
        gameMenu.add(rulesItem);

        gameMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        gameMenu.add(exitItem);

        menuBar.add(gameMenu);
        frame.setJMenuBar(menuBar);
    }

    /**
     * Create header panel with stats
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        // Title
        JLabel titleLabel = new JLabel("Connections", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setBackground(BG_COLOR);

        scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(HEADER_FONT);
        statsPanel.add(scoreLabel);

        livesLabel = new JLabel("Lives: ♥♥♥♥", SwingConstants.CENTER);
        livesLabel.setFont(HEADER_FONT);
        livesLabel.setForeground(Color.RED);
        statsPanel.add(livesLabel);

        panel.add(statsPanel, BorderLayout.CENTER);

        // Message label
        messageLabel = new JLabel("Find groups of 4!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        panel.add(messageLabel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create control panel with buttons
     */
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

        JButton deselectBtn = createStyledButton("Deselect All");
        deselectBtn.addActionListener(e -> controller.deselectAll());
        panel.add(deselectBtn);

        JButton shuffleBtn = createStyledButton("Shuffle");
        shuffleBtn.addActionListener(e -> controller.shuffleTiles());
        panel.add(shuffleBtn);

        JButton hintBtn = createStyledButton("Hint (-50 pts)");
        hintBtn.addActionListener(e -> controller.useHint());
        panel.add(hintBtn);

        return panel;
    }

    /**
     * Create styled button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }

    /**
     * Update game board
     */
    public void updateBoard() {
        boardPanel.removeAll();
        tileButtons.clear();

        // Update solved categories
        updateSolvedCategories();

        // Add tiles
        List<Tile> tiles = model.getTiles();
        for (Tile tile : tiles) {
            if (!tile.isMatched()) {
                JButton button = createTileButton(tile);
                tileButtons.put(tile, button);
                boardPanel.add(button);
            }
        }

        // Fill empty spaces
        int unmatchedCount = model.getUnmatchedTiles().size();
        for (int i = unmatchedCount; i < GameModel.TOTAL_TILES; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setBackground(BG_COLOR);
            boardPanel.add(emptyPanel);
        }

        boardPanel.revalidate();
        boardPanel.repaint();
    }

    /**
     * Create tile button
     */
    private JButton createTileButton(Tile tile) {
        JButton button = new JButton("<html><center>" + tile.getWord() + "</center></html>");
        button.setFont(TILE_FONT);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        // Set colors based on state
        if (tile.isSelected()) {
            button.setBackground(TILE_SELECTED);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(TILE_DEFAULT);
            button.setForeground(Color.BLACK);
        }

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!tile.isSelected() && !model.isGameOver()) {
                    button.setBackground(TILE_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!tile.isSelected()) {
                    button.setBackground(TILE_DEFAULT);
                }
            }
        });

        // Click handler
        button.addActionListener(e -> controller.handleTileClick(tile));

        return button;
    }

    /**
     * Update solved categories display
     */
    private void updateSolvedCategories() {
        solvedPanel.removeAll();

        List<Category> solved = model.getSolvedCategories();
        for (Category category : solved) {
            JPanel categoryPanel = new JPanel();
            categoryPanel.setLayout(new BorderLayout());
            categoryPanel.setBackground(category.getColor());
            categoryPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
            categoryPanel.setMaximumSize(new Dimension(500, 70));
            categoryPanel.setPreferredSize(new Dimension(500, 70));

            // Category name
            JLabel nameLabel = new JLabel(category.getName().toUpperCase(), SwingConstants.CENTER);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
            categoryPanel.add(nameLabel, BorderLayout.NORTH);

            // Tiles in category
            StringBuilder tilesText = new StringBuilder();
            for (Tile tile : model.getTiles()) {
                if (tile.getCategory().equals(category)) {
                    if (tilesText.length() > 0) tilesText.append(", ");
                    tilesText.append(tile.getWord());
                }
            }
            JLabel tilesLabel = new JLabel(tilesText.toString(), SwingConstants.CENTER);
            tilesLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            categoryPanel.add(tilesLabel, BorderLayout.CENTER);

            solvedPanel.add(categoryPanel);
            solvedPanel.add(Box.createVerticalStrut(5));
        }

        solvedPanel.revalidate();
        solvedPanel.repaint();
    }

    /**
     * Update statistics display
     */
    public void updateStats() {
        scoreLabel.setText("Score: " + model.getScore());

        int lives = model.getRemainingLives();
        StringBuilder livesStr = new StringBuilder("Lives: ");
        for (int i = 0; i < lives; i++) {
            livesStr.append("♥");
        }
        for (int i = lives; i < GameModel.MAX_MISTAKES; i++) {
            livesStr.append("♡");
        }
        livesLabel.setText(livesStr.toString());
    }

    /**
     * Show message to user
     */
    public void showMessage(String message) {
        messageLabel.setText(message);

        // Clear message after 3 seconds
        Timer timer = new Timer(3000, e -> {
            if (messageLabel.getText().equals(message)) {
                messageLabel.setText("Find groups of 4!");
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Show game over dialog
     */
    public void showGameOver(boolean won) {
        String message;
        String title;

        if (won) {
            message = "Congratulations! You won!\n\n" +
                    "Final Score: " + model.getScore() + "\n" +
                    "Mistakes: " + model.getMistakes() + "/" + GameModel.MAX_MISTAKES + "\n" +
                    "Hints Used: " + model.getHintsUsed();
            title = "Victory!";
        } else {
            message = "Game Over!\n\n" +
                    "Final Score: " + model.getScore() + "\n" +
                    "You ran out of lives.\n\n" +
                    "Unsolved categories:\n";

            for (Category cat : model.getCategories()) {
                if (!model.getSolvedCategories().contains(cat)) {
                    message += "• " + cat.getName() + ": " + cat.getDescription() + "\n";
                }
            }
            title = "Game Over";
        }

        int option = JOptionPane.showConfirmDialog(
                frame,
                message + "\n\nPlay again?",
                title,
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            controller.newGame();
        }
    }

    /**
     * Set model (for loading)
     */
    public void setModel(GameModel model) {
        this.model = model;
    }

    /**
     * Get frame
     */
    public JFrame getFrame() {
        return frame;
    }
}