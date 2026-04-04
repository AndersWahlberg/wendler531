package fi.utu.wendler531.app;

import fi.utu.wendler531.app.LiftSettings.MainLift;

import java.util.List;
import java.util.Map;

/**
 * Service class for accessing and resetting assistance exercise settings
 * for each main lift.
 */
public class AssistanceService {

    /**
     * Returns the assistance exercises currently configured for the given main lift.
     *
     * @param lift the selected main lift
     * @param state the current application state
     * @return the list of assistance exercises for the given lift
     * @throws IllegalArgumentException if state or lift is null
     * @throws IllegalStateException if assistance settings are missing for the given lift
     */
    public List<AssistanceExercise> getAssistanceFor(MainLift lift, AppState state) {
        if (state == null) {
            throw new IllegalArgumentException("AppState cannot be null");
        }
        if (lift == null) {
            throw new IllegalArgumentException("Lift cannot be null");
        }

        Map<MainLift, List<AssistanceExercise>> map = state.getAssistanceByLift();
        if (map == null || !map.containsKey(lift)) {
            throw new IllegalStateException("Assistance settings missing for " + lift);
        }

        List<AssistanceExercise> list = map.get(lift);
        if (list == null) {
            throw new IllegalStateException("Assistance list is null for " + lift);
        }

        return list;
    }

    /**
     * Resets all assistance exercise settings to their default values.
     *
     * @param state the current application state
     * @throws IllegalArgumentException if state is null
     */
    public void resetToDefaults(AppState state) {
        if (state == null) {
            throw new IllegalArgumentException("AppState cannot be null");
        }

        state.setAssistanceByLift(AppState.defaultState().getAssistanceByLift());
    }
}