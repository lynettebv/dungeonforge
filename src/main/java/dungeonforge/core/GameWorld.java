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
import dungeonforge.generate.GeneratorRegistry;
import dungeonforge.generate.LevelGenerator;

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
    private final GeneratorRegistry generators = new GeneratorRegistry();
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
            LevelGenerator shape = generators.forDepth(d);
            levels.add(shape.generate(d, theme, roomsPerLevel, d == dungeonDepth));
        }
        linkLevelsTogether();
    }



    /**
     * WEEK 10 STARTER (provided) -- breadth-first search from the entrance, then throw away
     * anything it could not get to.
     *
     * There is nothing wrong with this method. The problem is that calling it is OPTIONAL, and
     * one of the three builders above does not.
     */

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
