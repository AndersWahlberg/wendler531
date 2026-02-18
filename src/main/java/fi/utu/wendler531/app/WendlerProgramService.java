package fi.utu.wendler531.app;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculates Wendler 5/3/1 main work sets based on Training Max (TM).
 * Keeps calculation logic out of UI.
 */
public class WendlerProgramService {

    /**
     * Returns the 3 main work sets for the given week (1..4) and TM.
     *
     * Week 1: 65/75/85% x 5/5/5
     * Week 2: 70/80/90% x 3/3/3
     * Week 3: 75/85/95% x 5/3/1
     * Week 4 (deload): 40/50/60% x 5/5/5
     *
     * @param trainingMaxKg training max (TM) in kg
     * @param week 1..4
     * @return list of 3 SetPrescription items
     */
    public List<SetPrescription> getMainWorkSets(double trainingMaxKg, int week) {
        if (trainingMaxKg <= 0 || trainingMaxKg > 1000) {
            throw new IllegalArgumentException("TM pitää olla järkevä positiivinen luku.");
        }
        if (week < 1 || week > 4) {
            throw new IllegalArgumentException("Viikon pitää olla välillä 1-4.");
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
            default -> { // 4
                percents = new double[]{0.40, 0.50, 0.60};
                reps = new int[]{5, 5, 5};
            }
        }

        List<SetPrescription> sets = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            double weight = trainingMaxKg * percents[i];

            // Pyöristys 2.5kg tarkkuuteen (yleinen salikäytäntö).
            weight = roundToNearest2p5(weight);

            sets.add(new SetPrescription(i + 1, reps[i], weight));
        }
        return sets;
    }

    private double roundToNearest2p5(double value) {
        return Math.round(value / 2.5) * 2.5;
    }
}