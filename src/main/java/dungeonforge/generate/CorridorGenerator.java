package dungeonforge.generate;

import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Room;

import java.util.List;

public class CorridorGenerator extends LevelGenerator {
    @Override
    protected void connectRooms(DungeonLevel level) {
        List<Room> rooms = level.getRooms();
        for (int r = 0; r + 1 < rooms.size(); r++) {
            rooms.get(r).link("north", rooms.get(r + 1));
            rooms.get(r + 1).link("south", rooms.get(r));
        }
    }

    @Override
    public String shapeName() {
        return "corridor";
    }
}
