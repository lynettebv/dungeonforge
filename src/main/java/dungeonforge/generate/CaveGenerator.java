package dungeonforge.generate;

import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Room;

import java.util.List;

public class CaveGenerator extends LevelGenerator {
    @Override
    protected void connectRooms(DungeonLevel level) {
        List<Room> rooms = level.getRooms();
        for (int r = 1; r < rooms.size(); r++) {
            attachToSomeEarlierRoom(rooms, r);

        }
    }

    private void attachToSomeEarlierRoom(List<Room> rooms, int r) {
        Room child = rooms.get(r);
        for (int attempt = 0; attempt < r * DIRECTIONS.length * 2; attempt++) {
            Room parent = rooms.get(rng().nextInt(r));
            String dir = DIRECTIONS[rng().nextInt(DIRECTIONS.length)];
            if ( tryConnect(parent, dir, child)) return;
        }
        // deterministic deep room connect in case the above loop is unlucky
        for (int i=0; i < r; i++) {
            for (String dir : DIRECTIONS) {
                if (tryConnect(rooms.get(i), dir, child)) return;
            }
        }
    }

    @Override
    public String shapeName() { return "cave"; }
}
