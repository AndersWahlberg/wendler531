package fi.utu.wendler531.app;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WendlerProgramServiceTest {

    private final WendlerProgramService service = new WendlerProgramService();

    @Test
    void week1_returnsThreeSetsWithCorrectReps() {
        List<SetPrescription> sets = service.getMainWorkSets(180.0, 1);

        assertEquals(3, sets.size());
        assertEquals(5, sets.get(0).targetReps());
        assertEquals(5, sets.get(1).targetReps());
        assertEquals(5, sets.get(2).targetReps());
    }

    @Test
    void week1_weightsAreRoundedToNearest2p5() {
        // TM 180, week1 percents: 65/75/85
        // 180*0.65 = 117.0 -> 117.5 (nearest 2.5)
        // 180*0.75 = 135.0 -> 135.0
        // 180*0.85 = 153.0 -> 152.5 (nearest 2.5)
        List<SetPrescription> sets = service.getMainWorkSets(180.0, 1);

        assertEquals(117.5, sets.get(0).weightKg(), 0.0001);
        assertEquals(135.0, sets.get(1).weightKg(), 0.0001);
        assertEquals(152.5, sets.get(2).weightKg(), 0.0001);
    }

    @Test
    void invalidWeek_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getMainWorkSets(180.0, 0));
        assertThrows(IllegalArgumentException.class, () -> service.getMainWorkSets(180.0, 5));
    }

    @Test
    void invalidTm_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.getMainWorkSets(0, 1));
        assertThrows(IllegalArgumentException.class, () -> service.getMainWorkSets(-10, 1));
    }
}