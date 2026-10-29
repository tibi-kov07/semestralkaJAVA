package logika;

import java.io.Serializable;
import java.util.Map;
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
        // Načítanie miestností a predmetov zo chránených rešursov (rooms.txt, items.txt)
        Map<String, Room> rooms = DataLoader.loadRooms("rooms.txt");
        DataLoader.loadItemsIntoRooms("items.txt", rooms);

        Room sklad = rooms.get("sklad");
        Room chodba = rooms.get("chodba");
        Room kancelaria = rooms.get("kancelaria");
        Room dielna = rooms.get("dielna");
        Room kotolna = rooms.get("kotolna");
        Room technicka = rooms.get("technicka");
        Room vystup = rooms.get("vystup");

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

        System.out.println("\n💡 AKCIE: vezmi [predmet] | pouzi [predmet] | procti [predmet] | jdi [miestnost] | mluv martin | dokazy | uloz | nacitaj | konec");
    }

}
