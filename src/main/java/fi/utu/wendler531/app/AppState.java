package fi.utu.wendler531.app;

import fi.utu.wendler531.app.LiftSettings.MainLift;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Stores the full application state of the Wendler 5/3/1 tracker.
 *
 * <p>This includes user profile data, lift settings, assistance exercise data,
 * workout history, and the currently selected one-rep max estimator type.</p>
 */
public class AppState {

    /**
     * Supported one-rep max estimation formulas.
     */
    public enum EstimatorType {
        EPLEY, BRZYCKI
    }

    /**
     * Stores the user's profile data, such as body weight history and height.
     */
    private UserProfile userProfile;

    /**
     * Stores lift-related settings, such as one-rep max values and training max settings.
     */
    private LiftSettings liftSettings;

    /**
     * Stores assistance exercises grouped by main lift.
     */
    private Map<MainLift, List<AssistanceExercise>> assistanceByLift;

    /**
     * Stores workout history as a list of completed workout entries.
     * Entries are typically kept in chronological order.
     */
    private List<WorkoutLogEntry> workoutHistory = new ArrayList<>();

    /**
     * Stores the currently selected one-rep max estimation method.
     * Epley is used as the default option.
     */
    private EstimatorType oneRepMaxEstimatorType = EstimatorType.EPLEY;

    /**
     * Creates an application state with default-initialized collections
     * and the default estimator type.
     */
    public AppState() {
        if (assistanceByLift == null) {
            assistanceByLift = buildDefaultAssistance();
        }
        if (workoutHistory == null) {
            workoutHistory = new ArrayList<>();
        }
        if (oneRepMaxEstimatorType == null) {
            oneRepMaxEstimatorType = EstimatorType.EPLEY;
        }
    }

    /**
     * Creates an application state with the given user profile and lift settings.
     * Workout history is initialized as empty and default assistance data is created.
     *
     * @param userProfile the user profile
     * @param liftSettings the lift settings
     */
    public AppState(UserProfile userProfile, LiftSettings liftSettings) {
        this.userProfile = userProfile;
        this.liftSettings = liftSettings;
        this.workoutHistory = new ArrayList<>();
        this.assistanceByLift = buildDefaultAssistance();
        this.oneRepMaxEstimatorType = EstimatorType.EPLEY;
    }

    /**
     * Creates an application state with the given user profile, lift settings,
     * and workout history. If the provided workout history is null,
     * an empty history list is created instead.
     *
     * @param userProfile the user profile
     * @param liftSettings the lift settings
     * @param workoutHistory the workout history, or null
     */
    public AppState(UserProfile userProfile, LiftSettings liftSettings, List<WorkoutLogEntry> workoutHistory) {
        this.userProfile = userProfile;
        this.liftSettings = liftSettings;
        this.workoutHistory = (workoutHistory != null) ? workoutHistory : new ArrayList<>();
        this.assistanceByLift = buildDefaultAssistance();
        this.oneRepMaxEstimatorType = EstimatorType.EPLEY;
    }

    /**
     * Creates a default application state with an empty user profile,
     * default lift settings, and empty workout history.
     *
     * @return a default application state
     */
    public static AppState defaultState() {
        return new AppState(new UserProfile(), new LiftSettings(), new ArrayList<>());
    }

    // ===== One-rep max estimator =====

    /**
     * Returns the currently selected estimator type.
     * If the estimator type is missing, the default value EPLEY is restored.
     *
     * @return the current estimator type
     */
    public EstimatorType getOneRepMaxEstimatorType() {
        if (oneRepMaxEstimatorType == null) {
            oneRepMaxEstimatorType = EstimatorType.EPLEY;
        }
        return oneRepMaxEstimatorType;
    }

    /**
     * Sets the currently selected estimator type.
     *
     * @param type the estimator type to use
     * @throws IllegalArgumentException if the estimator type is null
     */
    public void setOneRepMaxEstimatorType(EstimatorType type) {
        if (type == null) {
            throw new IllegalArgumentException("EstimatorType cannot be null");
        }
        this.oneRepMaxEstimatorType = type;
    }

