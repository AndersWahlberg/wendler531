package fi.utu.wendler531.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single completed workout entry stored in workout history.
 *
 * <p>A workout entry contains the workout date, the selected main lift,
 * the Wendler training week, the training max used for the session,
 * and the performed weights and reps for the recorded work sets.</p>
 *
 * <p>This record is intentionally defensive when handling invalid or incomplete
 * data loaded from storage. Instead of failing immediately, it normalizes some
 * fields to safe default values so that older or partially broken JSON data
 * does not crash the application.</p>
 *
 * @param dateIso the workout date in ISO-8601 string format
 * @param lift the main lift used in the workout
 * @param week the Wendler training week
 * @param tmKg the training max used for the workout in kilograms
 * @param weightsPerSet the performed weights for each set
 * @param repsPerSet the performed repetitions for each set
 */
public record WorkoutLogEntry(
        String dateIso,
        LiftSettings.MainLift lift,
        int week,
        double tmKg,
        List<Double> weightsPerSet,
        List<Integer> repsPerSet
) {

    /**
     * Creates a workout log entry and normalizes invalid or missing values to safe defaults.
     *
     * <p>This constructor is intentionally tolerant because stored JSON data may
     * come from older versions of the application or contain missing fields.</p>
     */
    public WorkoutLogEntry {
        // Keep old or partially broken JSON entries loadable.
        if (dateIso == null) {
            dateIso = "";
        }

        // Use a safe default lift if the stored value is missing.
        // Another possible approach would be to reject the whole entry during loading.
        if (lift == null) {
            lift = LiftSettings.MainLift.SQUAT;
        }

        // Week must stay within the Wendler cycle range.
        if (week < 1 || week > 4) {
            week = 1;
        }

        // Training max cannot be negative.
        // Zero is allowed for older entries where TM may not have been stored.
        if (tmKg < 0) {
            tmKg = 0.0;
        }

        // Ensure lists are never null and store them as mutable copies.
        if (weightsPerSet == null) {
            weightsPerSet = List.of();
        }
        if (repsPerSet == null) {
            repsPerSet = List.of();
        }

        weightsPerSet = new ArrayList<>(weightsPerSet);
        repsPerSet = new ArrayList<>(repsPerSet);
    }

    /**
     * Creates a simpler workout entry for cases where only reps are stored.
     *
     * <p>This is mainly intended for older or simplified workout history entries
     * that do not include training max or set weight data.</p>
     *
     * @param dateIso workout date in ISO format
     * @param lift the main lift
     * @param week the Wendler week
     * @param repsPerSet completed reps for each set
     */
    public WorkoutLogEntry(String dateIso, LiftSettings.MainLift lift, int week, List<Integer> repsPerSet) {
        this(dateIso, lift, week, 0.0, List.of(), repsPerSet);
    }
}