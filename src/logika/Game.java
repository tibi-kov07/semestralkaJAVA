package logika;

import java.io.Serializable;
import java.util.Scanner;

public class Game implements Serializable {
    private static final long serialVersionUID = 1L;

    private Player player;
    private GameState gameState;
    private CommandHandler commandHandler;
    private boolean isRunning = true;

    public Game() {
        this.gameState = new GameState();
        this.commandHandler = new CommandHandler(this);
        initGameWorld();
    }
  private void initGameWorld() {
        // Lokality z GDD[cite: 16, 17]
      Room sklad = new Room("sklad", "SKLAD", "Preberáš sa na studenej betónovej podlahe. Nad tebou bliká slabé svetlo. Dvere sú zamknuté. Na stene je napísané: 'Nemal si to zistiť.'", false, null);
      Room chodba = new Room("chodba", "HLAVNÁ CHODBA", "Úzka chodba. Naľavo sú dvere do kancelárie, napravo do dielne. Na konci chodby vidíš schody vedúce k zamknutému východu.", true, "Na otvorenie je potrebný kľúč od chodby.");
      Room kancelaria = new Room("kancelaria", "KANCELÁRIA", "Stôl, zamknutá zásuvka, fotografia správcu a dokumenty na stole.", false, null);
      Room dielna = new Room("dielna", "DIELŇA", "Police s náradím, skrutkovač a náhradná poistka.", false, null);
      Room kotolna = new Room("kotolna", "KOTOLŇA", "Starý kotol, ventily a skrinka s kľúčom.", false, null);
      Room technicka = new Room("technicka", "TECHNICKÁ MIESTNOSŤ", "Elektrický panel, monitor kamier a zariadenie na prehrávanie záznamov.", true, "Na otvorenie je potrebný kľúč od technickej miestnosti.");
      Room vystup = new Room("vystup", "VÝSTUP NA PRÍZEMIE", "Schody a zamknuté dvere do budovy.", true, "Na otvorenie treba kód z technickej miestnosti.");

        // Mapové prepojenia[cite: 17]
        sklad.setExit("chodba", chodba);
        chodba.setExit("sklad", sklad);
        chodba.setExit("kancelaria", kancelaria);
        chodba.setExit("dielna", dielna);
        chodba.setExit("kotolna", kotolna);
        chodba.setExit("technicka", technicka);
        chodba.setExit("vystup", vystup);

        kancelaria.setExit("chodba", chodba);
        dielna.setExit("chodba", chodba);
        kotolna.setExit("chodba", chodba);
        technicka.setExit("chodba", chodba);
        vystup.setExit("chodba", chodba);

        // Predmety[cite: 15]
        sklad.addItem(new Item("baterka", "baterka", "Umožní preskúmať tmavé časti.", false));
        sklad.addItem(new Item("kluc_chodba", "kluc od chodby", "Odomkne dvere zo skladu.", false));

        dielna.addItem(new Item("skrutkovac", "skrutkovac", "Používa sa na otvorenie krytu elektrického panela.", false));
        dielna.addItem(new Item("poistka", "poistka", "Po vložení do panela obnoví napájanie.", true));

        kancelaria.addItem(new Document("poznamka", "poznamka s indiciou", "Obsahuje informáciu potrebnú na otvorenie zásuvky alebo vyriešenie kódu.", "Indícia pre ventily v kotolni: Nastav tlak na hodnotu 3-1-4.", false));
        kancelaria.addItem(new Document("dokazny_dokument", "dokazny dokument", "Vysvetľuje motív únoscu a umožňuje dosiahnuť úplný koniec.", "Dokument potvrdzuje, že správca Martin tajne organizoval nelegálnu činnosť v budove.", true));

        this.player = new Player(sklad);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== ÚTEK Z BASEMENTU ===");

        while (isRunning) {
            printCurrentStatusAndOptions();
            System.out.print("\nZadaj príkaz > ");
            String input = scanner.nextLine().trim();
            System.out.println();
            commandHandler.handleCommand(input);
        }
    }

    private void printCurrentStatusAndOptions() {
        Room current = player.getCurrentRoom();

        System.out.println("\n--------------------------------------------------");
        System.out.println("📍 LOKALITA: " + current.getName());
        System.out.println(current.getDescription());
        System.out.println("--------------------------------------------------");

        System.out.println("📦 PREDMETY V MIESTNOSTI (vezmi [názov]):");
        if (current.getItems().isEmpty()) {
            System.out.println("   (Žiadne predmety tu neležia)");
        } else {
            current.getItems().forEach(item -> System.out.println("   -> " + item.getName() + " (" + item.getDescription() + ")"));
        }

        System.out.println("\n🚪 MOŽNÉ SMERY / PRECHODY (jdi [miestnosť]):");
        current.getExits().forEach((direction, room) -> {
            String lockStatus = room.isLocked() ? " 🔒 [ZAMKNUTÉ]" : " 🔓 [OTVORENÉ]";
            System.out.println("   -> jdi " + direction + " (" + room.getName() + ")" + lockStatus);
        });

        System.out.println("\n🎒 TVOJ INVENTÁR (pouzi [predmet] / procti [dokument]):");
        if (player.getInventory().isEmpty()) {
            System.out.println("   (Prázdny inventár)");
        } else {
            player.getInventory().forEach(item -> System.out.println("   -> " + item.getName() + (item instanceof Document ? " [Čitateľný dokument]" : "")));
        }
    }

    public void evaluateAndFinishGame() {
        boolean hasDocument = player.getItemFromInventory("dokazny_dokument").isPresent();
        boolean hasCameraRecord = player.getItemFromInventory("zaznam_z_kamery").isPresent();

        if (gameState.isExitCodeObtained() && hasDocument && hasCameraRecord) {
            triggerEnding("Úplné odhalenie");
        } else {
            triggerEnding("Útek bez dôkazov");
        }
    }

    public void triggerEnding(String endingType) {
        isRunning = false;
        System.out.println("\n--------------------------------------------------");
        System.out.println("🏆 KONEC HRY: " + endingType);
        System.out.println("--------------------------------------------------");

        switch (endingType) {
            case "Úplné odhalenie" -> System.out.println("Hráč má kód k východu a oba hlavné dôkazy; rozhodne sa počkať na políciu. Utečie, odovzdá dôkazy a identita i motív únoscu sa odhalia.");
            case "Útek bez dôkazov" -> System.out.println("Hráč získa kód k východu, ale nemá všetky dôkazy alebo odíde okamžite. Hráč sa zachráni, ale vinníka nedokáže jednoznačne usvedčiť.");
            default -> System.out.println("Hráč zvolí možnosť vzdať sa alebo ukončí príbeh bez úniku. Hráč zostane uväznený. Hra zobrazí neúspešný koniec.");
        }
    }

    public void loadStateFrom(Game loadedGame) {
        this.player = loadedGame.player;
        this.gameState = loadedGame.gameState;
    }

    public Player getPlayer() { return player; }
    public GameState getGameState() { return gameState; }
    public boolean isRunning() { return isRunning; }
}
