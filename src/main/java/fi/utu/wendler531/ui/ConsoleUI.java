package fi.utu.wendler531.ui;

import fi.utu.wendler531.app.AppState;
import fi.utu.wendler531.app.AssistanceExercise;
import fi.utu.wendler531.app.AssistanceService;
import fi.utu.wendler531.app.LiftSettings;
import fi.utu.wendler531.app.OneRepMaxEstimator;
import fi.utu.wendler531.app.SetPrescription;
import fi.utu.wendler531.app.UserProfile;
import fi.utu.wendler531.app.WendlerProgramService;
import fi.utu.wendler531.app.WorkoutHistoryService;
import fi.utu.wendler531.app.WorkoutLogEntry;
import fi.utu.wendler531.storage.SimpleJsonStorage;

import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Console-based user interface for the Wendler 5/3/1 application.
 *
 * <p>This class is responsible for user interaction, menu navigation,
 * and delegating calculations and data access to service and model classes.</p>
 */
public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);

    private final SimpleJsonStorage storage =
            new SimpleJsonStorage(Paths.get("wendler531_data.json"));

    private final AppState state;
    private final UserProfile profile;
    private final LiftSettings liftSettings;

    private final AssistanceService assistanceService = new AssistanceService();

    private final WorkoutHistoryService historyService = new WorkoutHistoryService();
    private final WendlerProgramService wendlerProgramService = new WendlerProgramService();

    /**
     * Creates the console UI and loads the saved application state if possible.
     * Falls back to a default state if loading fails or no saved state exists.
     */
    public ConsoleUI() {
        AppState loaded;
        try {
            loaded = storage.load(AppState.class);
        } catch (Exception e) {
            loaded = null;
        }

        // load() may return null, so use a default state when needed
        if (loaded == null) {
            loaded = AppState.defaultState();
        }
        this.state = loaded;

        if (state.getUserProfile() == null) {
            state.setUserProfile(new UserProfile());
        }
        if (state.getLiftSettings() == null) {
            state.setLiftSettings(new LiftSettings());
        }
        if (state.getWorkoutHistory() == null) {
            state.setWorkoutHistory(new ArrayList<>());
        }

        // Ensure the assistance map is initialized
        state.getAssistanceByLift();

        this.profile = state.getUserProfile();
        this.liftSettings = state.getLiftSettings();
    }

    /**
     * Saves the current application state to disk.
     * Prints an error message if saving fails.
     */
    private void saveState() {
        try {
            storage.save(state);
        } catch (Exception e) {
            System.out.println("Tallennus epäonnistui: " + e.getMessage());
        }
    }

    /**
     * Starts the main user interface loop and keeps showing the main menu
     * until the user chooses to exit the application.
     */
    public void start() {
        while (true) {
            tulostaValikko();
            String valinta = readChoice("Valintasi: ");

            switch (valinta) {
                case "1" -> omaProfiili();
                case "2" -> liikkeidenAsetukset();
                case "3" -> aloitaTreeni();
                case "4" -> treeniHistoria();
                case "0" -> {
                    blankLine();
                    System.out.println("Kiitos ohjelman käytöstä! Näkemiin!");
                    return;
                }
                default -> System.out.println("Virheellinen syöte, yritä uudestaan.");
            }
        }
    }

    /**
     * Reads a trimmed line of input after showing the given prompt.
     *
     * @param prompt the prompt to display
     * @return the trimmed user input
     */
    private String readChoice(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // --- UI formatting helpers ---

    /**
     * Prints a blank line to improve console readability.
     */
    private void blankLine() {
        System.out.println();
    }

    /**
     * Prints a simple section header in the console output.
     *
     * @param title the title to display
     */
    private void section(String title) {
        blankLine();
        System.out.println("=== " + title + " ===");
    }

    /**
     * Pauses the user interface until the user presses Enter.
     */
    private void pause() {
        System.out.print("\nPaina Enter jatkaaksesi...");
        scanner.nextLine();
    }

    // --- Main menu ---

    /**
     * Prints the main menu of the application.
     */
    private void tulostaValikko() {
        section("Wendler 5/3/1 -harjoitusohjelma");
        System.out.println("Valitse toiminto:");
        System.out.println("1. Oma profiili");
        System.out.println("2. Liikkeiden asetukset");
        System.out.println("3. Aloita treeni");
        System.out.println("4. Näytä treenihistoria");
        System.out.println("0. Lopeta");
    }

    // --- Lift settings ---

    /**
     * Shows the lift settings menu and allows the user to manage
     * main lifts, assistance exercises, training max percentage,
     * and the one-rep max estimator.
     */
    private void liikkeidenAsetukset() {
        while (true) {
            section("Liikkeiden asetukset");
            System.out.println("1. Pääliikkeet (1RM / TM)");
            System.out.println("2. Apuliikkeet");
            System.out.println("3. TM-% (training max -prosentti)");
            System.out.println("4. 1RM-estimaattori (Epley/Brzycki)");
            System.out.println("0. Takaisin päävalikkoon");

            String valinta = readChoice("Valintasi: ");

            switch (valinta) {
                case "1" -> showMainLiftsMenu();
                case "2" -> showAssistanceMenu();
                case "3" -> showTrainingMaxMenu();
                case "4" -> showEstimatorMenu();
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Virheellinen syöte, yritä uudestaan.");
                    pause();
                }
            }
        }
    }

    /**
     * Shows the menu for selecting the one-rep max estimation formula
     * and saves the selected estimator type to the application state.
     */
    private void showEstimatorMenu() {
        while (true) {
            section("1RM-estimaattori");
            System.out.println("Nykyinen: " + state.getOneRepMaxEstimator().name());
            System.out.println("1. Epley");
            System.out.println("2. Brzycki");
            System.out.println("0. Takaisin");

            String choice = readChoice("Valintasi: ");
            switch (choice) {
                case "1" -> {
                    state.setOneRepMaxEstimatorType(AppState.EstimatorType.EPLEY);
                    saveState();
                    System.out.println("Valittu estimaattori: Epley");
                    pause();
                    return;
                }
                case "2" -> {
                    state.setOneRepMaxEstimatorType(AppState.EstimatorType.BRZYCKI);
                    saveState();
                    System.out.println("Valittu estimaattori: Brzycki");
                    pause();
                    return;
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Virheellinen syöte.");
                    pause();
                }
            }
        }
    }

    // --- Assistance exercises ---

    /**
     * Shows the assistance exercise menu and allows the user to view
     * assistance exercises for each main lift.
     */
    private void showAssistanceMenu() {
        while (true) {
            section("Apuliikkeet (3x10)");

            System.out.println("Valitse treenipäivä / pääliike:");
            System.out.println("1. Kyykky");
            System.out.println("2. Penkkipunnerrus");
            System.out.println("3. Maastaveto");
            System.out.println("4. Pystypunnerrus");
            System.out.println("0. Takaisin");

            String valinta = readChoice("Valintasi: ");

            LiftSettings.MainLift lift = switch (valinta) {
                case "1" -> LiftSettings.MainLift.SQUAT;
                case "2" -> LiftSettings.MainLift.BENCH_PRESS;
                case "3" -> LiftSettings.MainLift.DEADLIFT;
                case "4" -> LiftSettings.MainLift.OVERHEAD_PRESS;
                case "0" -> null;
                default -> null;
            };

            if (lift == null) {
                if (valinta.equals("0")) {
                    return;
                }
                System.out.println("Virheellinen syöte, yritä uudestaan.");
                pause();
                continue;
            }

            printAssistanceForLift(lift);
            pause();
        }
    }

    /**
     * Prints the configured assistance exercises for the given main lift.
     *
     * @param lift the selected main lift
     */
    private void printAssistanceForLift(LiftSettings.MainLift lift) {
        section(formatLiftName(lift) + " - apuliikkeet");

        List<AssistanceExercise> list;
        try {
            list = assistanceService.getAssistanceFor(lift, state);
        } catch (IllegalStateException e) {
            System.out.println("Apuliikkeitä ei löytynyt tälle liikkeelle.");
            return;
        }

        for (int i = 0; i < list.size(); i++) {
            AssistanceExercise ex = list.get(i);
            System.out.println((i + 1) + ") " + ex.getName() + " " + ex.getSets() + "x" + ex.getReps());
        }
    }

    // --- Training max percentage ---

    /**
     * Shows the menu for updating the training max percentage
     * used when calculating Wendler set weights.
     */
    private void showTrainingMaxMenu() {
        while (true) {
            section("TM-% (Training Max)");
            System.out.println("Nykyinen TM-%: " + (int) Math.round(liftSettings.getTmPercent() * 100) + "%");
            System.out.println("Syötä uusi TM-% väliltä 75–95 (esim. 90).");
            System.out.println("0 = Takaisin");

            String input = readChoice("TM-%: ");
            if (input.equals("0")) {
                return;
            }

            try {
                double p = Double.parseDouble(input);
                if (p < 75 || p > 95) {
                    System.out.println("Virhe: TM-% pitää olla välillä 75–95.");
                    pause();
                    continue;
                }
                liftSettings.setTmPercent(p / 100.0);
                saveState();
                System.out.println("TM-% asetettu: " + (int) p + "%");
                pause();
                return;
            } catch (NumberFormatException e) {
                System.out.println("Virheellinen syöte, syötä numero.");
                pause();
            } catch (IllegalArgumentException e) {
                System.out.println("Virhe: " + e.getMessage());
                pause();
            }
        }
    }

    // --- Main lifts ---

    /**
     * Shows the menu for setting and viewing one-rep max values
     * for the main lifts.
     */
    private void showMainLiftsMenu() {
        while (true) {
            section("Pääliikkeet (1RM / TM)");
            System.out.println("1. Aseta 1RM: Kyykky");
            System.out.println("2. Aseta 1RM: Penkkipunnerrus");
            System.out.println("3. Aseta 1RM: Maastaveto");
            System.out.println("4. Aseta 1RM: Pystypunnerrus");
            System.out.println("5. Näytä nykyiset arvot");
            System.out.println("0. Takaisin");

            String valinta = readChoice("Valintasi: ");

            switch (valinta) {
                case "1" -> asetaPaaLiike(LiftSettings.MainLift.SQUAT, "Kyykky");
                case "2" -> asetaPaaLiike(LiftSettings.MainLift.BENCH_PRESS, "Penkkipunnerrus");
                case "3" -> asetaPaaLiike(LiftSettings.MainLift.DEADLIFT, "Maastaveto");
                case "4" -> asetaPaaLiike(LiftSettings.MainLift.OVERHEAD_PRESS, "Pystypunnerrus");
                case "5" -> {
                    naytaPaaLiikkeet();
                    pause();
                }
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Virheellinen syöte, yritä uudestaan.");
                    pause();
                }
            }
        }
    }

    /**
     * Prompts the user to set a one-rep max value for the given lift.
     *
     * @param lift the selected main lift
     * @param displayName the user-facing display name of the lift
     */
    private void asetaPaaLiike(LiftSettings.MainLift lift, String displayName) {
        section("Aseta 1RM");
        String input = readChoice("Syötä 1RM liikkeelle " + displayName + " (kg): ");
        try {
            double kg = Double.parseDouble(input);
            liftSettings.setOneRepMax(lift, kg);
            saveState();
            System.out.println(displayName + " 1RM asetettu: " + kg + " kg");
            pause();
        } catch (NumberFormatException e) {
            System.out.println("Virheellinen syöte, syötä numero.");
            pause();
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
            pause();
        }
    }

    /**
     * Prints the current one-rep max and training max values
     * for all main lifts.
     */
    private void naytaPaaLiikkeet() {
        section("Pääliikkeiden arvot");
        printLift(LiftSettings.MainLift.SQUAT, "Kyykky");
        printLift(LiftSettings.MainLift.BENCH_PRESS, "Penkkipunnerrus");
        printLift(LiftSettings.MainLift.DEADLIFT, "Maastaveto");
        printLift(LiftSettings.MainLift.OVERHEAD_PRESS, "Pystypunnerrus");
    }

    /**
     * Prints the one-rep max and training max for a single lift,
     * or a message if no one-rep max has been set.
     *
     * @param lift the selected main lift
     * @param name the user-facing display name of the lift
     */
    private void printLift(LiftSettings.MainLift lift, String name) {
        if (!liftSettings.hasOneRepMax(lift)) {
            System.out.println(name + ": 1RM ei asetettu");
            return;
        }
        double orm = liftSettings.getOneRepMax(lift);
        double tm = liftSettings.getTrainingMax(lift);
        System.out.println(name + ": 1RM " + orm + " kg | TM " + String.format("%.1f", tm) + " kg");
    }

    // --- Profile ---

    /**
     * Shows the profile menu and allows the user to view and edit
     * profile data such as name, height, body weight, and BMI.
     */
    private void omaProfiili() {
        while (true) {
            section("Oma profiili");
            System.out.println("1. Näytä profiili");
            System.out.println("2. Aseta nimi");
            System.out.println("3. Aseta pituus (cm)");
            System.out.println("4. Lisää paino (kg)");
            System.out.println("5. BMI-laskuri (nykyinen + hypoteettinen)");
            System.out.println("0. Takaisin päävalikkoon");

            String valinta = readChoice("Valintasi: ");

            switch (valinta) {
                case "1" -> {
                    naytaProfiili();
                    pause();
                }
                case "2" -> asetaNimi();
                case "3" -> asetaPituus();
                case "4" -> lisaaPaino();
                case "5" -> showBmiCalculator();
                case "0" -> {
                    return;
                }
                default -> {
                    System.out.println("Virheellinen syöte, yritä uudestaan.");
                    pause();
                }
            }
        }
    }

    /**
     * Prints the current profile information.
     */
    private void naytaProfiili() {
        section("Profiilitiedot");
        System.out.println("Nimi: " + (profile.getName() != null ? profile.getName() : "Ei asetettu"));
        System.out.println("Pituus: " + (profile.getHeightCm() > 0 ? profile.getHeightCm() + " cm" : "Ei asetettu"));
        if (profile.hasBodyWeightEntries()) {
            System.out.println("Viimeisin paino: " + profile.getLatestBodyWeightEntry().getWeightKg()
                    + " kg (" + profile.getLatestBodyWeightEntry().getDate() + ")");
        } else {
            System.out.println("Painohistoria: Ei tietoja");
        }
    }

    /**
     * Prompts the user to set a profile name.
     */
    private void asetaNimi() {
        section("Aseta nimi");
        String nimi = readChoice("Syötä nimi: ");
        if (nimi.isEmpty()) {
            System.out.println("Nimi ei voi olla tyhjä.");
            pause();
            return;
        }
        profile.setName(nimi);
        saveState();
        System.out.println("Nimi asetettu: " + nimi);
        pause();
    }

    /**
     * Prompts the user to set profile height in centimeters.
     */
    private void asetaPituus() {
        section("Aseta pituus");
        double pituus = readDoubleInRange("Syötä pituus senttimetreinä: ", 1.0, 230.0);
        try {
            profile.setHeightCm(pituus);
            saveState();
            System.out.println("Pituus asetettu: " + pituus + " cm");
            pause();
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
            pause();
        }
    }

    /**
     * Prompts the user to add a new body weight entry.
     */
    private void lisaaPaino() {
        section("Lisää paino");
        double paino = readDoubleInRange("Syötä paino kilogrammoina: ", 1.0, 300.0);
        try {
            profile.addBodyWeightEntry(paino);
            saveState();
            System.out.println("Paino lisätty: " + paino + " kg");
            pause();
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
            pause();
        }
    }

    // --- BMI calculator ---

    /**
     * Shows the BMI calculator. The method first ensures that
     * both height and at least one body weight entry are available.
     * It then displays the current BMI and allows the user to calculate
     * a hypothetical BMI without saving the hypothetical weight.
     */
    private void showBmiCalculator() {
        section("BMI-laskuri");

        if (!ensureHeightIsSetHereIfMissing()) {
            return;
        }
        if (!ensureWeightIsSetHereIfMissing()) {
            return;
        }

        double heightCm = profile.getHeightCm();
        double currentWeight = profile.getLatestBodyWeightEntry().getWeightKg();
        double currentBmi = calculateBmi(heightCm, currentWeight);

        System.out.println("Nykyinen pituus: " + heightCm + " cm");
        System.out.println("Nykyinen paino: " + currentWeight + " kg");
        System.out.println("Nykyinen BMI: " + formatBmi(currentBmi) + bmiCommentFi(currentBmi));

        blankLine();
        System.out.println("Syötä paino (kg)");
        System.out.println("HUOM: tämä EI tallenna painoa profiiliin (paino asetetaan kohdassa 'Lisää paino').");

        double hypotheticalWeight = readDoubleInRange("Paino (kg): ", 1.0, 300.0);
        double hypotheticalBmi = calculateBmi(heightCm, hypotheticalWeight);

        blankLine();
        System.out.println("BMI painolla " + hypotheticalWeight + " kg: "
                + formatBmi(hypotheticalBmi) + bmiCommentFi(hypotheticalBmi));
        pause();
    }

    /**
     * Ensures that height is available before using the BMI calculator.
     * Allows the user to set it immediately if missing.
     *
     * @return true if height is available, otherwise false
     */
    private boolean ensureHeightIsSetHereIfMissing() {
        if (profile.getHeightCm() > 0) {
            return true;
        }

        System.out.println("Pituutta ei ole asetettu.");
        boolean setNow = askYesNo("Haluatko asettaa pituuden nyt? (k/e): ");
        if (!setNow) {
            System.out.println("BMI-laskuri peruutettu.");
            pause();
            return false;
        }

        double height = readDoubleInRange("Syötä pituus senttimetreinä: ", 1.0, 230.0);
        try {
            profile.setHeightCm(height);
            saveState();
            System.out.println("Pituus asetettu: " + height + " cm");
            blankLine();
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
            pause();
            return false;
        }
    }

    /**
     * Ensures that at least one body weight entry exists before using the BMI calculator.
     * Allows the user to add it immediately if missing.
     *
     * @return true if weight data is available, otherwise false
     */
    private boolean ensureWeightIsSetHereIfMissing() {
        if (profile.hasBodyWeightEntries()) {
            return true;
        }

        System.out.println("Painoa ei ole vielä asetettu.");
        boolean setNow = askYesNo("Haluatko lisätä painon nyt? (k/e): ");
        if (!setNow) {
            System.out.println("BMI-laskuri peruutettu.");
            pause();
            return false;
        }

        double weight = readDoubleInRange("Syötä paino kilogrammoina: ", 1.0, 300.0);
        try {
            profile.addBodyWeightEntry(weight);
            saveState();
            System.out.println("Paino lisätty: " + weight + " kg");
            blankLine();
            return true;
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
            pause();
            return false;
        }
    }

    /**
     * Calculates BMI using height in centimeters and weight in kilograms.
     *
     * @param heightCm height in centimeters
     * @param weightKg weight in kilograms
     * @return the calculated BMI value
     */
    private double calculateBmi(double heightCm, double weightKg) {
        double heightM = heightCm / 100.0;
        return weightKg / (heightM * heightM);
    }

    /**
     * Formats a BMI value with one decimal place using a fixed locale.
     *
     * @param bmi the BMI value to format
     * @return the formatted BMI string
     */
    private String formatBmi(double bmi) {
        return String.format(Locale.US, "%.1f", bmi);
    }

    /**
     * Returns the Finnish BMI category label for the given BMI value.
     *
     * @param bmi the BMI value
     * @return the Finnish category label
     */
    private String bmiCategoryFi(double bmi) {
        if (bmi < 18.5) {
            return "alipainoinen";
        }
        if (bmi < 25.0) {
            return "normaalipainoinen";
        }
        if (bmi < 30.0) {
            return "ylipainoinen";
        }
        return "lihavuus";
    }

    /**
     * Returns a formatted Finnish BMI category comment in parentheses.
     *
     * @param bmi the BMI value
     * @return the formatted category comment
     */
    private String bmiCommentFi(double bmi) {
        return " (" + bmiCategoryFi(bmi) + ")";
    }

    // --- Start workout ---

    /**
     * Starts the workout flow by asking the user to select a lift and week,
     * calculating warm-up and work sets, collecting completed reps,
     * saving the workout entry, and showing possible PR information.
     */
    private void aloitaTreeni() {
        section("Aloita treeni");

        LiftSettings.MainLift lift = askMainLift();

        // If 1RM is missing, ask for it here and continue normally
        if (!ensureOneRepMaxIsSetAtWorkoutStart(lift)) {
            return;
        }

        // Previous best estimated 1RM using the selected estimator
        OptionalDouble prevBest = bestEstimated1rmFromMultiRepSets(lift);

        var latest = historyService.findLatestForLift(state, lift);
        System.out.println(historyService.formatLatest(latest));

        int suggestedWeek = historyService.suggestNextWeek(latest);

        int week;
        while (true) {
            week = askWeekWithDefault("Valitse viikko (1-4) [Enter = " + suggestedWeek + "]: ", suggestedWeek);

            if (week != suggestedWeek) {
                boolean ok = askYesNo("Valitsit viikon " + week + " vaikka ehdotus oli " + suggestedWeek + ". Jatketaanko? (k/e): ");
                if (!ok) {
                    continue;
                }
            }
            break;
        }

        double tm = liftSettings.getTrainingMax(lift);

        List<SetPrescription> warmUpSets = wendlerProgramService.getWarmUpSets(tm);
        List<SetPrescription> sets = wendlerProgramService.getMainWorkSets(tm, week);

        blankLine();
        System.out.println("Lämmittelysarjat (TM " + String.format("%.1f", tm) + " kg):");
        for (SetPrescription w : warmUpSets) {
            System.out.printf("Warm-up %d: %d reps -> %.2f kg%n", w.setNumber(), w.targetReps(), w.weightKg());
        }

        blankLine();
        System.out.println("Pääsarjat (TM " + String.format("%.1f", tm) + " kg):");
        for (SetPrescription s : sets) {
            System.out.printf("Set %d: %d reps -> %.2f kg%n", s.setNumber(), s.targetReps(), s.weightKg());
        }

        blankLine();
        System.out.println("Apuliikkeet (3x10):");
        printAssistanceInline(lift);

        blankLine();
        System.out.println("Syötä toteutuneet toistot jokaiselle setille:");

        ArrayList<Integer> repsPerSet = new ArrayList<>();
        ArrayList<Double> weightsPerSet = new ArrayList<>();

        for (SetPrescription s : sets) {
            weightsPerSet.add(s.weightKg());
            int repsDone = askIntInRange("Set " + s.setNumber() + " (tavoite " + s.targetReps() + "): ", 0, 50);
            repsPerSet.add(repsDone);
        }

        String today = LocalDate.now().toString();
        state.addWorkout(new WorkoutLogEntry(today, lift, week, tm, weightsPerSet, repsPerSet));
        saveState();

        System.out.println("Treeni tallennettu.");

        // Print PR message using the selected estimator
        printSetPrMessage(lift, prevBest);

        pause();
    }

    /**
     * Ensures that a one-rep max exists for the given lift before starting a workout.
     * If missing, the user is prompted to enter it immediately.
     *
     * @param lift the selected main lift
     * @return true if a valid one-rep max is available, otherwise false
     */
    private boolean ensureOneRepMaxIsSetAtWorkoutStart(LiftSettings.MainLift lift) {
        if (liftSettings.hasOneRepMax(lift)) {
            return true;
        }

        section("Puuttuva 1RM");
        System.out.println("Liikkeelle " + formatLiftName(lift) + " ei ole asetettu 1RM-arvoa.");
        System.out.println("Syötä 1RM nyt, jotta voit aloittaa treenin.");
        System.out.println("0 = Peruuta");

        while (true) {
            String input = readChoice("1RM (kg): ");

            if (input.equals("0")) {
                System.out.println("Treenin aloitus peruutettu.");
                pause();
                return false;
            }

            try {
                double kg = Double.parseDouble(input);
                liftSettings.setOneRepMax(lift, kg);
                saveState();
                System.out.println("1RM asetettu.");
                return true;
            } catch (NumberFormatException e) {
                System.out.println("Virheellinen syöte. Syötä numero (esim. 120) tai 0 peruuttaaksesi.");
            } catch (IllegalArgumentException e) {
                System.out.println("Virhe: " + e.getMessage());
            }
        }
    }

    /**
     * Prints assistance exercises inline during the workout start flow.
     *
     * @param lift the selected main lift
     */
    private void printAssistanceInline(LiftSettings.MainLift lift) {
        List<AssistanceExercise> list;
        try {
            list = assistanceService.getAssistanceFor(lift, state);
        } catch (IllegalStateException e) {
            System.out.println("- (ei apuliikkeitä määritelty)");
            return;
        }

        for (AssistanceExercise ex : list) {
            System.out.println("- " + ex.getName() + " " + ex.getSets() + "x" + ex.getReps());
        }
    }

    /**
     * Prompts the user to select one of the main lifts.
     *
     * @return the selected main lift
     */
    private LiftSettings.MainLift askMainLift() {
        section("Valitse pääliike");
        LiftSettings.MainLift[] lifts = LiftSettings.MainLift.values();

        for (int i = 0; i < lifts.length; i++) {
            System.out.println((i + 1) + ". " + formatLiftName(lifts[i]));
        }

        int choice = askIntInRange("Valinta: ", 1, lifts.length);
        return lifts[choice - 1];
    }

    /**
     * Returns the Finnish display name of the given main lift.
     *
     * @param lift the main lift
     * @return the Finnish display name
     */
    private String formatLiftName(LiftSettings.MainLift lift) {
        return switch (lift) {
            case SQUAT -> "Kyykky";
            case BENCH_PRESS -> "Penkkipunnerrus";
            case DEADLIFT -> "Maastaveto";
            case OVERHEAD_PRESS -> "Pystypunnerrus";
        };
    }

    // --- PR / set record using selected estimator ---

    /**
     * Calculates the best estimated one-rep max from historical multi-rep sets
     * for the given lift using the currently selected estimator.
     *
     * @param lift the selected main lift
     * @return the best estimated one-rep max, if available
     */
    private OptionalDouble bestEstimated1rmFromMultiRepSets(LiftSettings.MainLift lift) {
        OneRepMaxEstimator estimator = state.getOneRepMaxEstimator();

        return state.getWorkoutHistory().stream()
                .filter(e -> e != null && e.lift() == lift)
                .flatMapToDouble(e -> {
                    List<Double> w = e.weightsPerSet();
                    List<Integer> r = e.repsPerSet();
                    if (w == null || r == null) {
                        return java.util.stream.DoubleStream.empty();
                    }

                    int n = Math.min(w.size(), r.size());
                    return IntStream.range(0, n)
                            .filter(i -> r.get(i) != null && r.get(i) >= 2)
                            .filter(i -> w.get(i) != null && w.get(i) > 0)
                            .mapToDouble(i -> {
                                try {
                                    return estimator.estimate(w.get(i), r.get(i));
                                } catch (IllegalArgumentException ex) {
                                    return 0.0;
                                }
                            })
                            .filter(v -> v > 0);
                })
                .max();
    }

    /**
     * Prints a message about whether the user achieved a new estimated set PR
     * based on the currently selected one-rep max estimator.
     *
     * @param lift the selected main lift
     * @param prevBest the previous best estimated one-rep max before the new workout
     */
    private void printSetPrMessage(LiftSettings.MainLift lift, OptionalDouble prevBest) {
        OneRepMaxEstimator estimator = state.getOneRepMaxEstimator();
        OptionalDouble newBest = bestEstimated1rmFromMultiRepSets(lift);

        blankLine();
        section("Ennätykset");

        if (newBest.isEmpty()) {
            System.out.println("Ei sarjatietoja (reps >= 2) joista voisi laskea 1RM-arvion.");
            return;
        }

        boolean improved = isImproved(prevBest, newBest);

        if (improved) {
            System.out.println("TEIT UUDEN SARJAENNÄTYKSEN! (laskennallinen 1RM, " + estimator.name() + "): "
                    + formatKg(newBest.getAsDouble()) + " kg"
                    + (prevBest.isPresent()
                    ? " (aiemmin " + formatKg(prevBest.getAsDouble()) + " kg)"
                    : ""));
        } else {
            System.out.println("Ei uutta sarjaennätystä.");
            System.out.println("Aiempi sarjaennätys (laskennallinen 1RM, " + estimator.name() + "): "
                    + formatKg(newBest.getAsDouble()) + " kg");
        }
    }

    /**
     * Compares two optional estimated one-rep max values and returns
     * whether the current value is a real improvement over the previous one.
     *
     * @param prev the previous best value
     * @param now the current value
     * @return true if the current value is better, otherwise false
     */
    private boolean isImproved(OptionalDouble prev, OptionalDouble now) {
        if (now.isEmpty()) {
            return false;
        }
        if (prev.isEmpty()) {
            return true;
        }
        double eps = 0.0001;
        return now.getAsDouble() > prev.getAsDouble() + eps;
    }

    // --- Workout history (compact view) ---

    /**
     * Displays the workout history in a compact format,
     * showing the most recent entries first.
     */
    private void treeniHistoria() {
        section("Treenihistoria");

        if (state.getWorkoutHistory().isEmpty()) {
            System.out.println("Ei tallennettuja treenejä vielä.");
            pause();
            return;
        }

        state.getWorkoutHistory().stream()
                .sorted(Comparator.comparing(WorkoutLogEntry::dateIso).reversed())
                .limit(50)
                .forEach(e -> System.out.println(
                        e.dateIso()
                                + " | " + formatLiftName(e.lift())
                                + " | vko " + e.week()
                                + " | TM " + String.format("%.1f", e.tmKg())
                                + " | " + formatWorkSetsCompact(e)
                ));

        pause();
    }

    /**
     * Formats workout set data into a compact string such as "100x5, 110x5, 120x8".
     * Falls back to a reps-only format if set weight data is missing or inconsistent.
     *
     * @param e the workout log entry
     * @return the compact formatted set string
     */
    private String formatWorkSetsCompact(WorkoutLogEntry e) {
        List<Double> w = e.weightsPerSet();
        List<Integer> r = e.repsPerSet();

        if (w == null || r == null || w.isEmpty() || r.isEmpty() || w.size() != r.size()) {
            return "reps " + formatReps(r);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < w.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(formatKg(w.get(i))).append("x").append(r.get(i));
        }
        return sb.toString();
    }

    /**
     * Formats a weight value without decimals when it is effectively an integer,
     * otherwise with one decimal place.
     *
     * @param kg the weight value
     * @return the formatted weight string
     */
    private String formatKg(double kg) {
        if (Math.abs(kg - Math.rint(kg)) < 0.0001) {
            return String.valueOf((int) Math.rint(kg));
        }
        return String.format("%.1f", kg);
    }

    /**
     * Formats a list of reps into a slash-separated string.
     *
     * @param repsPerSet the reps performed for each set
     * @return the formatted reps string
     */
    private String formatReps(List<Integer> repsPerSet) {
        if (repsPerSet == null || repsPerSet.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repsPerSet.size(); i++) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(repsPerSet.get(i));
        }
        return sb.toString();
    }

    // --- Input helpers ---

    /**
     * Reads a week number from the user, allowing Enter to accept a default value.
     *
     * @param prompt the prompt to display
     * @param defaultWeek the default week to use if the input is empty
     * @return a valid week number between 1 and 4
     */
    private int askWeekWithDefault(String prompt, int defaultWeek) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                return defaultWeek;
            }

            try {
                int v = Integer.parseInt(line);
                if (v < 1 || v > 4) {
                    System.out.println("Anna luku väliltä 1-4.");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Virheellinen syöte. Anna 1-4 tai paina Enter.");
            }
        }
    }

    /**
     * Reads an integer within the given range.
     *
     * @param prompt the prompt to display
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @return a valid integer in the given range
     */
    private int askIntInRange(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();

            try {
                int value = Integer.parseInt(line);
                if (value < min || value > max) {
                    System.out.println("Anna luku väliltä " + min + "-" + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Virheellinen syöte. Anna numero.");
            }
        }
    }

    /**
     * Reads a double value within the given range.
     *
     * @param prompt the prompt to display
     * @param min the minimum allowed value
     * @param max the maximum allowed value
     * @return a valid double value in the given range
     */
    private double readDoubleInRange(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();

            try {
                double value = Double.parseDouble(line);
                if (value < min || value > max) {
                    System.out.println("Anna luku väliltä " + min + "-" + max + ".");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Virheellinen syöte. Anna numero (esim. 82.5).");
            }
        }
    }

    /**
     * Reads a yes/no answer from the user.
     *
     * @param prompt the prompt to display
     * @return true for yes, false for no
     */
    private boolean askYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim().toLowerCase();

            if (line.equals("k") || line.equals("kyllä") || line.equals("y") || line.equals("yes")) {
                return true;
            }
            if (line.equals("e") || line.equals("ei") || line.equals("n") || line.equals("no")) {
                return false;
            }
            System.out.println("Syötä 'k' (kyllä) tai 'e' (ei).");
        }
    }
}