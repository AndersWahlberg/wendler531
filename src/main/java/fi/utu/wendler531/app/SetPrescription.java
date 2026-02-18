package fi.utu.wendler531.app;

/**
 * A prescription for one main work set in Wendler 5/3/1.
 *
 * @param setNumber set index 1..3
 * @param targetReps target reps for the set (e.g. 5, 3, 1)
 * @param weightKg prescribed weight in kilograms (based on TM and week scheme)
 */
public record SetPrescription(int setNumber, int targetReps, double weightKg) { }