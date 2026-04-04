package fi.utu.wendler531.app;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Stores one-rep max values and training max settings for the main lifts
 * used in the Wendler 5/3/1 program.
 */
public class LiftSettings {

    /**
     * Supported main lifts in the Wendler 5/3/1 program.
     */
    public enum MainLift {
        SQUAT, BENCH_PRESS, DEADLIFT, OVERHEAD_PRESS
    }

    /**
     * Training max percentage used to calculate training max from one-rep max.
     * Default value is 90%.
     */
    private double tmPercent = 0.90;

    /**
     * Stores one-rep max values in kilograms for each main lift.
     */
    private final Map<MainLift, Double> oneRepMaxKg = new EnumMap<>(MainLift.class);

    /**
     * Creates lift settings with the default training max percentage
     * and an empty set of one-rep max values.
     */
    public LiftSettings() {
    }

    /**
     * Returns the configured training max percentage.
     *
     * @return the training max percentage
     */
    public double getTmPercent() {
        return tmPercent;
    }

    /**
     * Sets the training max percentage.
     *
     * @param tmPercent the training max percentage as a decimal value
     *                  (for example {@code 0.90} for 90%)
     * @throws IllegalArgumentException if the value is outside the allowed range
     */
    public void setTmPercent(double tmPercent) {
        if (tmPercent < 0.75 || tmPercent > 0.95) {
            throw new IllegalArgumentException("Training max percentage must be between 0.75 and 0.95");
        }
        this.tmPercent = tmPercent;
    }

    /**
     * Stores the one-rep max value for the given main lift.
     *
     * @param lift the main lift
     * @param kg the one-rep max in kilograms
     * @throws NullPointerException if lift is null
     * @throws IllegalArgumentException if kg is outside the allowed range
     */
    public void setOneRepMax(MainLift lift, double kg) {
        Objects.requireNonNull(lift, "Main lift cannot be null");

        if (kg <= 0 || kg > 600) {
            throw new IllegalArgumentException("One-rep max must be between 0 and 600 kg");
        }
        oneRepMaxKg.put(lift, kg);
    }

    /**
     * Returns whether a one-rep max has been set for the given lift.
     *
     * @param lift the main lift
     * @return {@code true} if a one-rep max exists for the lift, otherwise {@code false}
     * @throws NullPointerException if lift is null
     */
    public boolean hasOneRepMax(MainLift lift) {
        Objects.requireNonNull(lift, "Main lift cannot be null");
        return oneRepMaxKg.containsKey(lift);
    }

    /**
     * Returns the stored one-rep max for the given lift.
     *
     * @param lift the main lift
     * @return the stored one-rep max, or {@code null} if not set
     * @throws NullPointerException if lift is null
     */
    public Double getOneRepMax(MainLift lift) {
        Objects.requireNonNull(lift, "Main lift cannot be null");
        return oneRepMaxKg.get(lift);
    }

    /**
     * Returns whether a training max can be calculated for the given lift.
     * A training max exists when a one-rep max has been set.
     *
     * @param lift the main lift
     * @return {@code true} if a training max can be calculated, otherwise {@code false}
     * @throws NullPointerException if lift is null
     */
    public boolean hasTrainingMax(MainLift lift) {
        return hasOneRepMax(lift);
    }

    /**
     * Calculates and returns the training max for the given lift.
     *
     * @param lift the main lift
     * @return the training max in kilograms
     * @throws NullPointerException if lift is null
     * @throws IllegalStateException if no one-rep max has been set for the lift
     */
    public double getTrainingMax(MainLift lift) {
        Objects.requireNonNull(lift, "Main lift cannot be null");

        Double orm = oneRepMaxKg.get(lift);
        if (orm == null) {
            throw new IllegalStateException("No one-rep max has been set for this lift");
        }
        return orm * tmPercent;
    }

    /**
     * Returns all supported main lifts.
     *
     * @return an array of main lifts
     */
    public MainLift[] getMainLifts() {
        return MainLift.values();
    }

    /**
     * Returns a defensive copy of all stored one-rep max values.
     *
     * @return a copy of the one-rep max map
     */
    public Map<MainLift, Double> getOneRepMaxes() {
        return new EnumMap<>(oneRepMaxKg);
    }
}