package logika;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room  implements Serializable{
    private static final log serialVersionUID = 1L;

    private final String id;
    private final Sting name;
    private final String desctiption;
    private final Map <String, Room> exits; = new HashMap<>();
    private final List<Item> items; = new ArrayList<>();
    private boolean locked;
    private String lockRequirementMessage;

    public Room(String id, String name, String description, boolean locked, String lockRequirementMessage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.locked = locked;
        this.lockRequirementMessage = lockRequirementMessage;
    }
}
