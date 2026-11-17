package dungeonforge.generate;

import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Room;

import java.util.List;

public class RingGenerator extends LevelGenerator {
    @Override
    protected void connectRooms(DungeonLevel level) {
        List<Room> rooms = level.getRooms();
        int n = rooms.size();
        if (n < 2) return;
        for (int r = 0; r < n; r++) {
            connect(rooms.get(r), "north", rooms.get((r + 1) % n));
        }
    }

    @Override
    public String shapeName() {
        return "ring";
    }
}
