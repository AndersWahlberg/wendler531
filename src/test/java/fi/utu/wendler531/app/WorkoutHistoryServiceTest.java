package fi.utu.wendler531.app;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutHistoryServiceTest {

    private final WorkoutHistoryService service = new WorkoutHistoryService();

    @Test
    void suggestNextWeek_emptyHistory_returns1() {
        AppState state = AppState.defaultState();
        state.setWorkoutHistory(new ArrayList<>());

        int suggested = service.suggestNextWeek(state, LiftSettings.MainLift.SQUAT);
        assertEquals(1, suggested);
    }

    @Test
    void suggestNextWeek_afterWeek1_returns2() {
        AppState state = AppState.defaultState();
        state.setWorkoutHistory(new ArrayList<>());

        state.addWorkout(new WorkoutLogEntry(
                "2026-02-18",
                LiftSettings.MainLift.SQUAT,
                1,
                List.of(5, 5, 8)
        ));

        int suggested = service.suggestNextWeek(state, LiftSettings.MainLift.SQUAT);
        assertEquals(2, suggested);
    }

    @Test
    void suggestNextWeek_afterWeek4_returns1() {
        AppState state = AppState.defaultState();
        state.setWorkoutHistory(new ArrayList<>());

        state.addWorkout(new WorkoutLogEntry(
                "2026-02-18",
                LiftSettings.MainLift.SQUAT,
                4,
                List.of(5, 5, 5)
        ));

        int suggested = service.suggestNextWeek(state, LiftSettings.MainLift.SQUAT);
        assertEquals(1, suggested);
    }

    @Test
    void findLatestForLift_returnsLatestByDateIso() {
        AppState state = AppState.defaultState();
        state.setWorkoutHistory(new ArrayList<>());

        state.addWorkout(new WorkoutLogEntry("2026-02-10", LiftSettings.MainLift.SQUAT, 1, List.of(5, 5, 5)));
        state.addWorkout(new WorkoutLogEntry("2026-02-18", LiftSettings.MainLift.SQUAT, 2, List.of(3, 3, 5)));
        state.addWorkout(new WorkoutLogEntry("2026-02-12", LiftSettings.MainLift.BENCH_PRESS, 1, List.of(5, 5, 8)));

        var latestSquat = service.findLatestForLift(state, LiftSettings.MainLift.SQUAT);
        assertTrue(latestSquat.isPresent());
        assertEquals("2026-02-18", latestSquat.get().dateIso());
        assertEquals(2, latestSquat.get().week());
    }
}