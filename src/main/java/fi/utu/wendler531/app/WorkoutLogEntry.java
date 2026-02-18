package fi.utu.wendler531.app;

import java.util.List;

/**
 * One logged workout for a main lift.
 *
 * @param dateIso ISO date string (yyyy-MM-dd)
 * @param lift main lift
 * @param week Wendler week (1..4)
 * @param repsPerSet actual reps done for each prescribed main set (size 3)
 */
public record WorkoutLogEntry(
        String dateIso,
        LiftSettings.MainLift lift,
        int week,
        List<Integer> repsPerSet
) { }