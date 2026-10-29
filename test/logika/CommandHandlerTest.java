package logika;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class CommandHandlerTest {

    private Game game;
    private CommandHandler handler;
    private ByteArrayOutputStream output;
    private PrintStream originalOut;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        game = new Game();                      // uprav, ak má Game iný konštruktor
        handler = new CommandHandler(game);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    private String out() {
        return output.toString();
    }

    private void clearOut() {
        output.reset();
    }

    // ---------- základné príkazy ----------

    @Test
    void emptyInputDoesNothing() {
        handler.handleCommand("");
        assertEquals("", out());
    }

    @Test
    void helpListsCommands() {
        handler.handleCommand("pomoc");
        assertTrue(out().contains("Dostupné príkazy"));
        assertTrue(out().contains("vezmi"));
    }

    @Test
    void unknownCommandIsReported() {
        handler.handleCommand("blabla");
        assertTrue(out().contains("Tomuto príkazu nerozumiem"));
    }

    @Test
    void commandIsCaseInsensitive() {
        handler.handleCommand("POMOC");
        assertTrue(out().contains("Dostupné príkazy"));
    }

    @Test
    void lookPrintsRoomDescription() {
        handler.handleCommand("pozri");
        String description = game.getPlayer().getCurrentRoom().getDescription();
        assertTrue(out().contains(description));
    }


    @Test
    void searchEmptyRoomFindsNothing() {
        game.getPlayer().getCurrentRoom().getItems().clear();
        handler.handleCommand("prohledaj");
        assertTrue(out().contains("Nenašiel si nič nové."));
    }

    @Test
    void searchListsItemsInRoom() {
        game.getPlayer().getCurrentRoom().addItem(new Item("test_id", "Testovací predmet", "popis", false));
        handler.handleCommand("prohledaj");
        assertTrue(out().contains("Našiel si:"));
        assertTrue(out().contains("[Testovací predmet]"));
    }

    // ---------- inventár a dôkazy ----------

    @Test
    void emptyInventoryMessage() {
        game.getPlayer().getInventory().clear();
        handler.handleCommand("inventar");
        assertTrue(out().contains("Inventár je prázdny."));
    }

    @Test
    void inventoryListsItems() {
        game.getPlayer().addItem(new Item("test_id", "Lampa", "popis", false));
        handler.handleCommand("inventar");
        assertTrue(out().contains("Obsah inventára:"));
        assertTrue(out().contains("- Lampa"));
    }

    @Test
    void noEvidencesMessage() {
        handler.handleCommand("dokazy");
        assertTrue(out().contains("Zatiaľ si nezískal žiadne dôkazy."));
    }

    // ---------- vezmi ----------

    @Test
    void takeWithoutArgumentAsksForItem() {
        handler.handleCommand("vezmi");
        assertTrue(out().contains("Zadaj názov predmetu"));
    }

    @Test
    void takeNonexistentItem() {
        handler.handleCommand("vezmi neexistuje");
        assertTrue(out().contains("Taký predmet sa v miestnosti nenachádza."));
    }

    @Test
    void takeItemMovesItFromRoomToInventory() {
        Item item = new Item("test_xyz", "Testovaci predmet xyz", "popis", false);
        game.getPlayer().getCurrentRoom().addItem(item);

        handler.handleCommand("vezmi xyz");

        assertTrue(out().contains("Získal si predmet: Testovaci predmet xyz"), out());
        assertTrue(game.getPlayer().getInventory().contains(item), out());
        assertFalse(game.getPlayer().getCurrentRoom().getItems().contains(item));
    }

    @Test
    void takeItemWorksWithDiacriticsInInput() {
        Item item = new Item("test_zlty", "Žltý šíp", "popis", false);
        game.getPlayer().getCurrentRoom().addItem(item);

        handler.handleCommand("vezmi Žltý šíp");

        assertTrue(game.getPlayer().getInventory().contains(item), out());
    }
    @Test
    void takeItemWorksById() {
        Item item = new Item("kluc_test", "Kľúč", "Starý kľúč", false);
        game.getPlayer().getCurrentRoom().addItem(item);

        handler.handleCommand("vezmi kluc_test");

        assertTrue(game.getPlayer().getInventory().contains(item));
    }

    // ---------- jdi ----------

    @Test
    void moveWithoutArgumentFails() {
        handler.handleCommand("jdi");
        assertTrue(out().contains("Tadiaľto cesta nevedie."));
    }

    @Test
    void moveToNonexistentRoomFails() {
        var before = game.getPlayer().getCurrentRoom();
        handler.handleCommand("jdi nikam");
        assertTrue(out().contains("Tadiaľto cesta nevedie."));
        assertSame(before, game.getPlayer().getCurrentRoom());
    }

    // ---------- pouzi ----------

    @Test
    void useWithoutArgumentAsksForItem() {
        handler.handleCommand("pouzi");
        assertTrue(out().contains("Zadaj, čo chceš použiť."));
    }

    @Test
    void useItemNotInInventoryPrintsNothing() {
        handler.handleCommand("pouzi nieco");
        assertEquals("", out());
    }

    @Test
    void useItemWithNoEffect() {
        game.getPlayer().addItem(new Item("nic", "nic", "nema ucinok", false));
        clearOut();

        handler.handleCommand("pouzi nic");

        assertTrue(out().contains("Použil si predmet: nic"));
        assertTrue(out().contains("Tento predmet tu nevieš takto použiť."));
    }

    // ---------- procti ----------

    @Test
    void readNormalItemHasNoText() {
        game.getPlayer().addItem(new Item("lampa", "lampa", "popis", false));
        handler.handleCommand("procti lampa");
        assertTrue(out().contains("Tento predmet nemá žiadny text na prečítanie."));
    }

    @Test
    void readDocumentPrintsContent() {
        game.getPlayer().addItem(new Document("list", "list", "popis", "Tajný text", false));
        handler.handleCommand("procti list");
        assertTrue(out().contains("OBSAH DOKUMENTU"));
        assertTrue(out().contains("Tajný text"));
    }

    // ---------- mluv ----------

    @Test
    void talkToMartin() {
        handler.handleCommand("mluv martin");
        assertTrue(out().contains("Martin (správca budovy)"));
    }

    @Test
    void talkToUnknownCharacter() {
        handler.handleCommand("mluv niekto");
        assertTrue(out().contains("sa tu nenachádza"));
    }
}