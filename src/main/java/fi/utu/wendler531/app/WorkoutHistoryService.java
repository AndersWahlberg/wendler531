package fi.utu.wendler531.app;

import java.util.List;
import java.util.Optional;

/**
 * Provides helper methods for reading workout history
 * and suggesting the next Wendler training week.
 */
public class WorkoutHistoryService {

    /**
     * Finds the most recent workout entry for the given main lift.
     *
     * <p>The search starts from the end of the history list because entries are
     * assumed to be stored in chronological order, with the latest entry last.
     * This also works when multiple workouts exist for the same date, because
     * the last matching entry is treated as the most recent one.</p>
     *
     * @param state the current application state
     * @param lift the selected main lift
     * @return an {@link Optional} containing the latest matching workout entry,
     *         or an empty optional if none exists
     * @throws IllegalArgumentException if state or lift is null
     */
    public Optional<WorkoutLogEntry> findLatestForLift(AppState state, LiftSettings.MainLift lift) {
        if (state == null) {
            throw new IllegalArgumentException("AppState cannot be null");
        }
        if (lift == null) {
            throw new IllegalArgumentException("Lift cannot be null");
        }

        List<WorkoutLogEntry> history = state.getWorkoutHistory();
        if (history == null || history.isEmpty()) {
            return Optional.empty();
        }

        for (int i = history.size() - 1; i >= 0; i--) {
            WorkoutLogEntry entry = history.get(i);
            if (entry != null && entry.lift() == lift) {
                return Optional.of(entry);
            }
        }

        return Optional.empty();
    }

    /**
     * Suggests the next training week based on the latest completed workout.
     *
     * @param latest the latest workout entry, if available
     * @return week 1 if no valid previous week exists; otherwise the next week
     *         in the Wendler cycle
     */
    public int suggestNextWeek(Optional<WorkoutLogEntry> latest) {
        if (latest == null || latest.isEmpty()) {
            return 1;
        }

        int week = latest.get().week();
        if (week < 1 || week > 4) {
            return 1;
        }

        return (week == 4) ? 1 : (week + 1);
    }

    /**
     * Suggests the next training week for the given lift by first finding
     * the latest workout entry for that lift.
     *
     * @param state the current application state
     * @param lift the selected main lift
     * @return the suggested next Wendler week
     * @throws IllegalArgumentException if state or lift is null
     */
    public int suggestNextWeek(AppState state, LiftSettings.MainLift lift) {
        return suggestNextWeek(findLatestForLift(state, lift));
    }

    /**
     * Formats the latest workout entry as a short summary string for the UI.
     *
     * @param latest the latest workout entry, if available
     * @return a formatted summary string, or a fallback message if no entry exists
     */
    public String formatLatest(Optional<WorkoutLogEntry> latest) {
        if (latest == null || latest.isEmpty()) {
            return "Ei aiempia treenejä tälle liikkeelle.";
        }

        WorkoutLogEntry entry = latest.get();
        String reps = formatReps(entry.repsPerSet());
        return "Viimeksi: " + entry.dateIso() + ", viikko " + entry.week() + ", reps: " + reps;
    }

    /**
     * Formats a list of reps into a slash-separated string,
     * for example {@code "5/5/8"}.
     *
     * @param repsPerSet the reps performed in each set
     * @return a formatted reps string, or {@code "-"} if the list is null or empty
     */
    private String formatReps(List<Integer> repsPerSet) {
        if (repsPerSet == null || repsPerSet.isEmpty()) {
            return "-";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repsPerSet.size(); i++) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(repsPerSet.get(i));
        }
        return sb.toString();
    }
}