package com.tiles.model;

import java.io.Serializable;

/**
 * Validation Result - Encapsulates validation outcome
 * Demonstrates: Encapsulation, Data Transfer Object pattern
 *
 * Location: src/main/java/com/tiles/model/ValidationResult.java
 */
public class ValidationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean valid;
    private final String message;
    private final Category category;

    public ValidationResult(boolean valid, String message, Category category) {
        this.valid = valid;
        this.message = message;
        this.category = category;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public Category getCategory() {
        return category;
    }
}