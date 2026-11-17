package dungeonforge.generate;

import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Room;

import java.util.List;

public class CorridorGenerator extends LevelGenerator {
    @Override
    protected void connectRooms(DungeonLevel level) {
        List<Room> rooms = level.getRooms();
        for (int r = 0; r + 1 < rooms.size(); r++) {
            connect(rooms.get(r), "north", rooms.get(r+1));
        }
    }

    @Override
    public String shapeName() {
        return "corridor";
    }
}
