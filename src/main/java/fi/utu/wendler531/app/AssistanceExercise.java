package fi.utu.wendler531.app;

import java.util.Objects;

/**
 * Represents a single assistance exercise with its name
 * and target set/rep scheme.
 */
public class AssistanceExercise {

    private String name;
    private int sets;
    private int reps;

    /**
     * No-argument constructor for serialization frameworks.
     */
    public AssistanceExercise() {
    }

    /**
     * Creates a new assistance exercise.
     *
     * @param name the exercise name
     * @param sets the number of sets
     * @param reps the number of repetitions per set
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is blank or sets/reps are not positive
     */
    public AssistanceExercise(String name, int sets, int reps) {
        Objects.requireNonNull(name, "Exercise name cannot be null");

        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Exercise name cannot be blank");
        }
        if (sets <= 0 || reps <= 0) {
            throw new IllegalArgumentException("Sets and reps must be positive");
        }

        this.name = trimmed;
        this.sets = sets;
        this.reps = reps;
    }

    /**
     * Returns the exercise name.
     *
     * @return the exercise name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the exercise name.
     *
     * @param name the exercise name
     * @throws NullPointerException if name is null
     * @throws IllegalArgumentException if name is blank
     */
    public void setName(String name) {
        Objects.requireNonNull(name, "Exercise name cannot be null");

        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Exercise name cannot be blank");
        }

        this.name = trimmed;
    }

    /**
     * Returns the number of sets.
     *
     * @return the number of sets
     */
    public int getSets() {
        return sets;
    }

    /**
     * Sets the number of sets.
     *
     * @param sets the number of sets
     * @throws IllegalArgumentException if sets is not positive
     */
    public void setSets(int sets) {
        if (sets <= 0) {
            throw new IllegalArgumentException("Sets must be positive");
        }
        this.sets = sets;
    }

    /**
     * Returns the number of repetitions per set.
     *
     * @return the number of repetitions per set
     */
    public int getReps() {
        return reps;
    }

    /**
     * Sets the number of repetitions per set.
     *
     * @param reps the number of repetitions per set
     * @throws IllegalArgumentException if reps is not positive
     */
    public void setReps(int reps) {
        if (reps <= 0) {
            throw new IllegalArgumentException("Reps must be positive");
        }
        this.reps = reps;
    }

    /**
     * Returns a compact string representation such as {@code "Ylätalja 3x10"}.
     *
     * @return the formatted exercise string
     */
    @Override
    public String toString() {
        return name + " " + sets + "x" + reps;
    }
}