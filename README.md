# Wendler 5/3/1 CLI Tracker

A Java command-line application for tracking Wendler 5/3/1 training cycles.  
This project was built as a course assignment for **UTU TKO_7001 / Olio-ohjelmoinnin perusteet**.

The application focuses on:
- object-oriented design
- persistent data storage
- unit testing
- Maven-based build automation
- a runnable JAR package

## Features

- Console-based menu navigation
- User profile management
  - name
  - height
  - bodyweight history
  - BMI calculation
- Lift settings for main lifts
  - one-rep max values
  - training max percentage
- Wendler 5/3/1 workout calculation
  - warm-up sets
  - main work sets
  - week-based progression
- Assistance exercise support
- Workout logging and workout history
- Estimated 1RM / PR tracking
- Persistent save/load using JSON
- Input validation so incorrect input does not crash the application

## Project structure

The code is divided into clear responsibilities.

### Main classes

- `Main`  
  Starts the application.

- `ConsoleUI`  
  Handles the command-line user interface, menu navigation, and input handling.

### Application logic

- `AppState`  
  Stores the current application state.

- `LiftSettings`  
  Stores lift-related settings such as one-rep max values and training max settings.

- `WendlerProgramService`  
  Calculates warm-up sets and main work sets for the Wendler 5/3/1 program.

- `WorkoutHistoryService`  
  Handles workout history logic.

- `AssistanceService`  
  Provides assistance exercise logic and default assistance exercise data.

### Data model

- `UserProfile`  
  Stores user profile data such as name, height, and bodyweight history.

- `WorkoutLogEntry`  
  Represents a saved workout entry.

- `SetPrescription`  
  Represents a calculated set with weight and reps.

- `AssistanceExercise`  
  Represents an assistance exercise.

### Estimation interface

- `OneRepMaxEstimator`  
  A self-defined interface for one-rep max estimation.

- `EpleyEstimator`  
  One implementation of the `OneRepMaxEstimator` interface.

- `BrzyckiEstimator`  
  Another implementation of the `OneRepMaxEstimator` interface.

### Persistence

- `SimpleJsonStorage`  
  Loads and saves application data as JSON.

## Course requirement highlights

This project includes the following required course elements:

- object-oriented class structure
- minimal `main` method
- a self-defined interface:
  - `OneRepMaxEstimator`
- two implementations of the interface:
  - `EpleyEstimator`
  - `BrzyckiEstimator`
- file persistence
- automated unit tests
- JavaDoc comments and generated documentation
- Maven build configuration
- executable JAR packaging

## Technologies

- Java 17
- Maven
- Gson
- JUnit 5

## Build and run

### Requirements

Before running the application, make sure you have installed:

- Java JDK 17 or newer
- Apache Maven

### Run the application

After cloning or downloading the repository, open a terminal in the project directory (where `pom.xml` is located).

To compile and launch the application, run:

```bash
mvn clean compile exec:java
```

**Important:** If you are running the application for the first time, use the command above instead of just `mvn exec:java`. The application must be compiled before Maven can find and execute the main class.

### Build an executable JAR

To package the application into an executable JAR file, run:

```bash
mvn clean package
```

Once the build succeeds, launch the application with:

```bash
java -jar target/wendler531-1.0-SNAPSHOT.jar
```

### Run tests

To execute the automated tests, run:

```bash
mvn test
```

## Tests

The project includes tests for core functionality, including:

- one-rep max estimation
- Wendler set calculation
- workout history logic
- JSON storage behavior

## Persistence

Application data is saved to JSON so that profile data, lift settings, estimator selection, assistance exercise data, and workout history can be loaded again between runs.

## Example workflow

1. Start the program
2. Create or update your profile
3. Set one-rep max values for the main lifts
4. Start a workout for a selected lift and week
5. Enter completed reps
6. View workout history
7. Close the program and continue later with saved data
