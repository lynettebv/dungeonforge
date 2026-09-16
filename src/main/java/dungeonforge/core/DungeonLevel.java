package dungeonforge.core;

import java.util.ArrayList;
import java.util.List;

/** WEEK 1 -- one level of the dungeon. Nothing wrong with this class. */
public class DungeonLevel {

    private final int depth;
    private final List<Room> rooms = new ArrayList<>();

    public DungeonLevel(int depth) { this.depth = depth; }

    public int getDepth()        { return depth; }
    public List<Room> getRooms() { return rooms; }
    public void addRoom(Room r)  { rooms.add(r); }
}
