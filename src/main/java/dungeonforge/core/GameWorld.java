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

import java.util.ArrayList;
import java.util.List;

/**
 * WEEK 1 -- the world.
 * WEEK 3 -- configuration and randomness come from singletons.
 * WEEK 4 -- creation is gone.
 *
 * Read this class and notice what is ABSENT. There is no `new Monster(`. There is no species
 * list. There are no stat formulas. There is no flavour text. GameWorld no longer knows that
 * skeletons exist, and adding a fourth theme will not bring it back into the conversation.
 *
 * That absence is the deliverable. "Identify the aspects that vary and separate them from
 * what stays the same" -- what stays the same is the LOOP; what varies is everything the
 * loop puts inside a room.
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

    private void generate() {
        GameConfig cfg = GameConfig.getInstance();
        int dungeonDepth = cfg.getInt("dungeonDepth");
        int roomsPerLevel = cfg.getInt("roomsPerLevel");

        for (int d = 1; d <= dungeonDepth; d++) {
            ThemeKit theme = themes.forDepth(d);              // ABSTRACT FACTORY
            DungeonLevel level = new DungeonLevel(d, theme.themeName());

            for (int r = 0; r < roomsPerLevel; r++) {
                Room room = new Room("L" + d + "R" + r);
                boolean isEntrance = (r == 0);
                boolean isFinalRoom = (d == dungeonDepth && r == roomsPerLevel - 1);

                if (isEntrance) {
                    room.setFlavor(theme.createRoomFlavor());  // safe: no encounter
                } else {
                    populatorFor(theme, isFinalRoom).populate(room, d);   // FACTORY METHOD
                }
                level.addRoom(room);
            }
            levels.add(level);
        }
        linkRooms();
    }

    /**
     * WEEK 7 (provided): wire the rooms into something walkable.
     *
     * Deliberately a straight corridor -- "north" goes deeper, "south" goes back, and the last
     * room of a level has "down" to the next level. Week 10's generators replace this with a
     * real graph. It is here so that "move" is a genuine action this week.
     */
    private void linkRooms() {
        for (int d = 0; d < levels.size(); d++) {
            List<Room> rooms = levels.get(d).getRooms();
            for (int r = 0; r + 1 < rooms.size(); r++) {
                rooms.get(r).link("north", rooms.get(r + 1));
                rooms.get(r + 1).link("south", rooms.get(r));
            }
            if (d + 1 < levels.size()) {
                rooms.get(rooms.size() - 1).link("down", levels.get(d + 1).getRooms().get(0));
            }
        }
    }

    public Room startingRoom() { return levels.get(0).getRooms().get(0); }

    public DungeonLevel levelContaining(Room room) {
        for (DungeonLevel l : levels) if (l.getRooms().contains(room)) return l;
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

