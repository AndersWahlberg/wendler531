package fi.utu.wendler531.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {
    private String name;
    private List<BodyWeightEntry> bodyWeightHistory;
    private double heightCm;
    private Integer birthYear;

    public UserProfile() {
        this.bodyWeightHistory = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasBodyWeightEntries() {
        return !bodyWeightHistory.isEmpty();
    }

    
    public BodyWeightEntry getLatestBodyWeightEntry() {
        if (bodyWeightHistory.isEmpty()) {
            throw new IllegalStateException("Käyttäjällä ei ole painohistoriaa.");
        }
        return bodyWeightHistory.get(bodyWeightHistory.size() - 1);
    }

    public List<BodyWeightEntry> getBodyWeightHistory() {
        return new ArrayList<>(bodyWeightHistory);
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        if (heightCm <= 0) {
            throw new IllegalArgumentException("Pituus senttimetreinä on oltava positiivinen luku.");
        } else if (heightCm > 230) {
            throw new IllegalArgumentException("Pituus senttimetreinä ei voi olla yli 230 cm.");
        } else if (heightCm < 91) {
            throw new IllegalArgumentException("Pituus senttimetreinä ei voi olla alle 91 cm.");
        }
        this.heightCm = heightCm;
    }

    public void addBodyWeightEntry(double weightKg) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Paino kilogrammoina on oltava positiivinen luku.");
        } else if (weightKg > 300) {
            throw new IllegalArgumentException("Paino kilogrammoina ei voi olla yli 300 kg.");
        } else if (weightKg < 30) {
            throw new IllegalArgumentException("Paino kilogrammoina ei voi olla alle 30 kg.");
        }
        this.bodyWeightHistory.add(new BodyWeightEntry(LocalDate.now(), weightKg));
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        if (birthYear != null) {
            int currentYear = LocalDate.now().getYear();
            if (birthYear < 1900 || birthYear > currentYear) {
                throw new IllegalArgumentException("Syntymävuosi on oltava välillä 1900 - " + currentYear);
            }
        }
        this.birthYear = birthYear;
    }

    public double calculateBmi() {
        if (heightCm <= 0) {
            throw new IllegalStateException("Pituutta ei ole asetettu.");
        }
        if (bodyWeightHistory.isEmpty()) {
            throw new IllegalStateException("Ei painomerkintöjä.");
        }

        double heightM = heightCm / 100.0;
        double latestWeightKg = bodyWeightHistory.get(bodyWeightHistory.size() - 1).getWeightKg();
        return latestWeightKg / (heightM * heightM);
    }

    public static class BodyWeightEntry {
        private LocalDate date;
        private double weightKg;

        public BodyWeightEntry() {
            // Parametriton konstruktori JSON-serialisointia varten (esim. Gson)
        }

        public BodyWeightEntry(LocalDate date, double weightKg) {
            this.date = date;
            this.weightKg = weightKg;
        }

        public LocalDate getDate() {
            return date;
        }

        public double getWeightKg() {
            return weightKg;
        }
    }
}