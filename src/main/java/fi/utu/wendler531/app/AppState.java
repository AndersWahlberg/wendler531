package fi.utu.wendler531.app;

import java.util.ArrayList;
import java.util.List;

public class AppState {

    private UserProfile userProfile;
    private LiftSettings liftSettings;

    // uusi: treenihistoria
    private List<WorkoutLogEntry> workoutHistory = new ArrayList<>();

    public AppState() {
        // varmistetaan ettei workoutHistory ole null
        if (workoutHistory == null) {
            workoutHistory = new ArrayList<>();
        }
    }

    public AppState(UserProfile userProfile, LiftSettings liftSettings) {
        this.userProfile = userProfile;
        this.liftSettings = liftSettings;
        this.workoutHistory = new ArrayList<>();
    }

    
    public AppState(UserProfile userProfile, LiftSettings liftSettings, List<WorkoutLogEntry> workoutHistory) {
        this.userProfile = userProfile;
        this.liftSettings = liftSettings;
        this.workoutHistory = (workoutHistory != null) ? workoutHistory : new ArrayList<>();
    }

    public static AppState defaultState() {
        return new AppState(new UserProfile(), new LiftSettings(), new ArrayList<>());
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public LiftSettings getLiftSettings() {
        return liftSettings;
    }

    public void setLiftSettings(LiftSettings liftSettings) {
        this.liftSettings = liftSettings;
    }

    public List<WorkoutLogEntry> getWorkoutHistory() {
        return workoutHistory;
    }

    public void setWorkoutHistory(List<WorkoutLogEntry> workoutHistory) {
        this.workoutHistory = (workoutHistory != null) ? workoutHistory : new ArrayList<>();
    }

    public void addWorkout(WorkoutLogEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Workout entry cannot be null");
        }
        workoutHistory.add(entry);
    }
}