package logika;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room  implements Serializable{
    private static final long serialVersionUID = 1L;

    private final String id;
    private final String name;
    private final String description;
    private final Map <String, Room> exits; = new HashMap<>();
    private final List<logika.Item> items; = new ArrayList<>();
    private boolean locked;
    private String lockRequirementMessage;

    public Room(String id, String name, String description, boolean locked, String lockRequirementMessage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.locked = locked;
        this.lockRequirementMessage = lockRequirementMessage;
    }


    public void setExit(String direction, Room neighbor) {
        exits.put(direction.toLowerCase(), neighbor);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public Map<String, Room> getExits() {
        return exits;
    }

    public void addItem(Item item) {items.add(item);}
    public void removeItem(Item item) {items.remove(item);}
    public List<Item> getItems() {return items;}

    public String getId(){
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {return description;}

    public boolean isLocked() {
        return locked;
    }
    public void setLocked (boolean locked) {
        this.locked = locked;
    }
    public String getLockRequirementMessage() {
        return lockRequirementMessage;
    }

}
