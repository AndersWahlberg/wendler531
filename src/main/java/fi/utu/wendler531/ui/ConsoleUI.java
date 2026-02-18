package fi.utu.wendler531.ui;

import fi.utu.wendler531.app.AppState;
import fi.utu.wendler531.app.LiftSettings;
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
import java.util.Scanner;

public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);

    private final SimpleJsonStorage storage =
            new SimpleJsonStorage(Paths.get("wendler531_data.json"));

    private final AppState state;
    private final UserProfile profile;
    private final LiftSettings liftSettings;

    // Services
    private final WorkoutHistoryService historyService = new WorkoutHistoryService();
    private final WendlerProgramService wendlerProgramService = new WendlerProgramService();

    public ConsoleUI() {
        AppState loaded;
        try {
            loaded = storage.load(AppState.class);
        } catch (Exception e) {
            loaded = AppState.defaultState();
        }
        this.state = loaded;

        // jos tiedostosta tuli null, korjataan defaultiksi
        if (state.getUserProfile() == null) {
            state.setUserProfile(new UserProfile());
        }
        if (state.getLiftSettings() == null) {
            state.setLiftSettings(new LiftSettings());
        }
        // jos historia puuttuu vanhasta tiedostosta
        if (state.getWorkoutHistory() == null) {
            state.setWorkoutHistory(new ArrayList<>());
        }

        this.profile = state.getUserProfile();
        this.liftSettings = state.getLiftSettings();
    }

    private void saveState() {
        try {
            storage.save(state);
        } catch (Exception e) {
            System.out.println("Tallennus epäonnistui: " + e.getMessage());
        }
    }

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

    private String readChoice(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    // --- UI:n siistiminen (siistimpi tulostus) ---

    private void blankLine() {
        System.out.println();
    }

    private void section(String title) {
        blankLine();
        System.out.println("=== " + title + " ===");
    }

    private void pause() {
        System.out.print("\nPaina Enter jatkaaksesi...");
        scanner.nextLine();
    }

    // --- Menut ---

    private void tulostaValikko() {
        section("Wendler 5/3/1 -harjoitusohjelma");
        System.out.println("Valitse toiminto:");
        System.out.println("1. Oma profiili");
        System.out.println("2. Liikkeiden asetukset");
        System.out.println("3. Aloita treeni");
        System.out.println("4. Näytä treenihistoria");
        System.out.println("0. Lopeta");
    }

    private void liikkeidenAsetukset() {
        while (true) {
            section("Liikkeiden asetukset");
            System.out.println("1. Pääliikkeet (1RM / TM)");
            System.out.println("2. Apuliikkeet (myöhemmin)");
            System.out.println("3. TM-% (training max -prosentti)");
            System.out.println("0. Takaisin päävalikkoon");

            String valinta = readChoice("Valintasi: ");

            switch (valinta) {
                case "1" -> showMainLiftsMenu();
                case "2" -> {
                    System.out.println("Apuliikkeet: TODO");
                    pause();
                }
                case "3" -> showTrainingMaxMenu();
                case "0" -> { return; }
                default -> {
                    System.out.println("Virheellinen syöte, yritä uudestaan.");
                    pause();
                }
            }
        }
    }

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
                case "0" -> { return; }
                default -> {
                    System.out.println("Virheellinen syöte, yritä uudestaan.");
                    pause();
                }
            }
        }
    }

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

    private void naytaPaaLiikkeet() {
        section("Pääliikkeiden arvot");
        printLift(LiftSettings.MainLift.SQUAT, "Kyykky");
        printLift(LiftSettings.MainLift.BENCH_PRESS, "Penkkipunnerrus");
        printLift(LiftSettings.MainLift.DEADLIFT, "Maastaveto");
        printLift(LiftSettings.MainLift.OVERHEAD_PRESS, "Pystypunnerrus");
    }

    private void printLift(LiftSettings.MainLift lift, String name) {
        if (!liftSettings.hasOneRepMax(lift)) {
            System.out.println(name + ": 1RM ei asetettu");
            return;
        }
        double orm = liftSettings.getOneRepMax(lift);
        double tm = liftSettings.getTrainingMax(lift);
        System.out.println(name + ": 1RM " + orm + " kg | TM " + String.format("%.1f", tm) + " kg");
    }

    private void omaProfiili() {
        while (true) {
            section("Oma profiili");
            System.out.println("1. Näytä profiili");
            System.out.println("2. Aseta nimi");
            System.out.println("3. Aseta pituus (cm)");
            System.out.println("4. Lisää paino (kg)");
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
                case "0" -> { return; }
                default -> {
                    System.out.println("Virheellinen syöte, yritä uudestaan.");
                    pause();
                }
            }
        }
    }

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

    private void asetaPituus() {
        section("Aseta pituus");
        String input = readChoice("Syötä pituus senttimetreinä: ");
        try {
            double pituus = Double.parseDouble(input);
            profile.setHeightCm(pituus);
            saveState();
            System.out.println("Pituus asetettu: " + pituus + " cm");
            pause();
        } catch (NumberFormatException e) {
            System.out.println("Virheellinen syöte, syötä numero.");
            pause();
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
            pause();
        }
    }

    private void lisaaPaino() {
        section("Lisää paino");
        String input = readChoice("Syötä paino kilogrammoina: ");
        try {
            double paino = Double.parseDouble(input);
            profile.addBodyWeightEntry(paino);
            saveState();
            System.out.println("Paino lisätty: " + paino + " kg");
            pause();
        } catch (NumberFormatException e) {
            System.out.println("Virheellinen syöte, syötä numero.");
            pause();
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
            pause();
        }
    }

    // -----------------------------
    // Aloita treeni (viimeisimmän treenin tulostus + ehdotus seuraavalle treenille) + kysy jokaisen setin toteutuneet toistot
    // -----------------------------
    private void aloitaTreeni() {
        section("Aloita treeni");

        LiftSettings.MainLift lift = askMainLift();

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

        if (!liftSettings.hasTrainingMax(lift)) {
            System.out.println("Tälle liikkeelle ei ole asetettu 1RM/TM-arvoa.");
            System.out.println("Aseta 1RM kohdassa: Liikkeiden asetukset -> Pääliikkeet (1RM / TM).");
            pause();
            return;
        }

        double tm = liftSettings.getTrainingMax(lift);
        List<SetPrescription> sets = wendlerProgramService.getMainWorkSets(tm, week);

        blankLine();
        System.out.println("Pääsarjat (TM " + String.format("%.1f", tm) + " kg):");
        for (SetPrescription s : sets) {
            System.out.printf("Set %d: %d reps -> %.2f kg%n", s.setNumber(), s.targetReps(), s.weightKg());
        }

        blankLine();
        System.out.println("Syötä toteutuneet toistot jokaiselle setille:");

        ArrayList<Integer> repsPerSet = new ArrayList<>();
        for (SetPrescription s : sets) {
            int repsDone = askIntInRange("Set " + s.setNumber() + " (tavoite " + s.targetReps() + "): ", 0, 50);
            repsPerSet.add(repsDone);
        }

        String today = LocalDate.now().toString();
        state.addWorkout(new WorkoutLogEntry(today, lift, week, repsPerSet));
        saveState();

        System.out.println("Treeni tallennettu.");
        pause();
    }

    private LiftSettings.MainLift askMainLift() {
        section("Valitse pääliike");
        LiftSettings.MainLift[] lifts = LiftSettings.MainLift.values();

        for (int i = 0; i < lifts.length; i++) {
            System.out.println((i + 1) + ". " + formatLiftName(lifts[i]));
        }

        int choice = askIntInRange("Valinta: ", 1, lifts.length);
        return lifts[choice - 1];
    }

    private String formatLiftName(LiftSettings.MainLift lift) {
        return switch (lift) {
            case SQUAT -> "Kyykky";
            case BENCH_PRESS -> "Penkkipunnerrus";
            case DEADLIFT -> "Maastaveto";
            case OVERHEAD_PRESS -> "Pystypunnerrus";
        };
    }

    private String formatReps(List<Integer> repsPerSet) {
        if (repsPerSet == null || repsPerSet.isEmpty()) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repsPerSet.size(); i++) {
            if (i > 0) sb.append("/");
            sb.append(repsPerSet.get(i));
        }
        return sb.toString();
    }

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
                        e.dateIso() + " | " + formatLiftName(e.lift())
                                + " | viikko " + e.week()
                                + " | reps " + formatReps(e.repsPerSet())
                ));

        pause();
    }
}