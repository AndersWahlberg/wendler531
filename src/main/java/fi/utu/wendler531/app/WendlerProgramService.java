package fi.utu.wendler531.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates Wendler 5/3/1 set prescriptions based on the training max (TM).
 *
 * <p>This service contains the program calculation logic for warm-up sets
 * and main work sets, keeping it separate from the user interface layer.</p>
 */
public class WendlerProgramService {

    /**
     * Returns the three warm-up sets for the given training max.
     *
     * <p>Warm-up sets:
     * 40% x 5, 50% x 5, 60% x 3.</p>
     *
     * @param trainingMaxKg the training max in kilograms
     * @return a list containing three warm-up set prescriptions
     * @throws IllegalArgumentException if the training max is not within the allowed range
     */
    public List<SetPrescription> getWarmUpSets(double trainingMaxKg) {
        validateTm(trainingMaxKg);

        double[] percents = new double[]{0.40, 0.50, 0.60};
        int[] reps = new int[]{5, 5, 3};

        List<SetPrescription> sets = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            double weight = trainingMaxKg * percents[i];
            weight = roundToNearest2p5(weight);
            sets.add(new SetPrescription(i + 1, reps[i], weight));
        }
        return sets;
    }

    /**
     * Returns the three main work sets for the given week and training max.
     *
     * <p>Week 1: 65/75/85% x 5/5/5<br>
     * Week 2: 70/80/90% x 3/3/3<br>
     * Week 3: 75/85/95% x 5/3/1<br>
     * Week 4 (deload): 40/50/60% x 5/5/5</p>
     *
     * @param trainingMaxKg the training max in kilograms
     * @param week the training week (1 to 4)
     * @return a list containing three main work set prescriptions
     * @throws IllegalArgumentException if the training max is invalid or week is outside 1-4
     */
    public List<SetPrescription> getMainWorkSets(double trainingMaxKg, int week) {
        validateTm(trainingMaxKg);

        if (week < 1 || week > 4) {
            throw new IllegalArgumentException("Week must be between 1 and 4");
        }

        double[] percents;
        int[] reps;

        switch (week) {
            case 1 -> {
                percents = new double[]{0.65, 0.75, 0.85};
                reps = new int[]{5, 5, 5};
            }
            case 2 -> {
                percents = new double[]{0.70, 0.80, 0.90};
                reps = new int[]{3, 3, 3};
            }
            case 3 -> {
                percents = new double[]{0.75, 0.85, 0.95};
                reps = new int[]{5, 3, 1};
            }
            default -> { // Week 4 deload
                percents = new double[]{0.40, 0.50, 0.60};
                reps = new int[]{5, 5, 5};
            }
        }

        List<SetPrescription> sets = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            double weight = trainingMaxKg * percents[i];
            weight = roundToNearest2p5(weight);
            sets.add(new SetPrescription(i + 1, reps[i], weight));
        }
        return sets;
    }

    /**
     * Validates that the training max is within a reasonable range.
     *
     * @param trainingMaxKg the training max in kilograms
     * @throws IllegalArgumentException if the value is not within the allowed range
     */
    private void validateTm(double trainingMaxKg) {
        if (trainingMaxKg <= 0 || trainingMaxKg > 1000) {
            throw new IllegalArgumentException("TM:n pitää olla positiivinen luku ja alle 1000 kg");
        }
    }

    /**
     * Rounds the given weight to the nearest 2.5 kilograms.
     *
     * @param value the weight value to round
     * @return the rounded weight
     */
    private double roundToNearest2p5(double value) {
        return Math.round(value / 2.5) * 2.5;
    }
}