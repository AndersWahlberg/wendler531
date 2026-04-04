package fi.utu.wendler531.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores user profile information such as name, height, birth year,
 * and body weight history.
 */
public class UserProfile {

    private String name;
    private List<BodyWeightEntry> bodyWeightHistory;
    private double heightCm;
    private Integer birthYear;

    /**
     * Creates an empty user profile with an initialized body weight history list.
     */
    public UserProfile() {
        this.bodyWeightHistory = new ArrayList<>();
    }

    /**
     * Returns the internal body weight history list, initializing it if needed.
     *
     * @return the internal body weight history list
     */
    private List<BodyWeightEntry> bodyWeightHistoryInternal() {
        if (bodyWeightHistory == null) {
            bodyWeightHistory = new ArrayList<>();
        }
        return bodyWeightHistory;
    }

    /**
     * Returns the user's name.
     *
     * @return the user's name, or {@code null} if not set
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name the user's name, or {@code null}
     */
    public void setName(String name) {
        this.name = (name == null) ? null : name.trim();
    }

    /**
     * Returns whether the user has any body weight entries.
     *
     * @return {@code true} if at least one body weight entry exists, otherwise {@code false}
     */
    public boolean hasBodyWeightEntries() {
        return !bodyWeightHistoryInternal().isEmpty();
    }

    /**
     * Returns the most recent body weight entry.
     *
     * @return the latest body weight entry
     * @throws IllegalStateException if no body weight entries exist
     */
    public BodyWeightEntry getLatestBodyWeightEntry() {
        if (bodyWeightHistoryInternal().isEmpty()) {
            throw new IllegalStateException("User has no body weight history");
        }
        return bodyWeightHistoryInternal().get(bodyWeightHistoryInternal().size() - 1);
    }

    /**
     * Returns a defensive copy of the body weight history.
     *
     * @return a copy of the body weight history list
     */
    public List<BodyWeightEntry> getBodyWeightHistory() {
        return new ArrayList<>(bodyWeightHistoryInternal());
    }

    /**
     * Returns the user's height in centimeters.
     *
     * @return the height in centimeters
     */
    public double getHeightCm() {
        return heightCm;
    }

    /**
     * Sets the user's height in centimeters.
     *
     * @param heightCm the height in centimeters
     * @throws IllegalArgumentException if height is outside the allowed range
     */
    public void setHeightCm(double heightCm) {
        if (heightCm <= 0) {
            throw new IllegalArgumentException("Height must be a positive number");
        } else if (heightCm > 230) {
            throw new IllegalArgumentException("Height cannot be greater than 230 cm");
        } else if (heightCm < 91) {
            throw new IllegalArgumentException("Height cannot be less than 91 cm");
        }
        this.heightCm = heightCm;
    }

    /**
     * Adds a new body weight entry using the current date.
     *
     * @param weightKg the body weight in kilograms
     * @throws IllegalArgumentException if weight is outside the allowed range
     */
    public void addBodyWeightEntry(double weightKg) {
        if (weightKg <= 0) {
            throw new IllegalArgumentException("Weight must be a positive number");
        } else if (weightKg > 300) {
            throw new IllegalArgumentException("Weight cannot be greater than 300 kg");
        } else if (weightKg < 30) {
            throw new IllegalArgumentException("Weight cannot be less than 30 kg");
        }
        bodyWeightHistoryInternal().add(new BodyWeightEntry(LocalDate.now(), weightKg));
    }

    /**
     * Returns the user's birth year.
     *
     * @return the birth year, or {@code null} if not set
     */
    public Integer getBirthYear() {
        return birthYear;
    }

    /**
     * Sets the user's birth year.
     *
     * @param birthYear the birth year, or {@code null}
     * @throws IllegalArgumentException if the birth year is outside the allowed range
     */
    public void setBirthYear(Integer birthYear) {
        if (birthYear != null) {
            int currentYear = LocalDate.now().getYear();
            if (birthYear < 1900 || birthYear > currentYear) {
                throw new IllegalArgumentException("Birth year must be between 1900 and " + currentYear);
            }
        }
        this.birthYear = birthYear;
    }

    /**
     * Calculates BMI using the latest recorded body weight.
     *
     * @return the calculated BMI
     * @throws IllegalStateException if height is not set or no body weight entries exist
     */
    public double calculateBmi() {
        if (heightCm <= 0) {
            throw new IllegalStateException("Height has not been set");
        }
        if (bodyWeightHistoryInternal().isEmpty()) {
            throw new IllegalStateException("No body weight entries available");
        }

        double heightM = heightCm / 100.0;
        double latestWeightKg = getLatestBodyWeightEntry().getWeightKg();
        return latestWeightKg / (heightM * heightM);
    }

    /**
     * Represents a single body weight entry with a date and weight value.
     */
    public static class BodyWeightEntry {

        private LocalDate date;
        private double weightKg;

        /**
         * No-argument constructor for serialization frameworks.
         */
        public BodyWeightEntry() {
        }

        /**
         * Creates a new body weight entry.
         *
         * @param date the entry date
         * @param weightKg the body weight in kilograms
         * @throws IllegalArgumentException if the date is null or the weight is not positive
         */
        public BodyWeightEntry(LocalDate date, double weightKg) {
            if (date == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }
            if (weightKg <= 0) {
                throw new IllegalArgumentException("Weight must be a positive number");
            }

            this.date = date;
            this.weightKg = weightKg;
        }

        /**
         * Returns the entry date.
         *
         * @return the entry date
         */
        public LocalDate getDate() {
            return date;
        }

        /**
         * Sets the entry date.
         *
         * @param date the entry date
         * @throws IllegalArgumentException if the date is null
         */
        public void setDate(LocalDate date) {
            if (date == null) {
                throw new IllegalArgumentException("Date cannot be null");
            }
            this.date = date;
        }

        /**
         * Returns the body weight in kilograms.
         *
         * @return the body weight in kilograms
         */
        public double getWeightKg() {
            return weightKg;
        }

        /**
         * Sets the body weight in kilograms.
         *
         * @param weightKg the body weight in kilograms
         * @throws IllegalArgumentException if the weight is not positive
         */
        public void setWeightKg(double weightKg) {
            if (weightKg <= 0) {
                throw new IllegalArgumentException("Weight must be a positive number");
            }
            this.weightKg = weightKg;
        }
    }
}