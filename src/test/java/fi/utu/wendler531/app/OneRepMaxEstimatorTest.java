package fi.utu.wendler531.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for one-rep max estimator implementations.
 */
class OneRepMaxEstimatorTest {

    @Test
    void epleyEstimator_estimatesCorrectly() {
        OneRepMaxEstimator estimator = new EpleyEstimator();

        // Epley formula: 1RM = weight * (1 + reps / 30)
        // 100 * (1 + 5 / 30) = 116.666...
        double result = estimator.estimate(100.0, 5);

        assertEquals(116.6667, result, 0.001);
        assertEquals("Epley", estimator.name());
    }

    @Test
    void brzyckiEstimator_estimatesCorrectly() {
        OneRepMaxEstimator estimator = new BrzyckiEstimator();

        // Brzycki formula: 1RM = weight * 36 / (37 - reps)
        // 100 * 36 / 32 = 112.5
        double result = estimator.estimate(100.0, 5);

        assertEquals(112.5, result, 0.0001);
        assertEquals("Brzycki", estimator.name());
    }

    @Test
    void estimators_throwOnInvalidInputs() {
        OneRepMaxEstimator epley = new EpleyEstimator();
        OneRepMaxEstimator brzycki = new BrzyckiEstimator();

        assertThrows(IllegalArgumentException.class, () -> epley.estimate(0, 5));
        assertThrows(IllegalArgumentException.class, () -> epley.estimate(100, 0));

        assertThrows(IllegalArgumentException.class, () -> brzycki.estimate(0, 5));
        assertThrows(IllegalArgumentException.class, () -> brzycki.estimate(100, 0));
    }

    @Test
    void brzyckiEstimator_rejectsVeryHighReps() {
        OneRepMaxEstimator brzycki = new BrzyckiEstimator();

        assertThrows(IllegalArgumentException.class, () -> brzycki.estimate(100, 37));
        assertThrows(IllegalArgumentException.class, () -> brzycki.estimate(100, 100));
    }
}