package dungeonforge.core;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.factory.BossRoomPopulator;
import dungeonforge.factory.MonsterFactory;
import dungeonforge.factory.RoomPopulator;
import dungeonforge.factory.StandardRoomPopulator;
import dungeonforge.factory.ThemeKit;
import dungeonforge.factory.ThemeRegistry;
import dungeonforge.factory.TreasureRoomPopulator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WEEK 1 -- the world.
 * WEEK 3 -- configuration and randomness come from singletons.
 * WEEK 4 -- creation is gone.
 * WEEK 10 STARTER -- and three level SHAPES have arrived, which undid some of Week 4's work.
 *
 * ==================== READ THIS BEFORE PART A ====================
 * Week 7's linkRooms() built one shape: a straight corridor. Since then two more shapes have
 * been added, and each one was written by copying the previous method and changing the middle.
 *
 * The three builders below are 90% the same code. Read them side by side -- literally, two
 * windows -- and find the differences. There are more than you expect, and only ONE of them is
 * about the shape of the level.
 *
 * The other differences are DRIFT: steps that one copy does and another forgot. Nothing told
 * anybody. The compiler is perfectly happy. The tests pass. And two of the three shapes
 * produce levels you cannot finish.
 *
 * Run `mvn -q exec:java -Dexec.args="--report"` and read the table before you read the code.
 * =================================================================
 */
public class GameWorld {

    private final Player player;
    private final MonsterFactory monsterFactory = new MonsterFactory();
    private final ThemeRegistry themes = new ThemeRegistry(monsterFactory);
    private final List<DungeonLevel> levels = new ArrayList<>();

    public GameWorld(Player player) {
        this.player = player;
        generate();
    }

    /**
     * WEEK 10 STARTER -- and here is the other half of the problem: choosing between the three
     * shapes is a switch in GameWorld. Week 4 got creation out of this class. This switch put a
     * decision back in.
     */
    private void generate() {
        GameConfig cfg = GameConfig.getInstance();
        int dungeonDepth = cfg.getInt("dungeonDepth");
        int roomsPerLevel = cfg.getInt("roomsPerLevel");

        for (int d = 1; d <= dungeonDepth; d++) {
            ThemeKit theme = themes.forDepth(d);
            DungeonLevel level;
            switch (d % 3) {
                case 1:  level = buildCorridorLevel(d, theme, roomsPerLevel); break;
                case 2:  level = buildCaveLevel(d, theme, roomsPerLevel);     break;
                default: level = buildRingLevel(d, theme, roomsPerLevel);     break;
            }
            levels.add(level);
        }
        linkLevelsTogether();
    }

    // =====================================================================================
    // SHAPE 1 -- CORRIDOR. The original, written in Week 7. This one is correct.
    // =====================================================================================
    private DungeonLevel buildCorridorLevel(int d, ThemeKit theme, int roomsPerLevel) {
        DungeonLevel level = new DungeonLevel(d, theme.themeName());

        // --- step 1: create the rooms ---
        for (int r = 0; r < roomsPerLevel; r++) level.addRoom(new Room("L" + d + "R" + r));

        // --- step 2: connect them. THIS is the only part that is really about the shape. ---
        List<Room> rooms = level.getRooms();
        for (int r = 0; r + 1 < rooms.size(); r++) {
            rooms.get(r).link("north", rooms.get(r + 1));
            rooms.get(r + 1).link("south", rooms.get(r));
        }

        // --- step 3: the entrance stays safe ---
        rooms.get(0).setFlavor(theme.createRoomFlavor());

        // --- step 4: populate everything that is not the entrance ---
        for (int r = 1; r < rooms.size(); r++) {
            boolean isBossRoom = (r == rooms.size() - 1)
                    && (d == GameConfig.getInstance().getInt("dungeonDepth"));
            populatorFor(theme, isBossRoom).populate(rooms.get(r), d);
        }

        // --- step 5: make sure every room can actually be reached ---
        pruneUnreachable(level);

        return level;
    }

    // =====================================================================================
    // SHAPE 2 -- CAVE. A branching graph. Copied from buildCorridorLevel and edited.
    // =====================================================================================
    private DungeonLevel buildCaveLevel(int d, ThemeKit theme, int roomsPerLevel) {
        DungeonLevel level = new DungeonLevel(d, theme.themeName());

        // --- step 1: create the rooms ---
        for (int r = 0; r < roomsPerLevel; r++) level.addRoom(new Room("L" + d + "R" + r));

        // --- step 2: connect them -- each room links back to a random earlier room ---
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

        // --- step 3: the entrance stays safe ---
        rooms.get(0).setFlavor(theme.createRoomFlavor());

        // --- step 4: populate everything that is not the entrance ---
        for (int r = 1; r < rooms.size(); r++) {
            boolean isBossRoom = (r == rooms.size() - 1)
                    && (d == GameConfig.getInstance().getInt("dungeonDepth"));
            populatorFor(theme, isBossRoom).populate(rooms.get(r), d);
        }

        // --- step 5: ...
        //
        // DRIFT. There is no step 5 here. Whoever wrote this copied steps 1 to 4 and stopped.
        // Because step 2 overwrites Map-keyed exits, some rooms end up with no path from the
        // entrance -- including, sometimes, the room with the stairs down.
        //
        // Do not fix this by pasting pruneUnreachable(level) at the bottom. That is the third
        // copy of the same idea and it is how this file got into this state. US-8.1.

        return level;
    }

