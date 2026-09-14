package dungeonforge.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * WEEK 1 -- the world.
 *
 * TODO(week 3, US-1.1): dungeonDepth, roomsPerLevel and maxMonstersPerRoom are hardcoded.
 * TODO(week 3, US-1.2): randomness source #3 of 3.
 *
 * Run this program twice. You get a different dungeon each time, and there is no way to
 * ask for the one you saw before. That is the problem US-1.2 exists to solve.
 */
public class GameWorld {

    /** Randomness source #3 of 3. */
    private final Random random = new Random();

    private final Player player;
    private final List<DungeonLevel> levels = new ArrayList<>();

    public GameWorld(Player player) {
        this.player = player;
        generate();
    }

    private void generate() {
        int dungeonDepth = 3;         // hardcoded
        int roomsPerLevel = 8;        // hardcoded
        int maxMonstersPerRoom = 2;   // hardcoded

        for (int d = 1; d <= dungeonDepth; d++) {
            DungeonLevel level = new DungeonLevel(d);
            for (int r = 0; r < roomsPerLevel; r++) {
                Room room = new Room("L" + d + "R" + r);
                int count = random.nextInt(maxMonstersPerRoom + 1);
                for (int m = 0; m < count; m++) {
                    room.addMonster(spawn(d));
                }
                level.addRoom(room);
            }
            levels.add(level);
        }
    }

    private Monster spawn(int depth) {
        String[] species = {"Skeleton", "Crypt Rat", "Wight", "Bone Priest"};
        String pick = species[random.nextInt(species.length)];
        return new Monster(pick, 12 + depth * 4, 4 + depth, 6 + depth * 3);
    }

    public Player getPlayer()             { return player; }
    public List<DungeonLevel> getLevels() { return levels; }

    public int totalMonsters() {
        int n = 0;
        for (DungeonLevel l : levels) {
            for (Room r : l.getRooms()) n += r.getMonsters().size();
        }
        return n;
    }
}
