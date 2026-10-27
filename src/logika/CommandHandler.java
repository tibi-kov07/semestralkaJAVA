package logika;

import java.io.Serializable;
import java.text.Normalizer;
import java.util.Collection;
import java.util.Optional;

public class CommandHandler implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String ROOM_STORAGE = "sklad";
    private static final String ROOM_BOILER = "kotolna";
    private static final String ROOM_CORRIDOR = "chodba";
    private static final String ROOM_TECHNICAL = "technicka";
    private static final String ROOM_EXIT = "vystup";

    private static final String HELP_TEXT = "Dostupné príkazy: pozri, prohledaj, jdi [miestnost], "
            + "vezmi [predmet], inventar, pouzi [predmet], procti [predmet], mluv martin, "
            + "dokazy, uloz, nacitaj, konec";

    private final Game game;

    public CommandHandler(Game game) {
        this.game = game;
    }

    // ------------------------------------------------------------------
    // Spracovanie príkazov
    // ------------------------------------------------------------------

    public void handleCommand(String input) {
        if (input == null || input.isBlank()) return;

        String cleanInput = normalize(input.trim());
        String[] parts = cleanInput.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1].trim() : "";

        switch (command) {
            case "pomoc" -> System.out.println(HELP_TEXT);
            case "pozri" -> System.out.println(currentRoom().getDescription());
            case "prohledaj" -> handleSearch();
            case "jdi" -> processMove(argument);
            case "vezmi" -> processTake(argument);
            case "inventar" -> handleInventory();
            case "pouzi" -> processUse(argument);
            case "procti" -> processRead(argument);
            case "mluv" -> processTalk(argument);
            case "dokazy" -> handleEvidences();
            case "uloz" -> GameSaveManager.saveGame(game);
            case "nacitaj" -> handleLoad();
            case "konec" -> game.triggerEnding("Neúspešný koniec");
            default -> System.out.println("Tomuto príkazu nerozumiem. Napíš pomoc pre zoznam príkazov.");
        }
    }

    // ------------------------------------------------------------------
    // Jednoduché príkazy
    // ------------------------------------------------------------------

    private void handleSearch() {
        var items = currentRoom().getItems();
        if (items.isEmpty()) {
            System.out.println("Nenašiel si nič nové.");
            return;
        }
        System.out.print("Našiel si: ");
        items.forEach(item -> System.out.print("[" + item.getName() + "] "));
        System.out.println();
    }

    private void handleInventory() {
        var inventory = game.getPlayer().getInventory();
        if (inventory.isEmpty()) {
            System.out.println("Inventár je prázdny.");
            return;
        }
        System.out.println("Obsah inventára:");
        inventory.forEach(item -> System.out.println("- " + item.getName()));
    }

    private void handleEvidences() {
        var evidences = game.getPlayer().getCollectedEvidences();
        if (evidences.isEmpty()) {
            System.out.println("Zatiaľ si nezískal žiadne dôkazy.");
            return;
        }
        System.out.println("Zoznam získaných dôkazov:");
        evidences.forEach(item -> System.out.println("- " + item.getName() + ": " + item.getDescription()));
    }

    private void handleLoad() {
        Game loaded = GameSaveManager.loadGame();
        if (loaded != null) {
            game.loadStateFrom(loaded);
            System.out.println("Nachádzaš sa v: " + currentRoom().getName());
        }
    }

    private void processTalk(String target) {
        if (target.equals("martin")) {
            System.out.println("🗣️ Martin (správca budovy): Tvrdí, že o únose nič nevie, "
                    + "no jeho výpovede si odporujú s nájdenými dôkazmi.");
        } else {
            System.out.println("Takáto postava sa tu nenachádza.");
        }
    }

    private void processRead(String itemName) {
        Optional<Item> itemOpt = game.getPlayer().getItemFromInventory(itemName);
        if (itemOpt.isPresent() && itemOpt.get() instanceof Document doc) {
            System.out.println("📖 OBSAH DOKUMENTU: \"" + doc.getReadableContent() + "\"");
        } else {
            System.out.println("Tento predmet nemá žiadny text na prečítanie.");
        }
    }

    // ------------------------------------------------------------------
    // Pohyb
    // ------------------------------------------------------------------

    public void processMove(String targetRoom) {
        Room nextRoom = targetRoom.isEmpty() ? null : currentRoom().getExit(targetRoom);

        if (nextRoom == null) {
            System.out.println("Tadiaľto cesta nevedie.");
            return;
        }

        if (nextRoom.isLocked()) {
            System.out.println("Dvere sú zamknuté. " + nextRoom.getLockRequirementMessage());
            return;
        }

        if (nextRoom.getId().equals(ROOM_EXIT)) {
            if (game.getGameState().isExitCodeObtained()) {
                game.evaluateAndFinishGame();
            } else {
                System.out.println("Dvere sú zamknuté. Potrebuješ kód k východu z technickej miestnosti.");
            }
            return;
        }

        game.getPlayer().setCurrentRoom(nextRoom);
        System.out.println("Vchádzaš do: " + nextRoom.getName());
    }

    // ------------------------------------------------------------------
    // Brať predmety
    // ------------------------------------------------------------------

    public void processTake(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Chýbajúci predmet: Zadaj názov predmetu, ktorý chceš vziať.");
            return;
        }

        Room room = currentRoom();
        Optional<Item> foundItem = findItem(room.getItems(), itemName);

        if (foundItem.isEmpty()) {
            System.out.println("Chýbajúci predmet: Taký predmet sa v miestnosti nenachádza.");
            return;
        }

        Item item = foundItem.get();
        game.getPlayer().addItem(item);
        room.removeItem(item);
        System.out.println("✅ Získal si predmet: " + item.getName());
    }

    /** Najprv hľadá presnú zhodu (názov alebo ID), až potom čiastočnú zhodu v názve. */
    private Optional<Item> findItem(Collection<Item> items, String query) {
        String q = normalize(query);

        Optional<Item> exact = items.stream()
                .filter(item -> normalize(item.getName()).equals(q) || item.getId().equalsIgnoreCase(q))
                .findFirst();
        if (exact.isPresent()) return exact;

        return items.stream()
                .filter(item -> normalize(item.getName()).contains(q))
                .findFirst();
    }

    // ------------------------------------------------------------------
    // Používanie predmetov
    // ------------------------------------------------------------------

    public void processUse(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Chýbajúci predmet: Zadaj, čo chceš použiť.");
            return;
        }

        Optional<Item> itemOpt = game.getPlayer().getItemFromInventory(itemName);
        if (itemOpt.isEmpty()) return;

        Item item = itemOpt.get();
        System.out.println("Použil si predmet: " + item.getName());

        Room current = currentRoom();
        String roomId = current.getId();
        String itemId = item.getId();

        if (roomId.equals(ROOM_STORAGE) && (itemId.equals("kluc_chodba") || normalize(item.getName()).contains("kluc"))) {
            unlockRoom(current, ROOM_CORRIDOR, "🔓 Zámok cvakol. Dvere na chodbu sú teraz otvorené.");
        } else if (roomId.equals(ROOM_BOILER) && itemId.equals("poznamka")) {
            solveBoiler(current);
        } else if (roomId.equals(ROOM_CORRIDOR) && itemId.equals("kluc_technicka")) {
            unlockRoom(current, ROOM_TECHNICAL, "🔓 Odomkol si technickú miestnosť.");
        } else if (roomId.equals(ROOM_TECHNICAL) && itemId.equals("skrutkovac")) {
            game.getGameState().setElectricPanelOpen(true);
            System.out.println("🛠️ Skrutkovač otvoril kryt elektrického panela.");
        } else if (roomId.equals(ROOM_TECHNICAL) && itemId.equals("poistka")) {
            insertFuse(item);
        } else {
            System.out.println("Tento predmet tu nevieš takto použiť.");
        }
    }

    private void unlockRoom(Room from, String exitName, String message) {
        Room target = from.getExit(exitName);
        if (target != null) {
            target.setLocked(false);
            System.out.println(message);
        }
    }

    private void solveBoiler(Room boilerRoom) {
        game.getGameState().setBoilerSolved(true);
        boilerRoom.addItem(new Item("kluc_technicka", "kluc od technickej miestnosti",
                "Odomkne technickú miestnosť.", false));
        System.out.println("🔧 Podľa indície si nastavil ventily. Zo skrinky vypadol kľúč od technickej miestnosti!");
    }

    private void insertFuse(Item fuse) {
        if (!game.getGameState().isElectricPanelOpen()) {
            System.out.println("Najprv musíš otvoriť kryt elektrického panela skrutkovačom!");
            return;
        }

        game.getGameState().setPowerRestored(true);
        game.getPlayer().removeItem(fuse);
        System.out.println("⚡ Po vložení poistky sa obnovilo napájanie budovy.");

        game.getGameState().setExitCodeObtained(true);
        Document recording = new Document("zaznam_z_kamery", "zaznam z kamery",
                "Odhaľuje, kto priviedol hráča do basementu.",
                "Záznam z kamery odhaľuje motív únoscu a kód k východu: 4821.", true);
        game.getPlayer().addItem(recording);
        System.out.println("🎥 Na obrazovke sa prehral záznam z kamery! "
                + "Získal si záznam z kamery ako dôkaz a kód k východu.");
    }

    // ------------------------------------------------------------------
    // Pomocné metódy
    // ------------------------------------------------------------------

    private Room currentRoom() {
        return game.getPlayer().getCurrentRoom();
    }

    /** Odstráni diakritiku (aj z veľkých písmen) a prevedie text na malé písmená. */
    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }
}