package dungeonforge.core;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 1 -- one level of the dungeon.
 * WEEK 4 -- a level now knows what KIND of place it is. It stores the theme's NAME rather
 * than the kit itself, because a level is a piece of world state, not a factory.
 */
public class DungeonLevel {

    private final int depth;
    private final String themeName;
    private final List<Room> rooms = new ArrayList<>();

    public DungeonLevel(int depth, String themeName) {
        this.depth = depth;
        this.themeName = themeName;
    }

    public int getDepth()        { return depth; }
    public String getThemeName() { return themeName; }
    public List<Room> getRooms() { return rooms; }
    public void addRoom(Room r)  { rooms.add(r); }
}
