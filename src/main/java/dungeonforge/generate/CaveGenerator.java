package dungeonforge.generate;

import dungeonforge.config.RandomSource;
import dungeonforge.core.DungeonLevel;
import dungeonforge.core.Room;

import java.util.List;

public class CaveGenerator extends LevelGenerator {
    @Override
    protected void connectRooms(DungeonLevel level) {
        List<Room> rooms = level.getRooms();
        String[] dirs = { "north", "south" };
        for (int r = 1; r < rooms.size(); r++) {
            int back = RandomSource.getInstance().nextInt(r);
            String dir = dirs[RandomSource.getInstance().nextInt(dirs.length)];
            String rev = dir.equals("north") ? "south" : "north";
            // Map-keyed exits: if this room already has an exit in `dir`, this OVERWRITES it.
            rooms.get(back).link(dir, rooms.get(r));
            rooms.get(r).link(rev, rooms.get(back));
        }
    }

    @Override
    public String shapeName() {
        return "cave";
    }
}
