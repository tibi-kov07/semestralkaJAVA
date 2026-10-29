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
}
