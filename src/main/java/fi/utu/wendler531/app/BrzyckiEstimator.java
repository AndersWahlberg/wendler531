package fi.utu.wendler531.app;

/**
 * One-rep max estimator implementation based on the Brzycki formula.
 */
public class BrzyckiEstimator implements OneRepMaxEstimator {

    /**
     * Estimates a one-rep max from the lifted weight and completed repetitions
     * using the Brzycki formula.
     *
     * @param weightKg the lifted weight in kilograms
     * @param reps the number of completed repetitions
     * @return the estimated one-rep max in kilograms
     * @throws IllegalArgumentException if the weight is not positive, reps is less than 1,
     *                                  or reps is too high for the Brzycki formula
     */
    @Override
    public double estimate(double weightKg, int reps) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        if (reps < 1) {
            throw new IllegalArgumentException("Reps must be at least 1");
        }
        if (reps >= 37) {
            throw new IllegalArgumentException(
                    "Brzycki formula is not reliable for very high rep counts (maximum 36 reps)"
            );
        }

        // Brzycki formula: 1RM = weight * 36 / (37 - reps)
        return weightKg * 36.0 / (37.0 - reps);
    }

    /**
     * Returns the display name of this estimator.
     *
     * @return the estimator name
     */
    @Override
    public String name() {
        return "Brzycki";
    }
}