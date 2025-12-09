# NYT Tiles Game (Connections)

## Member
James Kim

## How to Run

### Using Maven (Recommended)
1. Clone repository: `git clone [your-repo-url]`
2. Navigate to project: `cd tiles-game`
3. Compile: `mvn clean compile`
4. Run: `mvn exec:java`

### Using IntelliJ IDEA
1. Open IntelliJ IDEA
2. File → Open → Select project folder
3. Wait for Maven to import dependencies
4. Right-click `TilesGameApp.java` → Run


### Running Tests
```bash
mvn test
```
Expected: Tests run: 12, Failures: 0

## Features Implemented

### Core Features
- **4x4 Grid Layout**: Interactive tile board with 16 tiles
- **Selection Mechanism**: Click to select/deselect tiles (maximum 4)
- **Category Validation**: Automatic validation when 4 tiles selected
- **Scoring System**: Points based on difficulty (100-250 points per category)
- **Difficulty Levels**: Four levels (Easy, Medium, Hard, Tricky) with color coding
- **Clear Board**: Matched groups removed and displayed at top
- **Win Condition**: Game won when all 4 categories found
- **Lose Condition**: Game over after 4 mistakes

### Extra Credit Features
- **[Extra Credit] Hint System**: Reveals category of selected tile (costs 50 points)
- **[Extra Credit] Shuffle Function**: Rearranges unmatched tiles
- **[Extra Credit] Auto-Save**: Game automatically saves after each move
- **[Extra Credit] JUnit Tests**: 12 comprehensive tests covering game logic

### Additional Features
- Deselect All button for clearing selections
- Lives display with visual hearts (♥/♡)
- Statistics tracking (score, mistakes, hints used)
- Game rules accessible via menu
- Continue previous game on startup

## Controls

### Mouse Controls
- **Left Click on Tile**: Select/deselect a tile
- **Deselect All Button**: Clear all selected tiles
- **Shuffle Button**: Rearrange unmatched tiles
- **Hint Button**: Reveal category of selected tile

### Menu Bar
- **Game → New Game**: Start a fresh game
- **Game → How to Play**: Display game rules
- **Game → Exit**: Quit the application

### Automatic Actions
- **Auto-Check**: When 4 tiles selected, validation runs automatically
- **Auto-Save**: Game saves after each correct match or mistake

## Known Issues

### Minor Issues
- **Shuffle Animation**: Tiles shuffle instantly without animation
    - **Workaround**: None needed, functionality works correctly

- **Test Randomness**: Shuffle test has rare chance of false failure due to random ordering
    - **Workaround**: Re-run tests if needed (probability < 0.01%)

### Platform Notes
- **macOS**: May prompt for file permissions on first save
    - **Workaround**: Accept permission prompt when it appears

- **All Platforms**: Window size is fixed (not resizable)
    - **Workaround**: None needed, designed for optimal viewing

## External Libraries

### Core Dependencies (Built-in)
- **Java Swing**: GUI framework (javax.swing)
- **Java AWT**: Graphics and colors (java.awt)
- **Java Serialization**: Save/load functionality (java.io)

### Testing Dependencies
- **JUnit 4.13.2**: Unit testing framework
- **Hamcrest 2.2**: Assertion matchers for JUnit

### No External JARs Required
All libraries are either built into Java or managed by Maven. No manual dependency installation needed.

## Project Structure

```
tiles-game/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/java/com/tiles/
│   │   ├── TilesGameApp.java       # Main entry point
│   │   ├── model/                   # Game logic (5 files)
│   │   ├── view/                    # UI layer (1 file)
│   │   ├── controller/              # Control layer (1 file)
│   │   └── persistence/             # Data storage (1 file)
│   └── test/java/com/tiles/model/
│       └── GameModelTest.java       # JUnit tests (12 tests)
├── README.md                        # This file
└── REPORT.md                        # Design decisions
```

## Game Rules

### Objective
Find 4 groups of 4 tiles that share a common category.

### How to Play
1. Click 4 tiles to select them
2. Tiles are automatically checked when 4 are selected
3. If correct, the group is revealed at the top
4. If incorrect, you lose one life
5. You have 4 mistakes before game over

### Difficulty Levels
- **Yellow (Easy)**: Straightforward - 100 points
- **Green (Medium)**: Requires thought - 150 points
- **Blue (Hard)**: Tricky connections - 200 points
- **Purple (Tricky)**: Very challenging - 250 points

### Tips
- Start with obvious connections
- Use Shuffle to see tiles differently
- Use Hint when stuck (costs 50 points)
- Game auto-saves your progress

---