    // =====================================================================================
    // SHAPE 3 -- RING. A loop, so you can walk all the way around. Copied from the cave one.
    // =====================================================================================
    private DungeonLevel buildRingLevel(int d, ThemeKit theme, int roomsPerLevel) {
        DungeonLevel level = new DungeonLevel(d, theme.themeName());

        // --- step 1: create the rooms ---
        for (int r = 0; r < roomsPerLevel; r++) level.addRoom(new Room("L" + d + "R" + r));

        // --- step 2: connect them in a circle ---
        List<Room> rooms = level.getRooms();
        int n = rooms.size();
        for (int r = 0; r < n; r++) {
            Room here = rooms.get(r);
            Room next = rooms.get((r + 1) % n);
            here.link("north", next);
            next.link("south", here);
        }

        // --- step 3: the entrance ---
        rooms.get(0).setFlavor(theme.createRoomFlavor());

        // --- step 4: populate the rooms ---
        //
        // DRIFT. Look at where this loop starts. The other two begin at r = 1 so that the room
        // the player spawns in is empty. This one begins at r = 0.
        //
        // On a ring level the player materialises inside an occupied room, and the monsters get
        // to act before the player can do anything about it.
        for (int r = 0; r < rooms.size(); r++) {
            boolean isBossRoom = (r == rooms.size() - 1)
                    && (d == GameConfig.getInstance().getInt("dungeonDepth"));
            populatorFor(theme, isBossRoom).populate(rooms.get(r), d);
        }

        // --- step 5: make sure every room can be reached ---
        pruneUnreachable(level);

        return level;
    }

    // =====================================================================================

    /**
     * WEEK 10 STARTER (provided) -- breadth-first search from the entrance, then throw away
     * anything it could not get to.
     *
     * There is nothing wrong with this method. The problem is that calling it is OPTIONAL, and
     * one of the three builders above does not.
     */
    private void pruneUnreachable(DungeonLevel level) {
        List<Room> rooms = level.getRooms();
        if (rooms.isEmpty()) return;

        Set<Room> seen = new HashSet<>();
        Deque<Room> queue = new ArrayDeque<>();
        queue.add(rooms.get(0));
        seen.add(rooms.get(0));
        while (!queue.isEmpty()) {
            Room here = queue.poll();
            for (Room next : here.getExits().values()) {
                if (seen.add(next)) queue.add(next);
            }
        }
        rooms.removeIf(r -> !seen.contains(r));
    }

    /** The stairs between levels. The last room of each level leads down to the next. */
    private void linkLevelsTogether() {
        for (int d = 0; d + 1 < levels.size(); d++) {
            List<Room> above = levels.get(d).getRooms();
            List<Room> below = levels.get(d + 1).getRooms();
            if (above.isEmpty() || below.isEmpty()) continue;
            above.get(above.size() - 1).link("down", below.get(0));
        }
    }

    public Room startingRoom() { return levels.get(0).getRooms().get(0); }

    public DungeonLevel levelContaining(Room room) {
        for (DungeonLevel l : levels) {
            if (l.getRooms().contains(room)) return l;
        }
        return levels.get(0);
    }

    /**
     * The only place that chooses BETWEEN populators. Note that it returns the abstract type,
     * so everything downstream is written against RoomPopulator and never against a subclass.
     */
    private RoomPopulator populatorFor(ThemeKit theme, boolean isFinalRoom) {
        if (isFinalRoom) return new BossRoomPopulator(theme);
        if (RandomSource.getInstance().nextDouble() < 0.30) return new TreasureRoomPopulator(theme);
        return new StandardRoomPopulator(theme);
    }

    public Player getPlayer()             { return player; }
    public List<DungeonLevel> getLevels() { return levels; }
    public MonsterFactory getMonsterFactory() { return monsterFactory; }
    public ThemeRegistry getThemes()      { return themes; }

    public int totalMonsters() {
        int n = 0;
        for (DungeonLevel l : levels) {
            for (Room r : l.getRooms()) n += r.getMonsters().size();
        }
        return n;
    }

    public int totalLoot() {
        int n = 0;
        for (DungeonLevel l : levels) {
            for (Room r : l.getRooms()) {
                if (r.hasChest()) n += r.getChest().getContents().size();
            }
        }
        return n;
    }
}
