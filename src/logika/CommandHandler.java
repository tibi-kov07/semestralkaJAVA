package logika;

import java.io.Serializable;
import java.util.Optional;

public class CommandHandler implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Game game;

    public CommandHandler(Game game) {
        this.game = game;
    }

    public void handleCommand(String input) {
        if (input.isEmpty()) return;

        String cleanInput = removeDiacritics(input.toLowerCase());
        String[] parts = cleanInput.split(" ", 2);
        String command = parts[0];
        String argument = parts.length > 1 ? parts[1] : "";

        switch (command) {
            case "pomoc" -> System.out.println("Dostupné príkazy: pozri, prohledaj, jdi [miestnost], vezmi [predmet], inventar, pouzi [predmet], procti [predmet], mluv martin, dokazy, uloz, nacitaj, konec");
            case "pozri" -> System.out.println(game.getPlayer().getCurrentRoom().getDescription());
            case "prohledaj" -> handleSearch();
            case "jdi" -> processMove(argument);
            case "vezmi" -> processTake(argument);
            case "inventar" -> handleInventory();
            case "pouzi" -> processUse(argument);
            case "procti" -> processRead(argument);
            case "mluv" -> processTalk(argument);
            case "dokazy" -> handleEvidences();
            case "uloz" -> GameSaveManager.saveGame(game);
            case "nacitaj" -> {
                Game loaded = GameSaveManager.loadGame();
                if (loaded != null) {
                    game.loadStateFrom(loaded);
                    System.out.println("Nachádzaš sa v: " + game.getPlayer().getCurrentRoom().getName());
                }
            }
            case "konec" -> game.triggerEnding("Neúspešný koniec");
            default -> System.out.println("Tomuto príkazu nerozumiem. Napíš pomoc pre zoznam príkazov.");
        }
    }

    private void handleSearch() {
        if (game.getPlayer().getCurrentRoom().getItems().isEmpty()) {
            System.out.println("Nenašiel si nič nové.");
        } else {
            System.out.print("Našiel si: ");
            game.getPlayer().getCurrentRoom().getItems().forEach(item -> System.out.print("[" + item.getName() + "] "));
            System.out.println();
        }
    }

    private void handleInventory() {
        if (game.getPlayer().getInventory().isEmpty()) {
            System.out.println("Inventár je prázdny.");
        } else {
            System.out.println("Obsah inventára:");
            game.getPlayer().getInventory().forEach(item -> System.out.println("- " + item.getName()));
        }
    }

    private void handleEvidences() {
        var evidences = game.getPlayer().getCollectedEvidences();
        if (evidences.isEmpty()) {
            System.out.println("Zatiaľ si nezískal žiadne dôkazy.");
        } else {
            System.out.println("Zoznam získaných dôkazov:");
            evidences.forEach(item -> System.out.println("- " + item.getName() + ": " + item.getDescription()));
        }
    }

    public void processMove(String targetRoom) {
        if (targetRoom.isEmpty()) {
            System.out.println("Tadiaľto cesta nevedie.");
            return;
        }

        Room current = game.getPlayer().getCurrentRoom();
        Room nextRoom = current.getExit(targetRoom);

        if (nextRoom == null) {
            System.out.println("Tadiaľto cesta nevedie.");
            return;
        }

        if (nextRoom.isLocked()) {
            System.out.println("Dvere sú zamknuté. " + nextRoom.getLockRequirementMessage());
            return;
        }

        if (nextRoom.getId().equals("vystup")) {
            if (game.getGameState().isExitCodeObtained()) {
                game.evaluateAndFinishGame();
                return;
            } else {
                System.out.println("Dvere sú zamknuté. Potrebuješ kód k východu z technickej miestnosti.");
                return;
            }
        }

        game.getPlayer().setCurrentRoom(nextRoom);
        System.out.println("Vchádzaš do: " + nextRoom.getName());
    }

    public void processTake(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Chýbajúci predmet: Zadaj názov predmetu, ktorý chceš vziať.");
            return;
        }

        Optional<Item> foundItem = game.getPlayer().getCurrentRoom().getItems().stream()
                .filter(item -> removeDiacritics(item.getName()).equalsIgnoreCase(itemName)
                        || item.getId().equalsIgnoreCase(itemName)
                        || removeDiacritics(item.getName()).contains(itemName))
                .findFirst();

        if (foundItem.isPresent()) {
            Item item = foundItem.get();
            game.getPlayer().addItem(item);
            game.getPlayer().getCurrentRoom().removeItem(item);
            System.out.println("✅ Získal si predmet: " + item.getName());
        } else {
            System.out.println("Chýbajúci predmet: Taký predmet sa v miestnosti nenachádza.");
        }
    }

    public void processUse(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Chýbajúci predmet: Zadaj, čo chceš použiť.");
            return;
        }

        Optional<Item> itemOpt = game.getPlayer().getItemFromInventory(itemName);
        if (itemOpt.isPresent()) {
            System.out.println("Použil si predmet: " + itemOpt.get().getName());
        } else {
            return;
        }

        Item item = itemOpt.get();
        Room current = game.getPlayer().getCurrentRoom();

        if (current.getId().equals("sklad") && (item.getId().equals("kluc_chodba") || item.getName().contains("kluc"))) {
            Room chodba = current.getExit("chodba");
            if (chodba != null) {
                chodba.setLocked(false);
                System.out.println("🔓 Zámok cvakol. Dvere na chodbu sú teraz otvorené.");
            }
            return;
        }

        if (current.getId().equals("kotolna") && item.getId().equals("poznamka")) {
            game.getGameState().setBoilerSolved(true);
            Item klucTech = new Item("kluc_technicka", "kluc od technickej miestnosti", "Odomkne technickú miestnosť.", false);
            current.addItem(klucTech);
            System.out.println("🔧 Podľa indície si nastavil ventily. Zo skrinky vypadol kľúč od technickej miestnosti!");
            return;
        }

        if (current.getId().equals("chodba") && item.getId().equals("kluc_technicka")) {
            Room technicka = current.getExit("technicka");
            if (technicka != null) {
                technicka.setLocked(false);
                System.out.println("🔓 Odomkol si technickú miestnosť.");
            }
            return;
        }

        if (current.getId().equals("technicka") && item.getId().equals("skrutkovac")) {
            game.getGameState().setElectricPanelOpen(true);
            System.out.println("🛠️ Skrutkovač otvoril kryt elektrického panela.");
            return;
        }

        if (current.getId().equals("technicka") && item.getId().equals("poistka")) {
            if (!game.getGameState().isElectricPanelOpen()) {
                System.out.println("Najprv musíte otvoriť kryt elektrického panela skrutkovačom!");
                return;
            }
            game.getGameState().setPowerRestored(true);
            game.getPlayer().removeItem(item);
            System.out.println("⚡ Po vložení poistky sa obnovilo napájanie budovy.");

            game.getGameState().setExitCodeObtained(true);
            Document zaznam = new Document("zaznam_z_kamery", "zaznam z kamery", "Odhaľuje, kto priviedol hráča do basementu.", "Záznam z kamery odhaľuje motív únoscu a kód k východu: 4821.", true);
            game.getPlayer().addItem(zaznam);
            System.out.println("🎥 Na obrazovke sa prehral záznam z kamery! Získal si záznam z kamery ako dôkaz a kód k východu.");
            return;
        }

        System.out.println("Tento predmet tu nevieš takto použiť.");
    }

    private void processRead(String itemName) {
        Optional<Item> itemOpt = game.getPlayer().getItemFromInventory(itemName);
        if (itemOpt.isPresent() && itemOpt.get() instanceof Document doc) {
            System.out.println("📖 OBSAH DOKUMENTU: \"" + doc.getReadableContent() + "\"");
        } else {
            System.out.println("Tento predmet nemá žiadny text na prečítanie.");
        }
    }

    private void processTalk(String target) {
        if (target.equalsIgnoreCase("martin")) {
            System.out.println("🗣️ Martin (správca budovy): Tvrdí, že o únose nič nevie, no jeho výpovede si odporujú s nájdenými dôkazmi.");
        } else {
            System.out.println("Tákato postava sa tu nenachádza.");
        }
    }

    private String removeDiacritics(String text) {
        return text.replaceAll("[áä]", "a").replaceAll("[č]", "c").replaceAll("[ď]", "d")
                .replaceAll("[éě]", "e").replaceAll("[í]", "i").replaceAll("[ľĺ]", "l")
                .replaceAll("[ň]", "n").replaceAll("[óô]", "o").replaceAll("[ŕ]", "r")
                .replaceAll("[š]", "s").replaceAll("[ť]", "t").replaceAll("[úů]", "u")
                .replaceAll("[ý]", "y").replaceAll("[ž]", "z");
    }
}
