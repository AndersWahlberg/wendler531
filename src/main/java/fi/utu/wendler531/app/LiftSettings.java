package fi.utu.wendler531.app;

import java.util.EnumMap;
import java.util.Map;

public class LiftSettings {

    public LiftSettings() {
    }

    public enum MainLift {
        SQUAT, BENCH_PRESS, DEADLIFT, OVERHEAD_PRESS
    }

    private double tmPercent = 0.90; // 90% oletus
    private final Map<MainLift, Double> oneRepMaxKg = new EnumMap<>(MainLift.class);

    public double getTmPercent() {
        return tmPercent;
    }

    public void setTmPercent(double tmPercent) {
        if (tmPercent < 0.75 || tmPercent > 0.95) {
            throw new IllegalArgumentException("TM-% pitää olla välillä 75 - 95.");
        }
        this.tmPercent = tmPercent;
    }

    public void setOneRepMax(MainLift lift, double kg) {
        if (kg <= 0 || kg > 600) {
            throw new IllegalArgumentException("1RM pitää olla välillä 0 - 600 kg.");
        }
        oneRepMaxKg.put(lift, kg);
    }

    public boolean hasOneRepMax(MainLift lift) {
        return oneRepMaxKg.containsKey(lift);
    }

    public Double getOneRepMax(MainLift lift) {
        return oneRepMaxKg.get(lift); // voi olla null
    }

    public boolean hasTrainingMax(MainLift lift) {
        return hasOneRepMax(lift);
    }

    public double getTrainingMax(MainLift lift) {
        Double orm = oneRepMaxKg.get(lift);
        if (orm == null) {
            throw new IllegalStateException("Liikkeelle ei ole asetettu 1RM-arvoa.");
        }
        return orm * tmPercent;
    }

    public MainLift[] getMainLifts() {
        return MainLift.values();
    }

    public Map<MainLift, Double> getOneRepMaxes() {
        return new EnumMap<>(oneRepMaxKg);
    }
}