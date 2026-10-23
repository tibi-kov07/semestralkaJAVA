package logika;

import java.io.Serializable;
import java.util.Optional;

public class CommandHandler implements Serializable {
    private static final long serialVersionUID = 1L;

   private final Game game;

   public CommandHandler(Game game){
       this.game = game;
   }

   public void handleCommand (String input){
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
              case "vezmi" -> processUse(argument);
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
            System.out.print("Tvoj inventár obsahuje: ");
            game.getPlayer().getInventory().forEach(item -> System.out.print("[" + item.getName() + "] "));
            System.out.println();
        }
    }

    private void handleEvidences() {
        if (game.getPlayer().getEvidences().isEmpty()) {
            System.out.println("Nemáš zatiaľ žiadne dokazy.");
        } else {
            System.out.print("Tvoje dokazy: ");
            game.getPlayer().getEvidences().forEach(evidence -> System.out.print("[" + evidence.getName() + "] "));
            System.out.println();
        }
    }

    private void processMove(String targetRoom) {
        if (targetRoom.isEmpty()) {
            System.out.println("Musíš zadať smer alebo názov miestnosti, kam chceš ísť.");
            return;
        }
        Room current = game.getPlayer().getCurrentRoom();
        Room nextRoom = current.getExit(targetRoom);

        if (nextRoom == null) {
            System.out.println("Tadiaľto cesta nevedie.");
        }
        if (nextRoom.isLocked()) {
            System.out.println("Táto miestnosť je zamknutá. Možno potrebuješ kód alebo kľúč.");
        }
        if (nextRoom.getId().equals('vystup')){
            if (game.getGameState().isExitCodeObtained()){
                game.evaluateAndFinishGame();
            }
            else {
                System.out.println("Nemáš kód k východu. Nemôžeš odísť.");
            }

            game.getPlayer().setCurrentroom(nextRoom);
            System.out.println("Nachádzaš sa v: " + nextRoom.getName());
            System.out.println(nextRoom.getDescription());
        }


    }


    public void processUse(String itemName) {
        if (itemName.isEmpty()) {
            System.out.println("Musíš zadať názov predmetu, ktorý chceš použiť.");
            return;
        }

        Optional<Item> itemOpt = game.getPlayer().getItemFromInventory(itemName);
        if (itemOpt.isPresent()) {
            System.out.println("Použil si predmet: " + itemOpt.get().getName());

        }

        Item item= itemOpt.get();
        Room currentRoom = game.getPlayer().getCurrentRoom();

        if (currentRoom.getId().equals('sklad') && item.getId().equals('poznamka')) {
            Room chodba = currentRoom.getExit('chodba');
            if (chodba != null && chodba.isLocked()) {
                chodba.setLocked(false);
                System.out.println("Použil si poznámku. Dvere do chodby sú teraz odomknuté.");
            }
            return;
        }

        if (currentRoom.getId().equals("kotolna") && item.getId().equals("poznamka")) {)) {
            game.getGameState().setBoilerSolved(true);
            Item klucTech = new Item("kluc_technicka", "kluc od technickej miestnosti", "Odomkne technickú miestnosť.", false);
            currentRoom.addItem(klucTech);
            System.out.println("🔧 Podľa indície si nastavil ventily. Zo skrinky vypadol kľúč od technickej miestnosti!");
            return;
        }

        if (currentRoom.getId().equals("chodba") && item.getId().equals("kluc_technicka")) {)) {
                Room technicka = currentRoom.getExit("technicka");a");
                if (technicka != null) {
                    technicka.setLocked(false);
                    System.out.println("🔓 Odomkol si technickú miestnosť.");
                }
                return;
            }

            if (currentRoom.getId().equals("technicka") && item.getId().equals("skrutkovac")) {)) {
                game.getGameState().setElectricPanelOpen(true);
                System.out.println("🛠️ Skrutkovač otvoril kryt elektrického panela.");
                return;
            }

            if (currentRoom.getId().equals("technicka") && item.getId().equals("poistka")) {
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


    }





    private String removeDiacritics(String input) {
        return input.replaceAll("[áä]", "a")
                    .replaceAll("[č]", "c")
                    .replaceAll("[ď]", "d")
                    .replaceAll("[éě]", "e")
                    .replaceAll("[í]", "i")
                    .replaceAll("[ĺľ]", "l")
                    .replaceAll("[ň]", "n")
                    .replaceAll("[óô]", "o")
                    .replaceAll("[ŕř]", "r")
                    .replaceAll("[š]", "s")
                    .replaceAll("[ť]", "t")
                    .replaceAll("[úů]", "u")
                    .replaceAll("[ý]", "y")
                    .replaceAll("[ž]", "z");
    }
}