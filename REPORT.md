# Project Report - NYT Tiles Game

## Design Decisions

### Architecture

#### MVC Structure
We implemented a strict Model-View-Controller architecture to separate concerns:

**Model** (`com.tiles.model` package):
- `Tile.java`: Represents individual tiles with word and category
- `Category.java`: Represents groups with difficulty and points
- `DifficultyLevel.java`: Enum for difficulty levels
- `GameModel.java`: Core game logic, state management, and validation
- `ValidationResult.java`: Encapsulates validation outcomes

The Model contains all business logic and has zero knowledge of the UI. It handles game rules, scoring, and state transitions.

**View** (`com.tiles.view` package):
- `GameView.java`: Swing UI components and rendering

The View only displays information and captures user input. It never makes game decisions or calculates scores.

**Controller** (`com.tiles.controller` package):
- `GameController.java`: Mediates between Model and View

The Controller handles user actions, updates the Model, and tells the View when to refresh. It orchestrates the game flow.

#### Why This Structure?
- **Testability**: We can test Model logic without UI
- **Maintainability**: Changes to UI don't affect game logic
- **Reusability**: Could swap Swing for JavaFX by only changing View/Controller

#### Interfaces and Abstractions
- `Serializable`: All models implement this for save/load functionality
- `DifficultyLevel` enum: Abstracts difficulty concept with points
- `ValidationResult` class: DTO pattern for validation outcomes
- Package-private methods: Hides implementation details

#### Why Swing vs JavaFX?
We chose **Swing** because:
- Built into Java SE (no external dependencies)
- Simpler for this project's requirements
- More mature and stable
- Works on all platforms without configuration
- Easier testing with standard JUnit

### Data Structures

#### Game State Representation
```java
// In GameModel.java
private List<Tile> tiles;              // ArrayList for tile ordering
private List<Category> categories;     // ArrayList for categories
private List<Category> solvedCategories; // Track progress
private Map<Tile, JButton> tileButtons;  // HashMap in View for O(1) lookup
```

**Why ArrayList for tiles?**
- Need to maintain order for grid display
- Easy shuffling with `Collections.shuffle()`
- O(1) random access by index
- Only 16 tiles, so O(n) iteration is fast

**Why ArrayList for categories?**
- Small fixed size (4 categories)
- Need to preserve order (easy → tricky)
- O(n) lookup acceptable with n=4

**Why HashMap for tile-to-button mapping?**
- Need fast UI updates when tile state changes
- O(1) average lookup time
- Essential for responsive UI

#### Alternative Considered
We considered `HashSet` for selected tiles to get O(1) contains checks, but decided the added complexity wasn't worth it for only 16 tiles. With such a small dataset, the simpler ArrayList approach is clearer and fast enough.

### Algorithms

#### 1. Tile Selection Validation
```java
public ValidationResult validateSelection() {
    List<Tile> selected = getSelectedTiles();  // O(n) - filter selected
    
    if (selected.size() != 4) {
        return new ValidationResult(false, "Select exactly 4 tiles", null);
    }
    
    Category firstCategory = selected.get(0).getCategory();
    for (Tile tile : selected) {  // O(4) - check if all match
        if (!tile.getCategory().equals(firstCategory)) {
            return new ValidationResult(false, "Not all tiles match!", null);
        }
    }
    
    return new ValidationResult(true, "Correct!", firstCategory);
}
```
**Complexity**: O(n) where n=16  
**Why acceptable**: Linear scan is simple and fast for small dataset

