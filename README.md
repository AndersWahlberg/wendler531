# Wendler 5/3/1 CLI tracker

A Java command-line application for tracking Wendler 5/3/1 training cycles.  
Built as a course project (UTU TKO 7001), with a focus on clean OOP structure, testing, and persistence.

## Current features (18.2.2026)

- Console-based menu navigation (CLI)
- Improved console output formatting (helper methods like `blankLine()`, `section(title)`, `pause()`)
- Persistent app state (JSON):
  - Loads saved state on startup
  - Falls back to a default state if loading fails (missing/corrupt file)
- User profile:
  - Name and height
  - Bodyweight history entries (date + weight)
  - BMI calculation based on latest weight entry
- Lift settings:
  - Training max / 1RM related settings
  - Rounding rules for set weights
- Wendler 5/3/1 cycle support (4-week cycle):
  - Week 1: 5s
  - Week 2: 3s
  - Week 3: 5/3/1
  - Week 4: deload
- Workout session logging
- Workout history view
- Unit tests (JUnit 5):
  - Tests run successfully with `mvn clean test`

## Planned features

- 1RM estimation via interface:
  - `OneRepMaxEstimator` with at least two implementations (Epley, Brzycki)

## Tech stack

- Java
- Maven
- JUnit 5
- JSON persistence (Gson)

## Running locally

“Running locally” means how to build, test, and run this project on your own computer (outside GitHub).  
Run these commands in the project root folder (where `pom.xml` is located).

### Run tests

Runs unit tests and verifies the code still works after changes.

- `mvn` = Maven build tool
- `clean` = removes old build output (clears `target/`)
- `test` = compiles what’s needed and runs JUnit tests

Commands:
- `mvn clean test`

### Build a runnable JAR

Compiles the project and packages it into a JAR file under `target/`.

- `package` = builds and packages the application (typically into a `.jar`)

Commands:
- `mvn clean package`

### Run

Runs the packaged JAR produced by `mvn package`.

- Replace `<your-jar-name>.jar` with the actual file name created under `target/`
  (example name might look like `wendler531-1.0-SNAPSHOT.jar` depending on your Maven config)

Commands:
- `java -jar target/<your-jar-name>.jar`

## Notes

This project aims to demonstrate a clean separation of responsibilities (Single Responsibility Principle):

- UI (ConsoleUI) is responsible only for input/output:
  - prints menus and messages
  - reads user input
  - displays results  
  It should not contain core training logic or calculations.

- Services contain the program logic:
  - validations
  - Wendler rules and calculations
  - history handling
  - feature workflows (“what happens when user selects X”)

- Data models/state store the application data:
  - user profile, bodyweight entries, app state, etc.
  - JSON save/load reads/writes these models
