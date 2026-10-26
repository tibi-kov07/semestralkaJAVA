package logika;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private Room currentRoom;
    private final List<Item> inventoryy = new ArrayList<>();

    public Player(Room startingRoom){
        this.currentRoom = startingRoom;
    }
}