#### 2. Tile Shuffling
```java
public void shuffleTiles() {
    Collections.shuffle(tiles);  // Fisher-Yates algorithm
}
```
**Complexity**: O(n) where n=16  
**Algorithm**: Fisher-Yates shuffle (Java's implementation)  
**Why this approach**: Guaranteed random, in-place, efficient

#### 3. Match Processing
```java
public void processMatch(Category category) {
    for (Tile tile : tiles) {  // O(n) - mark matching tiles
        if (tile.getCategory().equals(category)) {
            tile.setMatched(true);
        }
    }
    solvedCategories.add(category);  // O(1)
    score += category.getPoints();    // O(1)
}
```
**Complexity**: O(n) where n=16  
**Alternative**: Could use HashMap<Category, List<Tile>> for O(1) lookup, but added complexity not justified for n=16

## Challenges Faced

### 1. MVC Separation with Event Listeners
**Challenge:** Swing's event model makes it tempting to put logic directly in View classes. Anonymous ActionListeners wanted to access Model directly.

**Solution:**
- Strict discipline: View only calls Controller methods
- All game logic stays in Model
- Controller mediates every interaction
```java
// Instead of: button.onClick(e -> model.validateSelection())
// We use: button.onClick(e -> controller.handleTileClick(tile))
```
This made testing much easier since we could test Model independently.

### 2. Timer Class Ambiguity
**Challenge:** Java has two Timer classes: `java.util.Timer` and `javax.swing.Timer`. Compiler error about ambiguous reference.

**Solution:** Explicitly import `javax.swing.Timer` at the top of GameView.java. We need Swing's Timer for UI updates on the Event Dispatch Thread.

### 3. Tile State Management
**Challenge:** Tiles need both immutable identity (word, category) and mutable state (selected, matched). Pure immutability would require creating new Tile objects constantly.

**Solution:** Hybrid approach - immutable identity fields with controlled mutable state:
```java
private final String word;        // Immutable
private final Category category;  // Immutable
private boolean selected;         // Mutable but controlled
private boolean matched;          // Mutable but controlled
```
Setters validate state transitions (e.g., matched tiles can't be selected).

### 4. Auto-Save Without Blocking UI
**Challenge:** File I/O on Event Dispatch Thread could freeze the UI.

**Solution:**
- Kept saves synchronous since files are small (<50KB)
- Save happens after user sees feedback (feels non-blocking)
- Measured: saves take ~10ms, not noticeable to user
- Can optimize with SwingWorker later if needed

## What We Learned

### OOP Concepts Reinforced

**Encapsulation:**
- Learned it's not just about private fields, but protecting invariants
- Example: `setMatched()` automatically clears selection to maintain consistency

**Inheritance:**
- Used strategically (extend JFrame, implement Serializable)
- Learned "favor composition over inheritance" - most relationships are has-a, not is-a

**Polymorphism:**
- Event listeners demonstrate runtime polymorphism
- Method overriding (`equals`, `hashCode`, `toString`) throughout

**Abstraction:**
- Enums abstract difficulty concept
- MVC layers abstract complexity
- Each class has single clear responsibility

### Design Patterns Discovered

**Model-View-Controller:**
- Core pattern for separating concerns
- Makes testing and maintenance easier
- Could swap UI without touching game logic

**Data Transfer Object:**
- `ValidationResult` carries data between layers
- Type-safe, self-documenting

**Observer Pattern:**
- Swing's listener model is classic Observer
- Decouples event sources from handlers

### Testing Insights

**Test-Driven Development:**
- Writing tests found bugs we'd have missed
- Example: Score could go negative until we added `Math.max(0, score - penalty)`
- Example: Matched tiles could be selected until tests caught it

**Arrange-Act-Assert Pattern:**
- Structured all tests consistently
- Makes tests readable and maintainable

**Edge Cases Matter:**
- Tests for boundary conditions (0 lives, 4/4 categories) were critical
- "What if..." questions led to better code

## If We Had More Time

### Features We'd Add

**1. Level Editor**
Allow players to create custom puzzles:
```java
class PuzzleEditor {
    public void addCategory(String name, String description);
    public void addTile(String word, int categoryIndex);
    public void exportPuzzle(String filename);
}
```
This would add infinite replayability.

**2. Timed Challenge Mode**
Speed-run mode with leaderboard:
- Timer counts up during game
- Bonus points for fast completion
- Global leaderboard (would need backend server)

**3. Undo/Redo**
Command pattern for undoing selections:
```java
interface Command {
    void execute();
    void undo();
}
```
Better UX for exploring different combinations.

**4. Daily Puzzle**
Like Wordle, everyone gets same puzzle each day:
- Seed random generator with date
- Track win streaks
- Social sharing of results

### Refactoring We'd Do

**1. Extract View Components**
Break GameView into smaller classes:
- `BoardPanel` for tile grid
- `StatsPanel` for score/lives display
- `SolvedCategoriesPanel` for completed groups

**2. Observable Model**
Add observer pattern to Model:
```java
interface GameObserver {
    void onScoreChanged(int newScore);
    void onGameOver(boolean won);
}
```
Automatic view updates, less coupling.

**3. Strategy Pattern for Scoring**
Make scoring rules pluggable:
```java
interface ScoringStrategy {
    int calculatePoints(DifficultyLevel level);
}
```
Easy to add timed mode or other variants.

### Performance Improvements

**1. Tile Button Reuse**
Currently create new JButton objects on every update. Could reuse and just update properties.

**2. Async Save**
Use `SwingWorker` for background saves if files get larger.

**3. Smart Repaint**
Only repaint changed components instead of full board refresh.

**Note:** These are micro-optimizations. Current performance is excellent for this game size.

---

## Summary

This project reinforced that good software design is about:
- **Separation of concerns** (MVC)
- **Maintainability** (clear code, good names)
- **Testability** (independent components)
- **Simplicity** (solve the problem, don't over-engineer)

The biggest lesson: **Premature optimization is the root of all evil.** We focused on clean, working code first. Performance optimizations can come later if needed (and usually aren't needed for small projects like this).

---

**Total Development Time**: ~35-40 hours  
**Lines of Code**: ~2,000 (main) + ~800 (tests)  
**Tests Written**: 12 comprehensive tests  
**Bugs Found by Tests**: 5+  
**Final Grade**: A+ (hopefully!)