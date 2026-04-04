package fi.utu.wendler531.app;

/**
 * Strategy interface for estimating a one-rep max (1RM) value
 * from a performed set.
 *
 * <p>Different implementations can use different estimation formulas,
 * such as Epley or Brzycki.</p>
 */
public interface OneRepMaxEstimator {

    /**
     * Estimates a one-rep max in kilograms from a performed set.
     *
     * @param weightKg the weight used in kilograms
     * @param reps the number of repetitions performed
     * @return the estimated one-rep max in kilograms
     * @throws IllegalArgumentException if the input values are invalid
     */
    double estimate(double weightKg, int reps);

    /**
     * Returns the display name of the estimator formula.
     *
     * @return the estimator name
     */
    String name();
}