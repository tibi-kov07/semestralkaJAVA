package logika;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private Room currentRoom;
    private final List<Item> inventory = new ArrayList<>();

    public Player(Room startingRoom){
        this.currentRoom = startingRoom;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room room){
        this.currentRoom = room;
    }

    public List<Item> getInventory(){
        return inventory;
    }

    public void addItem(Item item){
        inventory.add(item);
    }

    public void removeItem(Item item){
        inventory.remove(item);
    }

    public Optional<Item> getItemFromInventory(String name){
        return inventory.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name) || item.getId().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<Item> getCollectedEvidences(){
        return inventory.stream()
                .filter(Item::isEvidence)
                .collect(Collectors.toList());
    }
}
