package dungeonforge.core;

import dungeonforge.config.GameConfig;
import dungeonforge.config.RandomSource;
import dungeonforge.factory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * WEEK 1 -- the world.
 * TODO(week 3, US-1.1): dungeonDepth, roomsPerLevel and maxMonstersPerRoom are hardcoded.
 * TODO(week 3, US-1.2): randomness source #3 of 3.
 * Run this program twice. You get a different dungeon each time, and there is no way to
 * ask for the one you saw before. That is the problem US-1.2 exists to solve.
 */
public class GameWorld {


    private final Player player;
    private MonsterFactory monsterFactory = new MonsterFactory();
    private final ThemeRegistry themes = new ThemeRegistry(monsterFactory);
    private final List<DungeonLevel> levels = new ArrayList<>();

    public GameWorld(Player player) {
        this.player = player;
        generate();
    }


    private void generate() {
        GameConfig cfg = GameConfig.getInstance();
        int dungeonDepth = cfg.getInt("dungeonDepth");         // hardcoded
        int roomsPerLevel = cfg.getInt("roomsPerLevel");        // hardcoded
        int maxMonstersPerRoom = cfg.getInt("maxMonstersPerRoom");

        for (int d = 1; d <= dungeonDepth; d++) {
            ThemeKit theme = themes.forDepth(d);
            DungeonLevel level = new DungeonLevel(d, theme.themeName());
            for (int r = 0; r < roomsPerLevel; r++) {
                Room room = new Room("L" + d + "R" + r);
                boolean isEntrance = (r == 0);
                boolean isFinalRoom = (d == dungeonDepth && r == roomsPerLevel - 1);

                if(isEntrance) {
                    room.setFlavor(theme.createRoomFlavor());
                } else {
                    populateFor(theme, isFinalRoom).populate(room, d);
                }
                level.addRoom(room);
            }
            levels.add(level);
        }
    }
    // ONLY place that chooses between populators
    private RoomPopulator populateFor(ThemeKit theme, boolean isFinalRoom) {
        if(isFinalRoom) return new BossRoomPopulator(theme);
        if(RandomSource.getInstance().nextDouble() < 0.30) return new TreasureRoomPopulator(theme);
        return new StandardRoomPopulator(theme);
    }

    public Player getPlayer()             { return player; }
    public List<DungeonLevel> getLevels() { return levels; }
    public MonsterFactory getMonsterFactory() { return monsterFactory; }
    public ThemeRegistry getThemes() { return themes; }

    public int totalMonsters() {
        int n = 0;
        for (DungeonLevel l : levels) {
            for (Room r : l.getRooms()) n += r.getMonsters().size();
        }
        return n;
    }
}
