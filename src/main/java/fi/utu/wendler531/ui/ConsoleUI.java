package fi.utu.wendler531.ui;
import fi.utu.wendler531.app.UserProfile;
import java.util.Scanner;
public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final UserProfile profile = new UserProfile();

    public void start() {

        while (true) {
            tulostaValikko();
            System.out.print("Valintasi: ");
            String valinta = scanner.nextLine().trim();
            // Käyttäjän syöte, trim() poistaa mahdolliset ylimääräiset välilyönnit

            switch (valinta) {
                case "1":
                    omaProfiili();
                    break;

                case "2":
                    liikkeidenAsetukset();
                    break;

                case "3":
                    aloitaTreeni();
                    break;

                case "4":
                    treeniHistoria();
                    break;

                case "0":
                    System.out.println("Kiitos ohjelman käytöstä! Näkemiin!");
                    return;

                default:
                    System.out.println("Virheellinen syöte, yritä uudestaan.");
                    break;
            }

            System.out.println(); // Tyhjä rivi erottamaan toiminnot

        }
    }

    private void tulostaValikko() {
        System.out.println("=== Wendler 5/3/1 -harjoitusohjelma ===");
        System.out.println("Valitse toiminto:");
        System.out.println("1. Oma profiili");
        System.out.println("2. Liikkeiden asetukset");
        System.out.println("3. Aloita treeni");
        System.out.println("4. Näytä treenihistoria");
        System.out.println("0. Lopeta");
    }

    private void omaProfiili() {
        while(true) {
            System.out.println("=== Oma profiili ===");
            System.out.println("1. Näytä profiili");
            System.out.println("2. Aseta nimi");
            System.out.println("3. Aseta pituus (cm)");
            System.out.println("4. Lisää paino (kg)");
            System.out.println("0. Takaisin päävalikkoon");
            System.out.print("Valintasi: ");
            String valinta = scanner.nextLine().trim();

            switch (valinta) {
                case "1":
                    naytaProfiili();
                    break;

                case "2":
                    asetaNimi();
                    break;

                case "3":
                    asetaPituus();
                    break;

                case "4":
                    lisaaPaino();
                    break;

                case "0":
                    return; // Palaa päävalikkoon
                default:
                    System.out.println("Virheellinen syöte, yritä uudestaan.");
                    System.out.println();
                    break;
            }

            System.out.println(); // Tyhjä rivi erottamaan toiminnot
            
        }
    }


    private void naytaProfiili() {
        System.out.println("=== Profiilitiedot ===");
        System.out.println("Nimi: " + (profile.getName() != null ? profile.getName() : "Ei asetettu"));
        System.out.println("Pituus: " + (profile.getHeightCm() > 0 ? profile.getHeightCm() + " cm" : "Ei asetettu"));
        if (profile.hasBodyWeightEntries()) {
            System.out.println("Viimeisin paino: " + profile.getLatestBodyWeightEntry().getWeightKg() + " kg (" + profile.getLatestBodyWeightEntry().getDate() + ")");
        } else {
            System.out.println("Painohistoria: Ei tietoja");
        }
    }

    private void asetaNimi() {
        System.out.print("Syötä nimi: ");
        String nimi = scanner.nextLine().trim();
        if (nimi.isEmpty()) {
            System.out.println("Nimi ei voi olla tyhjä.");
            return;
        }
        profile.setName(nimi);
        System.out.println("Nimi asetettu: " + nimi);
    }

    private void asetaPituus() {
        System.out.print("Syötä pituus senttimetreinä: ");
        String input = scanner.nextLine().trim();
        try {
            double pituus = Double.parseDouble(input);
            profile.setHeightCm(pituus);
            System.out.println("Pituus asetettu: " + pituus + " cm");
        } catch (NumberFormatException e) {
            System.out.println("Virheellinen syöte, syötä numero.");
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
        }
    }

    private void lisaaPaino() {
        System.out.print("Syötä paino kilogrammoina: ");
        String input = scanner.nextLine().trim();
        try {
            double paino = Double.parseDouble(input);
            profile.addBodyWeightEntry(paino);
            System.out.println("Paino lisätty: " + paino + " kg");
        } catch (NumberFormatException e) {
            System.out.println("Virheellinen syöte, syötä numero.");
        } catch (IllegalArgumentException e) {
            System.out.println("Virhe: " + e.getMessage());
        }
    }

    private void liikkeidenAsetukset() {
    }

    private void aloitaTreeni() {
    }

    private void treeniHistoria() {
    }
}
