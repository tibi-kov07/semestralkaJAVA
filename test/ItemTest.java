
import logika.Item;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void getId() {
        Item item = new Item("1", "Kľúč", "Starý hrdzavý kľúč", false);
        assertEquals("1", item.getId());
    }

    @Test
    void getName() {
        Item item = new Item("1", "Kľúč", "Starý hrdzavý kľúč", false);
        assertEquals("Kľúč", item.getName());
    }

    @Test
    void getDescription() {
        Item item = new Item("1", "Kľúč", "Starý hrdzavý kľúč", false);
        assertEquals("Starý hrdzavý kľúč", item.getDescription());
    }

    @Test
    void isEvidence() {
        Item item = new Item("1", "Kľúč", "Starý hrdzavý kľúč", false);
        assertFalse(item.isEvidence());
    }
}