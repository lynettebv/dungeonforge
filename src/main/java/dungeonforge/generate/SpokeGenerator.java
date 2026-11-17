package dungeonforge.generate;

import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Room;

import java.util.List;

public class SpokeGenerator extends LevelGenerator {
    @Override
    protected void connectRooms(DungeonLevel level) {
        List<Room> rooms = level.getRooms();
        if (rooms.size() < 2) return;

        Room hub = rooms.get(0);
        Room[] armEnd = new Room[DIRECTIONS.length];
        for (int r = 1; r <rooms.size(); r++) {
            int arm = (r - 1) % DIRECTIONS.length;
            String direction = DIRECTIONS[arm];
            Room room = rooms.get(r);
            if (armEnd[arm] == null) {
                if (tryConnect(hub, direction, room)) armEnd[arm] = room;
            } else if (tryConnect(armEnd[arm], direction, room)) {
                armEnd[arm] = room;
            }
        }
    }

    @Override
    public String shapeName() { return "spoke"; }
}
