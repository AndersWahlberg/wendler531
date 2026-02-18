package fi.utu.wendler531.app;

import java.util.Comparator;
import java.util.Optional;

/**
 * Service for reading workout history and making simple suggestions
 * (e.g. show latest workout for a lift + suggest next Wendler week).
 */
public class WorkoutHistoryService {

    public Optional<WorkoutLogEntry> findLatestForLift(AppState state, LiftSettings.MainLift lift) {
        if (state == null) {
            throw new IllegalArgumentException("AppState cannot be null");
        }
        if (lift == null) {
            throw new IllegalArgumentException("Lift cannot be null");
        }

        return state.getWorkoutHistory().stream()
                .filter(e -> e != null && e.lift() == lift)
                // ISO yyyy-MM-dd sorts correctly lexicographically
                .max(Comparator.comparing(WorkoutLogEntry::dateIso));
    }

    public int suggestNextWeek(Optional<WorkoutLogEntry> latest) {
        if (latest == null || latest.isEmpty()) {
            return 1;
        }

        int w = latest.get().week();
        if (w < 1 || w > 4) {
            return 1;
        }

        return (w == 4) ? 1 : (w + 1);
    }

    public int suggestNextWeek(AppState state, LiftSettings.MainLift lift) {
        return suggestNextWeek(findLatestForLift(state, lift));
    }

    public String formatLatest(Optional<WorkoutLogEntry> latest) {
        if (latest == null || latest.isEmpty()) {
            return "Ei aiempia treenejä tälle liikkeelle.";
        }
        WorkoutLogEntry e = latest.get();
        String reps = formatReps(e.repsPerSet());
        return "Viimeksi: " + e.dateIso() + ", viikko " + e.week() + ", reps: " + reps;
    }

    private String formatReps(java.util.List<Integer> repsPerSet) {
        if (repsPerSet == null || repsPerSet.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repsPerSet.size(); i++) {
            if (i > 0) sb.append("/");
            sb.append(repsPerSet.get(i));
        }
        return sb.toString();
    }
}