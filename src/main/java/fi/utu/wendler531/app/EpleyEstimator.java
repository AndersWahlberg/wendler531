package fi.utu.wendler531.app;

/**
 * One-rep max estimator implementation based on the Epley formula.
 */
public class EpleyEstimator implements OneRepMaxEstimator {

    /**
     * Estimates a one-rep max from the lifted weight and completed repetitions
     * using the Epley formula.
     *
     * @param weightKg the lifted weight in kilograms
     * @param reps the number of completed repetitions
     * @return the estimated one-rep max in kilograms
     * @throws IllegalArgumentException if the weight is not positive or reps is less than 1
     */
    @Override
    public double estimate(double weightKg, int reps) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        if (reps < 1) {
            throw new IllegalArgumentException("Reps must be at least 1");
        }

        // Epley formula: 1RM = weight * (1 + reps / 30)
        return weightKg * (1.0 + reps / 30.0);
    }

    /**
     * Returns the display name of this estimator.
     *
     * @return the estimator name
     */
    @Override
    public String name() {
        return "Epley";
    }
}