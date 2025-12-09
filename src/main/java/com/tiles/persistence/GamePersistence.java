package com.tiles.persistence;

import com.tiles.model.GameModel;

import java.io.*;

/**
 * Game Persistence - Handles save/load operations
 * Demonstrates: Abstraction (file I/O abstraction), Single Responsibility
 *
 * Location: src/main/java/com/tiles/persistence/GamePersistence.java
 */
public class GamePersistence {
    private static final String SAVE_FILE = "tiles_game_save.dat";
    private static final String BACKUP_FILE = "tiles_game_save.backup";

    /**
     * Save game state to file
     * @param model Game model to save
     * @return true if successful
     */
    public static boolean saveGame(GameModel model) {
        if (model == null) {
            return false;
        }

        // Create backup of existing save
        File saveFile = new File(SAVE_FILE);
        if (saveFile.exists()) {
            File backupFile = new File(BACKUP_FILE);
            saveFile.renameTo(backupFile);
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(model);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());

            // Restore backup if save failed
            File backupFile = new File(BACKUP_FILE);
            if (backupFile.exists()) {
                backupFile.renameTo(new File(SAVE_FILE));
            }

            return false;
        }
    }

    /**
     * Load game state from file
     * @return Loaded game model or null if load fails
     */
    public static GameModel loadGame() {
        File saveFile = new File(SAVE_FILE);

        if (!saveFile.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof GameModel) {
                return (GameModel) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading game: " + e.getMessage());

            // Try loading backup
            return loadBackup();
        }

        return null;
    }

    /**
     * Load backup save file
     */
    private static GameModel loadBackup() {
        File backupFile = new File(BACKUP_FILE);

        if (!backupFile.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(BACKUP_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof GameModel) {
                return (GameModel) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading backup: " + e.getMessage());
        }

        return null;
    }

    /**
     * Check if save file exists
     */
    public static boolean saveExists() {
        return new File(SAVE_FILE).exists();
    }

    /**
     * Delete save file
     */
    public static boolean deleteSave() {
        File saveFile = new File(SAVE_FILE);
        File backupFile = new File(BACKUP_FILE);

        boolean deleted = true;
        if (saveFile.exists()) {
            deleted = saveFile.delete();
        }
        if (backupFile.exists()) {
            backupFile.delete();
        }

        return deleted;
    }

    /**
     * Export game configuration to custom file
     */
    public static boolean exportGame(GameModel model, String filename) {
        if (model == null || filename == null) {
            return false;
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(model);
            return true;
        } catch (IOException e) {
            System.err.println("Error exporting game: " + e.getMessage());
            return false;
        }
    }

    /**
     * Import game configuration from custom file
     */
    public static GameModel importGame(String filename) {
        if (filename == null) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof GameModel) {
                return (GameModel) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error importing game: " + e.getMessage());
        }

        return null;
    }
}