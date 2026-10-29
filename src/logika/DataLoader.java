package logika;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
public class DataLoader {
 
    public static Map<String, Room> loadRooms(String resourcePath) {
        Map<String, Room> rooms = new HashMap<>();
 
        try (InputStream is = DataLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Súbor rešursu nebol nájdený: " + resourcePath);
                return rooms;
            }
 
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                boolean isHeader = true;
 
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || isHeader) {
                        isHeader = false;
                        continue;
                    }
 
                    String[] parts = line.split("\\|");
                    if (parts.length >= 5) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        String description = parts[2].trim();
                        boolean isLocked = Boolean.parseBoolean(parts[3].trim());
                        String lockMsg = parts[4].trim().equals("null") ? null : parts[4].trim();
 
                        rooms.put(id, new Room(id, name, description, isLocked, lockMsg));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Chyba pri načítavaní miestností: " + e.getMessage());
        }
 
        return rooms;
    }
 
    public static void loadItemsIntoRooms(String resourcePath, Map<String, Room> rooms) {
        try (InputStream is = DataLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Súbor rešursu nebol nájdený: " + resourcePath);
                return;
            }
 
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                boolean isHeader = true;
 
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || isHeader) {
                        isHeader = false;
                        continue;
                    }
 
                    String[] parts = line.split("\\|");
                    if (parts.length >= 8) {
                        String id = parts[0].trim();
                        String name = parts[1].trim();
                        String description = parts[2].trim();
                        String roomId = parts[3].trim();
                        boolean isConsumable = Boolean.parseBoolean(parts[4].trim());
                        boolean isDocument = Boolean.parseBoolean(parts[5].trim());
                        String readableContent = parts[6].trim().equals("null") ? null : parts[6].trim();
                        boolean isEvidence = Boolean.parseBoolean(parts[7].trim());
 
                        Item item;
                        if (isDocument) {
                            item = new Document(id, name, description, readableContent, isEvidence);
                        } else {
                            item = new Item(id, name, description, isConsumable);
                        }
 
                        Room targetRoom = rooms.get(roomId);
                        if (targetRoom != null) {
                            targetRoom.addItem(item);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Chyba pri načítavaní predmetov: " + e.getMessage());
        }
    }
}
