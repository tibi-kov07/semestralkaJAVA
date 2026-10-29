package logika;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    private Room startRoom;
    private Player player;

    @BeforeEach
    void setUp() {
        startRoom = new Room("sklad", "SKLAD", "popis", false, null);
        player = new Player(startRoom);
    }

    @Test
    void getCurrentRoom() {
        assertSame(startRoom, player.getCurrentRoom());
    }

    @Test
    void setCurrentRoom() {
        Room other = new Room("chodba", "CHODBA", "popis", false, null);
        player.setCurrentRoom(other);
        assertSame(other, player.getCurrentRoom());
    }

    @Test
    void getInventory() {
        assertNotNull(player.getInventory());
        assertTrue(player.getInventory().isEmpty());
    }

    @Test
    void addItem() {
        Item item = new Item("baterka", "baterka", "popis", false);
        player.addItem(item);

        assertEquals(1, player.getInventory().size());
        assertTrue(player.getInventory().contains(item));
    }

    @Test
    void removeItem() {
        Item item = new Item("baterka", "baterka", "popis", false);
        player.addItem(item);

        player.removeItem(item);

        assertFalse(player.getInventory().contains(item));
        assertTrue(player.getInventory().isEmpty());
    }

    @Test
    void getItemFromInventory() {
        Item item = new Item("kluc_chodba", "Kluc od chodby", "popis", false);
        player.addItem(item);

        assertSame(item, player.getItemFromInventory("kluc od chodby").get());
        assertSame(item, player.getItemFromInventory("KLUC OD CHODBY").get());

        assertSame(item, player.getItemFromInventory("kluc_chodba").get());

        assertTrue(player.getItemFromInventory("neexistuje").isEmpty());
    }

    @Test
    void getCollectedEvidences() {
        player.addItem(new Item("lampa", "lampa", "popis", false));
        assertTrue(player.getCollectedEvidences().isEmpty());

        Document notEvidence = new Document("list", "list", "popis", "text", false);
        Document evidence = new Document("dokaz", "dokaz", "popis", "text", true);
        player.addItem(notEvidence);
        player.addItem(evidence);

        List<Item> result = player.getCollectedEvidences();

        assertEquals(1, result.size());
        assertSame(evidence, result.get(0));
    }
}