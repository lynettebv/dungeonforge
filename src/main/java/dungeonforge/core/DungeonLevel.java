package dungeonforge.core;

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 1 -- one level of the dungeon.
 *
 * TODO(week 4, US-2.2): a level has no idea what KIND of place it is. Every level is
 * identical in character. A level should know its theme.
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
