package logika;

import java.io.Serializable;

public class Item implements Serializable {
    private final String id;
    private final String name;
    private final String description;
    private final boolean consumable;

    public Item(String id, String name, String description, boolean consumable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.consumable = consumable;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEvidence(){
        return false;
    }
}
