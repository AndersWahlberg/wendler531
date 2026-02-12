# Wendler 5/3/1 CLI tracker

A Java command-line application for tracking Wendler 5/3/1 training cycles.  
Built as a course project (UTU TKO 7001), with a focus on clean OOP structure, testing, and persistence.

## Current features (12.2.2026)
- Console-based menu navigation
- User profile: name, height, bodyweight history
- BMI calculation based on latest weight entry

## Planned features
- Lift settings (TM/1RM, rounding rules)
- Workout session logging and history view
- JSON save/load for persistent data
- Unit tests (JUnit) + JavaDoc + runnable JAR via Maven

## Tech stack
- Java
- Maven
- JUnit (planned)

## Running locally
```bash
mvn test
mvn package
java -jar target/<your-jar-name>.jar