    /**
     * Returns the currently active one-rep max estimator through the interface type.
     *
     * <p>This centralizes estimator creation and keeps the user interface
     * and other client code independent of concrete estimator classes.</p>
     *
     * @return the active {@link OneRepMaxEstimator} implementation
     */
    public OneRepMaxEstimator getOneRepMaxEstimator() {
        return switch (getOneRepMaxEstimatorType()) {
            case EPLEY -> new EpleyEstimator();
            case BRZYCKI -> new BrzyckiEstimator();
        };
    }

    // ===== Assistance exercises =====

    /**
     * Returns the assistance exercise mapping grouped by main lift.
     * If the mapping has not been initialized, default assistance data is created.
     *
     * @return the assistance exercise mapping
     */
    public Map<MainLift, List<AssistanceExercise>> getAssistanceByLift() {
        if (assistanceByLift == null) {
            assistanceByLift = buildDefaultAssistance();
        }
        return assistanceByLift;
    }

    /**
     * Sets the assistance exercise mapping.
     * If the given mapping is null, default assistance data is used instead.
     *
     * @param assistanceByLift the assistance exercise mapping, or null
     */
    public void setAssistanceByLift(Map<MainLift, List<AssistanceExercise>> assistanceByLift) {
        this.assistanceByLift = (assistanceByLift != null) ? assistanceByLift : buildDefaultAssistance();
    }

    /**
     * Builds the default assistance exercise plan for each main lift.
     *
     * @return a map containing default assistance exercises grouped by main lift
     */
    private static Map<MainLift, List<AssistanceExercise>> buildDefaultAssistance() {
        Map<MainLift, List<AssistanceExercise>> assistance = new EnumMap<>(MainLift.class);

        assistance.put(MainLift.SQUAT, List.of(
                new AssistanceExercise("Bulgarialainen askelkyykky", 3, 10),
                new AssistanceExercise("Käsipainosoutu", 3, 10),
                new AssistanceExercise("Voimapyörä", 3, 10)
        ));

        assistance.put(MainLift.BENCH_PRESS, List.of(
                new AssistanceExercise("Ylätalja", 3, 10),
                new AssistanceExercise("Kapea penkkipunnerrus tangolla", 3, 10),
                new AssistanceExercise("Roikkuen jalkojen nosto", 3, 10)
        ));

        assistance.put(MainLift.DEADLIFT, List.of(
                new AssistanceExercise("Romanialainen maastaveto", 3, 10),
                new AssistanceExercise("Leuanveto", 3, 10),
                new AssistanceExercise("Reverse hyper", 3, 10)
        ));

        assistance.put(MainLift.OVERHEAD_PRESS, List.of(
                new AssistanceExercise("Ylätalja", 3, 10),
                new AssistanceExercise("Vinopenkki käsipainoilla", 3, 10),
                new AssistanceExercise("Kävelyaskelkyykky", 3, 10)
        ));

        return assistance;
    }

    // ===== Other getters and setters =====

    /**
     * Returns the stored user profile.
     *
     * @return the user profile, or null if not set
     */
    public UserProfile getUserProfile() {
        return userProfile;
    }

    /**
     * Sets the user profile.
     *
     * @param userProfile the user profile, or null
     */
    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    /**
     * Returns the stored lift settings.
     *
     * @return the lift settings, or null if not set
     */
    public LiftSettings getLiftSettings() {
        return liftSettings;
    }

    /**
     * Sets the lift settings.
     *
     * @param liftSettings the lift settings, or null
     */
    public void setLiftSettings(LiftSettings liftSettings) {
        this.liftSettings = liftSettings;
    }

    /**
     * Returns the workout history list.
     * If the history list has not been initialized, an empty list is created.
     *
     * @return the workout history list
     */
    public List<WorkoutLogEntry> getWorkoutHistory() {
        if (workoutHistory == null) {
            workoutHistory = new ArrayList<>();
        }
        return workoutHistory;
    }

    /**
     * Sets the workout history list.
     * If the given list is null, an empty list is used instead.
     *
     * @param workoutHistory the workout history list, or null
     */
    public void setWorkoutHistory(List<WorkoutLogEntry> workoutHistory) {
        this.workoutHistory = (workoutHistory != null) ? workoutHistory : new ArrayList<>();
    }

    /**
     * Adds a completed workout entry to the workout history.
     *
     * @param entry the workout entry to add
     * @throws IllegalArgumentException if the given entry is null
     */
    public void addWorkout(WorkoutLogEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Workout entry cannot be null");
        }
        getWorkoutHistory().add(entry);
    }
}